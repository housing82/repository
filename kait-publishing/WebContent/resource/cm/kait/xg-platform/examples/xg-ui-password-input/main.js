(function() {
	$(document).ready(function() {
		var korean;
		korean = {
			passwordStrengthString: "암호 강도",
			tooShort: "너무 짧음",
			weak: "약함",
			fair: "적당함",
			good: "좋음",
			strong: "강력함"
		};
		new XgUIPasswordInput('.xg-password-input-module-1', {
			height: 30,
			width: 200,
			placeHolder: "Enter password.",
			showStrength: true,
			showStrengthPosition: 'left'
		});
		new XgUIPasswordInput('.xg-password-input-module-2', {
			height: 30,
			width: 200,
			placeHolder: "암호를 입력하세요.",
			showStrength: true,
			localization: korean
		}).on('change', function(event) {
			return $('#log-module').html('Change : ' + $(this).xgPasswordInput('val'));
		});
		$('.xg-password-input-plugin-1').xgPasswordInput({
			height: 30,
			width: 200,
			placeHolder: "Enter password.",
			showStrength: true,
			showStrengthPosition: 'top'
		});
		$('.xg-password-input-plugin-2').xgPasswordInput({
			height: 30,
			width: 200,
			placeHolder: "암호를 입력하세요.",
			showStrength: true,
			showStrengthPosition: 'bottom',
			localization: korean
		}).on('change', function(event) {
			return $('#log-plugin').html('Change : ' + $(this).xgPasswordInput('val'));
		});
		return $('.xg-password-input-data-api-2').on('change', function(event) {
			return $('#log-dataAPI').html('Change : ' + $(this).xgPasswordInput('val'));
		});
	});

}).call(this);
