var getLocalization = function(){
	var localizationobj = {};
	
	localizationobj.firstDay = 1;
    localizationobj.percentsymbol = "%";
    localizationobj.currencysymbol = "￥";
    localizationobj.currencysymbolposition = "before";
    localizationobj.decimalseparator = ".";
    localizationobj.thousandsseparator = ",";
	
	localizationobj.pagergotopagestring = "当前页:";
    localizationobj.pagershowrowsstring = "每页显示:";
    localizationobj.pagerrangestring = " 总共 ";
    localizationobj.pagernextbuttonstring = "后页";
    localizationobj.pagerpreviousbuttonstring = "前页";
	
	localizationobj.groupsheaderstring= '专栏放在该领域时，可设定小组';
    localizationobj.sortascendingstring = "正序";
    localizationobj.sortdescendingstring = "倒序";
    localizationobj.sortremovestring = "清除排序";
	localizationobj.groupbystring= '按该项目分组';
	localizationobj.groupremovestring= '取消分组';	
	localizationobj.filterclearstring= '解除';
	localizationobj.filterstring= '应用';
	localizationobj.filtershowrowstring= '过滤条件';
	localizationobj.filterorconditionstring= '或';
	localizationobj.filterandconditionstring= '还有';
	localizationobj.filterselectallstring= '(全部选择)';
	localizationobj.filterchoosestring= '请选择:';
	localizationobj.filterstringcomparisonoperators= ['空', '不空', '包含', '包含(区分大小文字)',
	'不包含', '不包含(区分大小文字)', '从~开始', '从~开始(区分大小文字)',
	'从~结束', '从~结束(区分大小文字)', '一样', '一样(区分大小文字)', 'null', '不是null'];
	localizationobj.filternumericcomparisonoperators= ['=', '!=', '<', '<=', '>', '>=', 'null', '不是null'];
	localizationobj.filterdatecomparisonoperators= ['=', '!=', '<', '<=', '>', '>=', 'null', '不是null'];
	localizationobj.filterbooleancomparisonoperators= ['=', '!='];
	
	localizationobj.validationstring= '错误值';
	localizationobj.emptydatastring= '搜索结果不存在。';
	localizationobj.filterselectstring= '过滤选择';
	localizationobj.loadtext= '加载(load)…';
	localizationobj.clearstring= '解除';
	localizationobj.todaystring= '今天';

	var days = {
	// full day names
	names: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],
	// abbreviated day names
	namesAbbr: ['周日','周一','周二','周三','周四','周五','周六'],
	// shortest day names
	namesShort: ['周日','周一','周二','周三','周四','周五','周六']
	};
	localizationobj.days = days;

	var months = {
	// full month names (13 months for lunar calendards — 13th month should be '' if not lunar)
	names: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月',''],
	// abbreviated month names
	namesAbbr: ['1','2','3','4','5','6','7','8','9','10','11','12','']
	};
	localizationobj.months = months;
	
    var patterns = {
        d: "yyyy/MM/dd",
        D: "yyyy'年'M'月'd'日'",
        t: "H:mm",
        T: "H:mm:ss",
        f: "yyyy'年'M'月'd'日' H:mm",
        F: "yyyy'年'M'月'd'日' H:mm:ss",
        M: "M'月'd'日'",
        Y: "yyyy'年'M'月'"
    }
    localizationobj.patterns = patterns;
    
    return localizationobj;
}


