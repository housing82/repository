(function() {
	$(document).ready(function() {
		var $largeColumnGrid, $leftGrid, $lowColumnGrid, $rightGrid, gradeCodeList, largeDa, largeDs, largeDt, leftDa, leftDs, leftDt, lowDa, lowDs, lowDt, rightDa, rightDs, rightDt;
		leftDs = new XgDataSet('leftDs');
		rightDs = new XgDataSet('rightDs');
		lowDs = new XgDataSet('lowDs');
		largeDs = new XgDataSet('largeDs');
		leftDt = new XgDataTable('leftDt', {
			url: {
				select: '/XG/jsp/select.jsp',
				update: '/XG/jsp/tr.jsp'
			}
		});
		rightDt = new XgDataTable('rightDt', {
			url: {
				select: '/XG/jsp/chartSelect.jsp',
				update: '/XG/jsp/tr.jsp'
			}
		});
		lowDt = new XgDataTable('lowDt', {
			url: {
				select: '/XG/jsp/testSelect1.jsp'
			}
		});
		largeDt = new XgDataTable('largeDt', {
			url: {
				select: '/XG/jsp/testSelect2.jsp'
			}
		});
		leftDt.addColumnForGauce('EMP_NO', 'string', 8, 'normal');
		leftDt.addColumnForGauce('EMP_NM', 'string', 20, 'normal');
		leftDt.addColumnForGauce('BIRTH_DATE', 'string', 8, 'normal');
		leftDt.addColumnForGauce('GRADE_CD', 'string', 10, 'normal');
		leftDt.addColumnForGauce('DEPT_CD', 'int', 10, 'normal');
		leftDt.addColumnForGauce('LEVEL_CD', 'string', 10, 'normal');
		leftDt.addColumnForGauce('PASSWORD', 'string', 10, 'normal');
		leftDs.addDataTable(leftDt);
		rightDt.addColumnForGauce('MONTH', 'string', 8, 'normal');
		rightDt.addColumnForGauce('DEPOSIT', 'string', 15, 'normal');
		rightDt.addColumnForGauce('TAX', 'string', 15, 'normal');
		rightDs.addDataTable(rightDt);
		lowDt.addColumnForGauce('EMPL_NUMB', 'string', 8, 'normal');
		lowDt.addColumnForGauce('USER_IDEN', 'string', 15, 'normal');
		lowDt.addColumnForGauce('EMPL_NAME', 'string', 20, 'normal');
		lowDs.addDataTable(lowDt);
		largeDt.addColumnForGauce('EMPL_NUMB', 'string', 8, 'normal');
		largeDt.addColumnForGauce('USER_IDEN', 'string', 15, 'normal');
		largeDt.addColumnForGauce('EMPL_NAME', 'string', 20, 'normal');
		largeDt.addColumnForGauce('DEPT_CODE', 'string', 8, 'normal');
		largeDt.addColumnForGauce('POLV_CODE', 'string', 10, 'normal');
		largeDt.addColumnForGauce('POSI_CODE', 'string', 10, 'normal');
		largeDt.addColumnForGauce('SECR_NUMB', 'string', 12, 'normal');
		largeDt.addColumnForGauce('MAIL_ADDR', 'string', 50, 'normal');
		largeDt.addColumnForGauce('INPU_EMNU', 'string', 15, 'normal');
		largeDt.addColumnForGauce('INPU_DATE', 'string', 15, 'normal');
		largeDt.addColumnForGauce('CELL_NUMB', 'string', 20, 'normal');
		largeDt.addColumnForGauce('REQU_KIN', 'string', 1, 'normal');
		largeDt.addColumnForGauce('RESIGN_YN', 'string', 1, 'normal');
		largeDs.addDataTable(largeDt);
		leftDa = new XgDataAdapter('leftDa');
		leftDa.setUseFragment(true);
		leftDa.setXgPath('../../');
		leftDa.setDataType('JSON');
		leftDa.setSOCD('(O:leftDs=leftDt,I:leftDs=leftDt)');
		leftDs.setDataAdapter(leftDa);
		rightDa = new XgDataAdapter('rightDa');
		rightDa.setUseFragment(true);
		rightDa.setXgPath('../../');
		rightDa.setDataType('JSON');
		rightDa.setSOCD('(O:rightDs=rightDt,I:rightDs=rightDt)');
		rightDs.setDataAdapter(rightDa);
		lowDa = new XgDataAdapter('lowDa');
		lowDa.setUseFragment(true);
		lowDa.setXgPath('../../');
		lowDa.setDataType('JSON');
		lowDa.setSOCD('(O:lowDs=lowDt,I:lowDs=lowDt)');
		lowDs.setDataAdapter(lowDa);
		largeDa = new XgDataAdapter('largeDa');
		largeDa.setUseFragment(true);
		largeDa.setXgPath('../../');
		largeDa.setDataType('JSON');
		largeDa.setSOCD('(O:largeDs=largeDt,I:largeDs=largeDt)');
		largeDs.setDataAdapter(largeDa);
		$leftGrid = $('.left-grid');
		$rightGrid = $('.right-grid');
		$lowColumnGrid = $('.low-column-grid');
		$largeColumnGrid = $('.large-column-grid');
		gradeCodeList = [{
			text: '회장',
			value: 0
		}, {
			text: '사장',
			value: 1
		}, {
			text: '전무',
			value: 2
		}, {
			text: '상무',
			value: 3
		}, {
			text: '이사',
			value: 4
		}, {
			text: '부장',
			value: 5
		}, {
			text: '차장',
			value: 6
		}, {
			text: '과장',
			value: 7
		}, {
			text: '대리',
			value: 8
		}, {
			text: '주임',
			value: 9
		}, {
			text: '사원',
			value: 10
		}, {
			text: '계약직',
			value: 11
		}];
		$leftGrid.xgGrid({
			width: '50%',
			height: 300,
			editable: true,
			pageable: true,
			xgInitOption: {
				xgDataSet: leftDs,
				xgDataTable: 'leftDt'
			},
			xgBindOption: {
				template: 'all'
			}
		});
		$rightGrid.xgGrid({
			width: '50%',
			height: 300,
			editable: true,
			xgInitOption: {
				xgDataSet: 'rightDs',
				xgDataTable: rightDt
			},
			xgBindOption: {
				template: 'all'
			}
		});
		$lowColumnGrid.xgGrid({
			width: '50%',
			height: 300,
			pageable: true,
			xgInitOption: {
				xgDataSet: 'lowDs',
				xgDataTable: lowDt
			},
			xgBindOption: {
				template: 'all'
			}
		});
		$largeColumnGrid.xgGrid({
			width: '50%',
			height: 300,
			pageable: true,
			xgInitOption: {
				xgDataSet: 'largeDs',
				xgDataTable: largeDt
			},
			xgBindOption: {
				template: 'all'
			}
		});
		leftDs.select('leftDt', function() {
			console.log('left dataTable loaded.');
			return $leftGrid.xgGrid({
				xgBindOption: {
					useEditor: {
						'GRADE_CD': {
							columntype: 'dropdownlist',
							source: gradeCodeList
						}
					}
				}
			});
		});
		rightDs.select('rightDt', function() {
			return console.log('right dataTable loaded.');
		});
		lowDs.select('lowDt', function() {
			return console.log('low column dataTable loaded.');
		});
		return largeDs.select('largeDt', function() {
			return console.log('large column dataTable loaded.');
		});
	});

}).call(this);
