//<![CDATA[
$(document).ready(function(){

	$("div.nav_tab span").click(function(){
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

	$("div.mditab span").click(function(){
		if(!$(this).hasClass("arrow")) {
			$(this).parents("ul").find("span").removeClass("current_mditab");
			$(this).addClass("current_mditab");
		}
	});

	$("div.tabmenu span").click(function(){
		$(this).parents("ul").find("span").removeClass("current_tabmenu");
		$(this).addClass("current_tabmenu");
	});	
});
//]]>