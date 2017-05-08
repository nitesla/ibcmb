

	var accountName = "null";

<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> LAST PUSH FROM FAROOQ TODAY
	/** This validates the input account number.
	 * 
	 * @param accountNumber the account number to check
	 */
<<<<<<< HEAD
=======
>>>>>>> 2118906330afc95f49863a5beb4dc252835c7c71
=======
>>>>>>> LAST PUSH FROM FAROOQ TODAY
	function validateAccountNumber(accountNumber){
		var accountName;
		$.ajax({
            type:'GET',
            url:"/rest/accountname/"+accountNumber,
            async:false,
            success:function(data1){
            	accountName = ''+String(data1);
				if(accountName == 'null'){
					//invalid account number
					alert("Account name not found");
				}else{
					//valid account number
					alert("Account name: " + accountName);
				}
            }
        }); 

        if(accountName===accountNumber){
        	return false;
        }else{
        	return true;
        }
<<<<<<< HEAD
<<<<<<< HEAD
        
=======
>>>>>>> 2118906330afc95f49863a5beb4dc252835c7c71
=======
        
>>>>>>> LAST PUSH FROM FAROOQ TODAY
	}
	

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
    var CONFIRM_PASSWORD_STEP = 2;
<<<<<<< HEAD
<<<<<<< HEAD
    var condition = [[${success}]];
    
//    $("#wizard-t-2").get(0).click();
=======
    // var condition = [[${success}]];

>>>>>>> 2118906330afc95f49863a5beb4dc252835c7c71
=======
    var condition = [[${success}]];
    
//    $("#wizard-t-2").get(0).click();
>>>>>>> LAST PUSH FROM FAROOQ TODAY
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
                var accountNumber = $('input[name="acct"]').val();
                return isValid && validateAccountNumber(accountNumber);
            }
            if(CONFIRM_PASSWORD_STEP === currentIndex){
                console.log("Current stp is the profile details step");
                form.submit();
//                return isValid && changePassword(username);
            }
            return form.valid();
        },
        onFinishing: function (event, currentIndex)
        {
            
            form.validate().settings.ignore = ":disabled";
            return form.valid();
        },
        onFinished: function (event, currentIndex)
        {
//            alert("Submitted!");
            $("#reg-form").submit();
        }
    });

