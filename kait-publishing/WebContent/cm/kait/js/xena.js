/*****************************************************************************************************************
 * XENA HTML5 화면 공통 함수
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/05/02
 *****************************************************************************************************************
 * @desc	화면 로딩시 자동으로 실행되며, 실행 완료후에 gfn_onload() 함수가 실행된다.
 *****************************************************************************************************************/

/**
 * 상수값 정의 부분
 **/
var def_xg_radiobutton = "xg-radiobutton";	// 라디오 버튼 객체
var def_xg_combobox = "xg-combobox";		// 콤보 박스
var def_xg_maskedinput = "xg-maskedinput";	// 마스크 입력 박스
var def_xg_input = "xg-input";				// 입력 박스
var def_xg_checkbox = "xg-checkbox";		// 체크 박스
var def_xg_listbox = "xg-listbox";			// 리스트 박스
var def_xg_passwordinput = "xg-passwordinput";	// 패스워드  입력
var def_xg_datetime = "xg-datetime";		// 날짜/시간 입력
var def_xg_grid = "xg-grid";				// 그리드
var def_xg_datatable = "xg-datatable";		// 데이터 테이블
var def_xg_numberinput = "xg-numberinput";	// 숫자 입력

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


/**
 * 데이터셋 생성.
 **/
function gfn_CreateDataSet(dsName){
    if (window.XgPlatform.XgDataSet[dsName]) {
        return window.XgPlatform.XgDataSet[dsName];
    } else {
        return new XgDataSet(dsName);
    }   
};


/**
 * 자주 사용하는 그리드 옵션을 설정해 주는 함수.
 * @param _gridId	그리드 객체 ID
 * @param dataTable	데이터테이블
 * @param options	그리드 옵션
 * @returns
 */
function gfn_CreateGridOption(_gridId, dataTable, options, columns) {
	var dataTableId = dataTable.getId();
	//var columns = eval("window." + dataTableId + "GridColumns");
	
	//var columns = $(window).attr(_gridId + "GridColumns");
	
    var defaultOptions = {
        width: '100%',
        height: '100%',
        xgInitOption: {
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
    
    console.log("gfn_CreateGridOption : " + JSON.stringify(defaultOptions, null, 1));
    
    defaultOptions.xgInitOption["xgDataSet"] = dataSet;
    defaultOptions.xgInitOption["xgDataTable"] = dataTable;

    // 그리드 옵션 제거.
    //$(window).attr(_gridId + "GridColumns", null);
    return defaultOptions;
};



/**
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
};**/

/**
function gfn_SetHeader(dataTable, header) {	
	for (var i=0; i < header.length; i++){
		if(header[i].colName != null){			
			dataTable.addColumnForGauce(header[i].colName, header[i].colType, header[i].colSize);
		}
	}		
};
**/



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
 * @param	extParam		기능 확장을 위한 JSON형태의 옵션값
 *****************************************************************************************************************/
function gfn_Select(appParam, dataTables, reqParam, callback, extParam) {
	
	var da = new XgDataAdapter(XgCommon.createUUID());
	da.dataType = "XML";
	
	// 기능확장을 위한 JSON 형태의 옵션값 처리.
	if (typeof extParam != "undefined" && typeof extParam == "object") {
		for(key in extParam){
			switch(key) {
				case "syncload":	// syncload = true 동기방식, syncload = false 비동기방식
					if (extParam[key] == "true" || extParam[key] == true) {
						da.setAsyncStatus(false);
					} else {
						da.setAsyncStatus(true);
					}
			}
		}
	}
	
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
 * @param	extParam		기능 확장을 위한 JSON형태의 옵션값
 *****************************************************************************************************************/
function gfn_Save(appParam, dataTables, reqParam, callback) {

	var da = new XgDataAdapter(XgCommon.createUUID());
	da.dataType = "XML";

	// 기능확장을 위한 JSON 형태의 옵션값 처리.
	if (typeof extParam != "undefined" && typeof extParam == "object") {
		for(key in extParam){
			switch(key) {
				case "syncload":	// syncload = true 동기방식, syncload = false 비동기방식
					if (extParam[key] == "true" || extParam[key] == true) {
						da.setAsyncStatus(false);
					} else {
						da.setAsyncStatus(true);
					}
			}
		}
	}
	
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


	var socdInfo = "(";

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
		//da.setSOCD("(I:" + dataTables.getId() +"=" + dataTables.getId() +"," + "O:" + "cdList" +"=" + "cdList" +")");
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
		switch (obj.attr("component")) {
			case def_xg_maskedinput:
		    	var _str = [];
		    	var _mask = obj.xgMaskedInput("mask");
		    	var _template = "#90ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz[]"
		    	var _len = _mask.length;
		    	for (i=0; i<_len; i++) {
		    		if (_template.indexOf(_mask.charAt(i)) == -1) {
		    			_str.push(_mask.charAt(i));
		    		}
		    	}
		    	
		    	var exp = "/" + obj.xgMaskedInput("promptChar") + "/g";
		    	exp = eval(exp);
		    	_value = obj.val().replace(exp, "");
		   
		    	for (i=0; i<_str.length; i++) {
		    		_value = gfn_replace(_value, _str[i], "");
		    	}
		    	return _value;
		    	break;
			case def_xg_datetime:
				var _str = [];
				var _template = "ymdYMDhHmMsS";
				var _format = obj.xgDateTimeInput("formatString");
		    	var _len = _format.length;
		    	for (i=0; i<_len; i++) {
		    		if (_template.indexOf(_format.charAt(i)) == -1) {
		    			_str.push(_format.charAt(i));
		    		}
		    	}		
		    	var _value = obj.val();
		    	for (i=0; i<_str.length; i++) {
		    		_value = gfn_replace(_value, _str[i], "");
		    	}
		    	return _value;
				break;
			case def_xg_radiobutton:
				if (obj.attr("aria-checked") == "true") {
					return obj.attr("value");		// 선택된 radio버튼에 대한 값 반환.
				}
				return null;						// 체크된 값이 없는 경우 null 반환
				break;
			case def_xg_numberinput:				// 숫자 입력 박스
			case def_xg_input:							// 텍스트 박스에 대한 값 반환 [텍스트 박스는 단건]
				if (typeof obj == "object") {
					var _returnType = obj.attr("returnValue");
					switch(_returnType.toUpperCase()) {
						case "LABEL":
							return obj.val().label;
							break;
						case "VALUE":
							return obj.val().value;
							break;
						case "JSON":
							return '{"label":"' + obj.val().label + '", "value":"' + obj.val().value + '"}';
							break
					}
				} else {
					return obj.val();
				}
				
				break;
			/**
			case def_xg_input:
				if (obj.attr("aria-checked") == "true") {
					return obj.attr("value");		// 선택된 checkbox에 대한 값 반환.
				} else {
					return null;					// 체크되지 않은 경우 null을 반환.
				}
				break;**/
			case def_xg_combobox:
				
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
		switch (obj.eq(0).attr("component")) {
			
			case def_xg_radiobutton:
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

	var def_xg_radiobutton = "xg-radiobutton";	// 라디오 버튼 객체
	var def_xg_combobox = "xg-combobox";		// 콤보 박스
	var def_xg_maskedinput = "xg-maskedinput";	// 마스크 입력 박스
	var def_xg_input = "xg-input";				// 입력 박스
	var def_xg_checkbox = "xg-checkbox";		// 체크 박스
	var def_xg_listbox = "xg-listbox";			// 리스트 박스
	var def_xg_passwordinput = "xg-passwordinput";	// 패스워드  입력
	var def_xg_datetime = "xg-datetime";		// 날짜/시간 입력
	var def_xg_grid = "xg-grid";				// 그리드
	var def_xg_datatable = "xg-datatable";		// 데이터 테이블
	var def_xg_numberinput = "xg-numberinput";	// 숫자 입력
	
	if (obj.size() == 1) {												// 객체가 1개만 있는 경우 처리
		switch (obj.attr("component")) {
		
			case def_xg_listbox:
				if (obj.attr("value") == value)	{			
					$(obj).xgListBox({checked:true});				// value랑 값이 일치하면 라디오박스를 선택상태로 변환.
				}
				break;		
			case def_xg_radiobutton:
				if (obj.attr("value") == value)	{			
					$(obj).xgRadioButton({checked:true});				// value랑 값이 일치하면 라디오박스를 선택상태로 변환.
				}
				break;
			case def_xg_datetime:
			case def_xg_passwordinput:
			case def_xg_numberinput:
			case def_xg_maskedinput:
			case def_xg_input:											// 텍스트 박스에 대한 값 반환 [텍스트 박스는 단건]
				return obj.val(value);
				break;
			case def_xg_checkbox:
			case def_xg_combobox:
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
		switch (obj.eq(0).attr("component")) {
			case def_xg_radiobutton:
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
		switch (obj.attr("component")) {
			case def_xg_radiobutton:											// 라디오 버튼인 경우
				if (obj.attr("aria-checked") == "true") {			// 선택된 라디오 버튼의 인덱스 반환
					return 0;
				}
				return -1;											// 체크된 값이 없는 경우 -1 반환
				break;

		}
	} else {														// 객체가 배열인 경우 처리 [radio]
		switch (obj.eq(0).attr("role")) {
			case def_xg_radiobutton:				// 라디오 버튼인 경우
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

		switch (obj.attr("component")) {

			case def_xg_radiobutton:
				if (obj.size() <= index) {
					alert("RadioButton Index Error\n" + obj.attr("id") + "의 index는 " + index + "보다 작습니다.");
					return;
				}
				$(obj).xgRadioButton({checked:true});
				break;
		}
	} else {							// 객체가 배열인 경우 처리 [radio]
	
		switch (obj.eq(0).attr("component")) {
			case def_xg_radiobutton:				// 라디오 버튼인 경우
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

/**
 * 객체의                                                                                         를 제거한 id값을 반환
 * @param _param	객체(object)
 * @returns
 */
function fn_getId(_param) {
	var _id = _param.attr("id");
	var prefix = "";

	switch(_param.attr("component")) {
		case def_xg_radiobutton:	// rdo_
			prefix = "rdo_";
			if (_id.indexOf(prefix) == 0) {
				_id = _id.substring(_id.indexOf(prefix)+4);
			}
			return _id;
		case def_xg_combobox:
			prefix = "cbo_";
			if (_id.indexOf(prefix) == 0) {
				_id = _id.substring(_id.indexOf(prefix)+4);
			}
			return _id;		
		case def_xg_input:
			prefix = "inp_";
			if (_id.indexOf(prefix) == 0) {
				_id = _id.substring(_id.indexOf(prefix)+4);
			}
			return _id;			
		case def_xg_checkbox:	
			prefix = "chk_";
			if (_id.indexOf(prefix) == 0) {
				_id = _id.substring(_id.indexOf(prefix)+4);
			}
			return _id;		
		case def_xg_listbox:
			prefix = "lib_";
			if (_id.indexOf(prefix) == 0) {
				_id = _id.substring(_id.indexOf(prefix)+4);
			}
			return _id;		
		case def_xg_maskedinput:
			prefix = "mki_";
			if (_id.indexOf(prefix) == 0) {
				_id = _id.substring(_id.indexOf(prefix)+4);
			}
			return _id;		
		case def_xg_numberinput:
			prefix = "nbi_";
			if (_id.indexOf(prefix) == 0) {
				_id = _id.substring(_id.indexOf(prefix)+4);
			}
			return _id;					
		case def_xg_passwordinput:
			prefix = "psi_";
			if (_id.indexOf(prefix) == 0) {
				_id = _id.substring(_id.indexOf(prefix)+4);
			}
			return _id;		
		case def_xg_datetime:
			prefix = "dti_";
			if (_id.indexOf(prefix) == 0) {
				_id = _id.substring(_id.indexOf(prefix)+4);
			}
			return _id;
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
						"[component='xg-numberinput']," +
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
		var _id = fn_getId($(this));
		switch($(this).attr("component")) {
			case def_xg_radiobutton:	// rdo_
			case def_xg_datetime:
			case def_xg_combobox:	
			case def_xg_maskedinput:	
				queryString += _id + "=" + encodeURIComponent(get($(this)));
				queryString += separate;
				break;
			case def_xg_input:
				var _val = $(this).val();
				if(typeof _val == "object") {
					queryString += _id + "=" + encodeURIComponent(get($(this)));
				} else {
					queryString += _id + "=" + $(this).val();
				}
				queryString += separate;
				break;
			case def_xg_checkbox:	
			case def_xg_numberinput:
			case def_xg_listbox:		    	
			case def_xg_passwordinput:
				queryString += _id + "=" + $(this).val();
				queryString += separate;
				break;
		}

	}) 
	
	return queryString;
};


/******************************************************
 * 특정 문자열로 치환해 주는 함수 
 * @author	jaewon Choi
 * @date 	2017/05/02
 ******************************************************
 * @param	p_source 	문자열 소스
 * @param 	p_old		변경 대상 문자
 * @param 	p_new		변경할 문자
 ******************************************************/
function gfn_replace(p_source, p_old, p_new) {
	_expr = p_old;
	_expr = _expr.replace(/\\/g, "\\\\");
	_expr = _expr.replace(/\^/g, "\\^");
	_expr = _expr.replace(/\$/g, "\\$");
	_expr = _expr.replace(/\*/g, "\\*");
	_expr = _expr.replace(/\+/g, "\\+");
	_expr = _expr.replace(/\?/g, "\\?");
	_expr = _expr.replace(/\./g, "\\.");
	_expr = _expr.replace(/\(/g, "\\(");
	_expr = _expr.replace(/\)/g, "\\)");
	_expr = _expr.replace(/\|/g, "\\|");
	_expr = _expr.replace(/\,/g, "\\,");
	_expr = _expr.replace(/\{/g, "\\{");
	_expr = _expr.replace(/\}/g, "\\}");
	_expr = _expr.replace(/\[/g, "\\[");
	_expr = _expr.replace(/\]/g, "\\]");
	_expr = _expr.replace(/\-/g, "\\-");	

  	var re = new RegExp(_expr, "g");
	return p_source.replace(re, p_new);		
}


function fn_tooltip(_item) {
	var _tooltip = _item.attr("tooltip");
	if (typeof _tooltip != "undefined") {
		_item.jqxTooltip({ content: _tooltip, position: 'mouse', name: 'movieTooltip'});
	}
}


/**
 * deprecated functions - 더이상 사용되지 않는 함수.
 * @author	jaewon.Choi
 * @date	2017/05/08
 **/

/**
function gfn_CreateDataBindInfo(dataType, dtName, selectUrl, updateUrl, header) {
	// dataSet, dataAdapter을 window 객체로 선언
	//window.dataSet = gfn_CreateDataSet('ds');

	
	// dataTable은  여러개 일 수 있으므로 별도 처리
	window.dataTable = gfn_CreateDataTable(dataSet, dtName, selectUrl, updateUrl, header);	
}; **/

/**
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
}; **/

/**
function gfn_SetDataHeader(dtName, header, selectUrl, updateUrl){
	
    var dataTable = window.dataSet.getDataTable(dtName);
    if (dataTable && dataTable.id) {
        return dataTable;
    } else {
    	eval("window." + dtName + " = new XgDataTable('" +dtName + "');" );

		// 데이터테이블을 식별하기 위한 role/component추가.
		var _dt = $(eval(dtName));
		_dt.attr("role","datatable");
		_dt.attr("component", "xg-datatable");
		
		
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
}; **/