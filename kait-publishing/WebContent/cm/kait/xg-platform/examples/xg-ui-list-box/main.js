(function() {
	$(document).ready(function() {
		var source;
		source = ["Café Bombón", "Café au lait", "Caffé Corretto", "Café Crema", "Caffé Latte", "Caffé macchiato", "Café mélange"];
		new XgUIListBox('.xg-list-box-module-1', {
			source: source,
			multiple: true
		});
		new XgUIListBox('.xg-list-box-module-2', {
			source: source,
			width: 300,
			height: 100
		}).on('select', function(event) {
			return $('#log-module').text("selected : item " + event.args.item.label + ", index " + event.args.index);
		}).xgListBox('insertAt', 'Breve', 3);
		$('.xg-list-box-plugin-1').xgListBox({
			source: source,
			allowDrag: true,
			allowDrop: true,
			dropAction: 'copy'
		}).xgListBox('disableAt', 0);
		$('.xg-list-box-plugin-2').xgListBox({
			source: source,
			width: 300,
			height: 100
		}).on('unselect', function(event) {
			return $('#log-plugin').text("unselected : index " + event.args.index);
		});
		return $('.xg-list-box-data-api-1').xgListBox({
			source: source,
			checkboxes: true,
			hasThreeStates: true
		});
	});

}).call(this);
