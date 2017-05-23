(function() {
	$(document).ready(function() {
		var afterDay, toDay;
		toDay = new Date();
		afterDay = new Date(toDay);
		afterDay.setDate(toDay.getDate() + 5);
		new XgUIDateTimeInput('.xg-date-time-input-module-1', {
			height: 25,
			width: 200,
			culture: 'ko-KR',
			formatString: 'yyyy-MM-dd',
			selectionMode: 'range'
		}).xgDateTimeInput('setRange', toDay, afterDay);
		new XgUIDateTimeInput('.xg-date-time-input-module-2', {
			height: 25,
			width: 200,
			readonly: true,
			allowKeyboardDelete: false
		}).on('valueChanged', function(evt) {
			return $('.xg-date-time-input-module-3').text('Selected Date : ' + evt.args.date);
		});
		$('.xg-date-time-input-plugin-1').xgDateTimeInput({
			height: 25,
			width: 200,
			culture: 'ko-KR',
			formatString: 'yyyy-MM-dd',
			selectionMode: 'range'
		}).xgDateTimeInput('setRange', toDay, afterDay);
		return $('.xg-date-time-input-plugin-2').xgDateTimeInput({
			height: 25,
			width: 200,
			readonly: true,
			allowKeyboardDelete: false
		}).on('valueChanged', function(evt) {
			return $('.xg-date-time-input-plugin-3').text('Selected Date : ' + evt.args.date);
		});
	});

}).call(this);
