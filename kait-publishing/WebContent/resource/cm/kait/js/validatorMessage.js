var unigroup_validator_message = {
	require : '必填项目',   //'required!',
	required : '@val1是需要的项目', //required!',	
	length : '可输入@val1~@val2字', //'Enter between @val1 and @val2 characters!',
	smallLength : "需要输入@val1个字以上",
	passchk : '密码不一致，请再确认密码', //'Password not match!',
	passchk2 : '@val1和@val2不一致',     //'@val1 doesn\'t match! @val2',
	phone : '请再确认号码',               //'Invalid phone number!',
	email : '请输入正确邮箱地址', //'Invalid e-mail!',
	checkbox : '请打钩至少一个项目',   //'you must check the checkbox at least one',
	radio : '请选择至少一个按钮', //'you must check the radio at least one',
	format : '限使用中文、英文、数字、下划线（_）',
	checkboxAll : '请打钩所有的项目', //'You have to check all CheckBox',
	image : '需要图片',      //'image required',
	pdf : '必须上传PDF文件',     //'pdf required',
	A003003image : '需要上传所有的图片', //'You have to upload All Images',
	userIdDuplChk : '该用户名已存在', //'Duplicated Id'
	validatorMessage1 : '请确认修改无误'
}

/* getRuleArray(array, [dataTable])로 받아온 배열들을 이용해서
 * { {action :'blur', input:'#username', message : 'message', rule = 'required'},
 *  {action :'blur', input:'#userid',  message : 'message2', rule = 'length=1,2'}}
 *  이런 형식으로 값을 만들어서 리턴한다
 *  
 *  dataTable은 array안에 담아서 넘기며, dataTable들의 id를 이용해서 length validation을 만든다
 **/
function getRules(array, dataTableArray){

	var ruleArray = getRuleArray(array, dataTableArray);
	var len = ruleArray.length;
	var rules = new Array();
	
	for(var i=0; i<len; i++){	
		var object = new Object();
		object.input = ruleArray[i][0];
		object.message = ruleArray[i][1];
		object.action = ruleArray[i][2];
		object.rule = ruleArray[i][3];
		
		//6번째 인자부터는 key-value 형식으로 넣어서 -로 split하여 object에 넣는다
		/*if(ruleArray[i].length > 5){
			for(var z = 5; z < ruleArray[i].length; z++){
				var out_param = ruleArray[i][z].split("-");	
				eval("object." + out_param[0] + " = '" + out_param[1] +"'");
			}
		}	*/	 
		rules[i] = (object);
	}
	return rules;
}

//넘어온 변수를 이용해서 ["#USER_NAME",  "Enter between 2 and 20 characters!","keyup,blur","length=2,20"]과 같은 형식으로 만들고,
//배열에 집어 넣어서 리턴한다. 예제의 배열 두번째 있는 메세지는 validation function에 따라서 세팅해준다
function getRuleArray(array, dataTableArray){
	var ruleArray = [];

	var len = array.length;
	
	for(var i = 0; i < len; i++){
		var rules = [];
		
		rules.push(array[i][0]);
		rules.push(getMessage(array[i]));
		rules.push(array[i][1]);
		
		if(typeof array[i][2] == 'object'){
			rules.push(array[i][2].func);
		}else{
			rules.push(array[i][2]);
		}	
		
		ruleArray.push(rules);
	}
	
	if(dataTableArray != undefined){	
		for(var x = 0, v = dataTableArray.length; x < v; x++){
			ruleArray = getLengValidationFromDataTable(ruleArray, dataTableArray[x]);
		}
	}
	
	return ruleArray;
};

//validation function을 받아서 종류에 따라서 message를 세팅해서 리턴한다
function getMessage(rule){
	var message;
	
	if(typeof rule[2] == 'string'){
		var validationType = rule[2].toUpperCase();
		
		if(validationType.indexOf('LENGTH') != -1){
			message = getLengthMessage(rule[2]);
		}
		
		if(validationType.indexOf('LENGTH') == -1){
			switch (validationType) {
				case 'REQUIRED':
					message = getRequiredMessage(rule);
					break;				
				case 'PHONE':
					message = unigroup_validator_message.phone;
					break;
				case 'EMAIL' :
					message =  unigroup_validator_message.email;
					break;
				//숫자는 입력하면 안되는 validation 체크	
				case 'NOTNUMBER' :
					message =  unigroup_validator_message.notnumber;
					break;
			}
		}
		
	}else if (typeof rule[2] == 'object'){
		message = getFunctionMessage(rule[2])
	}
	
	return message;
}

//validation function이 required로 올 때
//acceptInput은 확인 checkbox를 체크했는지 확인하는 것이기 때문에 
//같은 requred 이지만 메세지가 다른 형태이다.
//acceptInput은 동의 체크 같은 것인데, required와 겹처서 id로 판별하게 해놨다
function getRequiredMessage(rule){
	var m;
	if(rule[3] == undefined && rule[3] == null){
		if(rule[0].substring(1) != 'acceptInput'){
			m = unigroup_validator_message.required;
			m = m.replace("@val1" , rule[0].substring(1));
	
		}else{
			unigroup_validator_message.acceptInput;
		}
	}else{
		m = eval('unigroup_validator_message.' + rule[3]);
	}
	return m;
}

//validation function이 length=2,20 등으로 올때
function getLengthMessage(rule){
	var m = unigroup_validator_message.length;
	
	var lengthValue = rule.split("=")[1].split(",");
	
	m = m.replace("@val1",lengthValue[0]).replace("@val2",lengthValue[1]);
	
	return m;
}

//validation function을 개발자가 만들어서 사용할 경우 object 안에 function 및 
//필요한 변수 들을 val1, val2, val3과 같은 이름으로 담아서 넘어온다
function getFunctionMessage(object){
	var m = eval("unigroup_validator_message." + object.message);
	var match = m.match(/@val/g);
	
	if(match != undefined){
		var len = match.length;
		for(var i = 1; i <= len; i++){
			eval("m = m.replace('@val" + i + "', object.val"+i+")");
		}
	}
	
	return m;
}

//dataTable이 넘어왔을 경우
//해당 text 엘리먼트의 id,와 dataTable의 헤더를 비교하고 같을 경우 size를 maxLength로 해서 1~maxLength의 길이 체크를 하는 function을 만든다
function getLengValidationFromDataTable(ruleArray, dataTable){
	
	//dataTable.header의 array 중 [1] 값은 헤당 header ID, [4]의 값은 columnSize 이다
	//header의 id가 aa라면, attribute로 lengthValidation='aa'가 있는 element를 찾아서 validation을 만든다
	for(var z = 0, headerLeng = dataTable.header.length; z < headerLeng; z++){
		var columnId = dataTable.header[z][1];
		
		if($("[lengthValidation=" + columnId + "]").length != 0){
			var array = [];
			var maxLength  = Number(dataTable.header[z][4]);			
			
			array.push("#" + columnId + "Validation");
			array.push(getLengthMessage("length=1," + maxLength));
			array.push("keyup");
			array.push(getLengthChkFunction(columnId, maxLength));
									
			ruleArray.push(array);
		}
		
	}
	
	
	return ruleArray;
}

//lengthValidation attribute를 사용해서 header와 연동하는 건 element의 id를 header의 id와 똑같이 설정 할 수 없을 경우가 있어서 그렇다.
//.val()로 value를 가져올 수 있는 경우에는 $("[lengthValidation=aa]).val() 이런식으로 가졍로 수 있는데
//ckEditor 같은 경우에는 id를 통해서만 값을 가져올 수 있기 때문에 ckEditor의 element는 무조건 id와 header의 id를 맞춰야한다
function getLengthChkFunction(columnId, maxLength){
	
	var func = function(input,commit){
		var ElementVal;
		var txtType = $("[lengthValidation=" + columnId + "]").attr('txtType');
		if(txtType == undefined){
			ElementVal = $("[lengthValidation=" + columnId + "]").val();
		}else if(txtType == "ckEditor"){ //ckEditor는 value를 가져오는 값이 다르기 때문에 다른 함수를 써야 한다
			ElementVal = eval("CKEDITOR.instances." + columnId + ".getData()");
		}
		//var ElementVal = $("[lengthValidation=" + columnId + "]").val();
		if(typeof ElementVal == "number"){
			ElementVal += "";
		}
			if (ElementVal.length <= maxLength) {
		        return true;
		    }
		    return false;	
	}	
	
	return func;
}
