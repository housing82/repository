(function() {
	$(document).ready(function() {
		var da, ds, dt;
		ds = new XgDataSet('ds');
		dt = new XgDataTable('dt', {
			url: {
				select: '/XG/jsp/select.jsp'
			}
		});
		da = new XgDataAdapter('da');
		da.setSOCD('(O:DS_01=dt,I:DS_01=dt)');
		da.setDataType('XML');
		ds.addDataTable(dt);
		ds.setDataAdapter(da);
		return ds.select('dt', function() {
			$('#xg-grid').xgGrid({
				width: 500,
				height: 500,
				xgInitOption: {
					xgDataSet: ds,
					xgDataTable: 'dt'
				},
				xgBindOption: {
					template: 'view'
				}
			});
			return $('#xg-input').xgInput({
				width: 500,
				height: 30,
				placeHolder: '선택된 행의 EMP_NO 값이 바인딩 됩니다.',
				xgInitOption: {
					xgDataSet: ds,
					xgDataTable: 'dt'
				},
				xgBindOption: {
					mode: 'read',
					bind: {
						type: 'column',
						column: 'EMP_NO'
					}
				}
			});
		});
	});

}).call(this);
