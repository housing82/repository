(function() {
	$(document).ready(function() {
		var $xgRadioModule2, clear;
		clear = function() {
			var log;
			log = $('#events').find('span');
			if (log.length >= 2) {
				return log.remove();
			}
		};
		new XgUIRadioButton('.xg-radio-button-module-1');
		$('.xg-radio-button-module-1').on('change', function(event) {
			clear();
			if (event.args.checked) {
				return $('#events').prepend('<div><span>Checked : Use module 1</span></div>');
			} else {
				return $('#events').prepend('<div><span>Unchecked : Use module 1</span></div>');
			}
		});
		$xgRadioModule2 = new XgUIRadioButton('.xg-radio-button-module-2', {
			height: 30,
			width: 150,
			checked: true
		});
		$xgRadioModule2.on('change', function(event) {
			clear();
			if (event.args.checked) {
				return $('#events').prepend('<div><span>Checked : Use module 2</span></div>');
			} else {
				return $('#events').prepend('<div><span>Unchecked : Use module 2</span></div>');
			}
		});
		$('.xg-radio-button-plugin-1').xgRadioButton({
			height: 30,
			width: 150,
			boxSize: "20px"
		});
		$('.xg-radio-button-plugin-1').on('change', function(event) {
			clear();
			if (event.args.checked) {
				return $('#events').prepend('<div><span>Checked : Use plugin 1</span></div>');
			} else {
				return $('#events').prepend('<div><span>Unchecked : Use plugin 1</span></div>');
			}
		});
		$('.xg-radio-button-data-api-1').on('change', function(event) {
			clear();
			if (event.args.checked) {
				return $('#events').prepend('<div><span>Checked : Use data-API 1</span></div>');
			} else {
				return $('#events').prepend('<div><span>Unchecked : Use data-API 1</span></div>');
			}
		});
		return $('.xg-radio-button-data-api-2').on('change', function(event) {
			clear();
			if (event.args.checked) {
				return $('#events').prepend('<div><span>Checked : Use data-API 2</span></div>');
			} else {
				return $('#events').prepend('<div><span>Unchecked : Use data-API 2</span></div>');
			}
		});
	});

}).call(this);
