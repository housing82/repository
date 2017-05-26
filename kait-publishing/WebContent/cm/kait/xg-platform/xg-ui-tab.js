(function(){"use strict";!function(a,b){return"function"==typeof define&&define.amd?define("XgUITab",["jquery"],b):a.XgUITab=b(a.jQuery,a.XgUICommon)}(window,function(a){var b,c,d,e,f={},g="xg-ui-tab.js";return XgPlatform.XgLogger.info(function(){return g+" : load start"}),d=function(b,d,e,h){var i,j,k;if(f.hasOwnProperty(e))throw new XgCommon.Error("Tab Name["+e+"] is already exists!",g);return j=a(b).attr("id"),k=(j?j:"noName")+("_"+XgCommon.createUUID()),i='&lt;div id="'+k+'" style="padding:20px;"&gt;&lt;/div&gt;',a(b).jqxTabs("addAt",d,e,i),a.get(h,function(d){var g,h,j,l,m;return i=a("&lt;div&gt;"+d+"&lt;/div&gt;"),l=a(i).find("script"),l.length>0&&(g="var _self = this; "+a(l).text(),j=new Function(g),m=new j,i.find("script").remove(),f[e]=m),h=a("#"+k),h.append(i).ready(function(){var d;return m.element=h,m.onLoad(),d=a(b).find(".jqx-tabs-close-button"),d.unbind(),d.click(function(){var d=a(b).jqxTabs("getSelectedItem"),e=a(b).jqxTabs("getTitleAt",d);return c(e),a(b).jqxTabs("removeAt",d)})})})},e=function(b,d){var e=a(b).jqxTabs("getTitleAt",d);return c(e),a(b).jqxTabs("removeAt",d)},c=function(a){var b=f[a];return b?(b.onClose(),delete f[a]):void 0},a.fn.xgTab=function(){var b,f,g,h=this,i=arguments;if(a(this).attr("data-xg-tab",""),this.selector&&(h=this.selector),XgPlatform.XgLogger.debug(function(){return["XgUITab",h,i]}),f=arguments[0],"loadAt"===f&&4===arguments.length)d(this,arguments[1],arguments[2],arguments[3]);else if("removeAt"===f&&2===arguments.length)e(this,arguments[1]);else if("removeFirst"===f&&1===arguments.length)b=a(element).jqxTabs("length"),b>0&&e(this,0);else if("removeLast"===f&&1===arguments.length)b=a(element).jqxTabs("length"),b>0&&e(this,b-1);else{if("destroy"!==f)return a.fn.jqxTabs.apply(this,arguments);for(b=a(element).jqxTabs("length");b--;)g=a(element).jqxTabs("getTitleAt",b),c(g)}},b=function(){var b=Array.prototype.slice.call(arguments,0,1)[0],c=Array.prototype.slice.call(arguments,1)[0];return c?a(b).xgTab(c):a(b).xgTab(),a(b)},a(document).ready(function(){var b,c,d,e,f=a("[data-xg-tab]"),g=[];for(c=0,d=f.length;d>c;++c)b=f[c],e=a(b).data("xg-option"),g.push(e?a(b).xgTab(e):a(b).xgTab());return g}),XgPlatform.XgLogger.info(function(){return g+" : load end"}),b})}).call(this);