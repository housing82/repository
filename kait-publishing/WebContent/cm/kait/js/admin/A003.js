
//첫해 모든 방의 임대료를 구함
function getAllRentAmtFirstYear(){
	var cnt = roomInfo.getRowCount();
	var result = 0;
	for(var i = 0; i < cnt; i++){
		result += getchrgPriYear(roomInfo.getValue(i,'chrgMthd'), Number(roomInfo.getValue(i,'chrgExtt')) , Number(roomInfo.getValue(i,'chrgPri')));
	}
	return result;
}

//총 임대료 - 금액,임대기간,인상률
//sum - 해당 방의 임대료
function fnRentAmt(sum) {
	var rentdt = $('#rentDt').val().trim().substring(0,1);
	var rise = Number($('#rise').val());
	var increase = sum*(rise/100)
	var result = 0;
	for(var i=1;i<=rentdt;i++){
		var a = 0;
		//연 이자률이 0퍼센트면 계산하지 않음, 2년째부터 계산함
		if(rise != 0 && i > 1){
			//2년엔 이자률 한번, 3년엔 이자률 두번 더하는 방식으로 올라감
			for(var z = 2; z <= i; z++){	
				a += increase;
			}
		}
		result += sum + a;
	}
	return result;
} 
 
//매물과 과금방식에 따라 첫해 임대료 계산
//chrgMthd - 과금방식
//chrgExtt - 과금면적
//chrgPri  - 과금단가
function getchrgPriYear(chrgMthd, chrgExtt, chrgPri ){
	var chrgPriYear;
	//매물과 과금방식에 따라 첫해 임대료 계산
	if(chrgMthd == '01'){
		chrgPriYear = chrgExtt * chrgPri * 365;	
	}else{
		chrgPriYear = chrgPri * 12;	
	}
	return chrgPriYear;
}
 
 
//예약 금 = 첫해 임대료 / 12
//chrgPriYear 척해 월 임대료
function getResvAmt(chrgPriYear){
	return (chrgPriYear/12).toFixed(2)
}

//보증금 - 첫해 임대료 * 보증금 임치 개월
//chrgPriYear 척해 월 임대료
//depoMon     보증금 임치 개월
function getDepo(chrgPriYear, depoMon){
	return (chrgPriYear/12 * depoMon).toFixed(2)
}

//계약금 - 첫회 임대료 + 보증금 - 예약금
//payMthd 과금방식
//rentAmt 총 임대료
//depoMon 보증금 임치 개월
//depo    보증금
//resvAmt 예약금
function getCntrAmt(payMthd, depo, resvAmt, rentDt, chrgPriYear){
	var firstVal;
	//임대기간 1년에 납부방식 연이면 일시불과 같음
	if(rentDt.substring(0,1) == "1" && payMthd == '02'){
		payMthd == '01';
	}
	
	//일시불,연,6개월,분기,월
	switch (payMthd) {
		case '01'  : 
			firstVal = chrgPriYear;
			break;
		case '02'  : 
			firstVal = chrgPriYear;
			break;
		case '03'  : 
			firstVal = chrgPriYear / 2;
			break;
		case '04'  : 
			firstVal = chrgPriYear / 4;
			break;
		case '05'  :
			firstVal = chrgPriYear / 12;
			break;
	}

	var cntrAmt = Number(firstVal) + depo - resvAmt;
	return cntrAmt.toFixed(2);
}

//DB에 예약금, 계약금 등이 저장되어 있지 않을 경우 계산해서 구함
var setRoomPriInfo = function(){
	var rowCnt = roomInfo.getRowCount();
	var chrgPriYear;
	
	for(var i = 0; i < rowCnt; i++){
		
		chrgPriYear = getchrgPriYear(roomInfo.getValue(i, 'chrgMthd'), Number(roomInfo.getValue(i, 'chrgExtt')), 
									Number(roomInfo.getValue(i, 'chrgPri')) );
		
		//예약금 = 첫해 임대료 / 12
		if(roomInfo.getValue(i, 'resvAmt') == ''){
			roomInfo.setValue(i, 'resvAmt', getResvAmt(chrgPriYear) );
		}
		
		//임치 개월이 없으면 3개월로 설정
		if(roomInfo.getValue(i, 'depoMon') == ''){
			roomInfo.setValue(i, 'depoMon', '03' );
		}
		
		//보증금 첫해 월 임대료 * 보증금 임치 개월
		if(roomInfo.getValue(i, 'depo') == ''){
			var depoMon = roomInfo.getValue(i, 'depoMon');
			roomInfo.setValue(i, 'depo',    getDepo(chrgPriYear, Number(depoMon)) );
			roomInfo.setValue(i, 'discAmt', getDepo(chrgPriYear, Number(depoMon)) );
		}
		
		//총 임대료 세팅
		if(roomInfo.getValue(i, 'rentAmt') == ''){
			roomInfo.setValue(i, 'rentAmt', fnRentAmt(chrgPriYear).toFixed(2) );
		}
		
		//계약금 첫회 임대료 + 보증금 - 예약금
 	    if(roomInfo.getValue(i, 'cntrAmt') == ''){
			var cntrAmt = getCntrAmt($("#payMthd").val(), Number(roomInfo.getValue(i, 'depo')), 
					                 Number(roomInfo.getValue(i, 'resvAmt')), $("#rentDt").val(), chrgPriYear );
					                 
			roomInfo.setValue(i, 'cntrAmt', cntrAmt);
		
		}
	}
}

//총계약금 구하기
function setTotalCntrAmt(){
	var cnt = roomInfo.getRowCount();
	var totalCntrAmt = 0;
	
	for(var i = 0; i < cnt; i++){
		totalCntrAmt += Number(roomInfo.getValue(i, 'cntrAmt'));
		$("#roomGrid").xgGrid('setcellvalue', i ,'cntrAmt', roomInfo.getValue(i, 'cntrAmt'));
	}
	$("#totalCntrAmt").html(totalCntrAmt.toFixed(2));
}

//작업일지 그리는 함수
function workCompMaker(){
	var len = arguments.length;
	
	var gridOption = {	columnsheight: 40, 
						rowsheight: 40,
			 	    	xgBindOption: {
			    	        cellEdit: true,
			    	        cellValidation: true,
			    	        showRowStatus: false
			    	    },
			    		width: '100%',
			    		height: '200px',
			            editable: false,
			            sortable: true,
			            keyboardnavigation: true,
			            sorttogglestates:0,
			            columnsresize: true,
			            columnsreorder: true,
			            enablekeyboarddelete: true,
			            selectionmode: 'singlerow'
					 } 
	
	for(var i = 1; i <= len; i++){
	   $('#workCompGrid' + i).xgGrid( ixGridCreateOptions(arguments[i - 1] , gridOption) );
	   $('#workCompGrid' + i).jqxGrid('localizestrings', getLocalization());
	}
	
}

function A003002Maker(row){	
	//datatable의 값을 bind한다
	ixCreateBind("#tab1 [bind=true]", compInfo, row);
	
	//업종
	var biz = compInfo.getValue(row, 'biz');

	switch (biz) {
	case "01": $('#biz').html('电子商务'); //전자상거래
		break;
	case "02": $('#biz').html('互联网');  //인터넷
		break;
	case "03": $('#biz').html('软件开发');  //소프트웨어 개발
		break;
	case "04": $('#biz').html('硬件开发');  //하드웨어개발
		break;
	case "05": $('#biz').html('广告公关');  //광고대행
		break;
	case "06": $('#biz').html('影视传媒');  //연예 및 언론매체
		break;
	case "07": $('#biz').html('其他');  //기타
		break;
	}
	
	//기업형태
	var compFrm = compInfo.getValue(row, 'compFrm');
	
	switch (compFrm) {
	case "01": $('#compFrm').html('私营'); //사기업
		break;
	case "02": $('#compFrm').html('国营');  //국경
		break;
	case "03": $('#compFrm').html('中外合资');  //중외합자
		break;
	case "04": $('#compFrm').html('外资');  //외자
		break;
	case "05": $('#compFrm').html('政府机关/事业单位');  //정부기가관,공사
		break;
	case "06": $('#compFrm').html('其他');  //기타
		break;
	}
	
	//사원수
	var empCnt = compInfo.getValue(row, 'empCnt');

	switch (empCnt) {
	case "01": $('#empCnt').html('1-49');
		break;
	case "02": $('#empCnt').html('50-99');  
		break;
	case "03": $('#empCnt').html('100-499');  
		break;
	case "04": $('#empCnt').html('500-999');  
		break;
	case "05": $('#empCnt').html('1000以上');  
		break;
	}
	
	//해당 기업 이미지 업로드 경로
	var imgDir = compInfo.getValue(row, 'applyImgDir');
		
	//사업자등록증,  조직 코드증, 법인 신분증, 책임자 신분증
	var SEQ = compInfo.getValue(row, 'compIdSeq') + "/";
	if(compInfo.getValue(row, 'applyCompLic') != "" && compInfo.getValue(row, 'applyGroupLic') != "" &&
		compInfo.getValue(row, 'applyCorpLic') != "" && compInfo.getValue(row, 'applyCustLic') != ""){
		$("#applyCompLic").attr('src', ImagePath + imgDir + "/" + SEQ + compInfo.getValue(row, 'applyCompLic'));
		$("#applyGroupLic").attr('src', ImagePath + imgDir + "/" + SEQ + compInfo.getValue(row, 'applyGroupLic'));
		$("#applyCorpLic").attr('src', ImagePath + imgDir + "/" + SEQ + compInfo.getValue(row, 'applyCorpLic'));
		$("#applyCustLic").attr('src', ImagePath + imgDir + "/" + SEQ + compInfo.getValue(row, 'applyCustLic'));
	}else{
		$("#applyCompLic").attr('src', ImagePath + imgDir + "/" + SEQ + compInfo.getValue(row, 'placeCompLic'));
		$("#applyGroupLic").attr('src', ImagePath + imgDir + "/" + SEQ + compInfo.getValue(row, 'placeGroupLic'));
		$("#applyCorpLic").attr('src', ImagePath + imgDir + "/" + SEQ + compInfo.getValue(row, 'placeCorpLic'));
		$("#applyCustLic").attr('src', ImagePath + imgDir + "/" + SEQ + compInfo.getValue(row, 'placeCustLic'));
	}
	
	if(compInfo.getValue(row, 'applyCorpLic') == "" && compInfo.getValue(row, 'placeCorpLic') == ""){
		$("#applyCorpLic").hide();
	}
	
	if(compInfo.getValue(row, 'applyCustLic') == "" && compInfo.getValue(row, 'placeCustLic') == ""){
		$("#applyCustLic").hide();
	}
	
    var param = "workSumry=A003001&compId=" + compInfo.getValue(row, 'compId')  + "&compIdSeq=" +  
    			 compInfo.getValue(row, 'compIdSeq');
	//작업일지 조회
	ixSelect(null, 'compWorkBizInfo', null, param , false, null, null, function(){});
}


function A003004Maker(row){
	$("[name=placeConfm]").xgRadioButton({checked: false, groupName: 'placeConfm'});
	$("[type=checkbox]").xgCheckBox();
	
	//datatable의 값을 bind한다
	ixCreateBind("#tab2 [bind=true]", compInfo, row);
	
	var images = ["corpConfmPict", "custConfmPict", "placeCompLic", "placeGroupLic", "placeCorpLic", "placeCustLic"];
	var imagesLen = images.length;
	
	//검수가 끝났을 경우 이미지 크게 보기 이벤트 추가
	if(compInfo.getValue(row, 'placeInspStat' ) == '02' || compInfo.getValue(row, 'placeInspStat' ) == '03'){		
		$("[name=Images]").on('click',function(){
			$("#showBigImgDiv").show();
			$("#showBigImg").attr('src', $(this).attr('src'));
		});
		
		//이미지가 있을 경우 보여줌
		var SEQ = compInfo.getValue(row, 'compIdSeq') + "/";
		for(var i = 0; i < imagesLen; i++){
			if(compInfo.getValue(row, images[i]) != ""){
				$("#" + images[i] + "Show").attr('src' , ImagePath + compInfo.getValue(row, 'applyImgDir') + "/" + SEQ + compInfo.getValue(row, images[i]));
			}
		}
		
		//현장검수자 종류에 따라 필요 없는 이미지 감추기
		if(compInfo.getValue(row, 'placeConfm') == '01'){
			$("#custConfmPicDiv, #custImage").hide();
		}else if(compInfo.getValue(row, 'placeConfm') == '02'){
			$("#corpConfmPicDiv, #corpImage").hide();
		}
		
		$("#tab2 [type=checkbox]").xgCheckBox({disabled : true});
		$("#tab2 [type=radio]").xgRadioButton({disabled : true});
	}
	
	if(compInfo.getValue(row,'applyCorpLic') == ''){
		$("#applyCorpLicShowTd").html('');
	}
	
	if(compInfo.getValue(row,'applyCompLic') == ''){
		$("#applyCompLicShowTd").html('');
	}
	
	
	//기존 사업자등록증, 법인신분증 큰 이미지로 보기
	$("#applyCorpLicShow, #applyCompLicShow").on('click',function(){
		var column = $(this).attr("id").substring(0, $(this).attr("id").length - 4);
		var imgDir = compInfo.getValue(row, 'applyImgDir');
		var SEQ = compInfo.getValue(row, 'compIdSeq') + "/";
		$("#showBigImg").attr('src',ImagePath + imgDir + "/" + SEQ + compInfo.getValue(row, column));
		$("#showBigImgDiv").show();
	});
    
    var param = "workSumry=A003003&compId=" + compInfo.getValue(row, 'compId')  + "&compIdSeq=" +  
	 compInfo.getValue(row, 'compIdSeq');
    
    //작업일지 조회
    ixSelect(null, 'compWorkPlaceInsp', null, param , false, null, null, function(){});
}

function A003004ComponentMaker(row, write, ip){
	//재심사 못하게 막기
	if(compInfo.getValue(row, 'placeInspStat' ) == '02' || compInfo.getValue(row, 'placeInspStat' ) == '03'){
		if(compInfo.getValue(row, 'placeInspStat' ) == '02'){
			$("#placeInspStatSpan").html('已通过');
		}else if(compInfo.getValue(row, 'placeInspStat' ) == '03'){
			$("#placeInspStatSpan").html('未通过');
		}
		
		$("#validationSection").html('');
		$("[name=imgCancleBtn]").remove();
		$("#bt_save2").remove();
		
		if(compInfo.getValue(row, 'placeInspStat' ) == '03'){
			$("#applyCompLicShowTd, #applyCorpLicShowTd").html("");
		}
		
		$("#tab2 [type=checkbox]").xgCheckBox({disabled : true});
		$("#tab2 [type=radio]").xgRadioButton({disabled : true});
		
		$("#bt_close").html("关闭")
	}else if(write, compInfo.getValue(row, 'placeInspStat' ) == '01' ){
		//심사가 안되어 있을 경우 이미지 추가 관련 이벤트들 추가
		//두번째 탭 클릭시에만 제출 버튼 보이게 변경
		$('#xg-tab').on('tabclick', function(a){
			if(a.args.item == 1){
				$("#bt_save2").show();
			}else{
				$("#bt_save2").hide();
			}
		});
	
		//쓰기 권한이 없으면 화면 처리 후 리턴
		if(!A003003W){
			$("#tab2 [type=checkbox]").xgCheckBox({disabled : true});
			$("#tab2 [type=radio]").xgRadioButton({disabled : true});
			$("#validationSection").html('');
			$("#bt_save2, [name=imgCancleBtn]").remove();
			$("#mdfyContPlaceInsp").attr('disabled','disabled');
			$("#bt_close").html("关闭");
			$('#xg-tab').xgTab('select', 1);
			return;
		}
		
		//기업법인, 기업책임자 validation 재설정 용
		function validationChange(show, hide){
			$("#" + hide + "ConfmPicDiv").hide();	
			$("#" + hide + "ConfmPict").val('');
			$("#" + hide + "ConfmPictImg").val('');
			$("#" + hide + "ConfmPictShow").attr('src','');
			$("#" + show + "ConfmPicDiv").show();
			
			$("#place" + hide.substring(0,1).toUpperCase() + hide.substring(1) + "Lic").val("");
			
			$("[name=" + hide + "Checkbox]").xgCheckBox({disabled : true}).xgCheckBox('uncheck');
			$("[name=" + show + "Checkbox]").xgCheckBox({disabled : false});

			$("#" + show + "Image").css('display','block');
			$("#place" + hide.substring(0,1).toUpperCase() + hide.substring(1) + "LicCancle").click();
			$("#" + hide + "Image").css('display','none');	
		}
		
		function ckboxAllCheck(ck){
			$("div[name=chkbox]").xgCheckBox(ck);
			
			var placeType = $("div[name=placeConfm][aria-checked=true]").attr('val') ;
			
			if(placeType == '01'){
				$("div[name=corpCheckbox]").xgCheckBox(ck);
			}else if(placeType == '02'){
				$("div[name=custCheckbox]").xgCheckBox(ck);
			}else{
				$("div[name=corpCheckbox], div[name=custCheckbox]").xgCheckBox(ck);
			}
		}

		//현장확인자 체크박스 모두 체크용
		$("#placeAllCheck").on('change',function(){
			if($("#placeAllCheck").xgCheckBox('checked')){
				ckboxAllCheck('check');
			}else{
				ckboxAllCheck('uncheck');
			}
		})
		
		//현장확인자 를 기업법인만 선택시  촬영항목에서 기업책임자항목 숨기기 및 값 초기화
		$("[name=placeConfm]").on('click',function(){
			//기업법인
			if($(this).attr('val') == '01'){
				validationChange("corp", "cust");
			//기업책임자
			}else if($(this).attr('val') == '02'){
				validationChange("cust", "corp");
			//둘다
			}else{
				$("#corpConfmPicDiv, #custConfmPicDiv").show();
				$("[name=custCheckbox], [name=corpCheckbox]").xgCheckBox({disabled:false});
				$("#corpImage, #custImage").css('display','block');
			}
		})
	
	
		//버튼 클릭시 input[type=file]이 클릭되도록 이벤트 추가
		$("[name=confirmPicBtn]").on('click', function(){
			$("#" + $(this).attr("id").substring(0, $(this).attr("id").length - 3) + "Img").click();
		});
		
		$("[type=file]").on('change',function(){
			var id = $(this).attr("id");
			var file = document.getElementById(id);
			var imgName = file.files[0].name;
			$("#" + id.substring(0, id.length - 3)).val(imgName);
			showImage(file, "#" + id.substring(0, id.length - 3) + "Show");
			
			//DB에 저장되어 있는 이미지를 삭제용 hidden 객체에 저장한다.
			//만약 빈값이 저장될 경우 아무것도 삭제되지 않는다
			$("#" + id.substring(0, id.length - 3) + "DelImg").val(compInfo.getValue(row,id.substring(0, id.length - 3)));
		});
		
		//이미지 취소 버튼 클릭시 이벤트
		$("[name=imgCancleBtn]").on('click',function(){
			var id = $(this).attr('id').substring(0, $(this).attr('id').length - 6);
			
			//type=file안의 값 제거
			$("#" + id + "Img").val("");
			
			//삭제할 이미지 이름 제거
			$("#" + id + "DelImg").val("");
			
			//DB안에 이미지 이름이 없으면 이미지의 src와 DB저장 값에 빈값을 넣고,
			//DB안에 이미지 이름이 있으면 이미지의 src와 DB 저장 값에 넣는다
			if(compInfo.getValue(row, id) != ''  && compInfo.getValue(row, id) != null){
				$("#" + id + "Show").attr('src' , ImagePath + compInfo.getValue(row, 'applyImgDir') + "/" + compInfo.getValue(row, id));	
				$("#" + id).val(compInfo.getValue(row, id));
			}else{
				$("#" + id + "Show").attr('src', '');
				$("#" + id).val("");
			}
		});
		
		//validator 생성 시작
		var radio = { 
	 			message : 'radio',
	 			func : function(input, commit){
	 				var name = input.selector.substring(1).replace("Validation","");
		            if ($('[name=' + name  + '][aria-checked=true]').length != 0) {
		                return true;
		            }
		            return false;	
	 			}
	 	};
	    
	    var text = { 
	 			message : 'require',
	 			func : function(input, commit){
		            if ($('#workDescr').val() != '') {
		                return true;
		            }
		            return false;	
	 			}
	 	};
	    
	    var checkbox = { 
	 			message : 'checkboxAll',
	 			func : function(input, commit){
	 				var placeChkKind = $("[name=placeConfm][aria-checked=true]").attr('val');
	 				var checkboxlength = $('[placeComfirmChk=true]').length;
	 					
	 				//기업법인 이거나 기업 책임자만 일 경우 3개는 뺀다
	 			    if(placeChkKind == '01' || placeChkKind == '02'){
	 					checkboxlength -= 1; 	
	 				} 
	 				
		            if ($('[placeComfirmChk=true][aria-checked=true]').length == checkboxlength) {
		                return true;
		            }
		            return false;	
	 			}
	 	};
	    
	    var image = { 
	 			message : 'A003003image',
	 			func : function(input, commit){
	 				var placeChkKind = $("[name=placeConfm][aria-checked=true]").attr('val');
	 				
		            if ($('#placeCompLic').val().trim() == '' || $('#placeGroupLic').val().trim() == '' || 
		            	$('#placeCorpLic').val().trim() == '' && placeChkKind != '02' || 
		            	$('#placeCustLic').val().trim() == '' && placeChkKind != '01' ) {
		                return false;
		            }
		            return true;	
	 			}
	 	};
	    
	    var image2 = { 
	 			message : 'A003003image',
	 			func : function(input, commit){
	 				var placeChkKind = $("[name=placeConfm][aria-checked=true]").attr('val');
	 				
		            if ($('#corpConfmPict').val().trim() == '' && placeChkKind != '02' || 
		            	$('#custConfmPict').val().trim() == '' && placeChkKind != '01' ) {
		                return false;
		            }
		            return true;	
	 			}
	 	};
	    
	 	var rules_array = [["#placeInspStatValidation" ,"keyup",radio],
	 	                   ["#placeConfmValidation" ,"keyup",radio],	              
		                   ["#checkboxValidation" ,"keyup", checkbox],
		                   ["#imageValidation" ,"keyup", image],
		                   ["#imageValidation2" ,"keyup", image2],
		                   ["#workDescrValidation" ,"keyup", text]]
		
		$('#validationSection').jqxValidator({
			position: 'right',
			hintType: 'label',
			animationDuration: 0,
		    rules: getRules(rules_array, [compWork])
		}); 
	 	//validator 생성 끝
	
		$("[name=imgDir]").val(compInfo.getValue(row, 'applyImgDir'));
		$("[name=compIdSeq]").val(compInfo.getValue(row, 'compIdSeq'));
		
		$('#xg-tab').xgTab('select', 1);
		
	   	$("#bt_save2").on('click', function(){
	   	    //validator 체크
			if($('#validationSection').jqxValidator('validate') != true){
				ixCreateError2('#alertSection' , 'C011');
	    		return;		
	    	}
	   		
	   		var bindingId = ["compLicOri", "compLicCp", "compLicCk", "groupCdOri", "groupCdCp", "groupCdCk", "corpLicOri",
	   		                 "corpLicCp", "corpLicCk", "custLicOri", "custLicCp", "custLicCk", "compStp", "compStpCk",
	   		                 "corpConfmPict", "custConfmPict", "placeCompLic", "placeGroupLic", "placeCorpLic", "placeCustLic",
	   		                 "placeConfm", "placeInspStatRadio"];
	   		
	   		ixCreateReverseBindbyId(bindingId, compInfo, row, true);
	   		
	 		/* if($("#placeInspStatChk").attr('aria-checked') == 'true'){
				compInfo.setValue(row, 'placeInspStat', '02');		
	 		} */
	   		
			compWork.insertRow(0, [compInfo.getValue(row, 'userId'),'','A003003', $("#workDescr").val(), ip, compInfo.getValue(row, 'compId'), 
			                       compInfo.getValue(row, 'compIdSeq'),'']);
			
			ixSave(null, ['compInfo','compWork'] , {button : $('#bt_save2')}, false, true, [".sift_alert" , "C001" ,'2', 2.5], false, function(result, errorMsg, userMsg, parameter){
				updateInspStatInspImage();
				
				//재조회 이후 팝업 닫기
				ixSelect($xgGrid, 'compInfo', null, ['div', 'id', 'search_wrap'], false, null, null, function(){});	
				$('#bt_close').trigger('click');
			});
		}); 
	}
}

function A003006Maker(row){
	$("[type=checkbox]").xgCheckBox();
	
	$("[type=combobox]").xgComboBox({
        autoDropDownHeight: true,
        itemHeight: 30,
        displayMember: 'text',
        valueMember: 'value',
        selectedIndex: 0,
        width: '100%'
	});
	
    $("#payMthd").xgComboBox({
    	source :[{text: '趸交',value: '01'}, {text: '年付',   value: '02'}, 
    	         {text: '半年付', value: '03'}, {text: '季付', value: '04'}, {text: '月付', value: '05'}],
        selectedIndex : 0
	});	
    
    $("#rentDt").xgComboBox({
    	source :[{text: '1年',value: '1年'}, {text: '2年',   value: '2年'}, 
    	         {text: '3年', value: '3年'}, {text: '4年', value: '4年'}, {text: '5年', value: '5年'}],
        selectedIndex : 0
	});	
    
    /*$("#rise").xgComboBox({
    	source :[{text: '-5%',value: '-5'}, {text: '-4%',value: '-4'}, {text: '-3%',value: '-3'}, {text: '-2%',value: '-2'},
    	         {text: '-1%',value: '-1'}, {text: '0%',value: '0'}, {text: '1%',value: '1'}, {text: '2%',   value: '2'}, 
    	         {text: '3%', value: '3'}, {text: '4%', value: '4'}, {text: '5%', value: '5'}],
    	selectedIndex : 5
	});	*/
    
    $("#depoMon").xgComboBox({
    	width: '15%',
    	source :[{text: '1个月',value: '01'}, {text: '2个月', value: '02'}, 
    	         {text: '3个月',value: '03'}],
	});	
    
    //comboBox내에 직접 입력 안되게 변경
    $(".jqx-combobox-input").attr('readonly','readonly');
    
    //datatable의 값을 bind한다
    ixCreateBind("#tab3 #cntrInfo [bind=true], #cntrCreateForm [bind=true]", compInfo, row);
    
    var roomInfoParam = "compId=" + compInfo.getValue(row , 'compId') +  "&rentId=" + compInfo.getValue(row , 'rentId');
    //방정보 조회
    ixSelect(null, 'roomInfo', null, roomInfoParam , false, null, null, function(){
    	setRoomPriInfo();	
    	setTotalCntrAmt();
    	
    	//그리드 클릭시 데이터 바인드
        $('#roomGrid').on('rowclick ', function(){
        	var roomRow = roomInfo.getRowPos();
        	ixCreateBind("#tab3 #roomSection [bind=true]", roomInfo, roomRow);
        });
    });
    
    var param = "workSumry=A003005&compId=" + compInfo.getValue(row, 'compId')  + "&compIdSeq=" +  
	 compInfo.getValue(row, 'compIdSeq') + "&rentId=" + compInfo.getValue(row, 'rentId') ;
    
    //작업일지 조회
    ixSelect(null, 'compWorkCntr', null, param , false, null, null, function(){});
    
  //체결 상태 결정시 재심사 못하게 변경
	if(compInfo.getValue(row, 'cntrStat' ) == '02'){
		$("#validationSection").remove();
		$("#cntrStatSpan").html('已签订');
		$("#cntrStatSection").html('');
		$("#tab3 [type=combobox]").xgComboBox({disabled : true});
		$("#tab3 [type=checkbox]").xgCheckBox({disabled : true});
		$("#tab3 [type=text]").attr('readonly','true');
		$("#tab3 [type=text]").css('border','none');
		$("#chrgPri").attr('readonly', 'readonly').css('border','none');
		
		$("#cntrCreateSection").remove();
		ixCreateBind("#cntrUpload", compInfo, row);

		$("#cntrDown").on('click',function(){
			$("#imgDirDown, #applyImgDir").val(compInfo.getValue(row, 'applyImgDir'));
			$("#cntrName, #cntrName2").val(compInfo.getValue(row, 'cntrUpload'));

			$.ajax({
				type : "POST",
				url : "/admin/cntrExistChk",
				data : $('#downloadCntr').serialize(),
				success : function(data) {
					if(data == "T"){
						$("#imgDirDown").val(compInfo.getValue(row, 'applyImgDir'));
						$("#cntrName").val(compInfo.getValue(row, 'cntrUpload'));
						$("#downloadCntr").submit();
					}else if(data == "F"){
						ixCreateError2('#alertSection' , 'C015');
					}
				}
			});
		});

		var cntrStartDy = $("#cntrStartDy").val();
		$("#cntrStartDyTd").html("<span>" + cntrStartDy + "</span>");	
		$("[name=cntrCreateTd]").html('');
		$("#bt_save3").remove();
		$("#bt_close").html("关闭");
	}
}