(function() {
	$(document).ready(function() {
		var $xgCheckModule2;
		new XgUICheckBox('.xg-check-box-module-1');
		$xgCheckModule2 = new XgUICheckBox('.xg-check-box-module-2', {
			height: 50,
			width: 150,
			hasThreeStates: true
		});
		$xgCheckModule2.on('checked', function(event) {
			return $('.xg-check-box-module-1').xgCheckBox({
				checked: true
			});
		});
		$xgCheckModule2.on('unchecked', function(event) {
			return $('.xg-check-box-module-1').xgCheckBox({
				checked: false
			});
		});
		$('.xg-check-box-plugin-1').xgCheckBox();
		$('.xg-check-box-plugin-2').xgCheckBox({
			height: 25,
			width: 150,
			boxSize: "20px"
		});
		$('.xg-check-box-data-api-1').on('change', function(event) {
			var checked;
			checked = event.args.checked;
			if (checked) {
				return $(this).find('span')[1].innerHTML = 'Checked';
			} else {
				return $(this).find('span')[1].innerHTML = 'Unchecked';
			}
		});
		$('.xg-check-box-data-api-2').on('checked', function(event) {
			return $(this).find('span')[1].innerHTML = 'Checked';
		});
		$('.xg-check-box-data-api-2').on('unchecked', function(event) {
			return $(this).find('span')[1].innerHTML = 'Unchecked';
		});
		return $('.xg-check-box-data-api-2').on('indeterminate', function(event) {
			return $(this).find('span')[1].innerHTML = 'Use data-API 2';
		});
	});

}).call(this);
