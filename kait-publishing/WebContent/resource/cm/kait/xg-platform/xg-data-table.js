(function(){"use strict";var a=[].indexOf||function(a){for(var b=0,c=this.length;c>b;b++)if(b in this&&this[b]===a)return b;return-1};!function(a,b){return"function"==typeof define&&define.amd?define("XgDataTable",["XgCommon"],b):a.XgDataTable=b(a.XgCommon)}(window,function(b){var c,d,e,f,g,h,i,j="xg-data-table.js";return XgPlatform.XgLogger.info(function(){return j+" : load start"}),c=function(a,c){var d;return this.id=a,this.id||(this.id=b.createUUID()),this.header=[],this.body=[],this.rowPos=-1,this.parent=void 0,this.uiAdapter=void 0,this.booleanColumn={},this.url={},this.bindedComponents={},this.preventXSS=!1,this.source=null,this.overrideColumn=!1,c?(c.preventXSS===!0&&(this.preventXSS=!0),c.booleanColumn&&c.booleanColumn instanceof Array&&this.setBooleanColumn(c.booleanColumn),d=c.overrideColumn,(d===!0||d===!1)&&this.useOverrideColumn(c.overrideColumn),c.url instanceof Object&&Object.keys(c.url)&&this.setUrl(c.url),this):this},e=function(a){return"string"!=typeof a||isNaN(a)||(a=parseInt(a,10)),"number"!=typeof a&&(a=null),a},f=function(a,c){if(null===a)return a;if(void 0===a||!c)throw new b.Error("Can not convert value, type",j);switch(c){case"int":case"long":case"bigint":a=parseInt(a,10);break;case"decimal":a=parseFloat(a);break;case"bool":a=Boolean(a);break;default:a.toString()}return a},h=function(a){return b.getKeyByValue(b.constant.DATATYPE,a)},g=function(a){return b.getKeyByValue(b.constant.DATATYPE_GAUCE,a)},d=function(a,b,c,d,f,g){return f=e(f),g||(g="normal"),a.header.push([a.getColumnCount(),b,c,d,f,g]),a},i=c.prototype,i.getUrl=function(a){var b=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getUrl",b]}),a?this.url[a]:this.url},i.setUrl=function(a,c){var d=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.setUrl",d]}),"string"==typeof a)this.url[a]=c;else{if("object"!=typeof a)throw new b.Error("type of URL is object or string",j);this.url=a}return this},i.getId=function(){var a=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getId",a]}),this.id},i.clearData=function(){var a,b,c=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.clearData",c]}),this.body.length=0,this.rowPos=-1,a=this.source,b=this.uiAdapter,null!=(null!=a?a.localdata:void 0)&&(a.localdata.length=0),null!=(null!=b?b.cachedrecords:void 0)&&(b.cachedrecords.length=0),null!=(null!=b?b.records:void 0)&&(b.records.length=0),this.uiAdapter=null,this},i.resetDataTable=function(){var a=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.resetDataTable",a]}),this.header.length=0,this.clearData()},i.addColumn=function(a,c,e,f){var g,i=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.addColumn",i]}),!(a&&c&&e))throw new b.Error("name, type, size are required",j);if(g="string","number"==typeof c)c=h(c);else{if("string"!=typeof c)throw new b.Error('typeof "type" is not "number" or "string"',j);switch(c){case"int8":case"int16":case"int32":case"uint8":case"uint16":case"uint32":g="int";break;case"int64":case"uint64":g="bigint";break;case"double":g="decimal";break;case"unknown":g="blob"}}return d(this,a,c,g,e,f)},i.addColumnForGauce=function(a,c,e,f){var h,i=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.addColumnForGauce",i]}),!(a&&c&&e))throw new b.Error("name, typeOfGauce, size are required",j);if(h="string","number"==typeof c)c=g(c);else{if("string"!=typeof c)throw new b.Error('typeof "typeOfGauce" is not "number" or "string"',j);switch(c){case"int":case"long":h="int32";break;case"bigint":h="int64";break;case"decimal":h="double";break;case"blob":h="unknown"}}return d(this,a,h,c,e,f)},i.addRow=function(a,c){var d,e,f,g,h,i,k=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.addRow",k]}),a[-1]&&delete a[-1],d=this.getColumnCount(),0===d)throw new b.Error("DataTable header is empty. Can not add row.",j);if(a&&!(a instanceof Array))throw new b.Error("rowData must be array",j);if(a&&a.length)for(a[0]instanceof Array||(a=[a],a[0].status=b.constant.STATUS.INSERT),e=0,g=a.length;g>e;++e){if(h=a[e],h.length!==d)throw new b.Error("rowData size is not equal to columnCount",j);if(i=c?b.constant.STATUS.NORMAL:b.constant.STATUS.INSERT,a[e].status=i,this.preventXSS)for(f=a[e].length;f--;)a[e][f]=b.filterXSS(a[e][f])}return a||(a=[new Array(d)],a[0].status=b.constant.STATUS.INSERT),this.body=this.body.concat(a),this},i.insertRow=function(a,c){var d,f,g=this.getColumnCount(),h=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.insertRow",h]}),a=e(a),0===g)throw new b.Error("DataTable header is empty. Can not insert row.",j);if(c&&!(c instanceof Array))throw new b.Error("rowData must be array",j);if(c&&c.length!==g)throw new b.Error("rowData size is not equal to columnCount",j);return c||(c=new Array(g)),f=b.constant.STATUS.INSERT,d=this.getRowCount(),a>d&&(a=d,console.warn("rowIdx is bigger then rowCount. rowIdx set to lastIdx")),0>a&&(a=0,console.warn("rowIdx is smaller then 0. rowIdx set to 0")),c||(c=new Array(g)),c.status=f,this.body.splice(a,0,c),this},i.deleteRow=function(a){var c=this.getRow(a),d=arguments,e=c.status;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.deleteRow",d]}),e===b.constant.STATUS.DELETE?console.warn("Row already deleted."):e===b.constant.STATUS.INSERT?this.body.splice(a,1):this.setRowStatus(a,b.constant.STATUS.DELETE),this},i.getValue=function(a,b){var c=-1,d=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getValue",d]}),"string"==typeof b?c=this.getColumnIndex(b):"number"==typeof b&&(c=b),this.body[a][c]},i.setValue=function(a,c,d){var e,g,h,i=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.setValue",i]}),-1===c)return this;if(e=-1,void 0===d)throw new b.Error("Value is required",j);if("string"==typeof c?e=this.getColumnIndex(c):"number"==typeof c&&(e=c),h=this.getRowStatus(a),h===b.constant.STATUS.DELETE)console.warn("Row already deleted.");else{if(d=f(d,this.header[e][b.constant.COLUMN.TYPE_OF_GAUCE]),g=this.body[a][e],g===d)return this;this.preventXSS&&(d=b.filterXSS(d)),this.body[a][e]=d,h!==b.constant.STATUS.INSERT&&this.setRowStatus(a,b.constant.STATUS.UPDATE)}return this},i.getValueSize=function(a,b){var c=arguments,d=this.getValue(a,b);return XgPlatform.XgLogger.debug(function(){return["XgDataTable.setValue",c]}),d.length||(d=d.toString()),d.length},i.getRowStatus=function(a){var b=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getRowStatus",b]}),this.body[a].status},i.getRowStatusCode=function(a){var c=b.getKeyByValue(b.constant.STATUS,a),d=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getRowStatusCode",d]}),c="UNKNOWN"===c?"UN":c.substr(0,1),"I"===c&&(c="C"),c},i.setRowStatus=function(a,c){var d=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.setRowStatus",d]}),b.getKeyByValue(b.constant.STATUS,c),this.body[a].status=c,this},i.clearRowStatus=function(a){var c=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.clearRowStatus",c]}),this.setRowStatus(a,b.constant.STATUS.NORMAL)},i.getColumnCount=function(){var a=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getColumnCount",a]}),this.header.length},i.getColumnIndex=function(a){var c,d,e,f,g=-1,h=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.getColumnIndex",h]}),"*xgRowStatus"===a)return g;for(d=this.header,e=0,f=d.length;f>e;++e)if(c=d[e],c[b.constant.COLUMN.NAME]===a){g=c[b.constant.COLUMN.INDEX];break}return g},i.getColumnName=function(a){var c=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getColumnName",c]}),this.header[a][b.constant.COLUMN.NAME]},i.getColumnTypeStr=function(a){var c=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getColumnTypeStr",c]}),this.header[a][b.constant.COLUMN.TYPE]},i.getColumnType=function(a){var c=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getColumnType",c]}),b.constant.DATATYPE[this.getColumnTypeStr(a)]},i.getColumnTypeStrForGauce=function(a){var c=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getColumnTypeStrForGauce",c]}),this.header[a][b.constant.COLUMN.TYPE_OF_GAUCE]},i.getColumnTypeForGauce=function(a){var c=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getColumnTypeForGauce",c]}),b.constant.DATATYPE_GAUCE[this.getColumnTypeStrForGauce(a)]},i.getColumnSize=function(a){var c=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getColumnSize",c]}),this.header[a][b.constant.COLUMN.SIZE]},i.getColumnConstraint=function(a){var c=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getColumnConstraint",c]}),this.header[a][b.constant.COLUMN.CONSTRAINT]},i.getRow=function(a){var b=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getRow",b]}),this.body[a]},i.getRowCount=function(){var a=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getRowCount",a]}),this.body.length},i.getRowPos=function(){var a=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataTable.getRowPos",a]}),this.rowPos},i.setRowPos=function(a){var c=this.getRowCount()-1,d=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.setRowPos",d]}),0>c)throw new b.Error("Row data is not exist",j);return a=e(a),0>a&&(a=0,console.warn("rowIdx is smaller then 0. rowIdx set to 0")),a>c&&(a=c,console.warn("rowIdx is bigger then rowCount. rowIdx set to lastIdx")),this.rowPos=a,this},i.find=function(c,d,e){var f,g,h=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.find",h]}),!d)throw new b.Error("Value is required",j);for(e||(e=0,console.warn("StartRowIdx is undefined. StartRowIdx set to 0")),f=e,g=this.getRowCount();g>f;++f)if(d instanceof Array){if(a.call(d,this.body[f][c])>=0)return f}else if(this.body[f][c]===d)return f;return-1},i.findAll=function(c,d,e){var f,g,h,i=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.findAll",i]}),!d)throw new b.Error("Value is required",j);for(e||(e=0,console.warn("StartRowIdx is undefined. StartRowIdx set to 0")),f=[],g=e,h=this.getRowCount();h>g;++g)d instanceof Array?a.call(d,this.body[g][c])>=0&&f.push(g):this.body[g][c]===d&&f.push(g);return f},i.setBooleanColumn=function(a){var b,c,d,e=arguments;for(XgPlatform.XgLogger.debug(function(){return["XgDataTable.setBooleanColumn",e]}),b=0,c=a.length;c>b;++b)d=a[b],this.booleanColumn[d.column]=a[b];return this},i.useOverrideColumn=function(a){var c=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataTable.useOverrideColumn",c]}),0===arguments.length)return this.overrideColumn;if("boolean"!=typeof a)throw new b.Error("Parameter will be boolean type",j);return this.overrideColumn=a,this},XgPlatform.XgLogger.info(function(){return j+" : load end"}),c})}).call(this);