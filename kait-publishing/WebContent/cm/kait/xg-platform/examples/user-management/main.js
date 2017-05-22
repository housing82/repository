(function() {
	$(document).ready(function() {
		var $grid, createOption, dataAdapter, dataSet, levelDataTable, userDataTable;
		dataSet = new XgDataSet();
		userDataTable = new XgDataTable('userDataTable', {
			url: {
				select: '/XG/jsp/userSelect.jsp',
				update: '/XG/jsp/userUpdate.jsp'
			},
			booleanColumn: [{
				column: 'MARRIAGE',
				trueValue: 'Yes',
				falseValue: 'No'
			}]
		});
		levelDataTable = new XgDataTable('levelDataTable', {
			url: {
				select: '/XG/jsp/levelSelect.jsp'
			}
		});
		dataSet.addDataTable(userDataTable);
		dataSet.addDataTable(levelDataTable);
		dataAdapter = new XgDataAdapter('dataAdapter');
		dataAdapter.setDataType('XML');
		dataAdapter.setSOCD('(O:user=userDataTable,I:user=userDataTable,O:level=levelDataTable)');
		dataSet.setDataAdapter(dataAdapter);
		createOption = function(columnName, additionalOption) {
			var mergedOption, option;
			mergedOption = new Object();
			option = {
				xgInitOption: {
					xgDataSet: dataSet,
					xgDataTable: 'userDataTable'
				},
				xgBindOption: {
					mode: 'write',
					immediately: true,
					bind: {
						type: 'column',
						column: columnName
					}
				},
				width: 180,
				height: 30
			};
			$.extend(true, mergedOption, option, additionalOption);
			return mergedOption;
		};
		$grid = $('.grid');
		new XgUIButton('default', '.search', {
			template: 'info',
			width: 150,
			height: 40
		}).on('click', function() {
			return dataSet.select('userDataTable');
		});
		new XgUIButton('default', '.update', {
			template: 'warning',
			width: 150,
			height: 40
		}).on('click', function() {
			return dataSet.update('userDataTable');
		});
		new XgUIButton('default', '.add', {
			template: 'danger',
			width: 150,
			height: 40
		}).on('click', function() {
			$grid.xgGrid('addrow', null, {}, 'top');
			return $grid.xgGrid({
				selectedrowindex: 0
			});
		});
		return dataSet.select('levelDataTable', function() {
			return dataSet.select('userDataTable', function() {
				var $checkMarriage, $comboLevel, $inputAge, $inputId, $inputLastLoginDate, $inputName, $inputNo, $inputPassword, $inputRating, $inputRegDate, $radioGenderFemale, $radioGenderMale;
				$grid.xgGrid({
					xgInitOption: {
						xgDataSet: dataSet,
						xgDataTable: 'userDataTable',
						columns: [{
							datafield: 'NO',
							text: '번호',
							width: 80,
							align: 'center',
							cellsalign: 'right'
						}, {
							datafield: 'ID',
							text: '아이디',
							width: 100,
							align: 'center',
							cellsalign: 'center'
						}, {
							datafield: 'NAME',
							text: '이름',
							width: 80,
							align: 'center',
							cellsalign: 'center'
						}, {
							datafield: 'AGE',
							text: '나이',
							width: 70,
							align: 'center',
							cellsalign: 'center',
							columntype: 'numberinput',
							initeditor: function(row, cellvalue, editor) {
								editor.xgNumberInput({
									max: 120,
									min: 1,
									decimalDigits: 0
								});
								return editor.xgNumberInput('val', cellvalue);
							},
							cellsrenderer: function(rowIdx, datafield, cellValue) {
								var displayText;
								if (cellValue) {
									displayText = cellValue + ' 세';
									return "<div style='text-overflow: ellipsis; overflow: hidden; padding-bottom: 2px; text-align: center; margin-top: 4px;'>" + displayText + "</div>";
								} else {
									return '';
								}
							}
						}, {
							datafield: 'GENDER',
							text: '성별',
							width: 70,
							align: 'center',
							cellsalign: 'center'
						}, {
							datafield: 'LEVEL',
							text: '등급',
							width: 70,
							align: 'center',
							cellsalign: 'center'
						}, {
							datafield: 'MARRIAGE',
							text: '결혼',
							width: 70
						}, {
							datafield: 'REG_DATE',
							text: '등록일',
							width: 100,
							align: 'center'
						}]
					},
					xgBindOption: {
						template: 'all',
						useEditor: {
							LEVEL: {
								columntype: 'dropdownlist',
								source: dataSet.makeListTypeSource('levelDataTable', 'LABEL', 'CODE')
							},
							GENDER: {
								columntype: 'dropdownlist',
								source: [{
									text: '남성',
									value: 0
								}, {
									text: '여성',
									value: 1
								}]
							},
							REG_DATE: {
								columntype: 'datetimeinput'
							},
							MARRIAGE: {
								columntype: 'checkbox',
								checkedAll: true
							}
						},
						groupSubsum: {
							AGE: ['sum', 'avg', 'min', 'max', 'count']
						}
					},
					width: 670,
					height: 350,
					sortable: true,
					editable: true,
					editmode: 'dblclick',
					keyboardnavigation: true,
					columnsmenu: true,
					groupable: true,
					pageable: true,
					enablekeyboarddelete: true,
					filterable: true
				});
				$inputNo = new XgUIInput('.no', createOption('NO', {
					placeHolder: '사용자 번호를 입력하세요.'
				}));
				$inputId = new XgUIInput('.id', createOption('ID', {
					placeHolder: '아이디를 입력하세요.'
				}));
				$inputName = new XgUIInput('.name', createOption('NAME', {
					placeHolder: '이름을 입력하세요.'
				}));
				$inputPassword = new XgUIPasswordInput('.password', createOption('PASSWORD', {
					placeHolder: '비밀번호를 입력하세요.'
				}));
				$inputAge = new XgUIInput('.age', createOption('AGE', {
					placeHolder: '나이를 입력하세요.'
				}));
				$inputName = new XgUIInput('.addr', createOption('ADDR', {
					placeHolder: '주소를 입력하세요.',
					width: 493
				}));
				$comboLevel = new XgUIComboBox('.level', createOption('LEVEL', {
					placeHolder: '등급을 입력 또는 선택하세요.',
					source: dataSet.makeListTypeSource('levelDataTable', 'LABEL', 'CODE'),
					displayMember: 'text',
					valueMember: 'value'
				}));
				$inputRating = new XgUIInput('.rating', createOption('RATING', {
					placeHolder: '평점을 입력하세요'
				}));
				$radioGenderMale = new XgUIRadioButton('.male', createOption('GENDER', {
					width: 88,
					groupName: 'GENDER',
					animationShowDelay: 0,
					animationHideDelay: 0,
					xgBindOption: {
						value: 0
					}
				}));
				$radioGenderFemale = new XgUIRadioButton('.female', createOption('GENDER', {
					width: 88,
					groupName: 'GENDER',
					animationShowDelay: 0,
					animationHideDelay: 0,
					xgBindOption: {
						value: 1
					}
				}));
				$checkMarriage = new XgUICheckBox('.marriage', createOption('MARRIAGE', {
					boxSize: 20,
					animationShowDelay: 0,
					animationHideDelay: 0
				}));
				$inputRegDate = new XgUIDateTimeInput('.reg-date', createOption('REG_DATE', {
					culture: 'ko-KR',
					formatString: 'yyyy-MM-dd',
					dayNameFormat: 'firstLetter'
				}));
				return $inputLastLoginDate = new XgUIDateTimeInput('.last-login', createOption('LAST_LOGIN_TIME', {
					culture: 'ko-KR',
					formatString: 'yyyy-MM-dd',
					dayNameFormat: 'firstLetter'
				}));
			});
		});
	});

}).call(this);
