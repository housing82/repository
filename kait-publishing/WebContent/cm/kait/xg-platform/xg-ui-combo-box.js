(function(){"use strict";!function(a,b){return"function"==typeof define&&define.amd?define("XgUIComboBox",["jquery","XgCommon","XgUICommon"],b):a.XgUIComboBox=b(a.jQuery,a.XgCommon,a.XgUICommon)}(window,function(a,b,c){var d,e,f;return f="xg-ui-combo-box.js",XgPlatform.XgLogger.info(function(){return f+" : load start"}),e=function(d,e){var f,g,h,i,j,k;return i=d.data("xg-bind-data-set"),k=d.data("xg-bind-data-table-id"),j=i.getDataTable(k),f=d.data("xg-bind-option"),h={},f||(f={mode:"read",sync:!0,immediately:!0,bind:{type:"column"}},d.data("xg-bind-option",f)),c.setUUID(d),i.bindComponent(d,"jqxComboBox"),a.extend(!0,h,f,e),d.data("xg-bind-option",h),c.setBindColInfo(j,h),c.removeEvents(d,["select","xg-bind-event"]),("write"===(g=h.mode)||"read"===g)&&(d.on("xg-bind-event",function(c,e){var f,g,i,l,m,n,o,p;if(a(this).data("xg-uuid")===e.uuid||e.xgDataTableId!==k)return!1;if("setRowPos"===e.eventName){if(o=e.rowIdx,n=j.getRow(o),p=n.status,f=h.bind.columnIndex,d.data("xg-target-index",o),"column"===h.bind.type&&(m=n[f],null===m?d.jqxComboBox("clearSelection"):d.jqxComboBox("val",m)),h.sync&&(i=!1,(p===b.constant.STATUS.DELETE||p!==b.constant.STATUS.INSERT&&((l=j.getColumnConstraint(f))===b.constant.CONSTRAINT.KEY||l===b.constant.CONSTRAINT.SEQUENCE))&&(i=!0),d.jqxComboBox("disabled")!==i))return d.jqxComboBox({disabled:i})}else{if("xgDataTableInit"===e.eventName)return d.jqxComboBox("clearSelection");if(g=d.data("xg-target-index"),e.rowIdx!==g)return!1;if("setValue"===e.eventName&&"column"===h.bind.type&&e.bind.column===h.bind.column&&d.jqxComboBox("val",e.value),"deleteRow"===e.eventName&&h.sync)return d.jqxComboBox({disabled:!0})}}),"write"===h.mode&&d.on("select",function(a){return a.args&&"none"===a.args.type?void 0:i.$eventEmitter.trigger("xg-bind-event",{uuid:d.data("xg-uuid"),eventName:"setValue",bind:h.bind,rowIdx:j.getRowPos(),value:d.jqxComboBox("val"),xgDataTableId:k})})),d},a.fn.xgComboBox=function(){var d,g,h,i,j,k,l,m;if(a(this).attr("data-xg-combo-box",""),g=this,this.selector&&(g=this.selector),k=arguments,XgPlatform.XgLogger.debug(function(){return["XgUIComboBox",g,k]}),arguments[0]instanceof Object){if(j=arguments[0],j.xgInitOption){if(i={},h=a(this).data("xg-init-option"),a.extend(!0,i,h,j.xgInitOption),a(this).data("xg-init-option",i),"string"==typeof j.xgInitOption.xgDataSet&&(j.xgInitOption.xgDataSet=b.getDataSet(j.xgInitOption.xgDataSet)),m=c.setXgDataTable(j.xgInitOption.xgDataTable,j),"string"==typeof j.xgInitOption.xgDataTable){if(!j.xgInitOption.xgDataSet)throw new b.Error("Require xgDataSet option property",f);j.xgInitOption.xgDataTable=j.xgInitOption.xgDataSet.getDataTable(j.xgInitOption.xgDataTable)}return a(this).data("xg-bind-data-set",j.xgInitOption.xgDataSet),a(this).data("xg-bind-data-table-id",j.xgInitOption.xgDataTable.getId()),l=j.xgBindOption,b.deleteProperties(j,["xgInitOption","xgBindOption"]),d=a(this).jqxComboBox(j),l&&e(d,l,j),d}}else if("string"==typeof arguments[0]){if("xgInitOption"===arguments[0])return a(this).data("xg-init-option");if("xgBindOption"===arguments[0])return a(this).data("xg-bind-option")}return a.fn.jqxComboBox.apply(this,arguments)},d=function(){var b,c;return b=Array.prototype.slice.call(arguments,0,1)[0],c=Array.prototype.slice.call(arguments,1)[0],c||a(b).xgComboBox(),c&&a(b).xgComboBox(c),a(b)},a(document).ready(function(){var b,c,d,e,f,g,h,i;for(h=a("[data-xg-combo-box]"),i=[],f=e=0,g=h.length;g>e;f=++e)d=h[f],b=a(d),c=b.data("xg-option"),c&&b.xgComboBox(c),i.push(c?void 0:b.xgComboBox());return i}),XgPlatform.XgLogger.info(function(){return f+" : load end"}),d})}).call(this);