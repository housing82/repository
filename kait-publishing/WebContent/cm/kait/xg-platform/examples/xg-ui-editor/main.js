(function() {
	$(document).ready(function() {
		var $xgEditor2;
		new XgUIEditor('.xg-editor-module-1');
		$xgEditor2 = new XgUIEditor('.xg-editor-module-2', {
			width: '70%',
			height: '150px',
			tools: 'bold italic underline'
		});
		$xgEditor2.on('change', function(event) {
			return $('#log').html("Event is raised in use module 2 : " + event.args.command);
		});
		$('.xg-editor-plugin-1').xgEditor({
			width: '700px',
			height: '150px'
		});
		$('.xg-editor-plugin-1').on('change', function(event) {
			return $('#log').html("Change is raised in use jQuery plugin : " + event.args.command);
		});
		$('.xg-editor-data-api-1').on('change', function(event) {
			return $('#log').html("Change is raised in use data-API : " + event.args.command);
		});
	});

}).call(this);
