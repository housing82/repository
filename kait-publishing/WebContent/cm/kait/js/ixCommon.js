/*****************************************************************************
 * 공통 데이터셋, 데이터어답터 생성
 *****************************************************************************/
//헤더에 스프링 보안 시큐리티 토근 정보 
var header = $("meta[name='_csrf_header']").attr("content");
var token = $("meta[name='_csrf']").attr("content");

// 데이터 바인딩 정보 세팅
function ixCreateDataBindInfo(dataType, dtName, selectUrl, updateUrl, header) {
	// dataSet, dataAdapter을 window 객체로 선언
	window.dataSet = ixCreateDataSet('ds');
	window.dataAdapter = ixCreateAdapter(dataSet, 'da', dataType);
	
	// dataTable은  여러개 일 수 있으므로 별도 처리
	window.dataTable = ixCreateDataTable(dataSet, dtName, selectUrl, updateUrl, header);	
};

// 데이터셋 생성
function ixCreateDataSet(dsName){
    if (window.XgPlatform.XgDataSet[dsName]) {
        return window.XgPlatform.XgDataSet[dsName];
    } else {
        return new XgDataSet(dsName);
    }   
};

// 데이터어답터 생성
function ixCreateAdapter(dataSet, daName, dataType){

    // firstRow 기본값은 false, async 기본값은 비동기, requestType 기본값은 POST 	
    var dataAdapter = dataSet.getDataAdapter();
    if (dataAdapter && dataAdapter.xgDataAdapterId) {
        return dataAdapter;
    }
    if ((typeof daName) === undefined) {
        throw new Error('不存在Adapter名称');
    }
    
	dataAdapter = new XgDataAdapter(daName);
	dataAdapter.setDataType(dataType);
	dataAdapter.addRequestHeader(header, token);
	dataSet.setDataAdapter(dataAdapter);
    
    return dataAdapter;
};

// 데이터테이블 및 데이터테이블 헤더 정보 생성
function ixCreateDataTable(dataSet, dtName, selectUrl, updateUrl, header){
    var dataTable = dataSet.getDataTable(dtName);
    if(dataTable && dataTable.id){
        return dataTable;
    }  	
	if(typeof dtName === 'string'){
		ixSetDataTableInfo(dtName, header, selectUrl, updateUrl)
	}else{
		// 2개 이상의 배열로 받을 경우
		if(typeof selectUrl === 'string' && typeof updateUrl === 'string'){
			for(var i = 0; i < dtName.length; i++){
				if(i === 0){
					ixSetDataTableInfo(dtName[i], header[i], selectUrl, updateUrl);
				}else{
					ixSetDataTableInfo(dtName[i], header[i]);
				}
			}
		}else{
			for(var i = 0; i < dtName.length; i++){
				ixSetDataTableInfo(dtName[i], header[i], selectUrl[i], updateUrl[i]);
			}
		}
	}	
};

/*function resetDatas(){
	var dtIdArray = ds.getgetDataTableIds();
	var len = dtIdArray.length;
	
	for(var i = 0; i < len; i++){
		eval(dtIdArray[i] +" = null");
	}
	
	dataSet = null;
	dataAdapter = null;
}
*/
/*****************************************************************************
 * DataTable 정보 세팅
 *****************************************************************************
 * @param	dtName		    - 생성할 dataTable 이름
 * @param	header	        - header 정보
 * @param	selectUrl	    - select Url 정보
 * @param	updateUrl	    - update Url 정보
*****************************************************************************/
function ixSetDataTableInfo(dtName, header, selectUrl, updateUrl){
	
    var dataTable = window.dataSet.getDataTable(dtName);
    if (dataTable && dataTable.id) {
        return dataTable;
    } else {
    	eval("window." + dtName + " = new XgDataTable('" +dtName + "');" );

    	if(selectUrl){
    		eval(dtName + ".setUrl('select', '" + selectUrl +"');");
    	}
    	if(updateUrl){
    		eval(dtName + ".setUrl('update', '" + updateUrl +"');");
    	}

    	dataSet.addDataTable(eval(dtName));	
    	ixColumnHeaderInfo(eval(dtName), header);
    	ixGridColumnHeaderInfo(dtName, header);
    }   
};

/*****************************************************************************
 * 공통 header 정보 생성 (DB 기준)
 *****************************************************************************
 * @param	dataTable		- column을 생성할 dataTable [object]
 * @param	header	        - header 정보
*****************************************************************************/
function ixColumnHeaderInfo(dataTable, header) {	
	for (var i=0; i < header.length; i++){
		if(header[i].colName != null){			
			dataTable.addColumnForGauce(header[i].colName, header[i].colType, header[i].colSize);
		}
	}		
};

/*****************************************************************************
 * Grid header 정보 생성
 *****************************************************************************
 * @param	dataTable		- column을 생성할 dataTable 명 [String]
 * @param	header	        - header 정보
*****************************************************************************/
function ixGridColumnHeaderInfo(dataTable, header) {
	var gridColumns = [];
	
	for (var i=0; i < header.length; i++){
		if(header[i].gridColumnName != null){
			var defaultOptions = {};
			if(header[i].gridColumnName){
				defaultOptions = {
			        text: header[i].gridColumnName,
			        datafield: header[i].colName,
			        width: header[i].gridColumnWidth || 'auto',
			        align: 'center',
			        cellsalign: 'center'
				};
				if ((typeof header[i].gridColumnOptions) === 'object') {
			        for (name in header[i].gridColumnOptions) {
			            defaultOptions[name] = header[i].gridColumnOptions[name];
			        }
			    }			
				gridColumns.push(defaultOptions);		
			}
		}
	}
	
	eval("window."+dataTable+"GridColumns = gridColumns");
};

/*****************************************************************************
 * Grid 옵션 설정
 *****************************************************************************
 * @param	dtName		- Grid에 바인딩할 dataTable 이름 [string]
 * @param	columns 	- Grid columns 
 * @param	options		- 기본 옵션 외 사용자 지정 옶션
*****************************************************************************/
function ixGridCreateOptions(dataTable, options) {
	var dataTableId = dataTable.getId();
	var columns = eval("window." + dataTableId + "GridColumns");

    var defaultOptions = {
        width: '100%',
        height: '100%',
        xgInitOption: {
            xgDataSet: dataSet,
            xgDataTable: dataTable,
            columns: columns
        },
	    xgBindOption: {
	        cellEdit: true,
	        cellValidation: true
	    }
    };
    if ((typeof options) === 'object') {
        for (name in options) {
            defaultOptions[name] = options[name];
        }
    }
    return defaultOptions;
};

/*****************************************************************************
 * 공통 select 함수
 *****************************************************************************
 * @param	target		- load 이미지를 보여줄 grid [array] 그리드가 아닐경우 null을 보면 된다.
 * @param   dataTableId - 사용될 dataTable의 ID. 여러개일 경우 배열로 넘어온다[string or array]
 * @param	option		- 사용자 지정 옵션
 * 							> button:버튼 오브젝트 (버튼 비활성화/활성화)
 * @param	serialize		- 요청을 보낼 파라미터 [string / object]
 * @param	loadingYn	- 로딩이미지 출력 선택 [true:출력(default) / false:미출력)]
 * @param	messageYn	- 처리 결과 메시지 출력 선택 [true:출력(default) / false:미출력)]
 * @param   alertConfig - ixCreateAlert 함수를 실행하기 위한 파라미터가 담긴 배열  messageYn이 false일 경우 쓰이지 않는다 - array
 * @param	callback	- 사용자 지정 함수
 *****************************************************************************/
function ixSelect(target, dataTableId, options, serialize, loadingYn, messageYn, alertConfig, callback) { 
	
	//각각 index.jsp 에 mainType이라는 변수가 있음
	if(parent.mainType == 'admin' || parent.mainType == 'user'){
		//세션 체크 프레임을 리로드 시킨다. admin, user의 sessionChk.jsp를 보면 비로그인 상태일 경우 튕겨내는 로직이 있다
		parent.document.getElementById("sessionChkFrame").src = "/" + parent.mainType + "/sessionChk";		
	}
	
	// opts에 button이 있을 경우 비활성화 처리
    var opts = options || {};
	if (opts.button) {
        opts.button.xgButton({disabled: true});
    }
	
	// 시리얼라이즈 값이 넘어 올 경우 내부적으로 params 만들어준다.
	var params = "";

	if(serialize){
		if(typeof serialize === 'object'){
			//params += '&X-UUID=' +guid() + "&";
			param = ixGetSerialize(serialize[0], serialize[1], serialize[2], serialize[3]);
			if (param != null) {
				params += param;
			}
			//params += ixGetSerialize(serialize[0], serialize[1], serialize[2], serialize[3]);

		}else if(typeof serialize === 'string'){
			params += serialize;
			
		}
	}
	alert(params);
				
	// targetType에 따른 옵션 실행
	if(loadingYn === true){
		ixLoadElement(target, 'show');
	}
   	    
	// dataTable 클리어
	if(typeof dataTableId == 'string'){
		eval(dataTableId + ".clearData()");  
	}else{
		for(var i = 0; i < dataTableId.length; i++){
			eval(dataTableId[i] + ".clearData()");  
		}
	}
     /**
	// socd 설정
	if(typeof dataTableId === 'string'){
		dataAdapter.setSOCD("'(O:"+dataTableId + "="+ dataTableId+")'");
	}else{
		var socd = "'(";		
		for(var i=0; i < dataTableId.length; i++){
			socd += "O:"+dataTableId[i] + "=" + dataTableId[i];
			
			if(i === dataTableId.length - 1){
				socd += ")'";
			}else{
				socd += ", ";
			}	
		}		
		dataAdapter.setSOCD(socd);
	} **/
	dataAdapter.setSOCD("(O:tb_lawReceipt=tb_lawReceipt,O:tb_lawReceipt2=tb_lawReceipt2,O:tb_lawReceipt3=tb_lawReceipt3)");
	//params를 null이나 빈값으로 보내면 그전에 날렸던 params를 던지기 때문에 초기화 해줘야됨
	dataAdapter.resetParams();
	dataSet.select(["tb_lawReceipt", "tb_lawReceipt2", "tb_lawReceipt3"], function (result, errorMsg, userMsg, allMsg) {
		// 콜백함수 실행 (재사용을 위해 함수로 만듬)
		ixCallback(result, errorMsg, userMsg, allMsg, messageYn, alertConfig, target, loadingYn, opts, dataTableId, 'select', false, callback); 
    }, params);      
};

/*****************************************************************************
 * 공통 update 함수
 *****************************************************************************
 * @param	target				- load 이미지를 보여줄 grid [array] 그리드가 아닐경우 null을 보면 된다.
 * @param   dataTableId 		- 사용될 dataTable의 ID. 여러개일 경우 배열로 넘어온다 [string or array]
 * @param	option				- 사용자 지정 옵션
 * 									> button:버튼 오브젝트 (버튼 비활성화/활성화)
 * @param	socd				- 서버로 전달할 SOCD 문자열 [string]
 * @param	loadingYn			- 로딩이미지 출력 선택 [true:출력(default) / false:미출력)]
 * @param	messageYn			- 처리 결과 메시지 출력 선택 [true:출력(default) / false:미출력)]
 * @param   alertConfig         - ixCreateAlert 함수를 실행하기 위한 파라미터가 담긴 배열 messageYn이 false일 경우 쓰이지 않는다 - array
 * @param	rowStatusReSetYn	- 저장 후 rowStatus 초기화 유무 [boolean]
 * @param	callback			- 사용자 지정 함수 
 *****************************************************************************/
function ixSave(target, dataTableId, options, loadingYn, messageYn, alertConfig, rowStatusReSetYn, callback) { 	
	//각각 index.jsp 에 mainType이라는 변수가 있음
	if(parent.mainType == 'admin' || parent.mainType == 'user'){
		//세션 체크 프레임을 리로드 시킨다. admin, user의 sessionChk.jsp를 보면 비로그인 상태일 경우 튕겨내는 로직이 있다
		parent.document.getElementById("sessionChkFrame").src = "/" + parent.mainType + "/sessionChk";		
	}
	
	// opts에 button
    var opts = options || {};
	if (opts.button) {
        opts.button.xgButton({
            disabled: true
        });
    }
	
	// targetType에 따른 옵션 실행
	var loadingYn;
	if(loadingYn === true){
		ixLoadElement(target, 'show');
	}

	// socd 설정
	if(typeof dataTableId === 'string'){
		dataAdapter.setSOCD("'(I:"+dataTableId + "="+ dataTableId+")'");
	}else{
		var socd = "'(";	
		for(var i=0; i < dataTableId.length; i++){
			socd += "I:"+dataTableId[i] + "=" + dataTableId[i];
			
			if(i === dataTableId.length - 1){
				socd += ")'";
			}else{
				socd += ", ";
			}	
		}		
		dataAdapter.setSOCD(socd);
	}
	
    dataSet.update(dataTableId, function (result, errorMsg, userMsg, allMsg) {
    	// 콜백함수 실행 (재사용을 위해 함수로 만듬)
    	ixCallback(result, errorMsg, userMsg, allMsg, messageYn, alertConfig, target, loadingYn, opts, dataTableId, 'save', rowStatusReSetYn, callback);  	
    });
};
    
/*****************************************************************************
 * 바인딩 될 target 분석
 *****************************************************************************
 * @param	target	- 바인딩 할 xg UI 컴포넌트의 juQery Element [object]
*****************************************************************************/
function ixTargetAnalysis(target){
	var targetType;
	if(target.attr('id').indexOf('Grid') !== -1){
		targetType = 'grid';	
	}
	return targetType;
};

/*****************************************************************************
 * loading 이미지 보이기
 *****************************************************************************
 * @param	target	- 바인딩 할 xg UI 컴포넌트의 juQery Element 또는 Element Array [object, array]
 * @param   type    - show 또는 hide 컨트롤
*****************************************************************************/
function ixLoadElement(target, type){
	
	if(target.constructor != Array){
		if(ixTargetAnalysis(eval(target)) === 'grid'){
			eval(target).xgGrid(type + 'loadelement');	
		}
	}else{
		for(var i = 0; i < target.length; i++){
			if(ixTargetAnalysis(eval(target[i])) === 'grid'){
				eval(target[i]).xgGrid(type + 'loadelement');	
			}	
		}			
	}
};

/*****************************************************************************
 * 바인딩 될 targetType의 DataTale id 얻어오기
 *****************************************************************************
 * @param	target		- 바인딩 할 xg UI 컴포넌트의 juQery Element [object]
 * @param	targetType	- 바인딩 할 xg UI 컴포넌트의 targetType [string]
*****************************************************************************/
function ixGetDataTableId(target, targetType){
    var dataTableID;
    if(targetType === 'grid'){ 
    	var dataTable = target.xgGrid('getDataTable');
    	dataTableId = dataTable.id;   	 
    }
    return dataTableID;
};

/*****************************************************************************
 * select 또는 update 후 콜백함수 재정의
 *****************************************************************************
 * @param	result		- 성공여부 [boolean]
 * @param	errorMsg	- 에러메세지 [object]
 * @param	userMsg	    - 사용자메세지 [object]
 * @param	allMsg	    - 전체 메세지 [object]
 * @param	messageYn	- 처리 결과 메시지 출력 선택 [true:출력(default) / false:미출력)]
 * @param   alertConfig         - ixCreateAlert 함수를 실행하기 위한 파라미터가 담긴 배열 messageYn이 false일 경우 쓰이지 않는다 - array
 * @param	target		- 바인딩 할 xg UI 컴포넌트의 juQery Element [object]
 * @param	loadingYn	- 로딩이미지 출력 선택 [true:출력(default) / false:미출력)]
 * @param	opts 		- 사용자 지정 옵션
 * @param   dataTableId	- 데이터테이블 ID
 * @param	type 		- 조회/저장 플래그
 * @param	rowStatusReSetYn	- 저장 후 rowStatus 초기화 유무 [boolean]
 * @param	callback	- 사용자 지정 함수
*****************************************************************************/
function ixCallback(result, errorMsg, userMsg, allMsg, messageYn, alertConfig, target, loadingYn, opts, dataTableId, type, rowStatusReSetYn, callback){
	// 개발자가 직접 콜백함수를 재정의할 경우 result, errorMsg, messages를 인자로 넘겨준 후 실행 	
	var errorMessage, parameter, b = false;
	
	for(var i in allMsg){	
		if(allMsg[i].code == "500"){
			errorMessage = allMsg[i].text;
			errorMsg = 'true';
			b = true;
		}
		else if(allMsg[i].code == "001" && allMsg[i].type == "parameter"){
			parameter = allMsg[i].text;
		}
    }
	
	//에러가 발생했을때 에러를 항상 출력한다.
	if(b){
		if(typeof alertConfig == 'array'){
			ixCreateError(alertConfig[0] , errorMessage);
		}else{
			ixCreateError('.sift_alert' , errorMessage);
		}
		
		callback = "";
	}
	
	if ((typeof callback) === 'function') {
        callback(result, errorMsg, userMsg, parameter);
    }	
    
	// targetType에 따른 옵션 실행
    if(loadingYn === true){
    	ixLoadElement(target, 'hide');
    }
	
	// messageYn 값에 따른 메세지 보여주기 - 에러가 없을 경우에만
	if(messageYn === true && !b && alertConfig != undefined && alertConfig != '' && alertConfig != null){
		if(userMsg['0000'] != undefined && userMsg['0000'] != ''){
			alertConfig[1] = userMsg['0000'];
		}	
		ixCreateAlert(alertConfig[0], alertConfig[1], alertConfig[2], alertConfig[3]);
/*		for(var i in userMsg){	
			alert(userMsg[i].split("-")[0]);
        }
		*/
	}
	
	// 저장일 경우 모든 로우의 status 값을 초기화 시킨다.
	if(rowStatusReSetYn === true){
		if(result === true && b === false){
			if(type === 'save'){
				var rowCnt = eval(dataTableId+".getRowCount()");
				for(var i=0; i < rowCnt; i++){
					dataTableId.setRowStatus( i, XgCommon.constant.STATUS.NORMAL );
				}
			}		
		}
	}

	// 비활성화 처리한 button을 다시 활성화 처리 
    if (opts.button) {
        opts.button.xgButton({
            disabled: false
        });
    }  	
};

/*****************************************************************************
 * Input 옵션 설정
 *****************************************************************************
 * @param	dataTable	- 바인딩 할 데이터 테이블 [object]
 * @param	columnName	- 바인딩할 column 이름 [string]
 * @param	option	    - 옵션에 추가할 내용들 [object]
*****************************************************************************/
function ixInputCreateOptions (dataTable, columnName, options) {
    var defaultOptions = {
            xgInitOption: {
                xgDataSet: dataSet,
                xgDataTable: dataTable
            },
    	    xgBindOption: {
    	    	mode: 'write',
    	    	immediately: false,
    	    	bind: {
					type: 'column',
					column: columnName
    	    	}
    	    }
        };
        if ((typeof options) === 'object') {
            for (name in options) {
                defaultOptions[name] = options[name];
            }
        }
        return defaultOptions;
};

/*****************************************************************************
 * Input 옵션 설정
 *****************************************************************************
 * @param	dataTableId	- 바인딩 할 데이터 테이블의 id [string]
*****************************************************************************/
function ixSetRowPos(dataTableId){
	dataSet.$eventEmitter.trigger('xg-bind-event', {
		uuid: undefined,
		eventName: 'setRowPos',
		bind:{ type : 'column'}  ,
		rowIdx: 0,
		value: 0,
		xgDataTableId: dataTableId
	});
};

/*****************************************************************************
 * 테이블에 row 추가
 *****************************************************************************
 * @param	dataTable	- row를 추가할 dataTable [object]
 * @param	status	    - 작업의 종류 [int]
*****************************************************************************/
ixdatTableAddRow = function(dataTable, status){	
	var len = dataTable.getColumnCount();
	var colName;
	var values = [];
	var selector;
	for (var i = 0; i < len; i++) {
		colName = dataTable.getColumnName(i);
		selector = $('#' + colName);

		if(selector.attr('type') == 'radio'){
			values.push($("[name=" + colName + "][aria-checked=true]").attr('val'));
			
		}else if(selector.attr('type') == 'checkbox'){
			var chkLen = $('[name=' + colName + '][aria-checked=true]').length;
			var chkVal = "";
			if(chkLen != 0){
				$('[name=' + colName + '][aria-checked=true]').each(function(){
					chkLen--;
					chkVal += $(this).attr('val');
					
					if(chkLen != 0){
						chkVal += ",";
					}
				});
			}
			values.push(chkVal);
			
		}else if(selector.attr('type') == 'text' || selector.attr('type') == 'password' ||
			     selector.attr('type') == 'combobox' || selector.attr('type') =='number' ||
		    	 selector.attr('type') == 'hidden'){
			values.push($("#" + colName).val().toString());
		}else if(selector.attr('type') == undefined){
			values.push("");
		}		

	}

	dataTable.insertRow(0, values);
	dataTable.setRowStatus(0 ,status);
	return true;
};

/*****************************************************************************
 * serialize 함수
 *****************************************************************************
 * @param	elementType	- div, span 등의 종류 [string]
 * @param	selectKind	- name, id, class중 선택 [string]
 * @param	name    	- selectKind에 따른 실제 값 [string]
 * @param   checkboxNames - checkbox엘리먼트의 name [array]
*****************************************************************************/
function ixGetSerialize(elementType, selectKind, name, checkboxNames){
	var selector = elementType + "[" + selectKind + "=" + name +"]";
	var sub_selector = "select , input[type=password], input[type=text], input[type=checkbox]:checked " +
			           ", input[type=radio]:checked, [type=combobox], [data-role=input] [type=textarea] " +
			           ", [role=radio][aria-checked=true], [type=number]";
	/*
	 *  [data-role=input] [type=textarea] : XgUIDateTimeInput을 선택하기 위한 selector
	 *  [type=combobox]                   : XgUIComboBox를 선택하기 위한 selector
	 *  [role=radio][aria-checked=true]   : XgUIRadido 중 체크된 값만 선택하기 위한 selector
	 */
	var len = $(selector).find(sub_selector).size();
	
	var string_query = "";
	
	$(selector).find(sub_selector).each(function(i){
		if($(this).val() != ""){
			string_query += $(this).attr("name") + "=" + $(this).val();
		
			if( i != len - 1){
				string_query += "&";
			}
		}
	}) 
	
	//checkbox name이 여러개일 경우 배열로 name들을 넘겨준다
	if(checkboxNames != undefined && checkboxNames != '' && checkboxNames != null){
		string_query += "&";
		var checkLeng = checkboxNames.length;	
		var checkName;
		
		for(var i = 0; i < checkLeng; i++){
			checkName = checkboxNames[i];
		    var checkboxLeng = $(selector + ' [name=' + checkName +  '][role=checkbox][aria-checked=true]').length;
		    var lengChk = 1;
		    var firstTrue = true;
		    
		    if(checkboxLeng != 0){
		    	 $(selector + ' [name=' + checkName +  '][role=checkbox][aria-checked=true]').each(function(){    	    		 	
		    		 	if($(this).val() == true){
		    		 		//이전에 만들어 놓은 stringquery가 있기 때문에 첫 시작시 &을 붙여서 시작한다
		    		 		if(firstTrue == true){
		    		 			string_query += checkName + "=";
		    		 			firstTrue = false;
		    		 		}
				    		string_query += $(this).attr('val');
				    					
			    		 	if(lengChk != checkboxLeng){
			    		 		string_query += "-";
			    		 	}    
			    			lengChk++;
				    	}    		 
			    	}); 	
		    }	    
		    firstTrue = true;    
		    if((i+1) != checkLeng){
		    	string_query += "&";
		    }
		}
		
	}else{
		//checkbox의 name이 한 종류일 경우(checkboxNames param을 안보냈을 경우) 쓴다
	    var checkboxLeng = $(selector + ' [role=checkbox][aria-checked=true]').length;
	    var lengChk = 1;
	    var firstTrue = true;
	    if(checkboxLeng != 0){
	    	 $(selector + ' [role=checkbox]').each(function(){    	    		 	
	    		 	if($(this).val() == true){
	    		 		//이전에 만들어 놓은 stringquery가 있기 때문에 첫 시작시 &을 붙여서 시작한다
	    		 		if(firstTrue == true){
	    		 			string_query += "&" + $(this).attr('name') + "=";
	    		 			firstTrue = false;
	    		 		}
			    		string_query += $(this).attr('val');
			    		
			    		//마지막 XGUICheckbox가 아니면 &을 추가한다
		    		 	if(lengChk != checkboxLeng){
		    		 		string_query += "-";
		    		 	}    
		    			lengChk++;
			    	}    		 
		    	}); 	
	    }
	}
	
	if(string_query == ""){
		string_query = null;
	}
	
	return string_query;
};


/*****************************************************************************
 * serialize 함수 - id로 element에 접근한다
 *****************************************************************************
 * param은 받지 않고 arguments로 queryString을 만든다
 * elements의 name = elements의 value 형식으로 뽑아낸다.
*****************************************************************************/
function ixGetSerializeById(){
	    if(arguments != 'undifined'){
		    var string_query ='';
		    var len = arguments.length;
		    for(var i = 0; i < len; i++){
		    	     string_query += $("#" + arguments[i]).attr("name") + "=" + $("#" + arguments[i]).val();
			
				if( i != len - 1){
					string_query += "&";
				}		
			}
		     return string_query;
	    }
};

/*****************************************************************************
 * row를 그려주는 renderer
 * 순번 renderer  1부터 시작해서 하나씩 증가한다
 * 직접 호출해서 사용하진 않고 grid를 그릴 때 {cellsrenderer: rowNumRender} 식으로 쓴다
 *****************************************************************************
*****************************************************************************/
var rowNumRender = function(row, datafield, value) {		
	return '<div style="text-align:center;margin-top:10px;">' + (row + 1) + '</div>';	
};


/*****************************************************************************
 * 데이터 테이블 값을 엘리멘트에 세팅
 *****************************************************************************
 * @param	selectors - 바인드할 객체들 selector 예: '.sift_popup [name=bind]' - string 
 *                      바인드 할 엘리먼트를 같이 넣어주면 더 빨리 작업이 진행된다
 * @param	datatable - 바인드 할 dataTable - object
 * @param	row	      - 바인드 할 dataTable의 row - int
*****************************************************************************/
function ixCreateBind(selector, datatable, row){
	var bind = $(selector);
	var type;
	var style;
		bind.each(function(){
			type = $(this).attr('type');
		
			if(type == 'span' || type =='textarea'){
				style = 'html';
			}else if(type == 'radio'){
				style = 'radio';
			}else if(type == 'text' || type == 'combobox' || type == 'hidden'){
				style = 'val';
			}else if(type == 'checkbox'){
				style = 'checkbox'
			}
			
			switch (style) {
			case 'html':
				if(datatable.getValue(row, $(this).attr('id'))){
					$(this).html(datatable.getValue(row, $(this).attr('id')));
				}		
				break;
			case 'radio':	
				var nameValue = $(this).attr('name');
				var colValue = datatable.getValue(row, $(this).attr('name'));
				if(colValue != null && colValue != ''  ){
					$('[name=' + nameValue + '][val=' + colValue +']').jqxRadioButton('check');
				}				
				break;
			case 'val':
				if(datatable.getValue(row, $(this).attr('id'))){
					$(this).val(datatable.getValue(row, $(this).attr('id')));
				}		
				break;
			case 'checkbox':
				if(datatable.getValue(row, $(this).attr('id')) == 'T' || datatable.getValue(row, $(this).attr('id')) == '1' ){
					if($(this).attr('aria-checked') != 'true'){
						$(this).xgCheckBox('check');
					}
				}else{
					if($(this).attr('aria-checked') != 'false'){
						$(this).xgCheckBox('uncheck');
					}
				}		
			break;
		}
		});
		
}

/*****************************************************************************
 * 엘리멘트 값을 데이터테이블에 세팅
 *****************************************************************************
 * @param	selectors - 바인드할 객체의 id 배열 - array
 * @param	datatable - 바인드 할 dataTable - object
 * @param	row	      - 바인드 할 dataTable의 row - int
 * @param   status    - status를 3(update)로 변경할지 정하는 파라미터
*****************************************************************************/
function ixCreateReverseBindbyId(selectors, datatable, row, status){
	var len = selectors.length;
	var selector;
	var type;
	
	for(var i = 0; i<len; i++){
		selector = $("#" + selectors[i]);
		type = selector.attr('type');
		if(type != undefined){
		
			if(type == 'textarea' || type == 'text' || type == 'combobox' || type == 'hidden' ||
			   type == 'number'){
				type = 'val'
			}
			
			switch (type) {
			case 'val':
				datatable.setValue(row, selector.attr('id') , selector.val());
				break;
			case 'span':
				datatable.setValue(row, selector.attr('id') , selector.html());
				break;		
			case 'radio':	
				var name = selector.attr('name');
				var radioVal = $('[name=' + name +'][aria-checked=true]').attr('val');
				//혹시 라디오 박스가 선택된게 없을 경우 setValue를 하지 않는다
				if(radioVal != undefined){
					datatable.setValue(row, name , radioVal );
				}
				break;
			case 'checkbox':
				if(selector.attr('aria-checked') == "true"){
					datatable.setValue(row, selector.attr('id') , selector.attr('val'));
				}else{
					datatable.setValue(row, selector.attr('id') , "");
				}
				break;
			}	
		}
	}
	if(status != false && status != undefined){
		datatable.setRowStatus(row, 3);
	}
}


/*****************************************************************************
 * 엘리멘트 값을 데이터테이블에 세팅- 리스트
 * <tr id="0"><select name="a"></tr><tr id="1"><select name="a"></tr> 이런 리스트들을 역바인드 한다
 * 이름은 같기 때문에 tr 같은 한 row를 묶는 엘리먼트를 통해서 접근한다 $('#0 [name=a]')
 *****************************************************************************
 * @param	selectors - 바인드할 객체의 id 배열 - array
 * @param	datatable - 바인드 할 dataTable - object
 * @param	row	      - 바인드 할 dataTable의 row - int
 * @param   status    - status를 3(update)로 변경할지 정하는 파라미터
*****************************************************************************/
function ixCreateReverseBind(selectors, datatable, row, status){
	var len = selectors.length;
	var selector;
	var type;
	
	for(var i = 0; i<len; i++){
		selector = $(selectors[i]);
		type = selector.attr('type');
		if(type != undefined){
		
			if(type == 'textarea' || type == 'text' || type == 'combobox' || type == 'hidden' ||
			   type == 'number'){
				type = 'val'
			}
			
			switch (type) {
			case 'val':
				datatable.setValue(row, selector.attr('id') , selector.val());
				break;
			case 'span':
				datatable.setValue(row, selector.attr('id') , selector.html());
				break;	
			case 'radio':	
				var name = selector.attr('name');
				var radioVal = $(selectors[i] + '[aria-checked=true]').attr('val');		
				//혹시 라디오 박스가 선택된게 없을 경우 setValue를 하지 않는다
				if(radioVal != undefined){
					datatable.setValue(row, name , radioVal );
				}
				break;
			case 'checkbox':
				if(selector.attr('aria-checked') == "true"){
					datatable.setValue(row, selector.attr('id') , selector.attr('val'));
				}else{
					datatable.setValue(row, selector.attr('id') , "");
				}
				break;
			}	
		}
	}
	if(status != false && status != undefined){
		datatable.setRowStatus(row, 3);
	}
}

/*****************************************************************************
 * 동적 Bind
 *****************************************************************************
 * @param	datatable - 바인드 할 dataTable - object
 * @param	row		    - 활성화된 rowIndex
 * @param	div	        - 바인드될 div 정보
 * @param	obj	        - 바인드될 컬럼 정보
                        - 배열형식으로 넘겨지며 화면상 동일한 ID를 가진 TAG가 존재해야 함
*****************************************************************************/
function ixBind(datatable, row, div, obj){
	var rowCnt = datatable.getRowCount();
	if(rowCnt > 0)
		datatable.setRowPos(1);
	else
		return;
	
	var colCnt = datatable.getColumnCount();
	var data = "{";
	var bind = "{";
	
	for(var i=0; i<colCnt; i++){
		for(var j=0; j<obj.length; j++){
			if(datatable.getColumnName(i) == obj[j]) {
				data += datatable.getColumnName(i) + " : '" + datatable.getValue(row, datatable.getColumnName(i)) + "',";
				bind += "\"#"+datatable.getColumnName(i)+"\":"+"\""+datatable.getColumnName(i)+"\",";
				continue;
			}
		}
	}
	
	data = data.slice(0,-1);
	data += "}";
	bind = bind.slice(0,-1);
	bind += "}";

	data = eval("("+data+")");
	bind = eval("("+bind+")");
	
	$(div).my("remove");
	$(div).my({ui:bind}, data);
}

/*****************************************************************************
 * 알람창 동적 변경
 *****************************************************************************
 * @param	selector    - 알람창이 만들어질 엘리먼트의 selector - string  예) ".popupSection'
 * @param	textValue   - 표시할 텍스트 번호 - message.js에 정의된 변수 -string
 * @param	imageNumber - 보여질 image 파일의 이름을 결정하는 번호. - string
 * 	                    - icon_alert2는 완료 표시 이미지
 * 					    - icon_alert3은 경고 표시 이미지
 * @param   fadeOutTime - fadeOut 되는 시간(초단위) - double
 * 
 * 기본적인 alert창은 header에 있지만 새창을 띄울 경우 새창에 alert 부분을 새로 만들어줘야함
*****************************************************************************/
function ixCreateAlert(selector, textValue, imageNumber, fadeOutTime){
	$('#sift_errorMessageDiv').css('display', 'none');
	
	var text = "";
	if( (textValue.substring(0,1) == 'C' && textValue.length == 4) ||
		(textValue.substring(0,1) == 'L' && textValue.length == 8) ||
		(textValue.substring(0,1) == 'A' && textValue.length == 9) ||
		(textValue.substring(0,1) == 'E' && textValue.length == 9) ||
		(textValue.substring(0,1) == 'U' && textValue.length == 9) ) {
		text = eval("siftMsg." + textValue);
	}else{
		text = textValue;
	}
	
    $(selector + " .alert_text").text(text);
	$(selector + " .alert_img").attr('src', '/images/custom/admin/icons/icon_alert' + imageNumber + '.png');

	$(selector).css("display","block");
	$(selector).fadeOut(fadeOutTime * 1000, function(){});
}


/*****************************************************************************
 * 에러메세지 창 동적 변경
 *****************************************************************************
 * @param	selector    - 에러메시지가 만들어질 엘리먼트의 selector - string  예) ".popupSection'
 * @param	textValue   - 표시할 텍스트 번호 - message.js에 정의된 변수 -string
 * 
*****************************************************************************/
function ixCreateError(selector, errorMsg){
	$('#sift_errorMessageDiv').css('display', 'block');
	
	var text = "";
	if( (errorMsg.substring(0,1) == 'C' && errorMsg.length == 4) ||
		(errorMsg.substring(0,1) == 'L' && errorMsg.length == 8) ||
		(errorMsg.substring(0,1) == 'A' && errorMsg.length == 9) ||
		(errorMsg.substring(0,1) == 'E' && errorMsg.length == 9) ||
		(errorMsg.substring(0,1) == 'U' && errorMsg.length == 9) ) {
		text = eval("siftMsg." + errorMsg);
	}else{
		text = errorMsg;
	}
	
    $(selector + " .alert_text").html(text);
	$(selector + " .alert_img").attr('src', '/images/custom/admin/icons/icon_alert3.png');
	$(selector + " textarea").text(text);
	$(selector).css("display","block");
	
	$(selector + ' .sift_error_button').click(function(){
		$(selector).css("display","none");
		$(selector + ' #sift_errorMessageDiv').css('display', 'none');
	});
}

/*****************************************************************************
 * 에러메세지 창 동적 변경 - 상세보기는 보여주지 않음
 *****************************************************************************
 * @param	selector    - 에러메시지가 만들어질 엘리먼트의 selector - string  예) ".popupSection'
 * @param	textValue   - 표시할 텍스트 번호 - message.js에 정의된 변수 -string
 * @param	callback    - 닫기 버튼 클릭시 실행될 함수
 * 
*****************************************************************************/
function ixCreateError2(selector, errorMsg, callback){
	$(selector).css("display","block");
	$('.sift_error_top').css('display','none');
	
	var text = "";
	if( (errorMsg.substring(0,1) == 'C' && errorMsg.length == 4) ||
		(errorMsg.substring(0,1) == 'L' && errorMsg.length == 8) ||
		(errorMsg.substring(0,1) == 'A' && errorMsg.length == 9) ||
		(errorMsg.substring(0,1) == 'E' && errorMsg.length == 9) ||
		(errorMsg.substring(0,1) == 'U' && errorMsg.length == 9) ||
		(errorMsg.substring(0,1) == 'M' && errorMsg.length == 7) ) {
		text = eval("siftMsg." + errorMsg);
	}else{
		text = errorMsg;
	}
	
    $(selector + " .alert_text").html(text);
	$(selector + " .alert_img").attr('src', '/images/custom/admin/icons/icon_alert3.png');
	$(selector + " textarea").text(text);
	$('#sift_errorMessageDiv').css('display', 'block');
	
	$(selector + ' .sift_error_button').click(function(){
		$(selector).css("display","none");
		$(selector + ' #sift_errorMessageDiv').css('display', 'none');
	});
}

/*****************************************************************************
 * addrow 시 그리드의 포커스와 스크롤을 마지막으로 이동시킨다.
 *****************************************************************************
 * @param	grid		-	addrow 할 그리드 [object]
 * @param	dataTable	-	그리드에 바인딩 된 dataTable [object]
*****************************************************************************/
function ixGridAddrow(grid, dataTable){
	
	grid.xgGrid( 'addrow', null, {} );
	
	var row = dataTable.getRowCount()-1;
	grid.xgGrid( 'ensurerowvisible', row );
	grid.xgGrid( 'selectrow', row );
}

/*****************************************************************************
 * dataTable의 body의 값들을 XGComboBox에서 쓰일 array로 만든다.
 * 형식은 [ {label: "서울", value: "seoul"},{label: "강원", value: "gangwun"}} 이다
 * array 안에 json
 *****************************************************************************
 * @param	dataTable	- 값들을 뽑아낼 dataTable [object]
 * @param label     - label로 쓰일 column이름 [string]
 * @param value     - value로 쓰일 column이름 [string] 
*****************************************************************************/
function fcSetComboboxSource(dataTable, label, value, comboBox){
	var len = dataTable.getRowCount();
	
	for(var i = 0; i < len; i ++){
		comboBox.xgComboBox( 'addItem', { label: dataTable.getValue(i , label), 
								    value: dataTable.getValue(i , value) } );
	}
	
}

/*****************************************************************************
 * dataTable의 body의 값들을 XGComboBox에서 쓰일 array로 만들어 리턴한다.
 *****************************************************************************
 * @param	dataTable	- 값들을 뽑아낼 dataTable [object]
 * @param label     - label로 쓰일 column이름 [string]
 * @param value     - value로 쓰일 column이름 [string] 
 * @param array     - 콤보박스 값들을 입력할 array
*****************************************************************************/
function fcgetComboboxSource(dataTable, label, value, array){
	var len = dataTable.getRowCount();
	
	for(var i = 0; i < len; i ++){
		array.push({ label: dataTable.getValue(i , label), value: dataTable.getValue(i , value) } );
	}
	
	return array;
}

/*****************************************************************************
 * 동적 Bind
 *****************************************************************************
 * @param	datatable - 바인드 할 dataTable - object
 * @param	row		    - 활성화된 rowIndex
 * @param	div	        - 바인드될 div 정보
 * @param	obj	        - 바인드될 컬럼 정보
                        - 배열형식으로 넘겨지며 화면상 동일한 ID를 가진 TAG가 존재해야 함
*****************************************************************************/
function ixBind(datatable, row, div, obj){
	var rowCnt = datatable.getRowCount();
	if(rowCnt > 0)
		datatable.setRowPos(1);
	else
		return;
	
	var colCnt = datatable.getColumnCount();
	var data = "{";
	var bind = "{";
	
	for(var i=0; i<colCnt; i++){
		for(var j=0; j<obj.length; j++){
			if(datatable.getColumnName(i) == obj[j]) {
				data += datatable.getColumnName(i) + " : '" + datatable.getValue(row, datatable.getColumnName(i)) + "',";
				bind += "\"#"+datatable.getColumnName(i)+"\":"+"\""+datatable.getColumnName(i)+"\",";
				continue;
			}
		}
	}
	
	data = data.slice(0,-1);
	data += "}";
	bind = bind.slice(0,-1);
	bind += "}";

	data = eval("("+data+")");
	bind = eval("("+bind+")");
	
	$(div).my("remove");
	$(div).my({ui:bind}, data);
}

/*****************************************************************************
 * on 이벤트를 추가하여 값이 변경할 때 해당하는 datatable의 값과 
 * grid의 값을 동적으로 변환시킨다
 *****************************************************************************
 * @param	dataTable	- 값을 변환시킬 dataTable dataTable [object]
 * @param   selector    - dataTable, grid와 바인드 시킬 엘리먼트들 [object]
 * @param   grid        - 값을 변화시킬 그리드[object]
*****************************************************************************/
function ixCreateChangeEvent(dataTable, selector, grid){
	selector.each(function(){
		var type = $(this).attr('type');
		
		if(type == 'text'){
			$(this).on('keyup',function(){
				var row = dataTable.getRowPos();
				dataTable.setValue(row, $(this).attr('id'), $(this).val());
				//해당 컬럼이 존재하는지 확인
				if(grid.xgGrid('getcolumnindex', $(this).attr('id')) != -1){
					grid.xgGrid('setcellvalue', row ,$(this).attr('id'), $(this).val());
				}
			});
		}else if(type == 'checkbox'){
			$(this).on('change',function(){
				var row = dataTable.getRowPos();
				if($(this).attr('aria-checked') == 'false'){
					dataTable.setValue(row, $(this).attr('id'), $(this).attr('val'));
				}else{
					var TF;
					if($(this).attr('val') == "T"){
						TF = 'F';
					}else{
						TF = '0';
					}
					dataTable.setValue(row, $(this).attr('id'), TF);
				}
			});
		}else if(type == 'combobox'){
			$(this).on('change',function(){
				var row = dataTable.getRowPos();
				dataTable.setValue(row, $(this).attr('id'), $(this).val());
				//해당 컬럼이 존재하는지 확인
				if(grid.xgGrid('getcolumnindex', $(this).attr('id')) != -1){
					grid.xgGrid('setcellvalue', row ,$(this).attr('id'), $(this).val());
				}
			});
		}
	});
	
}

/*****************************************************************************
 * dataTable에 업데이트 된 값이 하나라도 있는지 체크한다
 *****************************************************************************
 * @param	dataTable	- status를 확인할 dataTable [object]
*****************************************************************************/
function ixCheckRowStatus(dataTable){
	var cnt = dataTable.getRowCount();	
	for(var i = 0; i < cnt; i++){
		if(dataTable.getRowStatus(i) != 1){
			return true;
		}
	}
	return false;
}

/*****************************************************************************
 * dataTable의 현재 로우값을 배열로 저장한다.
 *****************************************************************************
 * @param	dataTable	- status를 확인할 dataTable [object]
 * @param	row			- 현재 위치한 로우 [int]
*****************************************************************************/
function ixSaveData(datatable, row){
	var rowCnt = datatable.getRowCount();
	var colCnt = datatable.getColumnCount();
	
	var data = "{";
	for(var i=0; i<colCnt; i++){
		data += datatable.getColumnName(i) + " : '" + datatable.getValue(row, datatable.getColumnName(i)) + "',";
	}
	
	data = data.slice(0,-1);
	data += "}";
	
	data = eval("("+data+")");
	
	return data;
}

/*****************************************************************************
 * 배열로 생성된 두 값을 비교한다.
 *****************************************************************************
 * @param	oldValue			- 비교 전 데이터 [string]
 * @param	newValue			- 비교 후 데이터 [string]
*****************************************************************************/
function ixCompare( oldValue, newValue ){
	var data = "";
	var type = typeof oldValue, i;
	
	for( i in oldValue ){
		if( oldValue.hasOwnProperty(i) ){
			if( oldValue[i] != newValue[i] ){
				var attr = $('#'+i+'').attr("name");
				if (typeof attr !== typeof undefined && attr !== false) {
					data += attr + ",";
				}
			}
		}
	}
	
	data = data.slice(0,-1);
	
	return data;
}


/*****************************************************************************
 * admin header 메뉴 active 클래스 추가 및 제거
 * 상위 메뉴들에게는 menuLv=1의 attributte들이 있다. 
 * $("[menuLv=1]").removeClass('active') 이렇게 하면 특정한 상위 메뉴 id를 알기 힘들 때 쓰기 좋다
 *****************************************************************************
 * @param	remove			- active를 삭제할 선택자 string 
 * @param	add			    - active를 추가할 선택자 string
 * @param  lowMenu          - active를 추가하거나 삭제할 하위 선택자 arrya(0번이 삭제할 선택자(여러개일 경우 ,로 구분), 1번이 추가할 선택자(string))
 * 
 * 각 header 상위 메뉴는 menuLv=1, 중 메뉴는 menuLv=2라는 Attribute가 있다.
 * remove할 선택자에 해당 Attrubute를 넣어주면 상위메뉴, 중메뉴를 한번에 class 제거를 할 수 있다
*****************************************************************************/
function ixChageMenuClass( remove, add,  lowMenu){
	$(remove).removeClass("active");
	$(add).addClass("active");
	
	if(lowMenu != undefined){
		var removeList = lowMenu[0].split(",");
		for(var i = 0, len = removeList.length; i < len; i++){ 
			$(removeList[i]).removeClass("active");
		}
		$(lowMenu[1]).addClass("active");
	}
}

/*****************************************************************************
 * 날짜 검색하는 항목에서 시작 날짜 선택 후 끝 날짜 선택 시 
 * 시작 날짜보다 이전으로 할 경우 경고창을 보여주는 이벤트를 끝 날짜 엘리먼트에
 * onChange및 focusout으로 걸어준다
 * 2016-05-04 이런 식의 형태의 값들만 체크가 된다
 *****************************************************************************
 * @param	start			- 시작날짜 선택자 String
 * @param	end			    - 끝 날자 선택자 String
*****************************************************************************/
function ixCreateSearchDateChk(start, end){
	var endDt = $(end);
	var startDt = $(start);
	
	function chkStartDate(){
		var	startDate = Number(startDt.val().replace(new RegExp("-", "gi"), ""));	
		var endDate = Number(endDt.val().replace(new RegExp("-", "gi"), ""));
		if(endDate == 0){
			return;
		}

		if(endDate != 0 && startDate > endDate){			
			//날짜를 바꾸기전  change 이벤트로 무한루프를 돌지 않도록 언바인드 한다
			startDt.unbind('change');
			endDt.unbind('change');
			
			var temp = startDt.val();
			startDt.val(endDt.val());				
			endDt.val(temp);
			
			endDt.on('change' , function(){
				chkEndDate();
			});
			startDt.on('change' , function(){
				chkStartDate();
			});
		}
	}
	
	startDt.on('focusout', function(){
		chkStartDate();
	}).on('change' , function(){
		chkStartDate();
	});
	

	function chkEndDate(){
		var	startDate = Number(startDt.val().replace(new RegExp("-", "gi"), ""));	
		var endDate = Number(endDt.val().replace(new RegExp("-", "gi"), ""));
		
		if(startDate == 0 && endDate == 0 ||
		   startDate == "" && endDate == ""){
			return;
		}
		
		if(startDate == 0 || startDate == "" || startDate > endDate){			
			if(endDate == 0){
				return;
			}
			//날짜를 바꾸기전  change 이벤트로 무한루프를 돌지 않도록 언바인드 한다
			startDt.unbind('change');
			endDt.unbind('change');
			
			var temp = startDt.val();
			startDt.val(endDt.val());
			endDt.val(temp);
			
			endDt.on('change' , function(){
				chkEndDate();
			});
			startDt.on('change' , function(){
				chkStartDate();
			});
		}
	}
	
	endDt.on('focusout', function(){
		chkEndDate();
	}).on('change' , function(){
		chkEndDate();
	});
}



/*****************************************************************************
 * 오늘 날짜 구하는 함수
 * 2016-05-06 이런 형태로 return한다
*****************************************************************************/
function ixGetToday(){
	var d = new Date();		
	var year, month, day, createDt;
	
	year = d.getFullYear();
	month = (d.getMonth() + 1);
	day = d.getDate();
	
	if (month < 10 ){
		month = '0' + month;
	};		
	if(day < 10){
		day = '0' + day;
	}		
	createDt = year + '-' + month + '-' + day; 
	
	return createDt;
};

/*****************************************************************************
 * keyup 이벤트에서 원하지 않는 값이 입력되었을 경우 삭제하는 함수
 *****************************************************************************
 * @param	e			- keyup 이벤트 함수의 parameter
 * @param	regexp	    - 체크할 문자열   /[0-9-]/ 이런식으로 넘어오면 0-9 숫자와 -이외에 값에는 false를 반환한다
 * @param   thisEle		- keyup 이벤트가 발생한 element
*****************************************************************************/
function ixCheckInputValue(e, regexp, thisEle){
	//<- , ->, 마우스 클릭 시는 이벤트 발생 시키지 않음
	if(e.keyCode === 38 || e.keyCode == 37 || e.keyCode == 39){
	    return false;
	}
	
	var value = thisEle.val();
	
	//replace할 경우 value의 length가 계속 변하기 때문에 replace로 삭제된 길이를 저장해 놔야함
	var changed = 0;
	
	for(var i = 0, len = value.length; i < len; i++){
		var nowIndex =  i - changed;
		var nowValue = value.substring(nowIndex, nowIndex+1);

		if(!regexp.test(nowValue)){
			value = value.replace(nowValue, '');
			changed++;
		}
	}
	
	if(changed != 0 ){
		thisEle.val(value);	
	}
};

