(function() {
	$(document).ready(function() {
		new XgUIMaskedInput('.xg-masked-input-module-1', {
			height: 25,
			width: 200,
			mask: "###-###"
		});
		new XgUIMaskedInput('.xg-masked-input-module-2', {
			height: 25,
			width: 200,
			mask: "[0][1][016789]-####-####",
			textAlign: 'right'
		}).on('valueChanged', function(event) {
			return $('#log-module').html('Value changed : ' + event.args.value);
		});
		$('.xg-masked-input-plugin-1').xgMaskedInput({
			height: 25,
			width: 200,
			mask: "9999-9999-9999-9999"
		});
		$('.xg-masked-input-plugin-2').xgMaskedInput({
			height: 25,
			width: 200,
			mask: "AAAA-AAAA-LLLL-LLLL"
		}).on('change', function(event) {
			return $('#log-plugin').html('Change : ' + event.args.value);
		});
		$('.xg-masked-input-data-api-2').on('valueChanged', function(event) {
			return $('#log-dataAPI').html('Value changed : ' + event.args.value);
		});
	});

}).call(this);
