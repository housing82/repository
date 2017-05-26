/*
//fadeTo
	$('.sift_cont > a').mouseover(function(){
		$(this).children('.sift_cover').fadeTo("fast", 0.15);
		$(this).children('.sift_img').fadeTo("fast", 1);
	});
	$('.sift_cont>a').mouseleave(function(){
		$(this).children('.sift_cover').fadeTo("500", 0.9);
		$(this).children('.sift_img').fadeTo("500", 0.5);
	});
*/


//modal
jQuery(function($){
	var $modal = $('.sift_modal');
	var $modal2 = $('.sift_modal2');
	// Append .modal_bg
/*	if($modal.length !== 0){
		$('body').append('<div class="sift_modal_bg hide">');
	}
	if($modal2.length !== 0){
		$('body').append('<div class="sift_modal_bg2 hide">');
	}*/
	var $modal_bg = $('.sift_modal_bg');
	var $modal_bg2 = $('.sift_modal_bg2');
	// Modal Anchor Click
	$('a[href^="#modal-"]').click(function(){
		var $this = $(this);
		var $target = $($this.attr('href'));
		$this.addClass('active');
		$modal_bg.fadeIn(200); // bg show
		$target.attr('tabindex','0').fadeIn(200).focus();
		//$target.appendTo('body').attr('tabindex','0').fadeIn(200).focus(); // Target is move to body, and show.
		$(window).resize();
		
		makeUserAddValidation();
		return false;
	});
	$('a[href^="#modal2-"]').click(function(){
		var $this = $(this);
		var $target = $($this.attr('href'));
		$this.addClass('active');
		$modal_bg2.fadeIn(200); // bg show
		$target.appendTo('body').attr('tabindex','0').fadeIn(200).focus(); // Target is move to body, and show.
		$(window).resize();
		return false;
	});
	// Window resize margin adjust
	$(window).resize(function(){
		$modal_visible = $('.sift_modal:visible');
		$modal_visible.css({
			marginTop: - $modal_visible.height()/2,
			marginLeft: - $modal_visible.width()/2
		});
	}).resize();
	$(window).resize(function(){
		$modal_visible = $('.sift_modal2:visible');
		$modal_visible.css({
			marginTop: - $modal_visible.height()/2,
			marginLeft: - $modal_visible.width()/2
		});
	}).resize();
	// Close modal window and bg
	function closeModal(){
		$modal.fadeOut(200, function(){
			$(".jqx-validator-error-label").html("");
			$("#joinUserId, #joinpassword, #passwordChk, #joinemail, #VerificationCode").val("");
		});
		$modal_bg.fadeOut(200, function(){
			$(".jqx-validator-error-label").html("");
			$("#joinUserId, #joinpassword, #passwordChk, #joinemail, #VerificationCode").val("");
		});
		$('a[href^="#modal-"].active').focus().removeClass('active');
		$("#user_id, #password").val("");
	};
	function closeModal2(){
		$modal2.fadeOut(200);
		$modal_bg2.fadeOut(200);
		$('a[href^="#modal2-"].active').focus().removeClass('active');
		$("#joinUserId, #joinassword, #passwordChk, #joinemail, #VerificationCode").val("");
	};
	// Close button
	$modal.find('.sift_close').click(function(){
		closeModal();
	});
	$modal2.find('.sift_close2').click(function(){
		closeModal2();
	});
	// ESC button
	$(document).keydown(function(e){
		if(e.keyCode != 27) return true;
		return closeModal();
		return closeModal2();
	});
});


//placeholder
$('[placeholder]').focus(function() {
	var input = $(this);
	if (input.val() == input.attr('placeholder')) {
		input.val('');
		input.css('color','#641f66');
		input.removeClass('placeholder');
	}
}).blur(function() {
	var input = $(this);
	if (input.val() == '' || input.val() == input.attr('placeholder')) {
		input.addClass('placeholder');
		input.css('color','#a7a7a7');
		input.val(input.attr('placeholder'));
	}
}).blur();
$('[placeholder]').parents('form').submit(function() {
	$(this).find('[placeholder]').each(function() {
		var input = $(this);
		if (input.val() == input.attr('placeholder')) {
			input.val('');
		}
	})
});
$(document).ready(function () {
	//checkbox 활성,비활성
	$('input').click(function(){
		$(this).parent().next().children('.sift_btn').toggleClass('disable');
		$(this).parent().children('.sift_btn').toggleClass('disable');
	});
});