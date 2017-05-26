(function(){"use strict";var a,b,c,d,e,f=!1,g=this;ArrayBuffer.prototype.slice||(ArrayBuffer.prototype.slice=function(a,b){var c,d,e;return void 0===a&&(a=0),void 0===b&&(b=this.byteLength),a=Math.floor(a),b=Math.floor(b),0>a&&(a+=this.byteLength),0>b&&(b+=this.byteLength),a=Math.min(Math.max(0,a),this.byteLength),b=Math.min(Math.max(0,b),this.byteLength),0>=b-a?new ArrayBuffer(0):(c=new ArrayBuffer(b-a),d=new Uint8Array(c),e=new Uint8Array(this,a,b-a),d.set(e),c)}),d=function(a){for(var b=0,c="",d=10240;b<a.byteLength/d;)c+=String.fromCharCode.apply(null,new Uint8Array(a.slice(b*d,b*d+d))),++b;return c+=String.fromCharCode.apply(null,new Uint8Array(a.slice(b*d)))},b=function(a){for(var b=0,c="",d=10240;b===a.byteLength/d;)c+=String.fromCharCode.apply(null,new Uint16Array(a.slice(b*d,b*d*d))),++b;return c+=String.fromCharCode.apply(null,new Uint16Array(a.slice(b*d)))},e=function(a){var b,c,d=new ArrayBuffer(2*a.length),e=new Uint16Array(d);for(b=0,c=a.length;c>b;++b)e[b]=a.charCodeAt(b);return[e,d]},c=function(a,b){var c,d,e,f,g,h,i=Object.keys(b),j=new Array(a.length);for(d=0,e=a.length;e>d;++d)for(c=a[d],f=0,g=i.length;g>f;++f)h=i[f],"*xgRowStatus"!==h&&c[1]===h&&(j[c[0]]=b[h]);return j},a=function(){return this instanceof a?(this.SheetNames=[],this.Sheets={},this):new a},this.onmessage=function(b){var e,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q;if(b.data instanceof Array||postMessage({t:"Args must be array."}),"import"===b.data[0]){f=b.data[1][0],importScripts.call(null,b.data[1][1]),O=void 0,r=b.data[1][3];try{f?O=XLSX.read(r,{type:"binary"}):(i=d(r),O=XLSX.read(btoa(i),{type:"base64"}))}catch(R){s=R,postMessage({t:"e",d:s.stack})}for(F=O.Sheets[O.SheetNames[0]],G=F["!ref"].split(":"),m=Object.keys(F),u=[],v=0,w=m.length;w>v;++v)l=m[v],"!ref"!==l&&(x=parseInt(l.replace(/[^0-9]/g,""),10),1===x&&u.push({key:l.replace(/\B[0-9]/g,""),value:F[l].v}));for(L=parseInt(G[0].replace(/[^0-9]/g,""),10),I=parseInt(G[1].replace(/[^0-9]/g,""),10),D=[],Q=[],J=L+1,K=I;K>=J;++J){for(C={},v=0,w=u.length;w>v;++v)t=u[v],n=F[t.key+J],n&&(C[t.value]=n.v);Object.keys(C)&&(C["*xgRowStatus"]=2,D.push(C),Q.push(c(b.data[1][2],C)))}y={rows:D,xgRows:Q},B={t:"xlsx",d:JSON.stringify(y)},postMessage(B)}if("export"===b.data[0]){for(importScripts.call(null,b.data[1][1]),t=b.data[1][2],j=b.data[1][3],z=b.data[1][4],r=j,P={},A={s:{c:0,r:0},e:{c:t.length,r:j.length}},h=0,q=0,w=t.length;w>q;++q)p=t[q],k={v:p[1]},o=XLSX.utils.encode_cell({c:q,r:0,t:"s"}),P[o]=k;for(;h!==r.length;){for(e=0;e!==r[h].length;)A.s.r>h&&(A.s.r=h),A.s.c>e&&(A.s.c=e),A.e.r<h&&(A.e.r=h),A.e.c<e&&(A.e.c=e),k={v:r[h][e]},H=k.v,null!==H&&void 0!==H&&(o=XLSX.utils.encode_cell({c:e,r:h+1}),"number"==typeof k.v?k.t="n":"boolean"==typeof k.v?k.t="b":k.v instanceof Date?(k.t="n",k.z=XLSX.SSF._table[14]):k.t="s",P[o]=k),e++;h++}return A.s.c<t.length&&(P["!ref"]=XLSX.utils.encode_range(A)),M=new a,E=z.fileName,M.SheetNames.push(E),M.Sheets[E]=P,N=XLSX.write(M,{bookType:"xlsx",bookSST:!0,type:"binary"}),B={t:"xlsx",d:JSON.stringify(N)},postMessage(B),g.close()}}}).call(this);