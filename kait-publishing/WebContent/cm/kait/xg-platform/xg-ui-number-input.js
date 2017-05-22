(function(){"use strict";!function(a,b){return"function"==typeof define&&define.amd?define("XgUINumberInput",["jquery","XgCommon","XgUICommon"],b):a.XgUINumberInput=b(a.jQuery,a.XgCommon,a.XgUICommon)}(window,function(a,b,c){var d,e,f;return f="xg-ui-number-input.js",XgPlatform.XgLogger.info(function(){return f+" : load start"}),e=function(d,e){var f,g,h,i,j,k;return i=d.data("xg-bind-data-set"),k=d.data("xg-bind-data-table-id"),j=i.getDataTable(k),f=d.data("xg-bind-option"),h={},f||(f={mode:"write",sync:!0,immediately:!0,bind:{type:"column"}},d.data("xg-bind-option",f)),c.setUUID(d),i.bindComponent(d,"jqxNumberInput"),a.extend(!0,h,f,e),d.data("xg-bind-option",h),c.setBindColInfo(i.getDataTable(k),h),c.removeEvents(d,["valueChanged","change","xg-bind-event"]),("write"===(g=h.mode)||"read"===g)&&d.on("xg-bind-event",function(a,c){var e,f,g,l,m,n,o,p,q;if(d.data("xg-uuid")===c.uuid||c.xgDataTableId!==k)return!1;if(q=void 0,"setRowPos"===c.eventName)o=c.rowIdx,n=j.getRow(o),p=n.status,e=h.bind.columnIndex,d.data("xg-target-index",o),"column"===h.bind.type&&(q=void 0===n[e]?0:n[e]),h.sync&&(g=!1,(p===b.constant.STATUS.DELETE||p!==b.constant.STATUS.INSERT&&((m=j.getColumnConstraint(e))===b.constant.CONSTRAINT.KEY||m===b.constant.CONSTRAINT.SEQUENCE))&&(g=!0),d.jqxNumberInput("disabled")!==g&&d.jqxNumberInput({disabled:g}));else if("xgDataTableInit"===c.eventName)q=0;else{if(f=d.data("xg-target-index"),c.rowIdx!==f)return!1;if("setValue"===c.eventName){if("column"!==h.bind.type||c.bind.column!==h.bind.column)return!1;q=c.value}"deleteRow"===c.eventName&&h.sync&&d.jqxNumberInput({disabled:!0})}return l=h.immediately?"valueChanged":"change","write"===h.mode&&d.off(l),void 0!==q&&d.jqxNumberInput("val",q),b.isIEBrowser()&&d.find("input").trigger("blur"),"write"===h.mode?d.on(l,function(){return i.$eventEmitter.trigger("xg-bind-event",{uuid:d.data("xg-uuid"),eventName:"setValue",bind:h.bind,rowIdx:i.getDataTable(k).getRowPos(),value:d.jqxNumberInput("val"),xgDataTableId:k})}):void 0}),d},a.fn.xgNumberInput=function(){var d,g,h,i,j,k,l,m;if(a(this).attr("data-xg-number-input",""),g=this,this.selector&&(g=this.selector),k=arguments,XgPlatform.XgLogger.debug(function(){return["XgUINumberInput",g,k]}),arguments[0]instanceof Object){if(j=arguments[0],j.xgInitOption){if(i={},h=a(this).data("xg-init-option"),a.extend(!0,i,h,j.xgInitOption),a(this).data("xg-init-option",i),m=c.setXgDataTable(j.xgInitOption.xgDataTable,j),"string"==typeof j.xgInitOption.xgDataTable){if(!j.xgInitOption.xgDataSet)throw new b.Error("Require xgDataSet option property",f);j.xgInitOption.xgDataTable=j.xgInitOption.xgDataSet.getDataTable(j.xgInitOption.xgDataTable)}return a(this).data("xg-bind-data-set",j.xgInitOption.xgDataSet),a(this).data("xg-bind-data-table-id",j.xgInitOption.xgDataTable.getId()),l=j.xgBindOption,b.deleteProperties(j,["xgInitOption","xgBindOption"]),d=a(this).jqxNumberInput(j),l&&e(d,l,j),d}}else if("string"==typeof arguments[0]){if("xgInitOption"===arguments[0])return a(this).data("xg-init-option");if("xgBindOption"===arguments[0])return a(this).data("xg-bind-option")}return a.fn.jqxNumberInput.apply(this,arguments)},d=function(){var b,c;return b=Array.prototype.slice.call(arguments,0,1)[0],c=Array.prototype.slice.call(arguments,1)[0],c||a(b).xgNumberInput(),c&&a(b).xgNumberInput(c),a(b)},a(document).ready(function(){var b,c,d,e,f,g,h,i;for(h=a("[data-xg-number-input]"),i=[],f=e=0,g=h.length;g>e;f=++e)d=h[f],b=a(d),c=b.data("xg-option"),c&&b.xgNumberInput(c),i.push(c?void 0:b.xgNumberInput());return i}),XgPlatform.XgLogger.info(function(){return f+" : load end"}),d})}).call(this);