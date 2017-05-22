(function() {
	$(document).ready(function() {
		var $xgTab2, clear;
		clear = function() {
			var log;
			log = $('#eventsLog').find('span');
			if (log.length >= 3) {
				return log.remove();
			}
		};
		new XgUITab('.xg-tab-module-1');
		$xgTab2 = new XgUITab('.xg-tab-module-2', {
			width: '50%',
			height: 100,
			position: 'bottom',
			scrollPosition: 'left'
		});
		$xgTab2.on('selected', function(event) {
			clear();
			return $('#eventsLog').prepend('<div><span>Selected : ' + $(this).xgTab('getTitleAt', event.args.item) + '</span></div>');
		});
		$('.xg-tab-plugin-1').xgTab({
			width: '50%',
			height: 100,
			position: 'top',
			selectionTracker: 'checked'
		});
		$('.xg-tab-plugin-1').on('unselected', function(event) {
			clear();
			return $('#eventsLog').prepend('<div><span>Unselected : ' + $(this).xgTab('getTitleAt', event.args.item) + '</span></div>');
		});
		return $('.xg-tab-data-api-1').on('tabclick', function(event) {
			clear();
			return $('#eventsLog').prepend('<div><span>TabClicked : ' + $(this).xgTab('getTitleAt', event.args.item) + '</span></div>');
		});
	});

}).call(this);
