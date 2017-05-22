(function() {
	$(document).ready(function() {
		var source;
		source = [{
			label: "Item 1",
			expanded: true,
			items: [{
				label: "Item 1.1"
			}, {
				label: "Item 1.2",
				selected: true
			}, {
				label: "Item 1.3",
				selected: false
			}, {
				label: "Item 1.4"
			}]
		}, {
			label: "Item 2"
		}, {
			label: "Item 3"
		}, {
			label: "Item 4",
			items: [{
				label: "Item 4.1"
			}, {
				label: "Item 4.2",
				items: [{
					label: "Item 4.2.1"
				}, {
					label: "Item 4.2.2",
					selected: true
				}, {
					label: "Item 4.2.3",
					selected: false
				}, {
					label: "Item 4.2.4"
				}]
			}]
		}, {
			label: "Item 5"
		}, {
			label: "Item 6"
		}, {
			label: "Item 7"
		}];
		new XgUITree('.xg-tree-module-1', {
			width: 450,
			height: 150
		});
		new XgUITree('.xg-tree-module-2', {
			source: source,
			width: 450,
			height: 150
		}).on('expand', function(event) {
			var args, item;
			args = event.args;
			item = $(this).xgTree('getItem', args.element);
			return $('#log-module').text("expand : " + item.label);
		});
		$('.xg-tree-plugin-1').xgTree({
			width: 400,
			height: 150
		});
		$('.xg-tree-plugin-2').xgTree({
			source: source,
			width: 400,
			height: 150,
			checkboxes: true
		}).on('checkChange', function(event) {
			var checkString;
			if (event.args.checked) {
				checkString = "checked : ";
			} else {
				checkString = "unchecked : ";
			}
			return $('#log-plugin').text("" + checkString + $(event.args.element).text());
		});
		return $('.xg-tree-data-api-1').xgTree({
			width: 350,
			height: 200
		});
	});

}).call(this);
