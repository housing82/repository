(function(){"use strict";!function(a,b){return"function"==typeof define&&define.amd?define("XgUIProgress",["jquery","XgCommon","XgUICommon"],b):a.XgUIProgress=b(a.jQuery,a.XgCommon,a.XgUICommon)}(window,function(a,b,c){var d,e,f="xg-ui-progress.js";return XgPlatform.XgLogger.info(function(){return f+" : load start"}),e=function(b,d){var e,f=b.data("xg-bind-option"),g={},h=b.data("xg-bind-data-set"),i=b.data("xg-bind-data-table-id"),j=h.getDataTable(i);return f||(f={mode:"read",immediately:!0,bind:{type:"column"}},b.data("xg-bind-option",f)),c.setUUID(b),h.bindComponent(b,"jqxProgressBar"),a.extend(!0,g,f,d),b.data("xg-bind-option",g),c.setBindColInfo(j,g),c.removeEvents(b,["xg-bind-event"]),("write"===(e=g.mode)||"read"===e)&&b.on("xg-bind-event",function(c,d){var e,f,h,k,l;if(a(this).data("xg-uuid")===d.uuid||d.xgDataTableId!==i)return!1;if("setRowPos"===d.eventName){if(k=d.rowIdx,h=j.getRow(k),e=g.bind.columnIndex,b.data("xg-target-index",k),"column"===g.bind.type)return l=h[e],"string"==typeof result&&(l=Number(l)),(void 0===l||null===l)&&(l=0),b.jqxProgressBar("val",l)}else{if("xgDataTableInit"===d.eventName)return b.jqxProgressBar("val",0);if(f=b.data("xg-target-index"),d.rowIdx!==f)return!1;if("setValue"===d.eventName&&"column"===g.bind.type&&d.bind.column===g.bind.column)return l=d.value,(void 0===l||null===l)&&(l=0),b.jqxProgressBar("val",l)}}),b},a.fn.xgProgress=function(){var c,d,f,g,h,i=this.selector||this,j=arguments;if(a(this).attr("data-xg-progress",""),XgPlatform.XgLogger.debug(function(){return["XgUIProgress",i,j]}),arguments[0]instanceof Object){if(g=arguments[0],g.xgInitOption){if(f={},d=a(this).data("xg-init-option"),a.extend(!0,f,d,g.xgInitOption),a(this).data("xg-init-option",f),"string"==typeof g.xgInitOption.xgDataTable){if(!g.xgInitOption.xgDataSet)throw new Error("Require xgDataSet option property");g.xgInitOption.xgDataTable=g.xgInitOption.xgDataSet.getDataTable(g.xgInitOption.xgDataTable)}return a(this).data("xg-bind-data-set",g.xgInitOption.xgDataSet),a(this).data("xg-bind-data-table-id",g.xgInitOption.xgDataTable.getId()),h=g.xgBindOption,b.deleteProperties(g,["xgInitOption","xgBindOption"]),c=a(this).jqxProgressBar(g),h&&e(c,h,g),c}}else if("string"==typeof arguments[0]){if("xgInitOption"===arguments[0])return a(this).data("xg-init-option");if("xgBindOption"===arguments[0])return a(this).data("xg-bind-option")}return a.fn.jqxProgressBar.apply(this,arguments)},d=function(){var b=Array.prototype.slice.call(arguments,0,1)[0],c=Array.prototype.slice.call(arguments,1)[0];return c?a(b).xgProgress(c):a(b).xgProgress(),a(b)},a(document).ready(function(){var b,c,d,e,f=a("[data-xg-progress]"),g=[];for(c=0,d=f.length;d>c;++c)b=f[c],e=a(b).data("xg-option"),g.push(e?a(b).xgProgress(e):a(b).xgProgress());return g}),XgPlatform.XgLogger.info(function(){return f+" : load end"}),d})}).call(this);