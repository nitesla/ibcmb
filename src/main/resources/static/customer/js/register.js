 function loadPhishingImages(){
		console.log("in load phishing imges");
		var listOfImages = [];
		$('#phishing').load("/rest/json/phishingimages");
	}

 var customerId = "null";



 /** This validates the input account number.
  *
  * @param accountNumber the account number to check
  */
 function validateAccountDetails(accountNumber, email, birthDate){
     var customerId;
     $.ajax({
         type:'GET',
         url:"/rest/accountdetails/"+accountNumber+"/"+email+"/"+birthDate,
         async:false,
         success:function(data1){
             customerId = ''+String(data1);
             if(customerId == "" || customerId === null){
                 //invalid account number
                 $.notify({
                     title: '<strong>Oops!</strong>',
                     message: 'Invalid Account Number'
                 },{
                     type: 'danger'
                 });
                 //alert("Account number not found");
             }else{
                 //valid account number
                 //alert("Customer Id: " + customerId);
                 $('input[name=customerId]').val(customerId);
             }
         }
     });

     if(customerId == "" || customerId === null){
         return false;
     }else{
         return true;
     }
 }

	function validateUsername(username){
        var result;
		$.ajax({
            type:'GET',
            url:"/rest/username/check/"+username,
            async:false,
            success:function(data1){
            	result = ''+String(data1);
				if(result == 'false'){
					//invalid account number
					//alert("user name not found");
                    $.notify({
                        title: '<strong>Oops!</strong>',
                        message: 'Username already exists'
                    },{
                        type: 'danger'
                    });
				}else{
					//valid account number
					//alert("user name: " + result);
				}
            }
        });

        if(result === 'true'){
            //username is valid and available
        	return true;
        }else{
        	return false;
        }
	}

     function validatePassword(password){
         var result;
         $.ajax({
             type:'GET',
             url:"/rest/password/check/"+password,
             async:false,
             success:function(data1){
                 result = ''+String(data1);
                 if(result == 'false'){
                     //invalid account number
                     //alert("user name not found");
                     $.notify({
                         title: '<strong>Oops!</strong>',
                         message: 'the entered password might not meet the set password policy'
                     },{
                         type: 'danger'
                     });

                 }else{
                     //valid account number
                     //alert("password: " + result);
                 }
             }
         });

         if(result === 'true'){
             //username is valid and available
             return true;
         }else{
             return false;
         }
     }

 function validateRegCode(code){
     var result;
     $.ajax({
         type:'GET',
         url:"/rest/regCode/check/"+code,
         async:false,
         success:function(data1){
             result = ''+String(data1);
             if(result == 'false'){
                 //invalid account number
                 //alert("user name not found");
                 $.notify({
                     title: '<strong>Oops!</strong>',
                     message: 'Enter the Registration code sent to your mobile'
                 },{
                     type: 'danger'
                 });

             }else{
                 //valid account number
                 //alert("password: " + result);
             }
         }
     });

     if(result === 'true'){
         //username is valid and available
         return true;
     }else{
         return false;
     }
 }


     function sendRegCode(){
         var accountNumber = $('input[name="accountNumber"]').val();
         var email = $('input[name="email"]').val();
         var birthDate = $('input[name="birthDate"]').val();
         var result;
         $.ajax({
             type:'GET',
             url:"/rest/regCode/"+accountNumber+"/"+email+"/"+birthDate,
             async:false,
             success:function(data1){
                 result = ''+String(data1);
                 if(result === 'false' || result=== '' || result ==null){
                     //invalid account number
                     //alert("user name not found");
                     $.notify({
                         title: '<strong>Oops!</strong>',
                         message: 'Registration Code Not Valid'
                     },{
                         type: 'danger'
                     });

                 }else{
                     //valid account number
                     //alert("code sent: " + result);
                 }
             }
         });
     }

    function registerUser(){
         var returnValue = false;
         $('#reg-form').submit(function(e){
             e.preventDefault();

             $.ajax({
                 url: '',
                 async:false,
                 type: "POST",
                 data: $(this).serialize(),
                 success: function(data)
                 {
                     //alert(data+" return ");
                     //callback methods go right here
                     if(data==="true"){
                         $('#returnValue').val(true);
                     }else {
                         $.notify({
                             title: '<strong>Oops!</strong>',
                             message: 'Self Registration Failed'
                         },{
                             type: 'danger'
                         });
                     }
                 }
             });
         });
         $('#reg-form').submit();
         returnValue = $('#returnValue').val();
         //alert(returnValue);
         return Boolean(returnValue);
    }


    //datepicker
    $( function() {
        $('#datepicker').datepicker(
            { changeMonth: true,
                changeYear: true,
                showButtonPanel: true,
                yearRange: "-100:+0",
                dateFormat: 'dd-mm-yy' }).val();

        //load the anti phishing images from the server
        //loadPhishingImages();

        //setup event handler on select option
        // $('#phishing').change(function(event){
        // 	console.log(this);
        // 	var url = $(this).val();
        // 	console.log(url);
        // 	$('#phishing-preview').attr('src', url);
        // });

        // addEvent(document.getElementById('send'), 'click', function() {
        //     sendRegCode();
        // });
    } );


    //steps with form
    var map = {};
    var form = $("#reg-form");
    form.validate({
        errorPlacement: function errorPlacement(error, element) {
            element.before(error);
        },
        rules: {
            confirm: {
                equalTo: "#password"
            }
        }
    });
    var ACCOUNT_DETAILS_STEP = 0;
    var PROFILE_DETAILS_STEP = 1;
    var SECURITY_QUESTION_STEP = 2;
    var PHISHING_IMAGE_STEP = 3;
    form.children("div").steps({
        headerTag: "h3",
        bodyTag: "section",
        transitionEffect: "slideLeft",
        onStepChanging: function (event, currentIndex, newIndex)
        {
            form.validate().settings.ignore = ":disabled,:hidden";
			console.log(currentIndex);
            var isValid = form.valid();
            if(ACCOUNT_DETAILS_STEP === currentIndex){
                console.log("Current step is the account details step");
                var accountNumber = $('input[name="accountNumber"]').val();
                var email = $('input[name="email"]').val();
                var birthDate = $('input[name="birthDate"]').val();
                return isValid && validateAccountDetails(accountNumber, email, birthDate);
            }
            if(PROFILE_DETAILS_STEP === currentIndex){
                console.log("Current stp is the profile details step");
                var username = $('input[name="userName"]').val();
                var confirm = $('input[name="confirm"]').val();
                var regCode = $('input[name="regCode"]').val();
                return isValid && validateUsername(username) && validatePassword(confirm) && validateRegCode(regCode);
            }
            if(SECURITY_QUESTION_STEP === currentIndex){
                console.log("Current Step is the security question step");
                //$("#reg-form").submit();
                return isValid;
            }
            if(PHISHING_IMAGE_STEP === currentIndex){
                console.log("Current Step is the phishing image step");
                //$("#reg-form").submit();
                return isValid && registerUser();
            }
            return form.valid();
        },
        onFinishing: function (event, currentIndex)
        {
            //form.validate().settings.ignore = ":disabled";
            return form.valid();
        },
        onFinished: function (event, currentIndex)
        {
//            alert("Submitted!");
            window.location.href = "/login/retail";
        }
    });

