/*****************************************************************************************************************
 * XENA HTML5 화면 초기화 함수
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/06
 *****************************************************************************************************************
 * @desc	화면 로딩시 자동으로 실행되며, 실행 완료후에 gfn_onload() 함수가 실행된다.
 *****************************************************************************************************************/

var _hasOwn = Object.prototype.hasOwnProperty;

/**
 * 그리드 타입 호환성을 확보를 위한 함수
 * @param _format
 * @returns		[true/신규 포멧, false/기존 포멧]
 */
function fn_GridFormatType(_format) {
    var _keys = [], name;
    for (name in _format[0]) {
        if (_hasOwn.call(_format[0], name)) {
           if (name == "colName") {
        	   return false;
           }
        }
    }
    return true;
}


/**
 * 데이터테이블을 생성하는 함수.
 * @author	jaewon.Choi
 * @date	2017/05/08
 * @param	_id		데이터테이블 ID
 * @param	_cols	컬럼 포멧
 **/
function fn_CreateDatatable(_id, _cols){
	
    var dataTable = window.dataSet.getDataTable(_id);
    if (dataTable && dataTable.id) {	
    	// 데이터 테이블이 존재하는 경우 데이터 테이블을 반환
        return dataTable;
    } else {
    	// 데이터 테이블이 없는 경우 신규 생성.
		var _dt = new XgDataTable(_id);
        
		// 데이터테이블을 식별하기 위한 role/component추가.
		var _ref_dt = $(_dt);
		_ref_dt.attr("role","datatable");
		_ref_dt.attr("component", "xg-datatable");
		
		// 컬럼명이 있는 경우 처리
		var columnNames = new Array();

		
		// 포멧 유형을 검사하여 호환성을 보장한다.
		var _formatType = fn_GridFormatType(_cols);

		if (_formatType == true) {
			// 데이터 테이블의 헤더를 셋팅.[신규포멧]
			for (var i=0; i < _cols.length; i++){
				if(_cols[i].id != null){	
					_dt.addColumnForGauce(_cols[i].id, _cols[i].type, _cols[i].size);
					if (typeof _cols[i].name != "undefined") {
						columnNames[_cols[i].id] = _cols[i].name;
						
					}
				}
			}		
		} else {
			// 데이터 테이블의 헤더를 셋팅. [기존 포멧]
			for (var i=0; i < _cols.length; i++){
				_dt.addColumnForGauce(_cols[i].colName, _cols[i].colType, _cols[i].colSize);
			}	
		}

		// 컬럼 명을 넣어 준다.
		_ref_dt.attr("columnNames", columnNames);
		
		// 데이터셋에 데이터테이블을 추가.
		dataSet.addDataTable(_dt);	
		
		// 윈도우 객체에 넣어 준다. [기존에 선언된 tag를 제거하는 효과]
		$(window).attr(_id, _dt);
    }   
};

	
/**
 * 그리드 포멧을 생성하는 함수.
 * @param _id			그리드객체 ID
 * @param _format		그리드포멧
 * @returns
 */
function fn_SetGridFormat(_id, _format) {
	var gridColumns = [];
	
	var _type = fn_GridFormatType(_format);
	if (_type == true) {
		for (var i=0; i < _format.length; i++){
			if(_format[i].name != null){
				var defaultOptions = {};
				if(_format[i].name){
					defaultOptions = {
				        text: _format[i].name,
				        datafield: _format[i].id,
				        width: _format[i].width || 'auto',
				        align: 'center',
				        cellsalign: 'center'
					};
					if ((typeof _format[i].colprop) === 'object') {
				        for (name in _format[i].colprop) {
				            defaultOptions[name] = _format[i].colprop[name];
				        }
				    }			
					gridColumns.push(defaultOptions);		
				}
			}
		}
	} else {
		for (var i=0; i < _format.length; i++){
			if(_format[i].gridColumnName != null){
				var defaultOptions = {};
				if(_format[i].gridColumnName){
					defaultOptions = {
				        text: _format[i].gridColumnName,
				        datafield: _format[i].colName,
				        width: _format[i].gridColumnWidth || 'auto',
				        align: 'center',
				        cellsalign: 'center'
					};
					if ((typeof _format[i].gridColumnOptions) === 'object') {
				        for (name in _format[i].gridColumnOptions) {
				            defaultOptions[name] = _format[i].gridColumnOptions[name];
				        }
				    }			
					gridColumns.push(defaultOptions);		
				}
			}
		}
	}
	
	$(window).attr(_id + "GridColumns", gridColumns);

};


/**
 * 그리드의 포멧과 데이터테이블을 생성하는 함수.
 * @author	jaewon.Choi
 * @date	2017/05/08
 * @param	_gridId			그리드 ID
 * @param	_dataTableId	데이터테이블 ID
 * @param	_cols			컬럼 포멧
 **/
function fn_CreateDatatableAndGridFormat(_gridId, _dataTableId, _cols){

	var gridColumns = [];

	// 데이터 테이블이 없는 경우 신규 생성.
	var _dt = new XgDataTable(_dataTableId);
    
	// 데이터테이블을 식별하기 위한 role/component추가.
	var _ref_dt = $(_dt);
	_ref_dt.attr("role","datatable");
	_ref_dt.attr("component", "xg-datatable");
	
	// 컬럼명이 있는 경우 처리
	var columnNames = new Array();

	// 컬럼을 숨길 컬럼을 저장 [show:false]
	var _hiddenColumns = new Array();
		
	// 포멧 유형을 검사하여 호환성을 보장한다.
	var _formatType = fn_GridFormatType(_cols);

	if (_formatType == true) {
		// 데이터 테이블의 헤더를 셋팅.[신규포멧]
		for (var i=0; i < _cols.length; i++){
			// 데이터테이블 컬럼 생성
			//if(_cols[i].id != null){	
			_dt.addColumnForGauce(_cols[i].id, _cols[i].type, _cols[i].size);

			// 포멧에 컬럼명이 존재하는 경우, 컬럼명을 셋팅 [추후 활용 방안?]
			if (typeof _cols[i].name != "undefined") {
				columnNames[_cols[i].id] = _cols[i].name;
			}
			//}
			if (typeof _cols[i].show != "undefined" && (_cols[i].show == "false" || _cols[i].show == false)) {
				_hiddenColumns[_hiddenColumns.length] = _cols[i].id;
			}
			
			// 그리드 포멧 생성
			if(_format[i].name != null){
				// 맵핑 테이블 적용 필요.
				var defaultOptions = {
						text: _format[i].name,
						datafield: _format[i].id,
						width: _format[i].width || 'auto',
						align: 'center',
						cellsalign: 'center'
					};
				if ((typeof _format[i].colprop) === 'object') {
					for (name in _format[i].colprop) {
						defaultOptions[name] = _format[i].colprop[name];
					}
				}			
				gridColumns.push(defaultOptions);		

			}
		}		
	} else {
		// 데이터 테이블의 헤더를 셋팅. [기존 포멧]
		for (var i=0; i < _cols.length; i++){
			_dt.addColumnForGauce(_cols[i].colName, _cols[i].colType, _cols[i].colSize);
			
			if (typeof _cols[i].show != "undefined" && (_cols[i].show == "false" || _cols[i].show == false)) {
				_hiddenColumns[_hiddenColumns.length] = _cols[i].colName;
			}

			if(_format[i].gridColumnName != null){
				var defaultOptions = defaultOptions = {
						text: _format[i].gridColumnName,
						datafield: _format[i].colName,
						width: _format[i].gridColumnWidth || 'auto',
						align: 'center',
						cellsalign: 'center'
					};
				if ((typeof _format[i].gridColumnOptions) === 'object') {
					for (name in _format[i].gridColumnOptions) {
						defaultOptions[name] = _format[i].gridColumnOptions[name];
					}
				}			
				gridColumns.push(defaultOptions);
			}
		}	
	}

	// 컬럼 명을 넣어 준다.
	_ref_dt.attr("columnNames", columnNames);
	
	// 숨김 컬럼
	$(window).attr(_gridId + "HiddenColumns", _hiddenColumns);
	
	// 데이터셋에 데이터테이블을 추가.
	dataSet.addDataTable(_dt);	
	
	// 윈도우 객체에 넣어 준다. [기존에 선언된 tag를 제거하는 효과]
	$(window).attr(_dataTableId, _dt);
	
	//console.log("fn_CreateDatatableAndGridFormat > " + JSON.stringify(gridColumns, null, 1));
	return gridColumns;
	// 그리드 포멧
//	$(window).attr(_gridId + "GridColumns", gridColumns);
};


/**
 * 그리드 포멧을 변경해 주는 함수
 * @param _id		그리드 객체 ID
 * @param _format	JSON 인자값 : {"format":포멧객체<format>}
 *        ex) {"format":fmt_Format1}
 * @returns
 */
function gfn_SetFormat(_id, _format) {
	var gridColumns = [];
	
	//var _format = "[" + $("#fmt_Format1")[0].innerHTML + "]";
	if (typeof _format == "object") {
		try {
			_format = JSON.parse("[" + _format.format.innerHTML + "]");
		} catch(exception) {
			alert("포멧 파싱중에 오류가 발생했습니다.\nJSON 표준 유형으로 정의해 주세요.");
			return;
		}
	} else {
		alert("그리드 포멧 유형이 틀립니다.\n<format>태그를 사용하여 생성해 주세요.");
		return;
	}	
	
	var _item = $("#" + _id);
	var dataTable = dataSet.getDataTable(_item.attr("dataid"));
    
	var _type = fn_GridFormatType(_format);
	
	if (_type == true) {
		for (var i=0; i < _format.length; i++){
			if(_format[i].name != null){
				var defaultOptions = {};
				if(_format[i].name){
					defaultOptions = {
				        text: _format[i].name,
				        datafield: _format[i].id,
				        width: _format[i].width || 'auto',
				        align: 'center',
				        cellsalign: 'center'
					};
					if ((typeof _format[i].colprop) === 'object') {
				        for (name in _format[i].colprop) {
				            defaultOptions[name] = _format[i].colprop[name];
				        }
				    }			
					gridColumns.push(defaultOptions);		
				}
			}
		}
	} else {
		for (var i=0; i < _format.length; i++){
			if(_format[i].gridColumnName != null){
				var defaultOptions = {};
				if(_format[i].gridColumnName){
					defaultOptions = {
				        text: _format[i].gridColumnName,
				        datafield: _format[i].colName,
				        width: _format[i].gridColumnWidth || 'auto',
				        align: 'center',
				        cellsalign: 'center'
					};
					if ((typeof _format[i].gridColumnOptions) === 'object') {
				        for (name in _format[i].gridColumnOptions) {
				            defaultOptions[name] = _format[i].gridColumnOptions[name];
				        }
				    }			
					gridColumns.push(defaultOptions);		
				}
			}
		}
	}
    var defaultOptions = {
            xgInitOption: {
                xgDataSet: dataSet,
                xgDataTable: dataTable,            	
                columns: gridColumns
                
            }
        };	
	_item.xgGrid(defaultOptions);

};

$(document).ready(function () {
	// 공통으로 데이터셋을 생성
	window.dataSet = gfn_CreateDataSet('ds');
	
	/** 일반 객체에 대한 공통 처리 부분 [Start] **/

	// 체크 박스에 대한 초기화
	$('[component="xg-datatable"]').each(
		function(index, item) {
			var _item = $(item);
			var _params = {};
			var _cols = [];

			// 데이터 테이블 ID
			var _id = _item.attr("id");
			
			// 데이터 테이블이 없는 경우 신규 생성.
			var _dt = new XgDataTable(_id);
		    
			// 데이터테이블을 식별하기 위한 role/component추가.
			var _ref_dt = $(_dt);
			_ref_dt.attr("role","datatable");
			_ref_dt.attr("component", "xg-datatable");
			

			_item.children().each(
				function(index){
					switch(this.tagName.toLowerCase()) {	// start switch
						case "format":
							var _item = $(this);
							_item.children().each(			// start main each	
								function(index){
									switch(this.tagName.toLowerCase()) {	// start switch #2
										case "column":
											// attribute 파싱.
											var _key = "";
											var _value = "";
											$.each( this.attributes,	// start each
													function() {							
														if(this.specified) {
															_params[this.name] = this.value;
														}	
													} 
											);							// end each.
											break;

									}											// end switch #2
									//_cols[_cols.length] = _params;
									_dt.addColumnForGauce(_params.id, _params.type, _params.size);
								});										// end main each
							// 데이터셋에 데이터테이블을 추가.
							dataSet.addDataTable(_dt);	
							
							// 윈도우 객체에 넣어 준다. [기존에 선언된 tag를 제거하는 효과]
							$(window).attr(_id, _dt);
							break;		
					}										// end switch 
				}
				
			);
		}
	);

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

			var codeList = "";
			var valueMember = "";
			var displayMember = "";
			var dataid = "";
			var _params = {};
			
			var _groupCode = _item.attr("cdGrpCd");
			var valueMember = _item.attr("valueMember");
			var displayMember = _item.attr("displayMember");
			var _keyName = _item.attr("keyName");
			
			fn_tooltip(_item);



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
			
			if (typeof _groupCode != "undefined") {
				/** 디폴트값 적용 **/
				if (typeof valueMember == "undefined") valueMember = "cd";
				if (typeof displayMember == "undefined") displayMember = "nm";
				if (typeof _keyName == "undefined") _keyName = "cdList";
				
				var _cols = '[{"id":"' + valueMember +'", "type":"string",	"size" :"10"},' + 
						     '{"id":"' + displayMember + '", "type":"string",	"size" :"100"}]';
				// 헤더 정보
				var _cols  = JSON.parse(_cols);
	
				if (dataid == "") {		// 데이터 ID가 없는 경우 자동 생성.[dt_ + 객체id]
					dataid = "dt_" + this.id;
				}
				
				// 접수 리스트 데이터 바인드 정보 세팅 [현재 서비스 호출 부분은 하드 코딩으로 처리]
				//gfn_CreateDataBindInfo('XML', dataid, '' , '', _cols);
				fn_CreateDatatable(dataid, _cols);
				
				var params = "cdGrpCd=" + _item.attr("cdGrpCd");
				
				gfn_Select(["SM-onl", "SSMA0101", "SSMA010101"], _keyName + "="+ dataid, params, 
					function() {
						// 데이터 조회후 콜백에서 값 설정
						var _options = {	source:dataSet.makeListTypeSource(dataid, displayMember, valueMember),
											valueMember:"value",
											displayMember:"text"  };
						_item.xgInput(_options);
						
				
					});
				
			} else {
				_item.xgInput({
					source: codeList,
					valueMember:valueMember,
					displayMember:displayMember
				});
			}			
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
			var valueMember = "cd";
			var displayMember = "nm";
			var dataid = "";
			var _params = {};
			
			var _groupCode = _item.attr("cdGrpCd");
			
			//fn_tooltip(_item);
			
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

			if (typeof _groupCode != "undefined") {
				/** 디폴트값 적용 **/
				valueMember = "cd";
				displayMember = "nm";
				
				var _cols = '[{"id":"' + valueMember +'", "type":"string",	"size" :"10"},' + 
						     '{"id":"' + displayMember + '", "type":"string",	"size" :"100"}]';
				// 헤더 정보
				var _cols  = JSON.parse(_cols);
	
				if (dataid == "") {		// 데이터 ID가 없는 경우 자동 생성.[dt_ + 객체id]
					dataid = "dt_" + this.id;
				}
				
				// 접수 리스트 데이터 바인드 정보 세팅 [현재 서비스 호출 부분은 하드 코딩으로 처리]
				//gfn_CreateDataBindInfo('XML', dataid, '' , '', _cols);
				fn_CreateDatatable(dataid, _cols);

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
			var valueMember = "cd";
			var displayMember = "nm";
			var dataid = "";
			var _params = {};
			
			var _groupCode = _item.attr("cdGrpCd");			
			

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
			
			if (typeof _groupCode != "undefined") {
				var _cols = '[{"id":"' + valueMember +'", "type":"string",	"size" :"10"},' + 
							 '{"id":"' + displayMember + '", "type":"string",	"size" :"100"}]';
				// 헤더 정보
				_cols  = JSON.parse(_cols);
				
				// 접수 리스트 데이터 바인드 정보 세팅 [현재 서비스 호출 부분은 하드 코딩으로 처리]
				//gfn_CreateDataBindInfo('XML', dataid, '' , '', _cols);
				if (dataid == "") {		// 데이터 ID가 없는 경우 자동 생성.[dt_ + 객체id]
					dataid = "dt_" + this.id;
				}
				
				fn_CreateDatatable(dataid, _cols);

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

	/** Gird NewType Start **/
	// 컬럼
	var $_grid_columns = [];

	var $_subTypes = {};

	// 그룹
	var $_columnGroups = [];


	// 그리드 레이아웃
	var $_gridLayout = {
		xgInitOption: {
			columns:[]			
		},
		xgBindOption: {
			useEditor:{			
			},
	        cellEdit: true,
	        cellValidation: true,
	        showRowStatus: false
		},
		columnsheight: 30, 
		rowsheight: 30,
		editable: true,
		sortable: true,
		sorttogglestates:0,
		height:460,
		keyboardnavigation: true,
		columnsresize: true,
		columnsreorder: true,
		enablekeyboarddelete: true		
	};
	
	// 데이터 테이블 ID
	var $_dataid = "";

	// 데이터 테이블
	var $_dt = null;
	
	var $_columnNames = null;
	
	var $_hiddenColumns = null;
	
	var _booleanColumns = {};
	
	// 헤드 체크 박스 기능
	var _headcheck = false;
	var _headtext = false;
	var _headname = "";
	
	/**
	 * 그리드 Markup을 파싱하는 내장 함수
	 * @author	jaewon.Choi
	 * @date	2017/05/12
	 **/
	function $_parseGridTag_(_item, _prevParent, _prevRow) {
		var _parent = "";

		if (typeof _prevParent != "undefined") _parent = _prevParent;
		if (typeof _prevRow == "undefined") _prevRow = {}
		
		_item.children().each(	// start main each
			function(index){
				
				switch(this.tagName.toLowerCase()) {	// start switch
					case "xgroup":
						// attribute 파싱.
						var _key = "";
						var _value = "";
						$.each( this.attributes,	// start each
							function() {							
								if(this.specified) {
									
									switch(this.name) {
										case "id":
											_key = this.value;
											break;
										case "name":
											_value = this.value;
											break;
									}
								}	
							} 
						);						// end each.
						
						$_columnGroups[$_columnGroups.length] = { "text":_value, "align":"center", "name":_key}
						$_parseGridTag_($(this), _key);
						break;
					case "group":
						// attribute 파싱.
						var _key = "";
						var _value = "";

						$.each( this.attributes,	// start each
							function() {							
								if(this.specified) {
									
									switch(this.name) {
										case "id":
											_key = this.value;
											break;
										case "name":
											_value = this.value;
											break;
									}
								}	
							} 
						);						// end each.

						if (_parent != "")	{
							$_columnGroups[$_columnGroups.length] = { "text":_value, "parentgroup":_parent, "align":"center", "name":_key}
						} else {
							$_columnGroups[$_columnGroups.length] = { "text":_value, "align":"center", "name":_key}
						}
						
						$_parseGridTag_($(this), _key);
						break;
					case "column":
						// attribute 파싱.
						var _key = "";
						var _value = "";
						var _row = {align: 'center', cellsalign: 'center'};	// default 값.
						var _dt_cols = {};

						$.each( this.attributes,	// start each
							function() {							
								if(this.specified) {
									switch(this.name) {
										case "id":
											_row["datafield"] = this.value;
											_dt_cols["datafield"] = this.value;
											break;
										case "name":
											_row["text"] = this.value;
											_dt_cols["text"] = this.value;
											break;
										default:
											_name = _xgGridMapper[this.name];
											_val = this.value;
											if (typeof _name != "undefined") {
												if (_val == "true" || _val == "false") {
													_row[_name] = JSON.parse(_val);
												} else {
													_row[_name] = _val;
													
												}
											}								
											_dt_cols[this.name] = this.value;
											break;
									}									
								}	
							} 
						);
						
						
						var _pos = $_grid_columns.length;
						//var _row = JSON.parse("{" + this.innerHTML + "}");
						
						if (_parent != "")	{
							_row["columngroup"] = _parent;
						}
						$_parseGridTag_($(this), _key, _row);
						
						// 숨김 컬럼
						if (typeof _row.show != "undefined" && (_row.show == "false" || _row.show == false)) {
							$_hiddenColumns[$_hiddenColumns.length] = _row.datafield;
						}
						
						// 데이터 테이블 생성.
						if(typeof _row.text != "undefined") {	// 한글명이 있는 경우에만 그리드 컬럼으로 선언
							$_columnNames[_row.datafield] = _row.text;
							$_grid_columns[_pos] = _row;
							$_dt.addColumnForGauce(_dt_cols.datafield, _dt_cols.type, _dt_cols.size);
						} else {								// 한글명이 없는 경우에는 데이터테이블만 생성
							$_dt.addColumnForGauce(_dt_cols.datafield, _dt_cols.type, _dt_cols.size);
						}
						break;
					case "prop":
						// attribute 파싱.
						var _key = "";
						var _value = "";
						$.each( this.attributes,	// start each
							function() {							
								if(this.specified) {
									switch(this.name) {
										case "headcheck":									
											_headcheck = this.value;
											_headname = _prevRow.text;
											break;
										case "headtext":
											_headtext = this.value;
											break;
										default:
											_prevRow[this.name] = this.value;
											break
									}
									
								}	
							} 
						);						// end each.

						if (_headcheck == "true") {
							var str = "<script>$('#" + _prevRow.datafield + "HeadCheck').xgCheckBox();</script>";
							if (_headtext == "true") {
								_prevRow["rendered"] = function (header) {
									header.html("<div id=" + _prevRow.datafield + "HeadCheck>" +"<span>" + _headname + "</span></div>" + str);
								};	
							} else {
								_prevRow["rendered"] = function (header) {
									header.html("<div id=" + _prevRow.datafield + "HeadCheck>" +"</div>" + str);
								};	
							}
						} 


						break;
					case "subtype":
						// attribute 파싱.
						var _key = "";
						var _value = "";
						var _row = {};
						$.each( this.attributes,	// start each
							function() {							
								if(this.specified) {
									switch(this.name.toLowerCase()) {
										case "type":
											_row["columntype"] = this.value;
											break;
										case "formatstring":
											_row["formatString"] = this.value;
											break;
										default:
											_row[this.name] = this.value;
											break;
									}
									

								}	
							} 
						);						// end each.
						switch(_row.columntype) {
							case "checkbox":
								_booleanColumns[_prevRow.datafield] = { trueValue: _row.checkvalue.split(",")[0] ,  falseValue: _row.checkvalue.split(",")[1] };
								break;

							case "dropdownlist":
								var _valMem = _row.valuemember;
								var _dispMem = _row.displaymember;
								var _keyName = _row.keyname;
								var _colid = _prevRow.datafield;
								var _dataid = "";
								var _type = _row.columntype;
								var _cdgrpcd = _row.cdgrpcd;

								// 디폴트값 적용 
								if (typeof _valMem == "undefined") _valMem = "cd";
								if (typeof _dispMem == "undefined") _dispMem = "nm";
								if (typeof _keyName == "undefined") _keyName = "cdList";	
								
								var _cols = '[{"id":"' + _valMem +'", "type":"string",	"size" :"10"},' + 
											 '{"id":"' + _dispMem + '", "type":"string",	"size" :"100"}]';
								// 헤더 정보
								var _cols  = JSON.parse(_cols);
					
								if (_dataid == "") {		// 데이터 ID가 없는 경우 자동 생성.[dt_ + 객체id]
									_dataid = "dt_grd_cols_" + _colid;
								}	
								
								fn_CreateDatatable(_dataid, _cols);	// 데이터 테이블 생성.
								
								var params = "cdGrpCd=" + _cdgrpcd;

								// 그리드에서는 특성한 syncload를 true로 설정하여 동기 방식으로 처리해야함 [나중에 캐싱]
								gfn_Select(["SM-onl", "SSMA0101", "SSMA010101"], _keyName + "="+ _dataid, params, 
										function() {
											// 데이터 조회후 콜백에서 값 설정	
											_row["columntype"] = _type;
											_row["source"] = dataSet.makeListTypeSource(_dataid, _dispMem, _valMem);
										}, {"syncload":true});							
								break;
							default:
								break;
						}
						// 속성 가공 필
						$_subTypes[_prevRow.datafield] = _row;

						break;
				}										// end switch
		} );					// end main each
	}

	// 그리드 포멧에 대한 처리
	$('[component="xg-grid"]').each(		// start main each
		function(index, item) {
			
			// 컬럼
			$_grid_columns = [];

			$_subTypes = {};

			// 그룹
			$_columnGroups = [];


			// 그리드 레이아웃
			$_gridLayout = {
				xgInitOption: {
					columns:[]			
				},
				xgBindOption: {
					useEditor:{			
					},
			        cellEdit: true,
			        cellValidation: true,
			        showRowStatus: false
				},
				columnsheight: 30, 
				rowsheight: 30,
				editable: true,
				sortable: true,
				sorttogglestates:0,
				height:460,
				keyboardnavigation: true,
				columnsresize: true,
				columnsreorder: true,
				enablekeyboarddelete: true		
			};
			
			// 데이터 테이블 ID
			$_dataid = "";
			
			_item = $(item);
			
			// param 테그 파싱 정보
			_params = {};
			
			// 컬럼의 한글명
			$_columnNames = {};
			
			// 숨김 컬럼
			$_hiddenColumns = new Array();

			// 그리드 DATAID만 먼저 파싱.
			_item.children().each(	// start main each
				function(index){
					switch(this.tagName.toLowerCase()) {	// start switch
						case "param":
							$.each(this.attributes, function() {
								if(this.specified) {
									switch(this.name.toLowerCase()) {
										case "dataid":
											/** 데이터 테이블 객체 생성 **/
											$_dataid = this.value;
											$_dt = new XgDataTable($_dataid);
											var _ref_dt = $($_dt);
											_ref_dt.attr("role","datatable");
											_ref_dt.attr("component", "xg-datatable");
											break;
									}
								}
							});							
							break;
					}										// end switch
			} );

			// 그리드 컴포넌트를 파싱.
			_item.children().each(	// start main each
				function(index){
			
					switch(this.tagName.toLowerCase()) {	// start switch
						case "format":
							$_parseGridTag_($(this));
							break;
						case "param":
							$.each(this.attributes, function() {
								if(this.specified) {
									switch(this.name.toLowerCase()) {
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
							break;
					}										// end switch
			} );
			
			//alert(JSON.stringify($_columnNames,null, 5));
			//alert(JSON.stringify($_hiddenColumns, null, 5));
			//alert(JSON.stringify($_dt, null, 5));
			console.log("gridOptions > " + JSON.stringify($_gridLayout, null, 5) );
			
			$_gridLayout.xgInitOption.columns = $_grid_columns;
			
			// 컬럼 그룹 설정
			if ( $_columnGroups.length != 0) {
				$_gridLayout.columngroups = $_columnGroups;
			}
			$_gridLayout.xgBindOption.useEditor = $_subTypes;

			// attribute 파싱
			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgGridMapper[this.name];
							_val = this.value;
							if (typeof _name != "undefined") {
								if (_val == "true" || _val == "false") {
									$_gridLayout[_name] = JSON.parse(_val);
								} else {
									$_gridLayout[_name] = _val;
								}
							}							
						}
				});
			
			console.log("GridFormat > " + JSON.stringify($_gridLayout, null, 5));

			// 데이터셋에 데이터테이블을 추가.
			dataSet.addDataTable($_dt);	
			
			// 윈도우 객체에 넣어 준다. [기존에 선언된 tag를 제거하는 효과]
			$(window).attr($_dataid, $_dt);
			
			// 데이터셋 바인딩
			$_gridLayout.xgInitOption["xgDataSet"] = dataSet;
			
			// 데이터 테이블 바인딩.
			$_gridLayout.xgInitOption["xgDataTable"] = $_dt;

			
			// 그리드 맵핑.
			_item.xgGrid($_gridLayout);
			
			// 그리드 파라미터.
			_item.xgGrid(_params);
			
			for (var i=0; i<$_hiddenColumns.length; i++) {
				_item.xgGrid('hidecolumn', $_hiddenColumns[i]);
			}		
	
		});									// end main each
	/** Grid NewType End **/
	
	/** Grid Start **/
	// 데이터셋의 헤더 정보/그리드 셋팅
    var headerInfo = "";

    var dataid = "";
	var format = "";

	$(".grid,[component='xg-grid22']").each(function(index, item){	
			var _item = $(item);
			var _gridId = this.id;
			var _params = {};
			var _booleanColumns = {};

			var option = "";
			switch(_item.attr("type")) {
				case "edit":
					 option = {
							xgBindOption: {
								useEditor : {}, /*콤보박스 컬럼을 만들기 위해 추가함 jsh 2017-04-25*/
								cellEdit: true,
								cellValidation: true,
								showRowStatus: false
							},
							width: '100%',
							height: '460px',
							columnsheight: 30, 
							rowsheight: 30,
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
								useEditor : {},  /*콤보박스 컬럼을 만들기 위해 추가함 jsh 2017-04-25*/
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
			
			_item.children().each(
				function(index){
					switch(this.tagName.toUpperCase()) {
						case "FORMAT":
							format = this.innerHTML;
							break;
						case "PARAM":
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
							break;
						case "USEEDITOR":
							if($(this).attr("column") == undefined){
								console.log("column이 없습니다 USEEDITOR 태그의 attribute를 확인해 주세요 ");
								return false;
							}						
							
							option.xgBindOption.useEditor[$(this).attr("column")] = {};
							
							var editorParam = eval("(" + this.innerHTML + ")");
							
							/*checkbox의 경우 dataTable에 booleanColumn이란 프로퍼티에 체크에 관한 세팅을 해주어야 값에 맞춰서 세팅을 한다*/
							if(editorParam["columntype"] == "checkbox"){
								
								_booleanColumns[$(this).attr("column")] = { trueValue: $(this).attr("checkValue").split(",")[0] ,  
																			falseValue: $(this).attr("checkValue").split(",")[1] };
							}
							
							for(param in editorParam){
								if (param == "source") {
							    	
									var _source = dataSet.makeListTypeSource('dt_inp_compCd', 'cdGrpCdNm', 'cdGrpCd');
									
									option.xgBindOption.useEditor[$(this).attr("column")][param] = _source;
								} else {
									option.xgBindOption.useEditor[$(this).attr("column")][param] = editorParam[param];
								}
							}		
							break;
						case "SUBTYPE":

							var _valMem = "";
							var _dispMem = "";
							var _keyName = "";
							var _colid = "";
							var _dataid = "";
							var _type = "";
							var _cdgrpcd = "";
							$.each(this.attributes, function() {
								if(this.specified) {
									switch(this.name.toLowerCase()) {
										case "column":
											_colid = this.value;
											break;
										case "type":
											_type = this.value;
											break;
										case "cdgrpcd":
											_cdgrpcd = this.value;
											break;
										case "valuemember":
											_valMem = this.value;
											break;
										case "displaymember":
											_dispMem = this.value;
											break;
										case "keyname":
											_keyName = this.value;
											break;
										default:
											break;
									}
								}
							});	
							
							option.xgBindOption.useEditor[_colid] = {};
							
							/** 디폴트값 적용 **/
							if (typeof _valMem == "undefined") _valMem = "cd";
							if (typeof _dispMem == "undefined") _dispMem = "nm";
							if (typeof _keyName == "undefined") _keyName = "cdList";
							
							var _cols = '[{"id":"' + _valMem +'", "type":"string",	"size" :"10"},' + 
									     '{"id":"' + _dispMem + '", "type":"string",	"size" :"100"}]';
							// 헤더 정보
							var _cols  = JSON.parse(_cols);
				
							if (_dataid == "") {		// 데이터 ID가 없는 경우 자동 생성.[dt_ + 객체id]
								_dataid = "dt_grd_cols_" + _colid;
							}
							
							// 접수 리스트 데이터 바인드 정보 세팅 [현재 서비스 호출 부분은 하드 코딩으로 처리]
							//gfn_CreateDataBindInfo('XML', dataid, '' , '', _cols);
							fn_CreateDatatable(_dataid, _cols);
							
							var params = "cdGrpCd=" + _cdgrpcd;
							
							/**
							 * 그리드에서는 특성한 syncload를 true로 설정하여 동기 방식으로 처리해야함 [나중에 캐싱]
							 */
							gfn_Select(["SM-onl", "SSMA0101", "SSMA010101"], _keyName + "="+ _dataid, params, 
								function() {
									// 데이터 조회후 콜백에서 값 설정	
									option.xgBindOption.useEditor[_colid]["columntype"] = _type;
									option.xgBindOption.useEditor[_colid]["source"] = dataSet.makeListTypeSource(_dataid, _dispMem, _valMem);
								}, {"syncload":true, "test":false});							
							break;
							
					}
					
					/**
					if (this.tagName.toUpperCase() == "FORMAT") {
						
					} else if (this.tagName.toUpperCase() == "PARAM") {	

					} else if(this.tagName.toUpperCase() == "USEEDITOR"){	
						
					}	**/
					
				}
			);
			
			// 헤더 정보
			try {
				_format = JSON.parse("[" + format + "]");
			} catch(exception) {
				_format = eval("[" + format + "]");
			}
			
			//console.log("init grid _format : " + JSON.stringify(_format, null, 2));
			//console.log(" _format " + JSON.stringify(_format, null, 2));
			
			// 데이터테이블과 그리드 포멧을 생성
			var _tmp_columns = fn_CreateDatatableAndGridFormat(_gridId, dataid, _format)
			
			// boolean 컬럼
			dataSet.getDataTable(dataid).booleanColumn = _booleanColumns;

			// 현재 연결된 데이터테이블을 설정
			_item.attr("dataid", dataid);
			
			// 그리드 dataSet, dtatTable, columns, options 설정 및 렌더링 [향후 타입에 따른 공통 처리]
			var xgGridOptions = gfn_CreateGridOption(_gridId, dataSet.getDataTable(dataid), option, _tmp_columns);
			
			_item.xgGrid(xgGridOptions);

			var _defaultParams = {};
			// 기본속성에 있는 정보를 입력한다.
			$.each(_item[0].attributes, 
				function() {
						if(this.specified) {
							_name = _xgGridMapper[this.name];
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
		
			// 컬럼 숨김 기능
			var _hiddenColumns = $(window).attr(_gridId + "HiddenColumns");
			for (var i=0; i<_hiddenColumns.length; i++) {
				_item.xgGrid('hidecolumn', _hiddenColumns[i]);
			}
		}
	); 
	
	/** Grid End **/

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