(function(){"use strict";var a=[].indexOf||function(a){for(var b=0,c=this.length;c>b;b++)if(b in this&&this[b]===a)return b;return-1},b={}.hasOwnProperty;!function(a,b){return"function"==typeof define&&define.amd?define("XgDataSet",["jquery","XgCommon","XgDataTable","XgDataAdapter","json3"],b):a.XgDataSet=b(a.jQuery,a.XgCommon,a.XgDataTable,a.XgDataAdapter,a.JSON)}(window,function(c,d,e,f,g){var h,i,j,k,l,m,n,o,p,q,r,s=["data-xg-grid","data-xg-chart"],t="xg-data-set.js";return XgPlatform.XgLogger.info(function(){return t+" : load start"}),j=function(a,b){var c,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I=void 0;try{for(I=document.implementation.createDocument("http://www.shift.co.kr/xmlns/shiftProtocol","sp:envelope",null),B=0,C=b.length;C>B;++B){for(A=b[B],c=a.getDataTable(b[B]),j=Object.keys(c.booleanColumn),s=I.createElement("sp:datatable"),s.setAttribute("name",c.getId()),I.documentElement.appendChild(s),x=I.createElement("sp:header"),s.appendChild(x),u=c.header,y=0,z=u.length;z>y;++y)w=u[y],n=c.header[y],r=I.createElement("sp:column"),r.setAttribute("name",n[d.constant.COLUMN.NAME]),r.setAttribute("type",n[d.constant.COLUMN.TYPE_OF_GAUCE]),r.setAttribute("size",n[d.constant.COLUMN.SIZE]),r.setAttribute("constraint",n[d.constant.COLUMN.CONSTRAINT]),x.appendChild(r);for(f=I.createElement("sp:body"),f.setAttribute("use","I"),s.appendChild(f),v=c.body,g=0,h=v.length;h>g;++g)if(F=v[g],e=c.body[g],G=e.status,G===d.constant.STATUS.INSERT||G===d.constant.STATUS.UPDATE||G===d.constant.STATUS.DELETE)for(E=I.createElement("sp:row"),E.setAttribute("crud",c.getRowStatusCode(e.status)),f.appendChild(E),y=0,z=u.length;z>y;++y)o=u[y],n=c.header[y],m=c.getValue(g,y),p=n[d.constant.COLUMN.NAME],q=n[d.constant.COLUMN.TYPE_OF_GAUCE],l=I.createElement(p),"int"!==q&&"long"!==q&&"decimal"!==q&&"bigint"!==q||m||(m=0),void 0===m||null===m?(l.setAttribute("isNull","true"),m=""):j.length&&c.booleanColumn[p]&&(i=c.booleanColumn[p],m=m?i.trueValue:i.falseValue),"string"!==q&&"url"!==q&&"blob"!==q&&"char"!==q||""===m?("date"===q&&""!==m&&("object"==typeof m?m=d.getGauceDateStr(m):"string"==typeof m&&8!==m.length&&(m=m.replace(/(\s*)/g,""),m=m.replace(/-/gi,""),m=m.replace(/:/gi,""))),H=I.createTextNode(m),l.appendChild(H)):(k=I.createCDATASection(m),l.appendChild(k)),E.appendChild(l)}return D=I.createElement("sp:messages"),D.setAttribute("issuccess","true"),I.documentElement.appendChild(D),I}catch(J){throw new d.Error("XML make fail",t)}finally{I=null}},i=function(b,c,e){var f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z={envelope:{datatable:[],messages:{"@issuccess":!0,message:[]}}};for(e=e||!1,t=0,u=c.length;u>t;++t){for(q=c[t],n=[],p=b.getDataTable(q),g=Object.keys(p.booleanColumn),f={"@name":q,header:{column:[]},body:{"@use":"I",row:[]}},e&&(delete f["@name"],f.body["@use"]="O"),r=p.header,l=0,s=r.length;s>l;++l)k=r[l],m=k[d.constant.COLUMN.NAME],v={"@name":m,"@type":k[d.constant.COLUMN.TYPE_OF_GAUCE],"@size":k[d.constant.COLUMN.SIZE].toString(),"@constraint":k[d.constant.COLUMN.CONSTRAINT]},f.header.column.push(v),n.push(m);for(h=p.body,x=0,i=h.length;i>x;++x){if(w=h[x],y=w.status,v={},!e){if(y!==d.constant.STATUS.INSERT&&y!==d.constant.STATUS.UPDATE&&y!==d.constant.STATUS.DELETE)continue;v={"@crud":p.getRowStatusCode(y)}}for(l=0,o=n.length;o>l;++l)m=n[l],j=p.getValue(x,l),e||(null===j||void 0===j)&&(j={"@isnull":"true"}),g.length&&a.call(g,m)>=0&&(j=j?p.booleanColumn[m].trueValue:p.booleanColumn[m].falseValue),"date"===f.header.column[l]["@type"]&&""!==j&&("object"==typeof j?j=d.getGauceDateStr(j):"string"==typeof j&&8!==j.length&&(j=j.replace(/(\s*)/g,""),j=j.replace(/-/gi,""),j=j.replace(/:/gi,""))),v[m]=j;f.body.row.push(v)}e?z=f:z.envelope.datatable.push(f)}return z},k=function(b,c,e){for(var f,g,h,i,j,k,m,n,o,p,q,r,s,t,u=d.constant.CONSTRAINT_NUM,v=d.constant.DATATYPE_GAUCE,w=c.length;w--;){for(o=b.getDataTable(c[w]),f=o.booleanColumn,g=Object.keys(f),o.resetDataTable(),m=e.columns,i=0,j=m.length;j>i;++i)h=m[i],o.addColumnForGauce(h.name,d.getKeyByValue(v,h.type),h.size,d.getKeyByValue(u,h.prop).toLowerCase()),a.call(g,h.name)>=0&&booleanKeyIndexes.push({key:h.name,idx:i});for(t=e.rows,r=0,s=t.length;s>r;++r){for(q=t[r],p=o.header,i=0,j=p.length;j>i;++i)h=p[i],n=l(h[d.constant.COLUMN.TYPE_OF_GAUCE],q.V[i]),k=h[d.constant.COLUMN.NAME],g.length&&a.call(g,k)>=0&&(n=n===f[k].trueValue),q.V[i]=n;o.addRow(q.V),o.clearRowStatus(r)}o.changeCount=0}return b},n=function(b,c,e){var f,g,h,i,j,k,m,n,o,p,q,r,s,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q="textContent";if(e=d.stringToXML(e),G=e.childNodes[0],G.xml&&(Q="text",G=e.getElementsByTagName("sp:envelope").item(0)),!G||"sp:envelope"!==G.nodeName||"http://www.shift.co.kr/xmlns/shiftProtocol"!==G.getAttribute("xmlns:sp"))throw XgPlatform.XgLogger.error(function(){return"XgDataSet._parseXML : Can not parse xml. Only can parse type of 'http://www.shift.co.kr/xmlns/shiftProtocol'"}),new d.Error('Can not parse xml. Only can parse type of "http://www.shift.co.kr/xmlns/shiftProtocol"',t);for(F={},x=G.childNodes,y=0,z=x.length;z>y;++y)if(E=x[y],"sp:datatable"===E.nodeName){if(s=E.getAttribute("name"),!b.isExistDataTable(s)&&!b.useXgServer)for(u=0,v=c.length;v>u;++u)if(N=c[u],b.isExistDataTable(N)){s=N;break}if(!s)continue;for(r=b.getDataTable(s),f=Object.keys(r.booleanColumn),r.useOverrideColumn()?r.clearData():r.resetDataTable(),A=E.childNodes,B=0,C=A.length;C>B;++B)if(g=A[B],"sp:header"!==g.nodeName||r.useOverrideColumn()){if("sp:body"===g.nodeName)for(L=g.childNodes,J=0,K=L.length;K>J;++J)if(H=L[J],H.nodeType===Node.ELEMENT_NODE){for(I=[],o=r.header,k=0,n=o.length;n>k;++k)j=o[k],m=j[d.constant.COLUMN.NAME],h=H.getElementsByTagName(m),q=null,h.length&&(q=l(j[d.constant.COLUMN.TYPE_OF_GAUCE],h[0][Q])),f.length&&a.call(f,m)>=0&&(q=q===r.booleanColumn[m].trueValue),I.push(q);r.addRow(I);var R=r.getRowCount()-1;r.clearRowStatus(R)}}else for(o=g.childNodes,k=0,n=o.length;n>k;++k)j=o[k],j.nodeType===Node.ELEMENT_NODE&&(D=j.getAttribute("name"),P=j.getAttribute("type"),M=j.getAttribute("size"),p=j.getAttribute("constraint"),r.addColumnForGauce(D,P,M,p));r.changeCount=0}else if("sp:messages"===E.nodeName){if(!E.getAttribute("issuccess"))throw new d.Error("Fail to receive xml data",t);for(F.result="true"===E.getAttribute("issuccess")?!0:!1,F.errorMsg=null,F.user={},F.msg=[],A=E.childNodes,w=0,B=0,C=A.length;C>B;B++)g=A[B],"sp:message"===g.nodeName&&(P=g.getAttribute("type"),i=g.getAttribute("code"),O=g.textContent||g.text,"INTERNAL"===P?F.errorMsg=O:"UserMsg"===P&&(F.user[i]=O),F.msg[w]={type:P,code:i,text:O},w++)}return F},q=function(b,c,e,f){var g,h,i,j,k,m,n,o,p,q,r,s,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P;if("row"!==f||e){for(F={},A=0,B=c.length;B>A;++A)if(x=c[A],b.useXgServer||b.isExistDataTable(x))switch(w=b.getDataTable(x),e=e.replace(/sp:/gi,""),e="<root>"+e+"</root>",P=d.stringToXML(e),m=P.childNodes[0],f){case"column":if(w.useOverrideColumn())w.clearData();else for(w.resetDataTable(),j=m.childNodes,p=0,q=j.length;q>p;++p)h=j[p],h.nodeType===Node.ELEMENT_NODE&&(E=h.getAttribute("name"),O=h.getAttribute("type"),M=h.getAttribute("size"),u=h.getAttribute("constraint"),w.addColumnForGauce(E,O,M,u));XgPlatform.XgLogger.info(function(){return"[ id : "+w.getId()+" ] init"}),b.$eventEmitter.trigger("xg-bind-event",{uuid:void 0,eventName:"xgDataTableInit",rowIdx:-1,xgDataTableId:w.getId()});break;case"row":for(g=Object.keys(w.booleanColumn),L=[],y=[],n=m.childNodes,J=0,K=n.length;K>J;++J)if(G=n[J],G.nodeType===Node.ELEMENT_NODE){for(H=[],I={"*xgRowStatus":d.constant.STATUS.NORMAL,"*xgSequence":-1},s=w.header,p=0,q=s.length;q>p;++p)o=s[p],r=o[d.constant.COLUMN.NAME],i=G.getElementsByTagName(r),v=null,i.length&&(v=l(o[d.constant.COLUMN.TYPE_OF_GAUCE],i[0].textContent||i[0].text)),g.length&&a.call(g,r)>=0&&(v=v===w.booleanColumn[r].trueValue),H.push(v),I[r]=v;L.push(H),y.push(I)}b.$eventEmitter.trigger("xg-bind-event",{uuid:x,eventName:"addRow",rowIdx:w.getRowCount(),rowValue:L,value:y,xgDataTableId:w.getId(),firstRowYn:!0});break;case"message":if(!m.childNodes[0].getAttribute("issuccess"))throw new d.Error("Fail to receive xml data",t);for(F.result=m.childNodes[0].getAttribute(!1)?!0:!1,F.errorMsg=null,F.user={},F.msg=[],k=m.childNodes,z=0,C=0,D=k.length;D>C;C++)h=k[C],"sp:message"===h.nodeName&&(O=h.getAttribute("type"),f=h.getAttribute("code"),N=h.textContent||h.text,"INTERNAL"===O?F.errorMsg=N:"UserMsg"===O&&(F.user[f]=N),F.msg[z]={type:O,code:f,text:N},z++);w.changeCount=0,XgPlatform.XgLogger.info(function(){return"[ id : "+w.getId()+" ] first row end"}),b.$eventEmitter.trigger("xg-bind-event",{uuid:x,eventName:"firstRowEnd",rowIdx:-1,xgDataTableId:w.getId(),firstRowYn:!0})}return null!==F.result&&void 0!==F.result?F:null}},p=function(b,c,e,f){var h,i,j,k,m,n,o,p,q,r,s,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J;if("row"!==f||e){for(z={},u=g.parse(e),r=0,s=c.length;s>r;++r)if(p=c[r],b.useXgServer||b.isExistDataTable(p))switch(o=b.getDataTable(p),f){case"column":if(o.useOverrideColumn())o.clearData();else for(o.resetDataTable(),n=u.column,j=0,k=n.length;k>j;++j)i=n[j],o.addColumnForGauce(i["@name"],i["@type"],i["@size"],i["@constraint"]);XgPlatform.XgLogger.info(function(){return"[ id : "+o.getId()+" ] init"}),b.$eventEmitter.trigger("xg-bind-event",{uuid:void 0,eventName:"xgDataTableInit",rowIdx:-1,xgDataTableId:o.getId()});break;case"row":for(h=Object.keys(o.booleanColumn),G=[],q=[],F=u.row,D=0,E=F.length;E>D;++D){for(A=F[D],B=[],C={"*xgRowStatus":d.constant.STATUS.NORMAL,"*xgSequence":-1},n=o.header,j=0,k=n.length;k>j;++j)i=n[j],J=d.constant.COLUMN.TYPE_OF_GAUCE,m=i[d.constant.COLUMN.NAME],y=null,void 0!==A[m]&&(y=l(i[J],A[m]),null!==y&&void 0!==y&&"object"==typeof y&&"true"===y["@isnull"]&&(y=null)),h.length&&a.call(h,m)>=0&&(y=y===o.booleanColumn[m].trueValue),B.push(y),C[m]=y;G.push(B),q.push(C)}b.$eventEmitter.trigger("xg-bind-event",{uuid:p,eventName:"addRow",rowIdx:o.getRowCount(),rowValue:G,value:q,xgDataTableId:o.getId(),firstRowYn:!0});break;case"message":if(!u.messages["@issuccess"])throw new d.Error("Fail to receive json data",t);if(v=u.messages.message,z.result="true"===u.messages["@issuccess"],z.errorMsg=null,z.user={},z.msg=[],v&&v.length)for("totalrecords"===v[0]["@code"]&&(b.totalRecords[p]=v[0]["#text"]),w=0,x=v.length;x>w;++w)I=v[w]["@type"],f=v[w]["@code"],H=v[w]["#text"],"INTERNAL"===I?z.errorMsg=H:"UserMsg"===I&&(z.user[f]=H),z.msg[w]={type:I,code:f,text:H};o.changeCount=0,XgPlatform.XgLogger.info(function(){return"[ id : "+o.getId()+" ] first row end"}),b.$eventEmitter.trigger("xg-bind-event",{uuid:p,eventName:"firstRowEnd",rowIdx:-1,xgDataTableId:o.getId(),firstRowYn:!0})}return null!==z.result&&void 0!==z.result?z:null}},m=function(b,c,e){var f,h,i,j,k,m,n,o,p,q,r,s,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K=g.parse(e);if(!K.envelope.messages["@issuccess"])throw new d.Error("Fail to receive json data",t);if(x=K.envelope.messages.message,B={},B.result="true"===K.envelope.messages["@issuccess"],B.errorMsg=null,B.user={},B.msg=[],x&&x.length>0)for(y=0,z=x.length;z>y;++y)J=x[y]["@type"],i=x[y]["@code"],I=x[y]["#text"],"INTERNAL"===J?B.errorMsg=I:"UserMsg"===J&&(B.user[i]=I),B.msg[y]={type:J,code:i,text:I};for(r=K.envelope.datatable,s=0,u=r.length;u>s;++s){if(f=r[s],q=f["@name"],!b.isExistDataTable(q)&&!b.useXgServer)for(v=0,w=c.length;w>v;++v)if(H=c[v],b.isExistDataTable(H)){q=H;break}if(q){if(p=b.getDataTable(q),h=Object.keys(p.booleanColumn),p.useOverrideColumn())p.clearData();else for(p.resetDataTable(),o=f.header.column,k=0,m=o.length;m>k;++k)j=o[k],p.addColumnForGauce(j["@name"],j["@type"],j["@size"],j["@constraint"]);for(G=f.body.row,E=0,F=G.length;F>E;++E){for(C=G[E],D=[],o=p.header,k=0,m=o.length;m>k;++k)j=o[k],n=j[d.constant.COLUMN.NAME],A=null,void 0!==C[n]&&(A=l(j[d.constant.COLUMN.TYPE_OF_GAUCE],C[n]),null!==A&&void 0!==A&&"object"==typeof A&&"true"===A["@isnull"]&&(A=null)),h.length&&a.call(h,n)>=0&&(A=A===p.booleanColumn[n].trueValue),D.push(A);p.addRow(D),p.clearRowStatus(E)}p.changeCount=0,x&&x.length&&"totalrecords"===x[0]["@code"]&&(b.totalRecords[q]=x[0]["#text"])}}return B},o=function(a,b,c){var e,f,h,i,j,k,l,m,n,o,p,q,r,s,u=a.getDataAdapter(),v={errorMsg:null,user:{},msg:[]},w=u.getDataType();if("XML"===w){if(s=d.stringToXML(c),q=s.childNodes[0],q.xml&&(q=s.getElementsByTagName("sp:envelope").item(0)),!q||"sp:envelope"!==q.nodeName||"http://www.shift.co.kr/xmlns/shiftProtocol"!==q.getAttribute("xmlns:sp"))throw new d.Error('Can not parse xml. Only can parse type of "http://www.shift.co.kr/xmlns/shiftProtocol"',t);for(p=q.childNodes,n=0,o=p.length;o>n;++n)if(m=p[n],"sp:messages"===m.nodeName){if(!m.getAttribute("issuccess"))throw new d.Error("Fail to receive xml data",t);for(v.result="true"===m.getAttribute("issuccess"),f=0,j=m.childNodes,k=0,l=j.length;l>k;++k)i=j[k],"sp:message"===i.nodeName&&(w=i.getAttribute("type"),e=i.getAttribute("code"),r=i.textContent||i.text,"INTERNAL"===w?v.errorMsg=r:"UserMsg"===w&&(v.user[e]=r),v.msg[f]={type:w,code:e,text:r},f++)}}else if("JSON"===w){if(h=g.parse(c),!h.envelope.messages["@issuccess"])throw new d.Error("Fail to receive json data",t);if(v.result="true"===h.envelope.messages["@issuccess"],i=h.envelope.messages.message,i&&i.length)for(k=0,l=i.length;l>k;++k)w=i[k]["@type"],e=i[k]["@code"],r=i[k]["#text"],"INTERNAL"===w?v.errorMsg=r:"UserMsg"===w&&(v.user[e]=r),v.msg[k]={type:w,code:e,text:r}}return v},l=function(a,b){var c;return c=b,"int"===a||"bigint"===a||"long"===a||"decimal"===a?(c="decimal"===a?parseFloat(b):parseInt(b,10),isNaN(c)&&(c=null)):("date"===a||"datetime"===a)&&(c=d.getDateStrFromGauceDate(b)),c},h=function(a,f,g){var h;return this.id=a,this.id||(this.id=d.createUUID()),d.setDataSet(this),this.dataTables={},this.dataAdapter={},this.useXgServer=!1,this.$eventEmitter=c("<div></div>"),this.totalRecords={},h=function(a){return a.$eventEmitter.on("xg-bind-event",function(c,d){var e,f,g,h,i,j=a.getDataTable(d.xgDataTableId),k=d.eventName,l=d.rowIdx,m=d.value;if("setRowPos"===k)j[k](m);else if("setRowPos"!==k&&"xgDataTableInit"!==k){if(h=j.getRowPos(),0>l)return!1;if("setValue"===k&&"column"===d.bind.type)j[k](l,d.bind.columnIndex,m);else if("addRow"===k)j[k](d.rowValue,d.firstRowYn);else if("deleteRow"===k)for(f=0,g=m.length;g>f;++f)i=m[f],h===i&&(d.rowIdx=i),i=parseInt(i.toString(),10),j[k](i)}return(e=function(a,c){var d,e,f,g,i,j,k=c.bindedComponents,m=[];h=c.getRowPos();for(f in k)if(b.call(k,f)){for(i=!1,d=c.bindedComponents[f].el,e=0,g=s.length;g>e;++e)if(j=s[e],""===d.attr(j)){i=!0;break}m.push(f===a.uuid||!i&&h!==l?void 0:d.trigger("xg-bind-event",a))}return m})(d,j)})},h(this),f&&this.addDataTable(new e(f,g)),this},r=h.prototype,r.makeJSONExportData=function(a){return i(this,[a],!0)},r.makeJSONHeader=function(a){var b,c,e,f,g,h=this.getDataTable(a),i=h.header,j={column:[]};for(c=0,e=i.length;e>c;c++)b=i[c],f=b[d.constant.COLUMN.NAME],g={"@name":f,"@type":b[d.constant.COLUMN.TYPE_OF_GAUCE],"@size":b[d.constant.COLUMN.SIZE].toString(),"@constraint":b[d.constant.COLUMN.CONSTRAINT]},j.column.push(g);return j},r.isBinded=function(a,b){var c=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataSet.isBinded",c]}),this.isExistDataTable(a),!!this.dataTables[a].bindedComponents[b]},r.bindComponent=function(a,b){var c,e,f=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataSet.bindComponent",f]}),c=a.data("xg-bind-data-table-id"),e=a.data("xg-uuid"),!c||!e)throw new d.Error("Can not bind not XgUIComponents to XgDataSet",t);return this.isBinded(c,e)||(this.dataTables[c].bindedComponents[e]={el:a,method:b}),this},r.getId=function(){var a=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataSet.getId",a]}),this.id},r.addDataTable=function(a){var b=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataSet.addDataTable",b]}),!(a instanceof e))throw new d.Error("parameter is not instanceof XgDataTable",t);return this.hasDataTable(a.getId(),!1),a.parent||(a.parent=this),this.dataTables[a.getId()]=a,this},r.hasDataTable=function(a,b){var c=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataSet.hasDataTable",c]}),b){if(!this.dataTables[a])throw new d.Error("dataTable is not exist in dataSet",t)}else if(this.dataTables[a])throw new d.Error("dataTable is exist in dataSet",t)},r.isExistDataTable=function(a){var b=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataSet.isExistDataTable",b]}),!!this.dataTables[a]},r.getDataTableIds=function(){var a=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataSet.getDataTableIds",a]}),Object.keys(this.dataTables)},r.getDataTable=function(a){var b=arguments;return XgPlatform.XgLogger.debug(function(){return["XgDataSet.getDataTable",b]}),this.dataTables[a]},r.removeDataTable=function(a){var b,d,e,f,g,h=a,i=arguments;for(XgPlatform.XgLogger.debug(function(){return["XgDataSet.removeDataTable",i]}),a||(h=this.getDataTableIds()),"string"==typeof a&&(h=[a]),g=h.length;g--;)if(f=this.getDataTable(h[g])){for(f.resetDataTable(),e=Object.keys(f.bindedComponents),d=e.length;d--;)b=f.bindedComponents[e[d]],c(b.el)[b.method]("destroy");f.bindedComponents=null,delete this.dataTables[h[g]]}return this},r.setDataAdapter=function(a){var b=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataSet.setDataAdapter",b]}),!a)throw new d.Error("dataAdapter is not define",t);if(!(a instanceof f))throw new d.Error("dataAdapter is not instanceof DataAdapter",t);return this.dataAdapter=a,this},r.getDataAdapter=function(){var a=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataSet.getDataAdapter",a]}),!this.dataAdapter)throw new d.Error("dataAdapter is not define",t);return this.dataAdapter},r.select=function(a,b,c){var e,f,h,l,o,r,s,u,v,w=arguments,x=typeof c;if(XgPlatform.XgLogger.debug(function(){return["XgDataSet.select",w]}),void 0===c||null===c||"object"!==x&&"string"!==x||this.getDataAdapter().setParams(c),(void 0===a||null===a)&&(a=this.getDataTableIds()),x=typeof a,("string"===x||"number"===x)&&(a=[a]),!(a instanceof Array))throw new d.Error("Invalid parameter",t);for(h=this,r=!0,f=this.getDataAdapter(),o=f.getDataType(),e=function(a,c,d){var e,f,g,i;if("all"===d){for("XML"===o?e=n(h,a,c):"JSON"===o?e=m(h,a,c):"BIN"===o&&(e=k(h,a,c)),g=0,i=a.length;i>g;++g)f=a[g],XgPlatform.XgLogger.info(function(){return"[ id : "+f+" ] init"}),h.$eventEmitter.trigger("xg-bind-event",{uuid:void 0,eventName:"xgDataTableInit",rowIdx:-1,xgDataTableId:f});b&&"function"==typeof b&&b(e.result,e.errorMsg,e.user,e.msg)}else"XML"===o?e=q(h,a,c,d,b):"JSON"===o&&(e=p(h,a,c,d,b)),"row"===d?r&&(r=!1):"message"===d&&(r=!0,b&&"function"==typeof b&&b(e.result,e.errorMsg,e.user,e.msg));return h},s=0,u=a.length;u>s;++s)l=a[s],this.getDataTable(l).getUrl("select")&&(v=this.getDataTable(l).getUrl("select"));return"XML"===o?f.CRUD(v,d.constant.CRUD.READ,a,j(this,a),e):f.CRUD(v,d.constant.CRUD.READ,a,g.stringify(i(this,a)),e),this},r.update=function(a,b){var c,e,f,h,k,l,m,n,p,q,r,s,u=arguments,v=typeof a;if(XgPlatform.XgLogger.debug(function(){return["XgDataSet.update",u]}),(void 0===a||null===a)&&(a=this.getDataTableIds()),("string"===v||"number"===v)&&(a=[a]),!(a instanceof Array))throw new d.Error("Invalid parameter",t);for(f=this.getDataAdapter(),l=f.getDataType(),"XML"===l&&(p=j(this,a,!1)),"function"==typeof Array.prototype.toJSON&&(r=Array.prototype.toJSON,delete Array.prototype.toJSON),"JSON"===l&&(p=g.stringify(i(this,a))),r&&(Array.prototype.toJSON=r),h=this,c=function(a,c){var d=o(h,a,c);return b&&"function"==typeof b&&b(d.result,d.errorMsg,d.user,d.msg),h},e=0,m=0,n=a.length;n>m;++m)k=a[m],this.getDataTable(k).getUrl("update")&&e++;if(e>1){for(q=[],m=0,n=a.length;n>m;++m)k=a[m],this.getDataTable(k).getUrl("update")&&(s=this.getDataTable(k).getUrl("update")),k=[k],q.push(f.CRUD(s,d.constant.CRUD.UPDATE,k,p,c));return q}return f.CRUD(this.getDataTable(a[0]).getUrl("update"),d.constant.CRUD.UPDATE,k,p,c)},r.makeListTypeSource=function(a,b,c){var e,f,g,h,i,j=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataSet.makeListTypeSource",j]}),!this.isExistDataTable(a))throw new d.Error("dataTable is not exist in dataSet",t);for(e=this.getDataTable(a),g=[],h=0,i=e.getRowCount();i>h;++h)f={text:e.getValue(h,b),value:e.getValue(h,c)},g.push(f);return g},r.makeTreeTypeSource=function(a,b,c,e,f,g,h,i,j,k,l){var m,n,o,p,q,r,s,u,v,w,x,y=arguments;if(XgPlatform.XgLogger.debug(function(){return["XgDataSet.makeTreeTypeSource",y]}),!this.isExistDataTable(a))throw new d.Error("dataTable is not exist in dataSet",t);m=this.getDataTable(a),s=m.getRowCount(),p={},q=function(){var a=[];for(u=0,v=s;v>u;++u)o={expanded:!0,id:m.getValue(u,c),label:m.getValue(u,e),level:m.getValue(u,b),value:u},""!==f&&(o.expanded=m.getValue(u,f)),""!==g&&(o.html=m.getValue(u,g)),""!==h&&(o.disabled=m.getValue(u,h)),""!==i&&(o.checked=m.getValue(u,i)),""!==j&&(o.selected=m.getValue(u,j)),""!==k&&(o.icon=m.getValue(u,k)),""!==l&&(o.iconSize=m.getValue(u,l)),void 0!==p[o.level-1]&&null!==p[o.level-1]&&(o.parent=p[o.level-1]),p[o.level]=o.id,a.push(o);return a}(),r=[];for(n in q)if(o=q[n],void 0!==o.parent&&null!==o.parent){for(w=0,x=q.length;x>w;++w)if(p=q[w],p.id===o.parent){delete o.parent,(void 0===p.items||null===p.items)&&(p.items=[]),p.items.push(o);break}}else r.push(o);return r},XgPlatform.XgLogger.info(function(){return t+" : load end"}),h})}).call(this);