

// Prototype
(function(old) {
  $.fn.attr = function() {
    if(arguments.length === 0) {
      if(this.length === 0) {
        return null;
      }

      var obj = {};
	
      $.each(this[0].attributes, function() {
        if(this.specified) {
          obj[this.name] = this.value;
		  
        }
      });
      return obj;
    }

    return old.apply(this, arguments);
  };
})($.fn.attr);


function gfn_CreateDataSet(dsName){
    if (window.XgPlatform.XgDataSet[dsName]) {
        return window.XgPlatform.XgDataSet[dsName];
    } else {
        return new XgDataSet(dsName);
    }   
};


function gfn_CreateDataBindInfo(dataType, dtName, selectUrl, updateUrl, header) {
	// dataSet, dataAdapter을 window 객체로 선언
	window.dataSet = gfn_CreateDataSet('ds');

	
	// dataTable은  여러개 일 수 있으므로 별도 처리
	window.dataTable = gfn_CreateDataTable(dataSet, dtName, selectUrl, updateUrl, header);	
};


function gfn_CreateDataTable(dataSet, dtName, selectUrl, updateUrl, header){
    var dataTable = dataSet.getDataTable(dtName);

    if(dataTable && dataTable.id){
        return dataTable;
    }  	
	if(typeof dtName === 'string'){
		gfn_SetDataHeader(dtName, header, selectUrl, updateUrl)
	}else{
		// 2개 이상의 배열로 받을 경우
		if(typeof selectUrl === 'string' && typeof updateUrl === 'string'){
			for(var i = 0; i < dtName.length; i++){
				if(i === 0){
					gfn_SetDataHeader(dtName[i], header[i], selectUrl, updateUrl);
				}else{
					gfn_SetDataHeader(dtName[i], header[i]);
				}
			}
		}else{
			for(var i = 0; i < dtName.length; i++){
				gfn_SetDataHeader(dtName[i], header[i], selectUrl[i], updateUrl[i]);
			}
		}
	}	
};


function gfn_SetDataHeader(dtName, header, selectUrl, updateUrl){
	
    var dataTable = window.dataSet.getDataTable(dtName);
    if (dataTable && dataTable.id) {
        return dataTable;
    } else {
    	eval("window." + dtName + " = new XgDataTable('" +dtName + "');" );

		// 데이터테이블을 식별하기 위한 role추가.
		$(eval(dtName)).attr("role","datatable");

    	if(selectUrl){
    		eval(dtName + ".setUrl('select', '" + selectUrl +"');");
    	}
    	if(updateUrl){
    		eval(dtName + ".setUrl('update', '" + updateUrl +"');");
    	}

    	dataSet.addDataTable(eval(dtName));	
    	gfn_SetHeader(eval(dtName), header);
    	gfn_SetGridFormat(dtName, header);
    }   
};


function gfn_CreateGridOption(dataTable, options) {
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

function gfn_SetGridFormat(dataTable, header) {
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
			            //alert("name > " + name + "    " + header[i].gridColumnOptions[name]);
			        }
			    }			
				gridColumns.push(defaultOptions);		
			}
		}
	}
	
	eval("window."+dataTable+"GridColumns = gridColumns");
};


function gfn_SetHeader(dataTable, header) {	
	for (var i=0; i < header.length; i++){
		if(header[i].colName != null){			
			dataTable.addColumnForGauce(header[i].colName, header[i].colType, header[i].colSize);
		}
	}		
};



/*****************************************************************************************************************
 * 데이터 조회용 함수 
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/03
 *****************************************************************************************************************
 * @param	appParam		BXM호출용 어플리케이션 파라미터 : application/service/operation 또는 URL
 *****************************************************************************************************************
 * @param	dataTables		데이터 테이블 객체 또는 데이터테이블 객체의 배열
 * @param	reqParam		파라미터값이 들어있는 영역 객체의 ID [div객체의 id값]
 * @param	callback		콜백함수지정[콜백이 없는 경우에는 null로 설정]
 *****************************************************************************************************************/
function gfn_Select(appParam, dataTables, reqParam, callback) {
	//dataAdapter.setAsyncStatus(false);
	var da = new XgDataAdapter(XgCommon.createUUID());

	dataSet.setDataAdapter(da);
	var url = "";
	var parameter = "";

	if( typeof appParam == "string")  {
		// URL인 경우
		url = appParam;
		
		// 파라미터 설정
		if (typeof reqParam == "string") {
			parameter = reqParam;
		} else {
			parameter = getParameter(reqParam);
		}
		
	} else {
		// BXM FW연동인 경우 
		if (appParam.length != 3) {
			alert("[Syntex Error]\n어플리케이션 호출 파라미터를 정확하게 입력해 주세요\nex)['TrustSampleProject', 'SGR1001S', 'SSGR1001S001']");
			return;
		}
		// 기본 URL
		url = "/ixyncService";

		// 파라미터 설정
		if (typeof reqParam == "string") {
			parameter = reqParam;
		} else {
			parameter = getParameter(reqParam);
		}
		parameter = "scrnId=SCRNID&trOccCd=IX&application=" + appParam[0] +"&service=" + appParam[1] + "&operation=" + appParam[2] + "&X-UUID=" + XgCommon.createUUID() + "&" + parameter;
		
	}
	
	// 데이터테이블 파라미터 설정
	var dataTablesParam = [];

	// SP-SOCD 정보
	var socdInfo = "(";

	// 인자로 넘어오는 데이터 테이블로 부터 SP-SOCD와 데이터셋의 select메소드 호출하기 위한 dataTablesParam을 생성한다.
	if (typeof dataTables == "undefined") {
		alert("[Syntex Error]\ndataTables값에는 데이터 테이블 객체 또는 데이터 테이블 객체의 배열만 사용이 가능합니다.\nex) sub01 또는 [sub01, sub02]");
		return;
	} else if (dataTables.constructor == Array) {	// 데이터 테이블이 여러개인 경우 처리.
		for (var i in dataTables) {
			if (typeof dataTables[i] == "object")	{
				if (i==0) {
					dataTables[i].setUrl("select", url);
				}
				socdInfo += "O:" + dataTables[i].getId() + "=" + dataTables[i].getId() + ",";
				dataTablesParam[i] = dataTables[i].getId();
			} else {
				alert("[Syntex Error]\ndataTables값에는 데이터 테이블 객체 또는 데이터 테이블 객체의 배열만 사용이 가능합니다.\nex) sub01 또는 [sub01, sub02]");
				return;
			}		
		}

		socdInfo = socdInfo.substring(0, socdInfo.lastIndexOf(","));
		socdInfo += ")";
	
		// SP-SOCD를 생성하여 셋팅.
		da.setSOCD(socdInfo);	
	} else if (typeof dataTables == "object")	{	// 데이터 테이블이 1개인 경우 처리.
		da.setSOCD("(O:" + dataTables.getId() +"=" + dataTables.getId() +")");
		dataTables.clearData();	// 기존 데이터를 삭제후 조회
		dataTablesParam = dataTables.getId();
		dataTables.setUrl("select", url);
	} else if (typeof dataTables == "string") {
		dataTablesParam = dataTables.substring(dataTables.indexOf("=")+1);
		
		da.setSOCD("(O:" + dataTables +")");
		dataTables = eval(dataTablesParam);
		dataTables.setUrl("select", url);		
	} else {										// 입력 오류 처리.
		alert("[Syntex Error]\ndataTables값에는 데이터 테이블 객체 또는 데이터 테이블 객체의 배열만 사용이 가능합니다.\nex) sub01 또는 [sub01, sub02]");
		return;
	}

	// 데이터 어댑터의 파라미터를 초기화
	da.resetParams();
	
	// 데이터 어댑터에 파라미터를 전송
	da.setParams(parameter);	
	
	// 콜백 메시지 처리용 객체.
	var ocm = new objectCallbackMessage();
	
	if (typeof callback == "function")	{
		ocm.setCallbackFunction(callback);
	} 
	console.log("gfn_Select 전송 파라미터 : " + parameter);
	// 데이터셋의 SELECT메소드 호출.
	dataSet.select(dataTablesParam, ocm.message, "");
	da = null;
}


/*****************************************************************************************************************
 * 데이터 저장용 함수 
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/03
 *****************************************************************************************************************
 * @param	appParam		BXM호출용 어플리케이션 파라미터 : application/service/operation 또는 URL
 *****************************************************************************************************************
 * @param	dataTables		데이터 테이블 객체 또는 데이터테이블 객체의 배열
 * @param	reqParam		파라미터값이 들어있는 영역 객체의 ID [div객체의 id값]
 * @param	callback		콜백함수지정[콜백이 없는 경우에는 null로 설정]
 *****************************************************************************************************************/
function gfn_Save(appParam, dataTables, reqParam, callback) {

	var da = new XgDataAdapter(XgCommon.createUUID());

	dataSet.setDataAdapter(da);
	var url = "";
	var parameter = "";

	if( typeof appParam == "string")  {
		// URL인 경우
		url = appParam;
		
		// 파라미터 설정
		parameter = getParameter(reqParam);
	} else {
		// BXM FW연동인 경우 
		if (appParam.length != 3) {
			alert("[Syntex Error]\n어플리케이션 호출 파라미터를 정확하게 입력해 주세요\nex)['TrustSampleProject', 'SGR1001S', 'SSGR1001S001']");
			return;
		}

		// 파라미터 설정
		parameter = "scrnId=SCRNID&trOccCd=IX&application=" + appParam[0] +"&service=" + appParam[1] + "&operation=" + appParam[2] + "&X-UUID=" + XgCommon.createUUID() + "&" + getParameter(reqParam);

		// 기본 URL
		url = "/ixyncService";
	}


	var socdInfo = "";

	// 데이터테이블 파라미터 설정
	var dataTablesParam = [];

	// 인자로 넘어오는 데이터 테이블로 부터 SP-SOCD와 데이터셋의 select메소드 호출하기 위한 dataTablesParam을 생성한다.
	if (typeof dataTables == "undefined") {
		alert("[Syntex Error]\ndataTables값에는 데이터 테이블 객체 또는 데이터 테이블 객체의 배열만 사용이 가능합니다.\nex) sub01 또는 [sub01, sub02]");
		return;
	} else if (dataTables.constructor == Array) {	// 데이터 테이블이 여러개인 경우 처리.
		for (var i in dataTables) {

			if (typeof dataTables[i] == "object")	{
				if (i==0) {
					dataTables[i].setUrl("update", url);
				}
				
				socdInfo += "I:" + dataTables[i].getId() + "=" + dataTables[i].getId() + ",";

				dataTablesParam[i] = dataTables[i].getId();
			} else {
				alert("[Syntex Error]\ndataTables값에는 데이터 테이블 객체 또는 데이터 테이블 객체의 배열만 사용이 가능합니다.\nex) sub01 또는 [sub01, sub02]");
				return;
			}		
		}

		socdInfo = socdInfo.substring(0, socdInfo.lastIndexOf(","));
		socdInfo += ")";
		
		// SP-SOCD를 생성하여 셋팅.
		da.setSOCD(socdInfo);	
	} else if (typeof dataTables == "object")	{	// 데이터 테이블이 1개인 경우 처리.
		da.setSOCD("(I:" + dataTables.getId() +"=" + dataTables.getId() +")");
		dataTablesParam = dataTables.getId();
		dataTables.setUrl("update", url);
	} else {										// 입력 오류 처리.
		alert("[Syntex Error]\ndataTables값에는 데이터 테이블 객체 또는 데이터 테이블 객체의 배열만 사용이 가능합니다.\nex) sub01 또는 [sub01, sub02]");
		return;
	}
	
	// 데이터 어댑터의 파라미터를 초기화
	da.resetParams();

	// 데이터 어댑터에 파라미터를 전송
	da.setParams(parameter);
	
	// 콜백 메시지 처리용 객체.
	var ocm = new objectCallbackMessage();
	
	ocm.setCallbackFunction(callback);

	dataSet.update(dataTablesParam, ocm.message);

	// 저장후 해당 데이터터 테이블 초기화
	gfn_ResetStatus(dataTables);
	da = null;
}



/*****************************************************************************************************************
 * 데이터테이블 초기화 함수 
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/03
 *****************************************************************************************************************
 * @param	dataTables	데이터테이블 객체 또는 객체 배열
 *****************************************************************************************************************/
function gfn_ResetStatus(dataTables) {
	if (dataTables.constructor == Array) {
		for (var i in dataTables) {
			if (typeof dataTables[i] == "object")	{
				
				var rowCnt = dataTables[i].getRowCount();
				for(var k=0; k < rowCnt; k++){
					dataTables[i].setRowStatus( k, XgCommon.constant.STATUS.NORMAL );
				}
				
			} else {
				alert("[Syntex Error]\ndataTables값에는 데이터 테이블 객체 또는 데이터 테이블 객체의 배열만 사용이 가능합니다.\nex) sub01 또는 [sub01, sub02]");
				return;
			}		
		}
	} else {
		var rowCnt = dataTables.getRowCount();
		for(var i=0; i < rowCnt; i++){
			dataTables.setRowStatus( i, XgCommon.constant.STATUS.NORMAL );
		}
	}
}



/*****************************************************************************************************************
 * 메시지 콜백을 위한 객체.
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/03
 *****************************************************************************************************************/
function objectCallbackMessage() {
	var p_result = "";
	var GET_CALL_BACK_FUNCTION;

	this.message = function (result, errorMsg, userMsg, allMsg) {
		
		/** 향후 이 부분은 공통 메시지 처리 영역 **/

		//alert(allMsg[0].text);
		//alert(allMsg[0].code);
		//alert(allMsg[0].type);
		if (typeof GET_CALL_BACK_FUNCTION() == "function") {
			// 콜백 함수 실행
			GET_CALL_BACK_FUNCTION()(result, errorMsg, userMsg, allMsg);
		}
		
	}

	this.setCallbackFunction=function(val) {
		p_result = val;
	}
	
	this.getCallbackFunction=function() {
		return p_result;
	}

	GET_CALL_BACK_FUNCTION = this.getCallbackFunction;
}




/*****************************************************************************************************************
 * 객체의 값을 반환하는 메소드 [RadioButton/TextBox/CheckBox]
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/03
 *****************************************************************************************************************
 * @id		객체의 id 또는 object
 *****************************************************************************************************************
 * @return	객체의 값[value]
 *****************************************************************************************************************/
function get(id) {

	// 객체 유형에 따라 id값을 꺼낸다.
	if (typeof id == "object")	{
		var size = $(id).size();

		if (size > 1) {
			id =  $(id).eq(0).attr("id");
		} else if (size == 1) {
			id =  $(id).attr("id");
		} 
	}
	var obj = $("[id=" + id + "]");

	if (obj.size() == 0) return null;				// 값이 없는 경우 null 반환

	if (obj.size() == 1) {							// 객체가 1개만 있는 경우 처리

		switch (obj.attr("role")) {
			case "radio":
				if (obj.attr("aria-checked") == "true") {
					return obj.attr("value");		// 선택된 radio버튼에 대한 값 반환.
				}
				return null;						// 체크된 값이 없는 경우 null 반환
				break;
			case "textbox":							// 텍스트 박스에 대한 값 반환 [텍스트 박스는 단건]
				return obj.val();
				break;
			case "checkbox":
				if (obj.attr("aria-checked") == "true") {
					return obj.attr("value");		// 선택된 checkbox에 대한 값 반환.
				} else {
					return null;					// 체크되지 않은 경우 null을 반환.
				}
				break;
			case "combobox":
				
				if(obj.xgComboBox("checkboxes")) {			// checkbox를 사용하는 경우
					var items  = obj.xgComboBox('getCheckedItems');
					var returnVal = "";
					for (var i=0;i < items.length ; i++) {
						returnVal += items[i].value + ",";
					}
					return returnVal.substring(0, returnVal.lastIndexOf(","));
				} else if(obj.xgComboBox("multiSelect")) {	// multiSelect기능을 사용하는 경우
					var items  = obj.xgComboBox('selectedItems');
					var returnVal = "";
					for (var i=0;i < items.length ; i++) {
						returnVal += items[i] + ",";
					}
					return returnVal.substring(0, returnVal.lastIndexOf(","));
				} else {									// 일반적인 콤보 박스인 경우.
					return obj.val();
				}

					
				break;
		}
	} else {										// 객체가 배열인 경우 처리 [radio]
		switch (obj.eq(0).attr("role")) {
			
			case "radio":
				for (var i=0;i<obj.size();i++)	{
					if (obj.eq(i).attr("aria-checked") == "true") {
						return obj.eq(i).attr("value");		// 선택된 radio버튼에 대한 값 반환.
					} 
				}
				
				return null;						// 체크된 값이 없는 경우 null 반환
				
				break;
		}
	}
}


/*****************************************************************************************************************
 * 객체의 값을 설정하는 메소드 [RadioButton/TextBox/CheckBox]
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/04
 *****************************************************************************************************************
 * @id		객체의 id 또는 object
 * @value	설정할 값
 *****************************************************************************************************************/
function set(id, value) {

	// 객체 유형에 따라 id값을 꺼낸다.
	if (typeof id == "object")	{
		var size = $(id).size();

		if (size > 1) {
			id =  $(id).eq(0).attr("id");
		} else if (size == 1) {
			id =  $(id).attr("id");
		} 
	}

	var obj = $("[id=" + id + "]");

	if (obj.size() == 0) return null;									// 값이 없는 경우 null 반환

	if (obj.size() == 1) {												// 객체가 1개만 있는 경우 처리

		switch (obj.attr("role")) {
			case "radio":
				if (obj.attr("value") == value)	{			
					$(obj).xgRadioButton({checked:true});				// value랑 값이 일치하면 라디오박스를 선택상태로 변환.
				}
				break;
			case "textbox":												// 텍스트 박스에 대한 값 반환 [텍스트 박스는 단건]
				return obj.val(value);
				break;
			case "checkbox":
				
				if (typeof value == "boolean"){							// value값이 boolean값이면 해당 값을 해당 체크박스에 설정
					$(obj).xgCheckBox({checked:value});
				} else {
					if (obj.attr("value") == value) {
						$(obj).xgCheckBox({checked:true});				// value값이 문자/숫자인 경우, value랑 일치하면 체크박스를 체크로 설정
					} 
				}
				break;
		}

	} else {															// 객체가 배열인 경우 처리 [radio]
		switch (obj.eq(0).attr("role")) {
			case "radio":
				for (var i=0;i<obj.size();i++)	{
					if (obj.eq(i).attr("value") == value) {
						$(obj).eq(i).xgRadioButton({checked:true});		// 선택된 radio버튼에 대한 값 체크
						break;
					} 
				}
				break;
		}
	}
}



/*****************************************************************************************************************
 * 객체의 선택된 index를 반환하는 메소드 [RadioButton]
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/03
 *****************************************************************************************************************
 * @id		객체의 id 또는 object
 *****************************************************************************************************************
 * @return	객체의 선택된 값의 index
 *****************************************************************************************************************/
function getIndex(id) {
	// 객체 유형에 따라 id값을 꺼낸다.
	if (typeof id == "object")	{
		var size = $(id).size();

		if (size > 1) {
			id =  $(id).eq(0).attr("id");
		} else if (size == 1) {
			id =  $(id).attr("id");
		} 
	}

	var obj = $("[id=" + id + "]");

	if (obj.size() == 0) return null;								// 값이 없는 경우 null 반환

	if (obj.size() == 1) {											// 객체가 1개만 있는 경우 처리
		switch (obj.attr("role")) {
			case "radio":											// 라디오 버튼인 경우
				if (obj.attr("aria-checked") == "true") {			// 선택된 라디오 버튼의 인덱스 반환
					return 0;
				}
				return -1;											// 체크된 값이 없는 경우 -1 반환
				break;

		}
	} else {														// 객체가 배열인 경우 처리 [radio]
		switch (obj.eq(0).attr("role")) {
			case "radio":				// 라디오 버튼인 경우
				for (var i=0;i<obj.size();i++)	{
					if (obj.eq(i).attr("aria-checked") == "true") {	// 선택된 라디오 버튼의 인덱스 반환
						return i;
					}
				}
				return -1;											// 체크된 값이 없는 경우 -1 반환
				break;
		}
	}
}

/*****************************************************************************************************************
 * 객체의 값을 index로 설정하는 메소드 [RadioButton]
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/03
 *****************************************************************************************************************
 * @id		객체의 id 또는 object
 * @index	index 번호 [0..*]
 *****************************************************************************************************************/
function setIndex(id, index) {
	// 객체 유형에 따라 id값을 꺼낸다.
	if (typeof id == "object")	{
		var size = $(id).size();

		if (size > 1) {
			id =  $(id).eq(0).attr("id");
		} else if (size == 1) {
			id =  $(id).attr("id");
		} 
	}

	var obj = $("[id=" + id + "]");
	
	if (obj.size() == 0) return null;

	if (obj.size() == 1) {				// 객체가 1개만 있는 경우 처리
		
		switch (obj.attr("role")) {

			case "radio":
				if (obj.size() <= index) {
					alert("RadioButton Index Error\n" + obj.attr("id") + "의 index는 " + index + "보다 작습니다.");
					return;
				}
				$(obj).xgRadioButton({checked:true});
				break;
		}
	} else {							// 객체가 배열인 경우 처리 [radio]
	
		switch (obj.eq(0).attr("role")) {
			case "radio":				// 라디오 버튼인 경우
				if (obj.size() <= index) {
					alert("RadioButton Index Error\n" + obj.eq(0).attr("id") + "의 index는 " + index + "보다 작습니다.");
					return;
				}

				for (var i=0;i<obj.size();i++)	{
					if (index == i) {
						$(obj.eq(i)).xgRadioButton({checked:true});
					}
					
				}
				return -1;			// 체크된 값이 없는 경우 -1 반환
				break;
		}
	}
}




/*****************************************************************************************************************
 * 특정 영역(div)안에 있는 객체를 파싱하여 파라미터 전송용 문자열을 만들어내는 함수
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/04
 *****************************************************************************************************************
 * @id		객체의 id 또는 object
 * separate	각 파라미터의 구분자를 지정한다 (default값은 "&")
 *****************************************************************************************************************
 * @return	XENA HTML5에서 사용될 파라미터 문자열
 *****************************************************************************************************************/
function getParameter(id, separate){

	// 파라미터가 없는 경우 nullString을 반환.
	if (id == null) return "";

	// 객체 유형에 따라 id값을 꺼낸다.
	if (typeof id == "object")	{
		var size = $(id).size();

		if (size >= 1) {
			id =  $(id).eq(0).attr("id");
		} else if (size == 1) {
			id =  $(id).attr("id");
		} 
	}

	if (typeof separate == "undefined") {
		separate = "&";						// 파라미터 구분자가 없는 경우 기본값 ("&")
	}
	var selector = "div[id=" + id +"]";
	var sub_selector = "[component='xg-input'],"  + 
						"[component='xg-checkbox']," + 
						"[component='xg-radiobutton']," +
						"[component='xg-combobox']," +
						"[component='xg-listbox']," + 
						"[component='xg-maskedinput']," + 
						"[component='xg-passwordinput']," + 
						"[component='xg-datetime']";
	/**var sub_selector = "select , input[type=password], input[type=text], input[type=checkbox]:checked " +
			           ", input[type=radio]:checked, [data-role=input] [type=textarea] " +
			           ", [role=radio][aria-checked=true], [type=number],[role=checkbox][aria-checked=true], ";**/
	/*
	 *  [data-role=input] [type=textarea] : XgUIDateTimeInput을 선택하기 위한 selector
	 *  [type=combobox]                   : XgUIComboBox를 선택하기 위한 selector
	 *  [role=radio][aria-checked=true]   : XgUIRadido 중 체크된 값만 선택하기 위한 selector
	 */
	var len = $(selector).find(sub_selector).size();
	
	var queryString = "";
	
	$(selector).find(sub_selector).each(function(i){

		switch($(this).attr("component")) {

		case "xg-radiobutton":
			queryString += $(this).attr("id") + "=" + encodeURIComponent(get($(this)));
			queryString += separate;
			break;
		case "xg-combobox":
			queryString += $(this).attr("id") + "=" + encodeURIComponent(get($(this)));
			queryString += separate;
			break;			
		case "xg-input":
		case "xg-checkbox":			
		case "xg-listbox":
		case "xg-maskedinput":
		case "xg-passwordinput":
		case "xg-datetime":
			queryString += $(this).attr("id") + "=" + $(this).val();
			queryString += separate;
			break;
		}
		/**
			switch($(this).attr("role")) {
				
				case "textbox":	
		
					switch($(this).attr("component")) {
						case "xg-datetime":
							alert($(this).val());
						break;
					}
					break;
					
				case "combobox":
					if($(this).val() != ""){
						queryString += $(this).attr("id") + "=" + encodeURIComponent(get($(this)));
						if( i != len - 1){
							queryString += separate;
						}
					}
					break;
					
				default :
					if($(this).val() != ""){
						
						queryString += $(this).attr("id") + "=" + get($(this));
						if( i != len - 1){
							queryString += separate;
						}
					}
					break;
			}**/

	}) 
	
	return queryString;
};





/*****************************************************************************************************************
 * XENA HTML5 화면 초기화 함수
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/06
 *****************************************************************************************************************
 * @desc	화면 로딩시 자동으로 실행되며, 실행 완료후에 gfn_onload() 함수가 실행된다.
 *****************************************************************************************************************/
$(document).ready(function () {
	
	/** 일반 객체에 대한 공통 처리 부분 [Start] **/
	// 버튼에 대한 초기화
	$('.btn_search,[component="xg-button"]').each(
		function(index, item) {
			var _item = $(item);

			var _params = {};

			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									default:
										// 객체별로 맵핑하는 부분이 다름
										_name = _xgButtonMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);
	
			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgButtonMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});
			_item.xgButton(_params);
		}
	);

	// 입력 박스에 대한 초기화
	$('.input,[component="xg-input"]').each(
		function(index, item) {
			var _item = $(item);

			var _params = {};

			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									default:
										// 객체별로 맵핑하는 부분이 다름
										_name = _xgInputMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);

			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgInputMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});
			_item.xgInput(_params);
		}
	);


	// 체크 박스에 대한 초기화
	$('.checkbox,[component="xg-checkbox"]').each(
		function(index, item) {
			var _item = $(item);
			
			var _params = {};

			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									default:
										// 객체별로 맵핑하는 부분이 다름
										_name = _xgCheckBoxMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);

			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgCheckBoxMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});
			_item.xgCheckBox(_params);
		}
	);

	// 라디오 버튼에 대한 초기화
	$(".radio,[component='xg-radiobutton']").each(
		function(index, item) {
			var _item = $(item);
			
			var _params = {};

			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									default:
										// 객체별로 맵핑하는 부분이 다름
										_name = _xgRadioButtonMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);

			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgRadioButtonMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});
			_item.xgRadioButton(_params);
			_item.xgRadioButton({groupName:_item.attr("id")});
		}
	);

	// 콤보 박스에 대한 초기화
	$(".comboBox,[component='xg-combobox']").each(
		function(index, item) {
			var _item = $(item);

			var codeList = "";
			var valueMember = "";
			var displayMember = "";
			var dataid = "";
			var _params = {};
			
			/** 자식 노드에 대한 파싱 **/
			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									case "dataid":
										dataid = this.value;
										break;
									case "valuemember":
										valueMember = this.value;
										break;
									case "displaymember":
										displayMember = this.value;
										break;
									default:
										_name = _xgComboBoxMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);
			
			/** 자기 자신에 대한 파싱 **/
			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgComboBoxMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});

			_item.xgComboBox(_params);
			
			if (dataid != "") {
				var headerInfo = '[{"colName":"' + valueMember +'", "colType":"string",	"colSize" :"10"},' + 
								 '{"colName":"' + displayMember + '", "colType":"string",	"colSize" :"100"}]';
				// 헤더 정보
				var headerInfo  = JSON.parse(headerInfo);
				
				// 접수 리스트 데이터 바인드 정보 세팅 [현재 서비스 호출 부분은 하드 코딩으로 처리]
				gfn_CreateDataBindInfo('XML', dataid, '' , '', headerInfo);

				var params = "cdGrpCd=" + _item.attr("cdGrpCd");
				
				gfn_Select(["SM-onl", "SSMA0101", "SSMA010101"], "cdList="+ dataid, params, 
					function() {
						// 데이터 조회후 콜백에서 값 설정
						var _options = {	source:dataSet.makeListTypeSource(dataid, displayMember, valueMember),
											valueMember:"value",
											displayMember:"text"  };

						_item.xgComboBox(_options);
				
					});
				
			} else {
				_item.xgComboBox({
					source: codeList,
					valueMember:valueMember,
					displayMember:displayMember
				});
			}
		}
	);


	// 리스트 박스에 대한 초기화
	$(".listbox,[component='xg-listbox']").each(
		function(index, item) {
			var _item = $(item);

			var codeList = "";
			var valueMember = "";
			var displayMember = "";
			var dataid = "";
			var _params = {};

			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {
						
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									case "dataid":
										dataid = this.value;
										break;
									case "valuemember":
										valueMember = this.value;
										break;
									case "displaymember":
										displayMember = this.value;
										break;
									default:
										_name = _xgListBoxMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);
			
			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgListBoxMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});

			_item.xgListBox(_params);
			
			if (dataid != "") {
				var headerInfo = '[{"colName":"' + valueMember +'", "colType":"string",	"colSize" :"10"},' + 
								 '{"colName":"' + displayMember + '", "colType":"string",	"colSize" :"100"}]';
				// 헤더 정보
				var headerInfo  = JSON.parse(headerInfo);
				
				// 접수 리스트 데이터 바인드 정보 세팅 [현재 서비스 호출 부분은 하드 코딩으로 처리]
				gfn_CreateDataBindInfo('XML', dataid, '' , '', headerInfo);

				var params = "cdGrpCd=" + _item.attr("cdGrpCd");
				
				gfn_Select(["SM-onl", "SSMA0101", "SSMA010101"], "cdList="+ dataid, params, 
					function() {
						var _options = {	source:dataSet.makeListTypeSource(dataid, displayMember, valueMember),
											valueMember:"value",
											displayMember:"text"  };

						_item.xgListBox(_options);
				
					});
				
			} else {
				_item.xgListBox({
					source: codeList,
					valueMember:valueMember,
					displayMember:displayMember
				});
			}
		}
	);


	// XgDataTimeInput에 대한 초기화
	$(" [component='xg-datetime']").each(
		function(index, item) {
			var _item = $(item);
			
			var _params = {};

			_item.children().each(
				function(index){
					
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									default:
										// 객체별로 맵핑하는 부분이 다름
										_name = _xgDateTimeInputMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);

			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgDateTimeInputMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});
			_item.xgDateTimeInput(_params);
		}
	);

	// XgMaskedInput 대한 초기화
	$(".maskedInput,[component='xg-maskedinput']").each(
		function(index, item) {
			var _item = $(item);
			
			var _params = {};

			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									default:
										// 객체별로 맵핑하는 부분이 다름
										_name = _xgMaskedInputMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);

			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgMaskedInputMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});
			_item.xgMaskedInput(_params);
		}
	);


	// NumberInput 에 대한 초기화
	$(".numberInput,[component='xg-numberinput']").each(
		function(index, item) {
			var _item = $(item);
			
			var _params = {};

			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									default:
										// 객체별로 맵핑하는 부분이 다름
										_name = _xgNumberInputMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);

			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgNumberInputMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});
			_item.xgNumberInput(_params);
		}
	);


	// PasswordInput 에 대한 초기화
	$(".passwdInput,[component='xg-passwordinput']").each(
		function(index, item) {
			var _item = $(item);
			
			var _params = {};

			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									default:
										// 객체별로 맵핑하는 부분이 다름
										_name = _xgNumberInputMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);

			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgPasswordInputMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});
			_item.xgPasswordInput(_params);
		}
	);

	/** 일반 객체에 대한 공통 처리 부분 [End] **/

	// 데이터셋의 헤더 정보/그리드 셋팅
    var headerInfo = "";

    var dataid = "";
	var format = "";

	$(".grid,[component='xg-grid']").each(function(index, item){	
			var _item = $(item);
			
			var _params = {};

			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "FORMAT") {
						format = this.innerHTML;
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									case "dataid":
										dataid = this.value;
										break;
									default:
										// 객체별로 맵핑하는 부분이 다름
										_name = _xgGridMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
												
											}
										}
										break;
								}
							}
						});
					}
				}
			);

			// 헤더 정보
			headerInfo = eval("[" + format + "]");
			// 접수 리스트 데이터 바인드 정보 세팅 [현재 서비스 호출 부분은 하드 코딩으로 처리]
			gfn_CreateDataBindInfo('XML', dataid, '' , '', headerInfo);

			var option = "";
			switch(_item.attr("type")) {
				case "edit":
					 option = {
							xgBindOption: {
								cellEdit: true,
								cellValidation: true,
								showRowStatus: false
							},
							width: '100%',
							height: '460px',
							columnsheight: 30, rowsheight: 30,
							editable: true,
							sortable: true,
							enabletooltips:true,
							
							editmode: 'selectedcell',
							sorttogglestates:0,
							keyboardnavigation: true,
							columnsresize: true,
							columnsreorder: true,
							enablekeyboarddelete: true,
						  
						};
					break;
				default:
					 option = {
							xgBindOption: {
								cellEdit: true,
								cellValidation: true,
								showRowStatus: false
							},
							width: '100%',
							height: '460px',
							columnsheight: 30, rowsheight: 30,
							editable: false,
							sortable: true,
							sorttogglestates:0,
							keyboardnavigation: true,
							columnsresize: true,
							columnsreorder: true,
							enablekeyboarddelete: true,
						  
						};
					break;
			}


		// 그리드 dataSet, dtatTable, columns, options 설정 및 렌더링 [향후 타입에 따른 공통 처리]
		var xgGridOptions = gfn_CreateGridOption(eval(dataid),option);
		
		_item.xgGrid(xgGridOptions);

		var _defaultParams = {};
		// 기본속성에 있는 정보를 입력한다.
		$.each(_item[0].attributes, 
			function() {
					if(this.specified) {
						_name = _xgPasswordInputMapper[this.name];
						_val = this.value;
						if (typeof _name != "undefined") {
							if (_val == "true" || _val == "false") {
								_defaultParams[_name] = JSON.parse(_val);
							} else {
								_defaultParams[_name] = _val;
							}
						}
					}
			});

		_item.xgGrid(_defaultParams);


		_item.xgGrid(_params);
		
		}
	);

	// TAB 에 대한 초기화
	$(".tab,[component='xg-tab']").each(
		function(index, item) {
			var _item = $(item);
			
			var _params = {};

			_item.children().each(
				function(index){
					if (this.tagName.toUpperCase() == "DATA")	{
						codeList = eval("[" + this.innerHTML +"]");
					} else if (this.tagName.toUpperCase() == "PARAM") {	
						$.each(this.attributes, function() {
							if(this.specified) {
								switch(this.name.toLowerCase()) {
									default:
										// 객체별로 맵핑하는 부분이 다름
										_name = _xgTabMapper[this.name];
										_val = this.value;
										if (typeof _name != "undefined") {
											if (_val == "true" || _val == "false") {
												_params[_name] = JSON.parse(_val);
											} else {
												_params[_name] = _val;
											}
										}
										break;
								}
							}
						});
					}
				}
			);

			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgTabMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									_params[_name] = JSON.parse(_val);
								} else {
									_params[_name] = _val;
								}
							}
						}
				});
			_item.xgTab(_params);
		}
	);
	// 각 화면별로 실행될 onload함수
	try	{
		gfn_onload();
	} catch (err) {}
	
});

