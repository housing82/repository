(function() {
	require.config({
		baseUrl: '../../',
		paths: {
			'jquery': 'lib/jquery/dist/jquery.min',
			'json3': 'lib/json3/lib/json3.min',
			'swidgets': 'lib/swidgets/swidgets',
			'XgCommon': 'xg-common',
			'XgDataAdapter': 'xg-data-adapter',
			'XgDataTable': 'xg-data-table',
			'XgDataSet': 'xg-data-set',
			'XgUICommon': 'xg-ui-common',
			'XgUIButton': 'xg-ui-button',
			'XgUICheckBox': 'xg-ui-check-box',
			'XgUIExcelButton': 'xg-ui-excel-button',
			'XgUIGrid': 'xg-ui-grid',
			'XgUIChart': 'xg-ui-chart',
			'XLSX': 'lib/js-xlsx/dist/xlsx.full.min',
			'FileSaver': 'lib/file-saver/FileSaver'
		}
	});

	define('dashboard', ['jquery', 'XgDataAdapter', 'XgDataTable', 'XgDataSet'], function($, XgDataAdapter, XgDataTable, XgDataSet) {
		var bankDataSet, numberWithCommas;
		numberWithCommas = function(num) {
			if (isNaN(num)) {
				return num;
			}
			return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
		};
		bankDataSet = new XgDataSet('bankDataSet');
		bankDataSet.setDataAdapter(new XgDataAdapter().setDataType('JSON').setSOCD('(I:chart=bankDataTable,O:chart=bankDataTable)'));
		bankDataSet.addDataTable(new XgDataTable('bankDataTable', {
			preventXSS: true,
			url: {
				select: '/XG/jsp/chartSelect.jsp',
				update: '/XG/jsp/chartUpdate.jsp'
			}
		}));
		return bankDataSet.select('bankDataTable', function() {
			return require(['XgUICommon', 'XgUIGrid', 'XgUIChart', 'XgUIButton', 'XgUIExcelButton'], function(XgUICommon, XgUIGrid, XgUIChart, XgUIButton, XgUIExcelButton) {
				new XgUIButton('default', 'button.search', {
					width: 100,
					height: 30,
					template: 'info'
				}).on('click', function() {
					return bankDataSet.select('bankDataTable');
				});
				new XgUIButton('default', 'button.update', {
					width: 100,
					height: 30,
					template: 'danger'
				}).on('click', function() {
					return bankDataSet.update('bankDataTable');
				});
				new XgUIExcelButton('.import', {
					xgInitOption: {
						type: 'import',
						xgDataSet: 'bankDataSet',
						xgDataTable: 'bankDataTable',
						xgUIWorkerUrl: '../../',
						xlsxUrl: 'lib/js-xlsx/dist'
					},
					xgBindOption: {
						start: function() {
							return $('.grid').xgGrid('showloadelement');
						},
						end: function() {
							return $('.grid').xgGrid('hideloadelement');
						}
					},
					width: 100,
					height: 30,
					template: 'success'
				});
				new XgUIExcelButton('.export', {
					xgInitOption: {
						type: 'export',
						xgDataSet: 'bankDataSet',
						xgDataTable: 'bankDataTable',
						xgUIWorkerUrl: '../../',
						xlsxUrl: 'lib/js-xlsx/dist',
						fileName: 'dashboard-export'
					},
					xgBindOption: {
						start: function() {
							return $('.grid').xgGrid('showloadelement');
						},
						end: function() {
							return $('.grid').xgGrid('hideloadelement');
						}
					},
					width: 100,
					height: 30,
					template: 'warning'
				});
				$('.grid').xgGrid({
					xgInitOption: {
						xgDataSet: 'bankDataSet',
						xgDataTable: 'bankDataTable',
						columns: [{
							datafield: 'MONTH',
							text: '기준 월',
							width: 80,
							align: 'center',
							cellsalign: 'center',
							editable: false
						}, {
							datafield: 'DEPOSIT',
							text: '예치금',
							width: 110,
							align: 'center',
							cellsalign: 'right',
							cellsformat: 'c'
						}, {
							datafield: 'TAX',
							text: '세금',
							width: 110,
							align: 'center',
							cellsalign: 'right',
							cellsformat: 'c'
						}]
					},
					xgBindOption: {
						template: 'all',
						contextMenu: false
					},
					editable: true,
					editmode: 'dblclick',
					width: '100%',
					height: 300,
					keyboardnavigation: true
				});
				$('.grid').xgGrid('localizestrings', XgUICommon.getLocalization('ko'));
				new XgUIChart('.row-column-chart', {
					xgInitOption: {
						xgDataSet: 'bankDataSet',
						xgDataTable: 'bankDataTable'
					},
					xgBindOption: {
						bind: {
							type: 'row'
						}
					},
					title: '월별 예치금 & 세금 현황',
					description: '2014년 기준(column chart)',
					width: '100%',
					height: 300,
					xAxis: {
						visible: false
					},
					valueAxis: {
						minValue: -250000,
						maxValue: 2500000,
						formatFunction: function(value) {
							return numberWithCommas(value) + ' 원';
						}
					},
					toolTipFormatFunction: function(value, itemIndex, serie) {
						var deposit, item, source, tax;
						source = $('.row-column-chart').xgChart('source');
						item = source.records[itemIndex];
						deposit = numberWithCommas(item.DEPOSIT);
						tax = numberWithCommas(item.TAX);
						return "<div style='text-align:left'><b>기준 월 : " + item.MONTH + "</b><br/><br/>예치금 : " + deposit + " 원<br/>세금 : " + tax + " 원</div>";
					},
					seriesGroups: [{
						type: 'column',
						bands: [{
							minValue: 2000000,
							maxValue: 2000000,
							fillColor: 'red',
							lineWidth: 3,
							dashStyle: '2,2'
						}],
						series: [{
							dataField: 'DEPOSIT',
							displayText: '예치금'
						}, {
							dataField: 'TAX',
							displayText: '세금'
						}]
					}]
				});
				new XgUIChart('.column-pie-chart', {
					xgInitOption: {
						xgDataSet: 'bankDataSet',
						xgDataTable: 'bankDataTable'
					},
					xgBindOption: {
						bind: {
							type: 'column',
							column: 'DEPOSIT'
						}
					},
					title: '월별 예치금',
					description: '2014년 기준(pie chart)',
					width: '100%',
					height: 300,
					showLegend: false,
					showToolTips: false,
					seriesGroups: [{
						type: 'pie',
						showLabels: true,
						formatFunction: function(value) {
							if (isNaN(value)) {
								return value;
							}
							return (value / 1000) + ' k';
						},
						series: [{
							dataField: 'DEPOSIT',
							displayText: '예치금'
						}]
					}]
				});
				$('.column-donut-chart').xgChart({
					xgInitOption: {
						xgDataSet: 'bankDataSet',
						xgDataTable: 'bankDataTable'
					},
					xgBindOption: {
						bind: {
							type: 'column',
							column: 'TAX'
						}
					},
					title: '월별 세금 현황',
					description: '2014년 기준(donut chart)',
					width: '100%',
					height: 300,
					showLegend: true,
					seriesGroups: [{
						type: 'donut',
						toolTipFormatFunction: function(value, itemIndex, serie) {
							var deposit, item, source, tax;
							source = $('.column-donut-chart').xgChart('source');
							item = source.records[itemIndex];
							deposit = numberWithCommas(item.DEPOSIT);
							tax = numberWithCommas(item.TAX);
							return "<div style='text-align:left'><b>기준 월 : " + item.MONTH + "</b><br/><br/>예치금 : " + deposit + " 원<br/>세금 : " + tax + " 원</div>";
						},
						series: [{
							dataField: 'TAX',
							displayText: 'TAX',
							labelRadius: 120,
							initalAngle: 90,
							radius: 60,
							innerRadius: 30,
							centerOffset: 0,
							legendFormatFunction: function(value) {
								return (numberWithCommas(value)) + ' 원';
							}
						}]
					}]
				});
				$('.full-line-chart').xgChart({
					xgInitOption: {
						xgDataSet: 'bankDataSet',
						xgDataTable: 'bankDataTable'
					},
					xgBindOption: {
						bind: {
							type: 'column',
							column: 'DEPOSIT'
						}
					},
					title: '월별 예치금 현황',
					description: '2014년 기준(line chart)',
					width: '100%',
					height: 300,
					showLegend: true,
					xAxis: {
						dataField: 'MONTH',
						formatFunction: function(value) {
							var date, month;
							date = new Date(value);
							month = date.getMonth() + 1;
							if (month < 10) {
								month = '0' + month;
							}
							return date.getFullYear() + '-' + month;
						},
						title: {
							text: '기준월'
						}
					},
					valueAxis: {
						title: {
							text: '예치금( 단위 : 원 )'
						},
						formatFunction: function(value) {
							return numberWithCommas(value);
						}
					},
					seriesGroups: [{
						type: 'line',
						series: [{
							dataField: 'DEPOSIT',
							displayText: '예치금',
							toolTipFormatFunction: function(value, itemIndex, serie) {
								var deposit, item, month, source;
								source = $('.full-line-chart').xgChart('source');
								item = source.records[itemIndex];
								month = item.MONTH;
								deposit = numberWithCommas(item.DEPOSIT) + ' 원';
								return "<div style='text-align:left'>기준 월 : " + month + "<br/>" + deposit + "</div>";
							}
						}, {
							dataField: 'TAX',
							displayText: '세금',
							toolTipFormatFunction: function(value, itemIndex, serie) {
								var item, month, source, tax;
								source = $('.full-line-chart').xgChart('source');
								item = source.records[itemIndex];
								month = item.MONTH;
								tax = numberWithCommas(item.TAX) + ' 원';
								return "<div style='text-align:left'>기준 월 : " + month + "<br/>" + tax + "</div>";
							}
						}]
					}]
				});
				return $(window).trigger('resize');
			});
		});
	});

}).call(this);
