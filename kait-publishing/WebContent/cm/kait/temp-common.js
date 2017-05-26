//<![CDATA[
/*******************************
 * 파일명 :	temp-common.js
 * 설 명 :	퍼블리싱 작업용 임시 스크립트
 * 			퍼블리싱작업 도중 퍼블리셔가 확인해야할 각 영역 내용 조회
 * 			1. 공통레이아웃 탭버튼 클릭 이벤트
 * 			2. 제나 기본 그리드 초기화
 * 			3. 좌측 메뉴 클릭시 컨텐츠 html 로딩 
 * 			PS: 고객에게 화면 시연시 필요
 * 작성자 :	김상우
 * 수정일 :	2017.05.17
 ******************************/

(function( $ ){
	$.extend({
		gfx_selector : {
			id : {
				content : "#kait_content" // $.gfx_selector.id.content 
			},
			attr : {
				xgGrid : "[component='xg-grid']"	// $.gfx_selector.attr.xgGrid
			}
		},
		gfx_content : {
			// $.gfx_content.innerWidth()
			innerWidth : function( selector ) {

				if($(selector).length > 0) {
					return $(selector).innerWidth();
				} 
				return parseInt($("article").css("min-width")) / 2;
			} 
		}, 
		// 컨텐츠 안의 그리드 레이아웃 출력용 임시 스크립트
		gfx_commonGrid : function( selector ) {
			console.log(" ## S. gfx_commonGrid ## ");
			
			var colCount = 20;
			var columns = new Array();
			
			for(var i = 1; i <= colCount; i++) {
				columns[i] = { text: '컬럼(' + i + ")", datafield: 'col' + i, columngroup: 'col' + i, width: 100, align: 'center' };
			}
 
			var xgGridWidth = $.gfx_content.innerWidth( $(selector) );
			console.log("xgGridWidth: " + xgGridWidth);
			$(selector).empty();
			$(selector).xgGrid({
				width: xgGridWidth,
				xgInitOption: {
					columns: columns
				}
			});
			
			console.log(" ## E. gfx_commonGrid ## ");
		},
		gfx_load : function ( element ) {
			var hrefURL = $(element).attr("href");
			console.log("hrefURL : " + hrefURL);
			if(hrefURL && hrefURL.length > 0 && hrefURL != "#") {
				hrefURL = "." + hrefURL;
				$.post(hrefURL, function( response ){
					$($.gfx_selector.id.content).empty();
					$($.gfx_selector.id.content).html(response);
				}).done(function() {
					
					var dynamicEL = $.gfx_selector.id.content;
					
					//S. xena grid init
					console.log("[START] xena grid init");
					var xgGrids = $(dynamicEL + " " + $.gfx_selector.attr.xgGrid);
					if(xgGrids.length > 0) {
						$.each(xgGrids, function(idx , item) {
							$.gfx_commonGrid( item );
						});	
					}  
					//E. xena grid init	

					// 체크 박스에 대한 초기화
					console.log("[START] xg-datatable");
					$(dynamicEL + " " + '[component="xg-datatable"]').each(
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
					console.log("[START] xg-button");
					$(dynamicEL + " " + ".btn_search," + dynamicEL + " " + "[component='xg-button']").each(
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
					console.log("[START] xg-input");
					$(dynamicEL + " " + ".input," + dynamicEL + " " + "[component='xg-input']").each(
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
					console.log("[START] xg-checkbox");
					$(dynamicEL + " " + ".checkbox," + dynamicEL + " " + "[component='xg-checkbox']").each(
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
					console.log("[START] xg-radiobutton");
					$(dynamicEL + " " + ".radio," + dynamicEL + " " + "[component='xg-radiobutton']").each(
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
					console.log("[START] xg-combobox");
					$(dynamicEL + " " + ".comboBox," + dynamicEL + " " + "[component='xg-combobox']").each(
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
					console.log("[START] xg-listbox");
					$(dynamicEL + " " + ".listbox," + dynamicEL + " " + "[component='xg-listbox']").each(
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
					console.log("[START] xg-datetime");
					$(dynamicEL + " " + "[component='xg-datetime']").each(
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
					console.log("[START] xg-maskedinput");
					$(dynamicEL + " " + ".maskedInput," + dynamicEL + " " + "[component='xg-maskedinput']").each(
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
					console.log("[START] xg-numberinput");
					$(dynamicEL + " " + ".numberInput," + dynamicEL + " " + "[component='xg-numberinput']").each(
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
					console.log("[START] xg-passwordinput");
					$(dynamicEL + " " + ".passwdInput," + dynamicEL + " " + "[component='xg-passwordinput']").each(
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
					
					// TAB 에 대한 초기화
					console.log("[START] xg-tab");
					$(dynamicEL + " " + ".tab,"  + dynamicEL + " " + "[component='xg-tab']").each(
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
					
					console.log( "done");
				}).fail(function() {
					console.log( "error" );
				}).always(function() {
					location.hash = hrefURL.substring(1);
					console.log( "finished: " + location.hash);
				});
			}
			else {
				$($.gfx_selector.id.content).empty();
				$($.gfx_selector.id.content).html("메뉴 링크정보가 존재하지 않습니다. [ " + $(element).text() + " ]");
				console.log("메뉴 링크정보가 존재하지 않습니다. [ " + $(element).text() + " ]");
			}
		},
		gfx_init : function() {
			var hash = location.hash;
			
			if( hash && hash != '' && hash != '#') {
				hash = hash.substring(1);
				console.log("hash : " + hash);
				if($("aside .all_tree a[href='"+hash+"']").length == 0) {
					alert("존제하지 않는 메뉴 링크입니다.");
				}
				else {
					$.gfx_load( $("aside .all_tree a[href='"+hash+"']") );
				}
			}
			else if($("aside .all_tree a[init='true']").length > 0) {
				$("aside .all_tree a[init='true']:eq(0)").click();
			}
			else {
				console.log("location.hash 또는 첫번째 컨텐츠 화면 초기화 속성이있는 메뉴 a태그가 존재하지 않습니다.");
			}
			
		}
	});
})( jQuery );

$(document).ready(function(){

	//S. tab click event
	$(document).on("click", "div.nav_tab span", function( e ){
		$(this).parents("ul").find("span").removeClass("current_tab");
		$(this).addClass("current_tab");
		var $li = $(this).parents("li");

		$("aside div.menu_my").hide();
		$("aside div.wrap_menu_all").hide();
		$("aside div.menu_recent").hide();

		if($li.hasClass("tab_my")) {
			$("aside div.menu_my").show();
		}
		else if($li.hasClass("tab_all")) {
			$("aside div.wrap_menu_all").show();
		}
		else if($li.hasClass("tab_recent")) {
			$("aside div.menu_recent").show();
		}
	});

	//S. 상단 MDI 텝메뉴
	$(document).on("click", "div.mditab span", function( e ){
		if(!$(this).hasClass("arrow")) {
			$(this).parents("ul").find("span").removeClass("current_mditab");
			$(this).addClass("current_mditab");
		}
	});
	//E. 상단 MDI 텝메뉴
	
	//S. 컨텐츠 내부 텝메뉴
	$(document).on("click", $.gfx_selector.id.content + " div.tabmenu span", function( e ) {
		$(this).parents("ul").find("span").removeClass("current_tabmenu");
		$(this).addClass("current_tabmenu");
	});	
	//E. 컨텐츠 내부 텝메뉴
	
	//E. tab click event
	
	//S. load content html
	$(document).on("click", "aside .all_tree a", function( e ) {
		e.preventDefault();
		$.gfx_load( this );
	});
	//E. load content html

	// INIT MENU Contents
	$.gfx_init();
});


//]]>