jQuery(document).ready(function($) {

	// Menu
	var menu = $('div.topnavWrap');
	var topnav = $('nav.topnav');
	var li_list = topnav.find('>ul>li');
	var li_sub = li_list.find('.gnb_sub>ul>li');
	var menu_all = li_list.find('.menu_all>.menu_all_box>ul>li');
	var menu_all_sub = li_list.find('.menu_all>.menu_all_box>ul>li>ul>li');

	// Selected
	function onselectmenu(){
		var myclass = [];

		$(this).parent('li').each(function(){
			myclass.push( $(this).attr('class') );
		});

		myclass = myclass.join(' ');
		if (!topnav.hasClass(myclass)) topnav.attr('class','topnav').addClass(myclass);

	}

	// Show Menu
	function show_menu(){
		t = $(this);
		if(t[0].offsetParent.className == "last active"){
			li_list.removeClass('active');
			$('nav.topnav>ul>li.last>a>span').removeClass('up')
			$('nav.topnav>ul>li.last>a>span').addClass('down')

		}else if(t[0].offsetParent.className == "last"){
			li_list.removeClass('active');
			t.parent('li').addClass('active');
			t.parent().children().children().children().removeClass('active')
			$('.gnb_sub>ul>li:first-child').addClass('active');
			$('nav.topnav>ul>li.last>a>span').removeClass('down')
			$('nav.topnav>ul>li.last>a>span').addClass('up')
		} else {
			li_list.removeClass('active');
			t.parent('li').addClass('active');
			t.parent().children().children().children().removeClass('active')
			$('.gnb_sub>ul>li:first-child').addClass('active');
			$('nav.topnav>ul>li.last>a>span').removeClass('up')
			$('nav.topnav>ul>li.last>a>span').addClass('down')
			
			//대메뉴 클릭시 하위메뉴 바로 실행
			eval(t.parent('li').find('>.gnb_sub>ul>li:first-child>a').attr('href'));
		}

	}

	li_list.find('>a').click(onselectmenu).click(show_menu);


	// Selected sub
	function onselectmenusub(){
		var subclass = [];

		$(this).parent('li').each(function(){
			subclass.push( $(this).attr('class') );
		});

		subclass = subclass.join(' ');
		if (!topnav.hasClass(subclass)) topnav.attr('class','topnav').addClass(subclass);
	}


	// Show Menu Sub
	function show_menu_sub(){
		t = $(this);
		li_sub.removeClass('active');
		t.parent('li').addClass('active');
	}

	li_sub.find('>a').click(onselectmenusub).focus(show_menu_sub);
	li_sub.find('>a').click(function () {
		$(this).addClass('active');
	});


	// close Menu All
	menu_all.find('>a').click(function() {
		li_list.removeClass('active');
		$('nav.topnav>ul>li.last>a>span').removeClass('up')
		$('nav.topnav>ul>li.last>a>span').addClass('down')
		
		//대메뉴 클릭시 하위메뉴 바로 실행
		eval($(this).parent('li').find('ul>li:first-child>a').attr('href'));
	});
	menu_all_sub.find('>a').click(function() {
		li_list.removeClass('active');
		$('nav.topnav>ul>li.last>a>span').removeClass('up')
		$('nav.topnav>ul>li.last>a>span').addClass('down')
	});
	$('.menu_all>.close').click(function() {
		li_list.removeClass('active');
		$('nav.topnav>ul>li.last>a>span').removeClass('up')
		$('nav.topnav>ul>li.last>a>span').addClass('down')
	});
	

});

//dataSet 생성
window.dataSet = new XgDataSet('ds');


/*
 * 로그인 체크용 iframe이 새로 고침 된 후 화면을 그리기 위해서 함수를 두개로 쪼갬
 */

var id,path;
//메뉴 선택
function mainFrameChange(id2, path2){
	id = id2;
	path = path2;

	sessionChk();
}


function mainFrameChange2(){
	//content 영역 이동
	if ( $("#contents_body").length > 0 ){
		$("#contents_body").remove();
	}
	
	$("#content_site").html("<div id=\"contents_body\"></div>");
	
	
	//네비게이트
	var target = document.getElementById('pagelocation');
	if(target){
		var mainText = $('#'+id+'').parent().parent().parent().children('a').text().replace("NEW","");
		var subText = $('#'+id+'>a').text().replace("NEW","");
		var mainTitle = "用户中心首页";
		if(mainText == ""){
			mainText = $('#'+id+'').parent().parent().children('a').text().replace("NEW","");
		}
		if(path.substring(1,6) == 'admin'){
			mainTitle = "后台管理首页";
		}
		
		if(mainText != "") {
			var mainMove, mainTextMove, subMove;
			
			if(mainTitle == '用户中心首页'){
				mainMove = '$(\'#U001000000>a\').click();';
			}else{
				mainMove = '$(\'#A002000000>a\').click();';
			}
			mainTextMove = '$(\'#' + id.substring(0,4) + '000000>a\').click();';
			subMove = 'mainFrameChange(\'' + id + '\', \'' + path + '\')';
			
			if(path.indexOf("U001001") != -1 || path.indexOf("A002001") != -1 || path.indexOf("A002002") != -1){ //메인화면일 경우 
				target.innerHTML = '<li><img src="/images/custom/admin/icons/icon_home.png"/></li><li>' + 
				'<span style="cursor:pointer" onclick="' + mainTextMove + '" class="menuHover">' + 
				mainText+'</span></li><li>></li><li>'+
				'<span style="cursor:pointer" onclick="' + subMove + '" class="menuHover">' + 
				subText+'</span></li>';
			}else{
				target.innerHTML = '<li><img src="/images/custom/admin/icons/icon_home.png"/></li><li>' + 
				'<span style="cursor: pointer;" onclick="' + mainMove  + '" class="menuHover">'+
				mainTitle+'</span></li><li>></li><li>'+ 
				'<span style="cursor:pointer" onclick="' + mainTextMove + '" class="menuHover">' + 
				mainText+'</span></li><li>></li><li>'+
				'<span style="cursor:pointer" onclick="' + subMove + '" class="menuHover">' + 
				subText+'</span></li>';
			}
		}
	}
		
	//dataSet에 존재하는 모든 dataTable 초기화 및 dataSet에서 삭제
	var dataTables = dataSet.getDataTableIds();
	var len = dataTables.length;
	for(var i = 0; i < len; i++){
		dataSet.removeDataTable(dataTables[i]);
		eval("window." + dataTables[i] + "= null");
	}
	
	if ( $("#contents_body").length > 0 ){
		if($("#contents_body").attr('sessionChk') == 'sessionOut'){
			//iframe일 경우 재귀 호출이 되기 때문에 분기를 나눌 수 있는 변수를 call에 담아서 보냄
			//header 밑에 그릴 경우
			//$("#contents_body").load("/login/sessionExpired?call=t");
			
			//화면 전체를 변경할 경우
			parent.document.getElementById("main").src = "/login/sessionExpired?call=t";
		}else{
			$("#contents_body").load(path);
		}
	}
	//keypress 이벤트 제거
	$(document).unbind('keypress');
	
}

function mainFrameChangeWithoutSessionChk(id2, path2 ){
	id = id2;
	path = path2;
	
	//content 영역 이동
	if ( $("#contents_body").length > 0 ){
		$("#contents_body").remove();
	}
	
	$("#content_site").html("<div id=\"contents_body\"></div>");
	
	//네비게이트
	var target = document.getElementById('pagelocation');
	if(target){
		var mainText = $('#'+id+'').parent().parent().parent().children('a').text();
		var subText = $('#'+id+'>a').text();
		var mainTitle = "用户中心首页";
		if(mainText == ""){
			mainText = $('#'+id+'').parent().parent().children('a').text();
		}
		if(path.substring(1,6) == 'admin'){
			mainTitle = "后台管理首页";
		}

		if(mainText != "") {
			if(path.indexOf("U001001") != -1 || path.indexOf("A002001") != -1 || path.indexOf("A002002") != -1){ //메인화면일 경우 
				target.innerHTML = '<li><img src="/images/custom/admin/icons/icon_home.png"/></li><li>'+mainText+'</li><li>></li><li>'+subText+'</li>';
			} else {
				target.innerHTML = '<li><img src="/images/custom/admin/icons/icon_home.png"/></li><li>'+mainTitle+'</li><li>></li><li>'+mainText+'</li><li>></li><li>'+subText+'</li>';
			}
		}
		
	}
		
	//dataSet에 존재하는 모든 dataTable 초기화 및 dataSet에서 삭제
	var dataTables = dataSet.getDataTableIds();
	var len = dataTables.length;
	for(var i = 0; i < len; i++){
		dataSet.removeDataTable(dataTables[i]);
		eval("window." + dataTables[i] + "= null");
	}
	
	if ( $("#contents_body").length > 0 ){
		$("#contents_body").load(path);	
	}
	//keypress 이벤트 제거
	$(document).unbind('keypress');
	
}

function createQusAlert(){
	$("#lawQusAlertHeader, #taxQusAlertHeader, #finQusAlertHeader, #tenQusAlertHeader").remove();
	
	//자문이 왔지만 아직 답변을 하지 않은 글이 있을 경우 new 를 추가
	var unAnswerQusHeaderInfo = [	
		{colName : 'cnt',   colType : 'string',  colSize : '30'},                      
		{colName : 'qusType',   colType : 'string',  colSize : '30'}                    
    ];				
	       				
	ixCreateDataBindInfo('XML', 'tb_unAnswerQus', '/admin/selectUnAnswerQus', '', unAnswerQusHeaderInfo);
	
	dataAdapter.setAsyncStatus(false);
	ixSelect(null, 'tb_unAnswerQus', null, null, false, null, null, function(){
		var myCount = tb_unAnswerQus.getRowCount();
		for(var i = 0; i < myCount; i++){
			switch (tb_unAnswerQus.getValue(i , 'qusType')) {
				case	'L' :
					$("#A006000000> a").append('<span id="lawQusAlertHeader" class="qusAlert">NEW</span>');
				break;
			
				case	'A' :
					$("#A007000000> a").append('<span id="taxQusAlertHeader" class="qusAlert">NEW</span>');
				break;
				
				case	'F' :
					$("#A008000000> a").append('<span id="finQusAlertHeader" class="qusAlert">NEW</span>');
				break;
				
				case	'Q' :
					$("#A009000000> a").append('<span id="tenQusAlertHeader" class="qusAlert">NEW</span>');
				break;
			}			    					
		}
	});
	dataAdapter.setAsyncStatus(true);
}

//답변이 왔지만 아직 읽지 않은 글이 있을 경우 new 를 추가
function createQusAlertUser(){
	$("#lawQusAlertHeader1, #lawQusAlertHeader2, #taxQusAlertHeader1, #taxQusAlertHeader2").remove();
	$("#finQusAlertHeader1, #finQusAlertHeader2, #tenQusAlertHeader1, #tenQusAlertHeader2").remove();
	
	var unreadQusHeaderInfo = [	
		{colName : 'cnt',   colType : 'string',  colSize : '30'},                      
		{colName : 'qusType',   colType : 'string',  colSize : '30'}                    
    ];
		
	ixCreateDataBindInfo('XML', 'tb_myUnreadQus', '/user/SelectUnreadQusForUser', '', unreadQusHeaderInfo);
	
	dataAdapter.setAsyncStatus(false);
	ixSelect(null, 'tb_myUnreadQus', null, null, false, null, null, function(){
		var myCount = tb_myUnreadQus.getRowCount();
		var type, functions, messages = "";
						 
		for(var i = 0; i < myCount; i++){
			switch (tb_myUnreadQus.getValue(i , 'qusType')) {      					
				case	'L' :
					$("#U003000000> a").append('<span id="lawQusAlertHeader1" class="qusAlert">NEW</span>');
					$("#U003002010> a").append('<span id="lawQusAlertHeader2" class="qusAlert">NEW</span>');
				break;
	
				case	'A' :
					$("#U004000000> a").append('<span id="taxQusAlertHeader1" class="qusAlert">NEW</span>');
					$("#U004002010> a").append('<span id="taxQusAlertHeader2" class="qusAlert">NEW</span>');
				break;
				
				case	'F' :
					$("#U005000000> a").append('<span id="finQusAlertHeader1" class="qusAlert">NEW</span>');
					$("#U005002010> a").append('<span id="finQusAlertHeader2" class="qusAlert">NEW</span>');
				break;
				
				case	'Q' :
					$("#U006000000> a").append('<span id="tenQusAlertHeader1" class="qusAlert">NEW</span>');
					$("#U006002010> a").append('<span id="tenQusAlertHeader2" class="qusAlert">NEW</span>');
				break;
			}			    					
		}
	}); 
	dataAdapter.setAsyncStatus(true);
}