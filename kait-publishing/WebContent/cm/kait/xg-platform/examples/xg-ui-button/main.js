(function() {
	$(document).ready(function() {
		var $xgButtonModule2, $xgDropDownModule, $xgDropDownPlugin, $xgSwitchModule, $xgSwitchPlugin, $xgToggleModule, $xgTogglePlugin, getRandomNum;
		getRandomNum = function() {
			return 1 + Math.round(Math.random() * 10);
		};

		/*
			default button
		 */
		new XgUIButton('default', '.xg-btn-module-1', {
			roundedCorners: 'top',
			width: '200px',
			disabled: true
		});
		$xgButtonModule2 = new XgUIButton('default', '.xg-btn-module-2', {
			height: 25,
			width: 150,
			template: 'danger'
		});
		$xgButtonModule2.on('click', function(evt) {
			return $(this).val('module : ' + getRandomNum());
		});
		$('.xg-btn-plugin-1').xgButton();
		$('.xg-btn-plugin-2').xgButton({
			height: 25,
			width: 150,
			template: 'danger'
		}).on('click', function(evt) {
			return $(this).val('plugin : ' + getRandomNum());
		});
		$('.xg-btn-data-api-2').on('click', function(evt) {
			return $(this).val('data-api : ' + getRandomNum());
		});

		/*
			toggle button
		 */
		$xgToggleModule = new XgUIButton('toggle', '.xg-btn-toggle-module-1');
		new XgUIButton('toggle', '.xg-btn-toggle-module-2', {
			height: 25,
			width: 150,
			template: 'danger'
		}).on('click', function(evt) {
			return $(this).xgToggleButton('val', 'module : ' + getRandomNum());
		});
		$('.xg-btn-toggle-module-3').on('click', function() {
			return $xgToggleModule.xgToggleButton('toggle');
		});
		$xgTogglePlugin = $('.xg-btn-toggle-plugin-1').xgToggleButton();
		$('.xg-btn-toggle-plugin-2').xgToggleButton({
			height: 25,
			width: 150,
			template: 'danger'
		}).on('click', function(evt) {
			return $(this).xgToggleButton('val', 'plugin : ' + getRandomNum());
		});
		$('.xg-btn-toggle-plugin-3').on('click', function() {
			return $xgTogglePlugin.xgToggleButton('toggle');
		});
		$('.xg-btn-toggle-data-api-2').on('click', function() {
			return $(this).xgToggleButton('val', 'data-api : ' + getRandomNum());
		});
		$('.xg-btn-data-api-2').on('click', function(evt) {
			return $(this).val('data-api : ' + getRandomNum());
		});

		/*
			switch button
		 */
		$xgSwitchModule = new XgUIButton('switch', '.xg-btn-switch-module-1');
		new XgUIButton('switch', '.xg-btn-switch-module-2', {
			height: 25,
			width: 150
		}).on('click', function(evt) {
			return $(this).xgSwitchButton('val', !$(this).xgSwitchButton('val'));
		});
		$('.xg-btn-switch-module-3').on('click', function() {
			return $xgSwitchModule.xgSwitchButton('toggle');
		});
		$xgSwitchPlugin = $('.xg-btn-switch-plugin-1').xgSwitchButton();
		$('.xg-btn-switch-plugin-2').xgSwitchButton({
			height: 25,
			width: 150
		}).on('click', function(evt) {
			return $(this).xgSwitchButton('val', !$(this).xgSwitchButton('val'));
		});
		$('.xg-btn-switch-plugin-3').on('click', function() {
			return $xgSwitchPlugin.xgSwitchButton('toggle');
		});

		/*
			drop down button
		 */
		$xgDropDownModule = new XgUIButton('dropDown', '.xg-btn-dropdown-module-1');
		$('.xg-dropdown-content-1').on('click', function(evt) {
			$xgDropDownModule.xgDropDownButton('setContent', $(this).text());
			if ($xgDropDownModule.xgDropDownButton('isOpened')) {
				return $xgDropDownModule.xgDropDownButton('close');
			}
		});
		new XgUIButton('dropDown', '.xg-btn-dropdown-module-2', {
			height: 25,
			width: 150
		});
		$xgDropDownPlugin = $('.xg-btn-dropdown-plugin-1').xgDropDownButton();
		$('.xg-dropdown-plugin-content-1').on('click', function(evt) {
			$xgDropDownPlugin.xgDropDownButton('setContent', $(this).text());
			if ($xgDropDownPlugin.xgDropDownButton('isOpened')) {
				return $xgDropDownPlugin.xgDropDownButton('close');
			}
		});
		$('.xg-btn-dropdown-plugin-2').xgDropDownButton({
			height: 25,
			width: 150
		});
		window.setDropDownContent = function(text) {
			$('.xg-btn-dropdown-data-api-1').xgDropDownButton('setContent', text);
			return $('.xg-btn-dropdown-data-api-1').xgDropDownButton('close');
		};

		/*
			button group
		 */
		new XgUIButton('buttonGroup', '.xg-btn-group-module-1', {
			mode: 'radio',
			disabled: true
		}).on('buttonclick', function(evt) {
			return $('.xg-btn-group-module-1-selected').text('selected text / index : ' + $(evt.args.button[0]).text() + ' / ' + evt.args.index);
		});
		new XgUIButton('buttonGroup', '.xg-btn-group-module-2', {
			mode: 'checkbox'
		});
		new XgUIButton('buttonGroup', '.xg-btn-group-module-3', {
			mode: 'default'
		});
		$('.xg-btn-group-plugin-1').xgButtonGroup({
			mode: 'radio'
		}).on('buttonclick', function(evt) {
			return $('.xg-btn-group-plugin-1-selected').text('selected text / index : ' + $(evt.args.button[0]).text() + ' / ' + evt.args.index);
		});
		$('.xg-btn-group-plugin-2').xgButtonGroup({
			mode: 'checkbox'
		});
		$('.xg-btn-group-plugin-3').xgButtonGroup({
			mode: 'default'
		});
		return window.selectGroup = function(text, idx) {
			return $('.xg-btn-group-data-api-1-selected').text('selected text / index : ' + text + ' / ' + idx);
		};
	});

}).call(this);
