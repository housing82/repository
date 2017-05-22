(function() {
	$(document).ready(function() {
		new XgUINumberInput('.xg-number-input-module-1', {
			height: 25,
			width: 200,
			max: 10,
			min: -10,
			decimalDigits: 0
		});
		new XgUINumberInput('.xg-number-input-module-2', {
			height: 25,
			width: 200,
			inputMode: 'simple'
		}).on('valueChanged', function(evt) {
			return $('.xg-number-input-module-3').text('value changed : ' + evt.args.value);
		});
		$('.xg-number-input-plugin-1').xgNumberInput({
			height: 25,
			width: 200,
			max: 10,
			min: -10,
			decimalDigits: 0
		});
		return $('.xg-number-input-plugin-2').xgNumberInput({
			height: 25,
			width: 200,
			inputMode: 'simple'
		}).on('valueChanged', function(evt) {
			return $('.xg-number-input-plugin-3').text('value changed : ' + evt.args.value);
		});
	});

}).call(this);
