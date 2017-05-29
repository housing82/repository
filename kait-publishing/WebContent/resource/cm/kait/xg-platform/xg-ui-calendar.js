(function(){"use strict";!function(a,b){return"function"==typeof define&&define.amd?define("XgUICalendar",["jquery","XgCommon","XgUICommon"],b):a.XgUICalendar=b(a.jQuery,a.XgCommon,a.XgUICommon)}(window,function(a,b,c){var d,e,f="xg-ui-calendar.js";return XgPlatform.XgLogger.info(function(){return f+" : load start"}),e=function(d,e){var f,g=d.data("xg-bind-option"),h={},i=d.data("xg-bind-data-set"),j=d.data("xg-bind-data-table-id"),k=i.getDataTable(j);return g||(g={mode:"write",immediately:!0,sync:!0,bind:{type:"column"}},d.data("xg-bind-option",g)),c.setUUID(d),i.bindComponent(d,"jqxCalendar"),a.extend(!0,h,g,e),d.data("xg-bind-option",h),c.setBindColInfo(i.getDataTable(j),h),c.removeEvents(d,["valueChanged","change","xg-bind-event"]),f=h.mode,("write"===f||"read"===f)&&d.on("xg-bind-event",function(a,c){var e,f,g,l,m,n,o,p,q;if(d.data("xg-uuid")===c.uuid||c.xgDataTableId!==j)return!1;if("setRowPos"===c.eventName)o=c.rowIdx,n=k.getRow(o),p=n.status,e=h.bind.columnIndex,d.data("xg-target-index",o),"column"===h.bind.type&&(m=n[e],q=void 0===m?new Date:b.getDateStrFromGauceDate(m)),h.sync&&(l=!1,f=k.getColumnConstraint(e),(p===b.constant.STATUS.DELETE||p!==b.constant.STATUS.INSERT&&(f===b.constant.CONSTRAINT.KEY||f===b.constant.CONSTRAINT.SEQUENCE))&&(l=!0),d.jqxCalendar("disabled")!==l&&d.jqxCalendar({disabled:l}));else if("xgDataTableInit"===c.eventName)q=new Date;else{if(g=d.data("xg-target-index"),c.rowIdx!==g)return!1;if("setValue"===c.eventName){if("column"!==h.bind.type||c.bind.column!==h.bind.column)return!1;q=b.getDateStrFromGauceDate(c.value)}else"deleteRow"===c.eventName&&h.sync&&d.jqxCalendar({disabled:!0})}return"write"===h.mode&&d.off("valueChanged"),d.jqxCalendar("val",q),"write"===h.mode?d.on("valueChanged",function(){return i.$eventEmitter.trigger("xg-bind-event",{uuid:d.data("xg-uuid"),eventName:"setValue",bind:h.bind,rowIdx:i.getDataTable(j).getRowPos(),value:b.getGauceDateStr(d.jqxCalendar("value")),xgDataTableId:j})}):void 0}),d},a.fn.xgCalendar=function(){var c,d,g,h,i,j=this.selector||this,k=arguments;if(a(this).attr("data-xg-calendar",""),XgPlatform.XgLogger.debug(function(){return["XgUICalendar",j,k]}),arguments[0]instanceof Object){if(h=arguments[0],h.xgInitOption){if(g={},d=a(this).data("xg-init-option"),a.extend(!0,g,d,h.xgInitOption),a(this).data("xg-init-option",g),"string"==typeof h.xgInitOption.xgDataSet&&(h.xgInitOption.xgDataSet=b.getDataSet(h.xgInitOption.xgDataSet)),"string"==typeof h.xgInitOption.xgDataTable){if(!h.xgInitOption.xgDataSet)throw new b.Error("Require xgDataSet option property",f);h.xgInitOption.xgDataTable=h.xgInitOption.xgDataSet.getDataTable(h.xgInitOption.xgDataTable)}return a(this).data("xg-bind-data-set",h.xgInitOption.xgDataSet),a(this).data("xg-bind-data-table-id",h.xgInitOption.xgDataTable.getId()),i=h.xgBindOption,b.deleteProperties(h,["xgInitOption","xgBindOption"]),c=a(this).jqxCalendar(h),i&&e(c,i,h),c}}else if("string"==typeof arguments[0]){if("xgInitOption"===arguments[0])return a(this).data("xg-init-option");if("xgBindOption"===arguments[0])return a(this).data("xg-bind-option")}return a.fn.jqxCalendar.apply(this,arguments)},d=function(){var b=Array.prototype.slice.call(arguments,0,1)[0],c=Array.prototype.slice.call(arguments,1)[0];return c||a(b).xgCalendar(),c&&a(b).xgCalendar(c),a(b)},a(document).ready(function(){var b,c,d,e,f,g=a("[data-xg-calendar]"),h=[];for(e=0,f=g.length;f>e;++e)d=g[e],b=a(d),c=b.data("xg-option"),c&&b.xgCalendar(c),h.push(c?void 0:b.xgCalendar());return h}),XgPlatform.XgLogger.info(function(){return f+" : load end"}),d})}).call(this);