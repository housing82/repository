(function() {
	$(document).ready(function() {
		var isPopOpend, source;
		source = ['Affogato', 'Americano', 'Bicerin', 'Breve', 'Café Bombón', 'Café au lait', 'Caffé Corretto', 'Café Crema', 'Caffé Latte'];
		isPopOpend = function(el, target) {
			return $(target).text('isPopOpend : ' + $(el).xgInput('opened'));
		};
		new XgUIInput('.xg-input-module-1', {
			height: 25,
			width: 200,
			placeHolder: '값을 입력해주세요.'
		});
		new XgUIInput('.xg-input-module-2', {
			source: source,
			height: 25,
			width: 200,
			placeHolder: 'a를 입력해보세요.'
		}).on('open', function(evt) {
			return isPopOpend(this, '.xg-input-module-3');
		}).on('close', function(evt) {
			return isPopOpend(this, '.xg-input-module-3');
		});
		$('.xg-input-plugin-1').xgInput({
			height: 25,
			width: 200,
			placeHolder: '값을 입력해주세요.'
		});
		return $('.xg-input-plugin-2').xgInput({
			source: source,
			height: 25,
			width: 200,
			placeHolder: 'a를 입력해보세요.'
		}).on('open', function(evt) {
			return isPopOpend(this, '.xg-input-plugin-3');
		}).on('close', function(evt) {
			return isPopOpend(this, '.xg-input-plugin-3');
		});
	});

}).call(this);
