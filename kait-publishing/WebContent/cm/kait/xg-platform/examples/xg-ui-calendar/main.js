(function() {
	$(document).ready(function() {
		new XgUICalendar('.xg-calendar-module-1');
		new XgUICalendar('.xg-calendar-module-2', {
			width: 250,
			height: 250,
			culture: 'ko-KR'
		});
		$('.xg-calendar-plugin-1').xgCalendar();
		return $('.xg-calendar-plugin-2').xgCalendar({
			width: 250,
			height: 250,
			culture: 'ko-KR'
		});
	});

}).call(this);
