var COMM = {};

COMM.splitter = {};
COMM.splitter.makeSplitter = function(id, option) {
    $(id).jqxSplitter(option);
};

COMM.tree = {};
COMM.tree.makeTreeItems = function(arr) {
    var items = [];

    for(var i =0; i < arr.length; i++) {
        items.push(
            COMM.tree.makeItem(arr[i][0], arr[i][1], arr[i][2], arr[i][3])
        );
    }
    return items;
};

COMM.tree.makeItem = function(id, label, value, icon) {
    var item = {
        id: id,
        label: label,
        value: value,
        icon: icon
    };
    return item;
};

COMM.tree.makeTreeMenu = function(folder, items) {
    var item = {
        id: folder[0],
        label: folder[1],
        value: folder[2],
        icon: folder[3],
        expanded: folder[4],
        items: COMM.tree.makeTreeItems(items),
        type: "div"
    }
    return item;
};

COMM.tab = {};
// Content tab 생성 시 iframe, div 등 다양한 태그로 생성 가능
COMM.tab.loadTab = function (item) {
    if(item.value != ""){
        var itemId = item.id.split('_');
        var url = item.value;
        var tabTitle = item.label;
        var tabIndex= COMM.tab.getTabIndex(tabTitle);

        var id = itemId[0];
        var type = $.trim(itemId[1]);
        type = (type === "div" || type === "iframe") ? type : "div";

        COMM.tab.loadLink(id, type, url, tabTitle, tabIndex);
    }
};

COMM.tab.loadLink = function (id, type, url, tabTitle, tabIndex) {
    if (COMM.tab.checkAleadyTab(tabIndex) == true)
        return;

    tabIndex = (tabIndex == -1) ? 0 : tabIndex;

    if (type == "iframe") {
        COMM.tab.loadIframe(id, tabIndex, tabTitle, url);
    } else if (type == "div") {
        COMM.tab.loadDiv(id, tabIndex, tabTitle, url);
    }
};

// iframe tab 생성
COMM.tab.loadIframe = function(id, tabIndex, tabTitle, url) {
    var newContent = "<iframe id='i_" + id + "' scrolling='auto' frameborder='0'  src='" + url + "' style='width:95%;height:95%;padding: 20px;'></iframe>";
    $('#xgTab').xgTab('addAt', tabIndex, tabTitle, newContent);
};

// div tab 생성
COMM.tab.loadDiv = function(id, tabIndex, tabTitle, url) {
    var newContent = "<div id='tab_" + id + "' style='padding:20px;'></div>";
    $('#xgTab').xgTab('addAt', tabIndex, tabTitle, newContent);
    $.get(url, function (data) {
        $("#tab_" + id).html(data);
    });
};

// 탭 생성 확인
COMM.tab.checkAleadyTab = function (tabIndex) {
    if(tabIndex != -1) {
        $('#xgTab').xgTab('select', tabIndex);
        return true;
    }
    return false;
};

// 탭 순서 확인
COMM.tab.getTabIndex = function (tabTitle) {
    var titleList = new Array();
    var tabCount = $('#xgTab').xgTab('length');
    for (var i = 0; i < tabCount; i++) {
        titleList[i] = $('#xgTab').xgTab('getTitleAt', i);
    }
    var tabIndex = titleList.indexOf(tabTitle);
    return tabIndex;
};

COMM.mdi = {
    parentId : "#contentPanel",
    appendToId : "#mdiContainer",
    defaultWidth : '1000'
};

COMM.mdi.loadLink = function (item, url) {
	if(item.value != ""){
        var id = "mdi_" + item.id;
        var label = item.label;
		if(COMM.mdi.checkPreOpen(id) === true) {
			$("#" + id).window("focus");
			return false;
		}
		COMM.mdi.create(id, label, url);
    }
};

COMM.mdi.create = function (id, label, url) {
	
	var h = $(COMM.mdi.parentId).height();
	
	$('<div/>', {
		'id': id,
		'class': 'window',
		'data-appendTo': COMM.mdi.appendToId,
		'data-minheight': '200',
		'data-title': label,
		'data-icon': '../../../images/default.png',
		'data-width': COMM.mdi.defaultWidth,
		'data-height': h - 50,
		'data-positionY': '0',
		'data-positionX': '0',
		'data-maximizable': 'true',
		'data-minimizable': 'true'			
	}).appendTo(COMM.mdi.parentId);
	
	var mdiId= "#"+ id;
	var options = {
		appendTo: $(mdiId).attr('data-appendTo'),
		autoOpen: ($(mdiId).attr('data-autoOpen') != "false"),
		closable: ($(mdiId).attr('data-closable') != "false"),
		closeOnEscape: ($(mdiId).attr('data-closeOnEscape') != "false"),
		closeText: $(mdiId).attr('data-closeText'),
		dialogClass: $(mdiId).attr('data-dialogClass'),
		draggable: ($(mdiId).attr('data-draggable') != "false"),
		height: parseInt($(mdiId).attr('data-height')),
		hide: ($(mdiId).attr('data-hide') == "true"),
		icon: ($(mdiId).attr('data-icon')),
		maxHeight: $(mdiId).attr('data-maxHeight'),
		maximizable: ($(mdiId).attr('data-maximizable') == "true"),
		maximized: ($(mdiId).attr('data-maximized') == "true"),
		maxWidth: $(mdiId).attr('data-maxWidth'),
		minHeight: (parseInt($(mdiId).attr('data-minHeight') != ''))? parseInt($(mdiId).attr('data-minHeight')): 150,
		minimizable: ($(mdiId).attr('data-minimizable') == "true"),
		minimized: ($(mdiId).attr('data-minimized') == "true"),
		minWidth: (parseInt($(mdiId).attr('data-minWidth') != ''))? parseInt($(mdiId).attr('data-minWidth')): 150,
		modal: ($(mdiId).attr('data-modal') == "true"),
		positionX : parseInt($(mdiId).attr('data-positionX')),
		positionY : parseInt($(mdiId).attr('data-positionY')),
		resizable: ($(mdiId).attr('data-resizable') != "false"),
		show: ($(mdiId).attr('data-show') != "false"),
		width: parseInt($(mdiId).attr('data-width')),
		title: $(mdiId).attr('data-title')
	};

	$(mdiId).window(options);
	$(mdiId).window("maximize");

	$("<iframe src='" + url + "' style='width:100%;height:100%;'>").appendTo(mdiId);
};

COMM.mdi.checkPreOpen = function (id) {
	if($("#" + id).length > 0) 
		return true;
	else
		return false;
}

COMM.mdi.closeAll = function() {
    $(COMM.mdi.appendToId + "> div" ).each(function( index ) {
        if( $( this ).children("div")[1] !== undefined) {
            var _windowId = $( this ).children("div")[1].id;
            $("#" + _windowId).window("close");
            console.log("close id" + _windowId);
        }
    });
};

COMM.mdi.arrangeAll = function() {
    var top = 10;
    var left = 10;

    $(COMM.mdi.appendToId + "> div" ).each(function( index ) {

        if( $( this ).children("div")[1] !== undefined) {
            var _windowId = $(this).children("div")[1].id;

            $("#" + _windowId).window("restore");
            $(this).css("left", left);
            $(this).css("top", top);
            $("#" + _windowId).window("focus");
            left = left + 30;
            top = top + 30;
            console.log("arrange window id" + $(this).id);
            console.log("left:" + left);
            console.log("top:" + top);
        }
    });
};

// 팝업
COMM.popup = { "popupId" : "popupWindow"  , "count" : 1 };
COMM.popup.createFromUrl = function (title, url) {
    var id = COMM.popup.popupId + COMM.popup.count++;
    var frameId = COMM.popup.popupId + COMM.popup.count++ + "_frame";

    var newContent = "<iframe id='" + frameId + "' scrolling='auto' frameborder='0'  src='" + url + "' style='width:95%;height:95%;'></iframe>";

    $(document.body).append('<div id="' + id + '"><div>' + title + '</div><div>' + newContent + '</div></div>');
    var windowOption = {
        showCollapseButton: true,
        maxHeight: 800,
        maxWidth: 900,
        minHeight: 300,
        minWidth: 400,
        height: 300,
        width: 365,
        position: {
            x: event.offsetX + 20 ,
            y: event.offsetY + 20
        }

    };
    $('#' + id).xgWindow(windowOption);
    $('#' + id).xgWindow("open");

    return  { "id" : id, "frameId" : frameId };
};


