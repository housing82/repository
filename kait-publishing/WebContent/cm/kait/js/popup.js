var popTitle, popUrl, first, popIndex, windowId;
popIndex = 1;

//Create Content
var initPopupContent = function (index, opts, footerYn, size, callback) { 

  	var framework = function(){};
  	var external = new framework();
  
  	framework.prototype.loader = {
  	        import : function(_url, callback) {
            if (_url == undefined)
                    return;
            if (_url.indexOf(popUrl) != -1) {
                $.ajax({
                        url : _url,
                        cache : false
                }).done(function(html) {
             	    $('#windowContent'+index).prepend(html);	
                    if (callback) { callback(); }
                                                     
                    if (opts.cancelButton) { 
                    	var obj = opts.cancelButton;
                    	
                    	$(obj['selector']).click(function(){
                    		$("#window"+index).jqxWindow('close');                    	                             
                  		});
                    }
                    
                    popIndex += 1;
                });		  
			}	
  		}
  	}
 	
  	if(popUrl.substr(0,1) == "A") {
  		external.loader.import("/admin/"+ popUrl, callback);
	} else if(popUrl.substr(0,1) == "U"){
		external.loader.import("/user/"+ popUrl, callback);
	}else{
		external.loader.import(popUrl, callback);
	}
  	
	//푸터 팝업 클래스
  	if(footerYn == true){
  		$("#"+windowId).addClass('xgWindowFooter');
  	}	
	$("#"+windowId).addClass(size);
	
	// 팝업창 가운데 띄우기 예외 처리
	if(popUrl == "U002010" || popUrl == "A010015"){
		// 팝업창 가운데 띄우기
		$("#"+ windowId).css("margin", "0");
		$("#"+ windowId).css("top", Math.max(0, (($(window).height() - $("#"+ windowId).outerHeight()) / 2) + $(window).scrollTop()) + "px");
		$("#"+ windowId).css("left", Math.max(0, (($(window).width() - $("#"+ windowId).outerWidth()) / 2) + $(window).scrollLeft()) + "px");
	}else{
		// 팝업창 가운데 띄우기
		$("#"+windowId).resize(function(){
			$modal_visible = $('.xgWindowFooter:visible');
			$modal_visible.css({
				left: '50%',
				top: '50%',
				marginTop: - $modal_visible.height()/2 - 32 ,
				marginLeft: - $modal_visible.width()/2
				
			});
		}).resize();
	}
  		
	$('.jqx-window-content').addClass('xgWindowContent');
  	$('#window'+index).jqxWindow('focus');  
}

//Creating the demo window
function fcCreateWindow(height, width, opts, footerYn, size, callback) {
  	$('#popup').append("<div style='border-radius:10px;border: 2px solid red;' id='window"+popIndex+"'><div style='overflow:hidden;' id='windowTitle"+popIndex+"'></div><div id='windowContent"+popIndex+"'></div></div>");
	$("#window"+popIndex).jqxWindow({
	    maxHeight: height,
		maxWidth: width,
	    height: height,
	    width: width,
        draggable: false, /* drag 안됨 */
        resizable: false, /* resize 안됨 */
        isModal: true, /* modal 팝업 여부 */
        modalOpacity: 0.3, /* modal 팝업 배경색 투명도 */
	    initContent: function () {	
	    	if(popTitle){
	    		$("#window"+popIndex).jqxWindow("setTitle", popTitle);
	    	}else{
	    		$("#windowTitle"+popIndex).remove();
	    		$("#windowContent"+popIndex).css('height', '');
	    	}
	    	 	
            $("#window"+popIndex).jqxWindow().on('close', function (event) {	  
                $("#" + event.target.id).remove();
            });         
        	initPopupContent(popIndex, opts, footerYn, size, callback);       
	    }
	});		
}; 

/*****************************************************************************
 * 공통 xgWindow 팝업 호출 함수
 *****************************************************************************
 * @param	height		- xgWindow 팝업창 높이
 * @param	url			- 팝업으로 띄울 파일명 [String]
 * @param	title		- 팝업창 제목 [String / null 로 넘어올 경우 제목을 그리지 않는다.]
 * @param	opts		- closeButton 으로 쓸 버튼 객체 [object]
 * @param	footerYn	- 버튼이 들어갈 footer 유무 [boolean]
 * @param   size 		- 팝업창 width 사이즈 [F:1240 / L:1024 / M:800 / S:400]
 * @param	callback	- 사용자 지정 함수
 *****************************************************************************/

var CreateWindowOption = {height : '', url : '', title : '', opts : '', footerYn : '', size : '', callback : ''};

// session check
function fcInitCreateWindow(height, url, title, opts, footerYn, size, callback){	
	CreateWindowOption.height = height;
	CreateWindowOption.url = url;
	CreateWindowOption.title = title;
	CreateWindowOption.opts = opts;
	CreateWindowOption.footerYn = footerYn;
	CreateWindowOption.size = size;
	CreateWindowOption.callback = callback;

	fcInitCreateWindow2();
};

//init Create window
function fcInitCreateWindow2(){	
	popTitle = CreateWindowOption.title;
	popUrl = CreateWindowOption.url;	
	windowId = "window"+popIndex;
	
	var width;	
	if(CreateWindowOption.size == 'F'){
		width = '1240'
	}else if(CreateWindowOption.size == 'L'){
		width = '1024';
		CreateWindowOption.height = '768';
	}else if(CreateWindowOption.size == 'M'){
		width = '800';
	}else if(CreateWindowOption.size == 'S'){
		width = '400';
	}else{
		width = CreateWindowOption.size;
	};
	
	fcCreateWindow(CreateWindowOption.height, width, CreateWindowOption.opts, CreateWindowOption.footerYn, CreateWindowOption.size, CreateWindowOption.callback);				
};
       
