(function() {
	function _generatedata() {
		var data = new Array;
		var firstNames = ['Andrew', 'Nancy', 'Shelley', 'Regina', 'Yoshi', 'Antoni', 'Mayumi', 'Ian', 'Peter', 'Lars', 'Petra', 'Martin', 'Sven', 'Elio', 'Beate', 'Cheryl', 'Michael', 'Guylene'];
		var lastNames = ['Fuller', 'Davolio', 'Burke', 'Murphy', 'Nagase', 'Saavedra', 'Ohno', 'Devling', 'Wilson', 'Peterson', 'Winkler', 'Bein', 'Petersen', 'Rossi', 'Vileid', 'Saylor', 'Bjorn', 'Nodier'];
		var productNames = ['Black Tea', 'Green Tea', 'Caffe Espresso', 'Doubleshot Espresso', 'Caffe Latte', 'White Chocolate Mocha', 'Cramel Latte', 'Caffe Americano', 'Cappuccino', 'Espresso Truffle', 'Espresso con Panna', 'Peppermint Mocha Twist'];
		var priceValues = ['2.25', '1.5', '3.0', '3.3', '4.5', '3.6', '3.8', '2.5', '5.0', '1.75', '3.25', '4.0'];
		var i = 0;
		while (i < 1000) {
			row = {};
			productindex = Math.floor(Math.random() * productNames.length);
			price = parseFloat(priceValues[productindex]);
			quantity = 1 + Math.round(Math.random() * 10);
			row['firstname'] = firstNames[Math.floor(Math.random() * firstNames.length)];
			row['lastname'] = lastNames[Math.floor(Math.random() * lastNames.length)];
			row['productname'] = productNames[productindex];
			row['price'] = price;
			row['quantity'] = quantity;
			row['total'] = price * quantity;
			data[i] = row;
			i++;
		}
		return data;
	}

	function _generatecolumns() {
		function __runsuppress(row, column, value, data) {

			//console.log("runsuppress");
			var prevIndex = $('.xg-grid-plugin').xgGrid("getrowboundindex", data.visibleindex - 1);
			var prevRow = $('.xg-grid-plugin').xgGrid("getrowdata", prevIndex);

			var nextIndex = $('.xg-grid-plugin').xgGrid("getrowboundindex", data.visibleindex + 1);
			var nextRow = $('.xg-grid-plugin').xgGrid("getrowdata", nextIndex);

			var instance = $('.xg-grid-plugin').xgGrid('getInstance');
			var vIndex = instance.visiblerows.length;
			var element = instance.table[0].rows[vIndex].cells[0];

			// if (!prevRow && !!nextRow && nextRow[column] == value) {
			//     // 시작일 경우 (첫행의 경우)
			//     // $(element).css("height", "50");
			//     return "xg-suppress-first";
			// } else if (!!prevRow && prevRow[column] != value && !!nextRow && nextRow[column] == value) {
			//     // 시작일 경우 (첫행이 아닌 경우)
			//     // $(element).css("height", "50");
			//     return "xg-suppress-first";
			// }

			// $(element).css("height", "inherit");

			if (!!nextRow && nextRow[column] == value) {
				if (!prevRow || prevRow[column] != value) {
					// first
					return "xg-suppress-first";
				} else {
					return "xg-suppress-cell";
				}
			}

			if (!!prevRow && prevRow[column] == value) {
				if (!nextRow || nextRow[column] != value) {
					// last
					return "xg-suppress-last";
				} else {
					return "xg-suppress-cell";
				}
			}
			// XgPlatform.XgLogger.error(function () {
			//     return "This is Jm's Message "+row;
			// });
		}

		var lastNames = ['Fuller', 'Davolio', 'Burke', 'Murphy', 'Nagase', 'Saavedra', 'Ohno', 'Devling', 'Wilson', 'Peterson', 'Winkler', 'Bein', 'Petersen', 'Rossi', 'Vileid', 'Saylor', 'Bjorn', 'Nodier'];
		return [{
			text: 'First Name',
			datafield: 'firstname',
			width: 100,
			//suppress: true,
			//cellclassname: customclass,
			cellclassname: __runsuppress,
			cellsrenderer: _cellsrender
		}, {
			text: 'Last Name',
			datafield: 'lastname',
			width: 100,
			columntype: 'dropdownlist',
			initeditor: function(row, cellvalue, editor) {
				return editor.jqxDropDownList({
					source: lastNames,
					autoDropDownHeight: true
				});
			}
		}, {
			text: 'Product',
			datafield: 'productname',
			width: 180,
			columntype: 'dropdownlist'
		}, {
			text: 'Quantity',
			datafield: 'quantity',
			width: 80,
			cellsalign: 'right',
			columntype: 'dropdownlist',
			initeditor: function(row, cellvalue, editor) {
				return editor.jqxDropDownList({
					source: ['1', '2', '3', '4', '5', '6'],
					autoDropDownHeight: true
				});
			}
		}, {
			text: 'Unit Price',
			datafield: 'price',
			width: 90,
			cellsalign: 'right',
			cellsformat: 'c2'
		}, {
			text: 'Total',
			datafield: 'total',
			width: 100,
			cellsalign: 'right',
			cellsformat: 'c2'
		}];
	}

	function _initRowDetails(index, parentElement, element, rowinfo) {
		var details = $($(parentElement).children()[0]);
		details.html("Details: " + index);
	}

	function _cellsrender(row, column, value, defaultRender, column, rowData) {
		console.log("_cellsrender " + value);
		if (row == "3") {
			return defaultRender;
			//return "test";
		}
	}

	function _rendered() {
		console.log("rendered");
	}

	function _rendergridrows() {
		console.log("_rendergridrows");
	}

	function _pagerrenderer() {
		console.log("pagerrenderer");
	}
	$(document).ready(function() {
		var source = {
			localdata: _generatedata(),
			datatype: 'array'
		};
		var dataAdapter = new $.jqx.dataAdapter(source, {
			loadComplete: function(data) {},
			loadError: function(xhr, status, error) {}
		});
		var columns = _generatecolumns();

		var $xgGrid = $('.xg-grid-plugin').xgGrid({
			width: 730,
			height: 340,
			sortable: true,
			editable: true,
			rendered: _rendered,
			rendergridrows: _rendergridrows,
			pagerrenderer: _pagerrenderer,
			// rowDetails: true,
			// initRowDetails: _initRowDetails,
			// rowdetailstemplate: {
			//     rowdetails: "<div style='margin: 10px;'><ul style='margin-left: 30px;'><li class='title'></li><li>Notes</li></ul><div class='information'></div><div class='notes'></div></div>",
			//     rowdetailsheight: 100,
			//     rowdetailshidden: false
			// },
			xgInitOption: {
				source: dataAdapter,
				columns: columns
			}
		});
		return true;
	});

}).call(this);
