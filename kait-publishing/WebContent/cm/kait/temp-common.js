//<![CDATA[
/*******************************
 * 파일명 :	temp-common.js
 * 설 명 :	퍼블리싱 작업용 임시 스크립트
 * 			퍼블리싱작업 도중 퍼블리셔가 확인해야할 각 영역 내용 조회
 * 			1. 공통레이아웃 탭버튼 클릭 이벤트
 * 			2. 제나 기본 그리드 초기화
 * 			3. 좌측 메뉴 클릭시 컨텐츠 html 로딩 
 * 			PS: 고객에게 화면 시연시 필요
 * 작성자 :	김상우
 * 수정일 :	2017.05.17
 ******************************/

(function( $ ){
	$.extend({
		gfx_selector : {
			id : {
				content : "#kait_content" // $.gfx_selector.id.content 
			},
			attr : {
				xgGrid : "[component='xg-grid']"	// $.gfx_selector.attr.xgGrid
			}
		},
		gfx_content : {
			// $.gfx_content.innerWidth()
			innerWidth : function( selector ) {

				if($(selector).length > 0) {
					return $(selector).innerWidth();
				} 
				return parseInt($("article").css("min-width")) / 2;
			} 
		}, 
		// 컨텐츠 안의 그리드 레이아웃 출력용 임시 스크립트
		gfx_commonGrid : function( selector ) {
			console.log(" ## S. gfx_commonGrid ## ");
			
			var colCount = 20;
			var columns = new Array();
			
			for(var i = 1; i <= colCount; i++) {
				columns[i] = { text: '컬럼(' + i + ")", datafield: 'col' + i, columngroup: 'col' + i, width: 100 };
			}
 
			var xgGridWidth = $.gfx_content.innerWidth( $(selector) );
			console.log("xgGridWidth: " + xgGridWidth);
			$(selector).empty();
			$(selector).xgGrid({
				width: xgGridWidth,
				xgInitOption: {
					columns: columns
				}
			});
			
			console.log(" ## E. gfx_commonGrid ## ");
		},
		gfx_load : function ( element ) {
			var hrefURL = $(element).attr("href");
			console.log("hrefURL : " + hrefURL);
			if(hrefURL && hrefURL.length > 0 && hrefURL != "#") {
				hrefURL = "." + hrefURL;
				$.post(hrefURL, function( response ){
					$($.gfx_selector.id.content).empty();
					$($.gfx_selector.id.content).html(response);
				}).done(function() {
					// Set location.hash
					location.hash = hrefURL.substring(1);
					
					//S. xena grid init
					var xgGrids = $($.gfx_selector.id.content + " " + $.gfx_selector.attr.xgGrid);
					if(xgGrids.length > 0) {
						$.each(xgGrids, function(idx , item) {
							$.gfx_commonGrid( item );
						});	
					}
					//E. xena grid init	
									
					console.log( "second success" );
				}).fail(function() {
					console.log( "error" );
				}).always(function() {
					console.log( "finished" );
				});
			}
			else {
				$("#kait_content").empty();
				console.log("메뉴 링크정보가 존재하지 않습니다. [ " + $(element).text() + " ]");
			}
		},
		gfx_init : function() {
			var hash = location.hash;
			
			if( hash && hash != '' && hash != '#') {
				hash = hash.substring(1);
				console.log("hash : " + hash);
				if($("aside .all_tree a[href='"+hash+"']").length == 0) {
					alert("존제하지 않는 메뉴 링크입니다.");
				}
				else {
					$.gfx_load( $("aside .all_tree a[href='"+hash+"']") );
				}
			}
			else if($("aside .all_tree a[init='true']").length > 0) {
				$("aside .all_tree a[init='true']:eq(0)").click();
			}
			else {
				console.log("location.hash 또는 첫번째 컨텐츠 화면 초기화 속성이있는 메뉴 a태그가 존재하지 않습니다.");
			}
			
		}
	});
})( jQuery );

$(document).ready(function(){

	//S. tab click event
	$(document).on("click", "div.nav_tab span", function( e ){
		$(this).parents("ul").find("span").removeClass("current_tab");
		$(this).addClass("current_tab");
		var $li = $(this).parents("li");

		$("aside div.menu_my").hide();
		$("aside div.wrap_menu_all").hide();
		$("aside div.menu_recent").hide();

		if($li.hasClass("tab_my")) {
			$("aside div.menu_my").show();
		}
		else if($li.hasClass("tab_all")) {
			$("aside div.wrap_menu_all").show();
		}
		else if($li.hasClass("tab_recent")) {
			$("aside div.menu_recent").show();
		}
	});

	//S. 상단 MDI 텝메뉴
	$(document).on("click", "div.mditab span", function( e ){
		if(!$(this).hasClass("arrow")) {
			$(this).parents("ul").find("span").removeClass("current_mditab");
			$(this).addClass("current_mditab");
		}
	});
	//E. 상단 MDI 텝메뉴
	
	//S. 컨텐츠 내부 텝메뉴
	$(document).on("click", $.gfx_selector.id.content + " div.tabmenu span", function( e ) {
		$(this).parents("ul").find("span").removeClass("current_tabmenu");
		$(this).addClass("current_tabmenu");
	});	
	//E. 컨텐츠 내부 텝메뉴
	
	//E. tab click event
	
	//S. load content html
	$(document).on("click", "aside .all_tree a", function( e ) {
		e.preventDefault();
		$.gfx_load( this );
	});
	//E. load content html

	// INIT MENU Contents
	$.gfx_init();
});


//]]>