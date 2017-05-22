/*****************************************************************************************************************
 * 데이터 레코드를 추가하는 함수 
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/03
 *****************************************************************************************************************/
 $.fn.addRow = function() {
	switch(arguments.length) {
		case 0:
			if(this.length === 0) {
				return null;
			}

			switch(this.attr("role")) {
				case "grid":
					this.xgGrid( 'addrow', null, {}, "last" );
					_dt = this.xgGrid('getDataTable');
					this.xgGrid('selectrow', _dt.getRowCount()-1);	// 해당 위치로 이동.
					break;
				case "datatable":
					_dt = this[0];
					_cnt = _dt.getColumnCount();
					var _val = [];
					for (var _i=0;_i<_cnt; _i++ ) {
						_val[_i] = '';
					}
					_dt.addRow(_val);	
					break;
				default:
					alert("addRow메소드는 dataTable 또는 Grid 에서만 사용이 가능합니다.");
					break;
			}
			break;
		case 1:
			switch(this.attr("role")) {
				case "grid":
					switch(typeof arguments[0]) {
						case "string":
							
							if (arguments[0].toUpperCase() == "LAST") {
								this.xgGrid( 'addrow', null, {}, "last");
								_dt = this.xgGrid('getDataTable');
								this.xgGrid('selectrow', _dt.getRowCount()-1);	// 해당 위치로 이동.
							} else {
								this.xgGrid( 'addrow', null, {}, "first");
								this.xgGrid('selectrow', 0);	// 해당 위치로 이동.
							}
							break;
						case "object":
							this.xgGrid( 'addrow', null, arguments[0], "last");
							_dt = this.xgGrid('getDataTable');
							this.xgGrid('selectrow', _dt.getRowCount()-1);	// 해당 위치로 이동.
							break
					}
					break;
				case "datatable":
					_dt = this[0];
					_dt.addRow(arguments[0]);	
					break;
				default:
					alert("addRow메소드는 dataTable 또는 Grid 에서만 사용이 가능합니다.");
					break;
			}
			break;
		case 2:
			if (arguments[1].toUpperCase() == "LAST") {
				this.xgGrid( 'addrow', null, arguments[0], "last");
				_dt = this.xgGrid('getDataTable');
				this.xgGrid('selectrow', _dt.getRowCount()-1);	// 해당 위치로 이동.
			} else {
				this.xgGrid( 'addrow', null, arguments[0], "first");
				this.xgGrid('selectrow', 0);	// 해당 위치로 이동.
			}
			break;
	}
 };


 $.fn.nameValue = function() {
	switch(arguments.length) {
		case 3:
			switch(this.attr("role")) {
				case "grid":
					this.xgGrid( 'setCellValue', arguments[0], arguments[1], arguments[2] );
					break;
				default:
					alert("nameValue메소드는 dataTable 또는 Grid 에서만 사용이 가능합니다.");
					break;
			}
			break;
	}
 };
 
 /*****************************************************************************************************************
  * 데이터 레코드를 갯수를 반환하는 함수
  *****************************************************************************************************************
  * @author	jwChoi
  * @date	2017/04/03
  *****************************************************************************************************************/
 $.fn.countRow = function() {
		switch(arguments.length) {
			case 0:
				if(this.length === 0) {
					return null;
				}

				switch(this.attr("role")) {
					case "grid":
						_dt = this.xgGrid('getDataTable');
						return _dt.body.length;
						break;
					case "datatable":
						_dt = this[0];
						return _dt.body.length;
						break;
					default:
						alert("countRow메소드는 dataTable 또는 Grid 에서만 사용이 가능합니다.");
						break;
				}
				break;
			case 1:
				if(this.length === 0) {
					return null;
				}
				//alert("ok");
				if (arguments[0] == true) {
					switch(this.attr("role")) {
						case "grid":
							var _dt = this.xgGrid('getDataTable');
							var _len = _dt.body.length-1;
							var _cnt = 0;
							for (var i=0; i<_len; i++) {
								if (_dt.body[i].status != 4) {
									_cnt = _cnt +1;
								}
							}
							//alert("_cnt > " + _cnt);
							return _cnt;
							break;
						case "datatable":
							var _dt = this[0];
							var _len = _dt.body.length-1;
							var _cnt = 0;
							for (var i=0; i<_len; i++) {
								if (_dt.body[i].status != 4) {
									_cnt = _cnt +1;
								}
							}
							//alert("_cnt > " + _cnt);
							return _cnt;
							break;
						default:
							alert("countRow메소드는 dataTable 또는 Grid 에서만 사용이 가능합니다.");
							break;
					}
				} 
				return -1;
				
				break;
		}
 };
/*****************************************************************************************************************
 * 데이터 레코드를 삭제하는 함수 
 *****************************************************************************************************************
 * @author	jwChoi
 * @date	2017/04/03
 *****************************************************************************************************************/
 $.fn.deleteRow = function() {
	if (this.xgGrid('getselectedrowindex') == -1) {
		alert("삭제할 레코드를 선택하세요.");
		return;
	}
	 
	switch(arguments.length) {
		case 0:
			if(this.length === 0) {
				return null;
			}

			switch(this.attr("role")) {
				case "grid":
					/**
						_dt = this.xgGrid('getDataTable');
						this.xgGrid('selectrow', _dt.getRowCount()-1);	// 해당 위치로 이동.
				     **/
					var _curRowPos = this.xgGrid('getselectedrowindex');

					var _dt = this.xgGrid('getDataTable');
					var _allRowCnt = $(this).countRow(true);
					
				
					if (_curRowPos == _allRowCnt) {
						_curRowPos = _curRowPos -1;
						//alert(">> " + _curRowPos);
						
					}
					this.xgGrid( 'deleterow', this.xgGrid('getselectedrowindex'));
					//this.xgGrid('selectrow',_curRowPos);
					break;
				default:
					alert("deleteRow메소드는 dataTable 또는 Grid 에서만 사용이 가능합니다.");
					break;
			}
			break;
		case 1:
			switch(this.attr("role")) {
				case "grid":
					this.xgGrid( 'deleterow', arguments[0]);
					break;
				case "datatable":
					_dt = this[0];
					_dt.addRow(arguments[0]);	
					break;
				default:
					alert("deleteRow메소드는 dataTable 또는 Grid 에서만 사용이 가능합니다.");
					break;
			}
			break;
	}
 };