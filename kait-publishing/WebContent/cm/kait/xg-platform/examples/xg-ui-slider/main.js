(function() {
	$(document).ready(function() {
		var $xgSlider2, fixHex, setColor;
		fixHex = function(hex) {
			if (hex.length < 2) {
				return '0' + hex;
			} else {
				return hex;
			}
		};
		setColor = function(rgb, value) {
			var code, color;
			if (rgb === 'red') {
				color = fixHex(Math.round(value).toString(16));
				code = "#" + color + "0000";
				return $('.colorRed').css('background-color', code);
			} else if (rgb === 'green') {
				color = fixHex(Math.round(value).toString(16));
				code = "#00" + color + "00";
				console.log("Green code: ", code);
				return $('.colorGreen').css('background-color', code);
			} else if (rgb === 'blue') {
				color = fixHex(Math.round(value).toString(16));
				code = "#0000" + color;
				console.log("Blue code: ", code);
				return $('.colorBlue').css('background-color', code);
			}
		};
		new XgUISlider('.xg-slider-module-1');
		$xgSlider2 = new XgUISlider('.xg-slider-module-2', {
			min: 0,
			max: 255,
			ticksFrequency: 25.5,
			value: 0,
			step: 25.5,
			mode: "fixed"
		});
		$xgSlider2.on('change', function(event) {
			var value;
			value = $xgSlider2.xgSlider('value');
			return setColor('red', value);
		});
		$('.xg-slider-plugin-1').xgSlider({
			min: 0,
			max: 255,
			ticksFrequency: 25.5,
			value: 0,
			step: 25.5,
			mode: "fixed",
			rtl: true
		});
		$('.xg-slider-plugin-1').on('change', function(event) {
			var value;
			value = $('.xg-slider-plugin-1').xgSlider('value');
			return setColor('green', value);
		});
		$('.xg-slider-data-api-1').on('change', function(event) {
			var value;
			value = $('.xg-slider-data-api-1').xgSlider('value');
			return setColor('blue', value);
		});
		setColor('red', 0);
		setColor('green', 0);
		setColor('blue', 0);
	});

}).call(this);
