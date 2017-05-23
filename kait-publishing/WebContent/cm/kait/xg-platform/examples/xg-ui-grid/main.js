(function() {
	$(document).ready(function() {
		var $xgGrid, $xgGridModule, $xgGridModule2, columns, data, dataAdapter, firstNames, i, lastNames, price, priceValues, productNames, productindex, quantity, row, source;
		data = new Array;
		firstNames = ['Andrew', 'Nancy', 'Shelley', 'Regina', 'Yoshi', 'Antoni', 'Mayumi', 'Ian', 'Peter', 'Lars', 'Petra', 'Martin', 'Sven', 'Elio', 'Beate', 'Cheryl', 'Michael', 'Guylene'];
		lastNames = ['Fuller', 'Davolio', 'Burke', 'Murphy', 'Nagase', 'Saavedra', 'Ohno', 'Devling', 'Wilson', 'Peterson', 'Winkler', 'Bein', 'Petersen', 'Rossi', 'Vileid', 'Saylor', 'Bjorn', 'Nodier'];
		productNames = ['Black Tea', 'Green Tea', 'Caffe Espresso', 'Doubleshot Espresso', 'Caffe Latte', 'White Chocolate Mocha', 'Cramel Latte', 'Caffe Americano', 'Cappuccino', 'Espresso Truffle', 'Espresso con Panna', 'Peppermint Mocha Twist'];
		priceValues = ['2.25', '1.5', '3.0', '3.3', '4.5', '3.6', '3.8', '2.5', '5.0', '1.75', '3.25', '4.0'];
		i = 0;
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
		source = {
			localdata: data,
			datatype: 'array'
		};
		dataAdapter = new $.jqx.dataAdapter(source, {
			loadComplete: function(data) {},
			loadError: function(xhr, status, error) {}
		});
		columns = [{
			text: 'First Name',
			datafield: 'firstname',
			width: 100
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
		$xgGridModule = new XgUIGrid('.xg-grid-module', {
			width: 500,
			height: 300,
			xgInitOption: {
				source: dataAdapter,
				columns: columns
			}
		});
		$xgGridModule2 = new XgUIGrid('.xg-grid-module-2', {
			width: 500,
			height: 300,
			sortable: true,
			groupable: true,
			xgInitOption: {
				source: dataAdapter,
				columns: columns
			}
		});
		return $xgGrid = $('.xg-grid-plugin').xgGrid({
			width: 730,
			height: 340,
			sortable: true,
			editable: true,
			xgInitOption: {
				source: dataAdapter,
				columns: columns
			}

			/*
      
						@remark 본예제에서 xgDataSet, xgDataTable의 옵션을 사용하기 위해서는 서버에서 데이터를 가져와
						연동하여야 하는데, 서버구축에 대한 리소스가 필요함으로 아직은 해당 옵션은 사용하지 않는다.
      
						@xgOption - required : 최초 그리드 초기화시에 xgDataSet, xgDataTable과의 연동을 원한다면
						하기 옵션( xgInitOption )을 반드시 넣어야한다. xgDataSet은 그리드 초기화전에 최초 1회 서버에서
						데이터를 가져온 상태여야 한다.
						
						@xgInitOption - 최초 그리드 초기화 시에 적용될 추가 옵션, @xgOption 3개의 옵션과 같이 넘어와야한다.
						xgInitOption: object
							xgDataSet: object or string		# xgDataSet ref 또는 name
							xgDataTable: object or string 	# xgDataTable ref 또는 name
							columns: array 					# 컬럼 정보 배열 ( 생략시 자동생성 한다 )
							source: object 					# 로우 정보 dataAdapter 오브젝트 ( 생략시 자동생성 한다 )
      
						@xgBindOption - optional : 탬플릿 기능에 관한 옵션이며 필수는 아니지만 기본적인
						xgDataSet, xgDataTable과의 연계기능을 활용하려면 사용토록 권장한다.
      
						xgBindOption:
							template: string 			# 템플릿 설정 : [ 'view', 'all' ] 만 허용
							contextMenu: 				# 우클릭 시 파업 메뉴
								rowControl: boolean 	# 로우 추가,수정,삭제 등의 메뉴기능
								export: boolean 		# excel, json 파일 저장 기능
							locale: object 				# 언어셋 설정 기능
							cellEdit: boolean 			# key 또는 sequence 컬럼에 대한 에티딩 설정 ( 추가시에는 수정가능하나 서버와 동기화 된 데이터의 경우 수정 불가 )
							cellValidation: boolean 	# 서버에서 넘겨준 컬럼정보를 기준으로 validation 수행
							columnFormat: boolean 		# 컬럼의 값 포메팅 기능
							showRowStatus: boolean 		# 로우의 상태값 컬럼 표시 여부
							useEditor: boolean | object	# number, date, password, dropdown, checkbox 형식의 컬럼의 경우 해당 inputControl 사용여부
														 * object타입으로 전달될 경우 하기의 양식에 맞춰야한다.
								datafield:				# 컬럼 이름(datafield)
									columntype: type    # [ 'numberinput', 'datetimeinput', 'dropdownlist', 'checkbox', 'passwordinput' ] 중 하나
      
								datafield:
									columntype: 'dropdownlist'
									source: array       # [ 생략가능 ] 배열안의 데이터는 text, value 쌍으로 이루어져야한다. eg.) [ { text: 'aaa', value: 1 } ]
      
								datafield:
									columntype: 'checkbox'
									checkedAll: boolean # [ 생략가능 ] columntype이 checkbox일때
      
							groupSubsum:
      
								datafield: array        # array안의 값은 [ 'sum', 'avg', 'min', 'max', 'count' ] 안에 포함된 값이여야 한다.
			 */
		});

		/*
			@remark
    
			data-api 방식으로 옵션을 전달한 경우 literal 자료형의 값만 옵션 지정이 가능하다.
			xgDataSet, xgDataTable의 옵션 사용 시 객체의 참조형이 전달되어야 함으로 data-api옵션
			만으로는 사용이 불가능 하다.
		 */
	});

}).call(this);
