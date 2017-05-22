(function() {
	$(document).ready(function() {
		var source;
		source = ['Affogato', 'Americano', 'Bicerin', 'Breve', 'Café Bombón', 'Café au lait', 'Caffé Corretto', 'Café Crema', 'Caffé Latte'];
		new XgUIComboBox('.xg-combobox-module-1', {
			source: source,
			renderSelectedItem: function(idx, item) {
				return '( ' + idx + ' ) ' + item.label;
			}
		}).xgComboBox('disableAt', 5);
		new XgUIComboBox('.xg-combobox-module-2', {
			source: source,
			width: 250,
			height: 30
		}).on('select', function(evt) {
			var item;
			item = $(this).xgComboBox('getItem', evt.args.index);
			return $('span.xg-combobox-module-3').text('selected item / index : ' + item.label + ' / ' + item.index);
		}).xgComboBox('disableAt', 5);
		$('.xg-combobox-plugin-1').xgComboBox({
			source: source,
			renderSelectedItem: function(idx, item) {
				return '( ' + idx + ' ) ' + item.label;
			}
		}).xgComboBox('disableAt', 5);
		return $('.xg-combobox-plugin-2').xgComboBox({
			source: source,
			width: 250,
			height: 30
		}).on('select', function(evt) {
			var item;
			item = $(this).xgComboBox('getItem', evt.args.index);
			return $('span.xg-combobox-plugin-3').text('selected item / index : ' + item.label + ' / ' + item.index);
		}).xgComboBox('disableAt', 5);
	});

}).call(this);
