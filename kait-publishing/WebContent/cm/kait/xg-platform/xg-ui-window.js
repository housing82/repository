(function(){"use strict";!function(a,b){return"function"==typeof define&&define.amd?define("XgUIWindow",["jquery"],b):a.XgUIWindow=b(a.jQuery)}(window,function(a){var b,c="xg-ui-window.js";return XgPlatform.XgLogger.info(function(){return c+" : load start"}),a.fn.xgWindow=function(){var b=this,c=arguments;return a(this).attr("data-xg-window",""),this.selector&&(b=this.selector),c=arguments,XgPlatform.XgLogger.debug(function(){return["XgUIWindow",b,c]}),a.fn.jqxWindow.apply(this,arguments)},b=function(){var b=Array.prototype.slice.call(arguments,0,1)[0],c=Array.prototype.slice.call(arguments,1)[0];return c?a(b).xgWindow(c):a(b).xgWindow(),a(b)},a(document).ready(function(){var b,c,d,e,f=a("[data-xg-window]"),g=[];for(c=0,d=f.length;d>c;++c)b=f[c],e=a(b).data("xg-option"),g.push(e?a(b).xgWindow(e):a(b).xgWindow());return g}),XgPlatform.XgLogger.info(function(){return c+" : load end"}),b})}).call(this);