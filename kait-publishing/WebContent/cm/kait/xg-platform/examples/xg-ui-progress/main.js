(function() {
	$(document).ready(function() {
		var $xgProgress2;
		new XgUIProgress('.xg-progress-module-1', {
			width: 350,
			height: 40,
			value: 50
		});
		$xgProgress2 = new XgUIProgress('.xg-progress-module-2', {
			width: 40,
			height: 350,
			value: 50,
			showText: true,
			orientation: 'vertical'
		});
		$xgProgress2.on('valueChanged', function(event) {
			return $("#log-module-2").html('Event : The new value is: ' + event.currentValue);
		});
		$('.xg-progress-plugin-1').xgProgress({
			width: 350,
			height: 40,
			value: 50,
			layout: 'reverse',
			showText: true
		});
		$('.xg-progress-plugin-1').on('complete', function(event) {
			return $("#log-plugin-1").html('Event : The complete event is triggered : ' + event.currentValue);
		});
		$('.xg-progress-data-api-1').on('invalidValue', function(event) {
			return $("#log-data-api-1").html('Event : The invalidvalue event is triggered : ' + event.currentValue);
		});
		$('.xg-set-value').xgButton({
			height: 30,
			width: 120
		});
		$('.xg-set-value').on('click', function() {
			var value;
			value = $('.xg-progress-module-2').xgProgress('value');
			$('.xg-progress-module-2').xgProgress({
				value: value + 10
			});
			value = $('.xg-progress-plugin-1').xgProgress('value');
			$('.xg-progress-plugin-1').xgProgress({
				value: value + 10
			});
			value = $('.xg-progress-data-api-1').xgProgress('value');
			$('.xg-progress-data-api-1').xgProgress({
				value: value + 10
			});
		});
	});

}).call(this);
