    var customerId = "null";

	/** This validates the input account number.
	 * 
	 * @param accountNumber the account number to check
	 */
	function validateAccountNo(accountNumber){
		var customerId;
		$.ajax({
            type:'GET',
            url:"/rest/retail/accountname/"+accountNumber,
            async:false,
            success:function(data1){
            	customerId = ''+String(data1);
				if(customerId == ""){
					//invalid account number
					alert("Account number not found");
				}else{
					//valid account number
					alert("Customer Id: " + customerId);
				}
            }
        }); 

        if(customerId == "" || customerId === null){
        	return false;
        }else{
        	return true;
        }
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
//    var CONFIRM_PASSWORD_STEP = 2;

    //var condition = [[${success}]];
    
//    $("#wizard-t-2").get(0).click();
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
                return isValid && validateAccountNo(accountNumber);
            }
//             if(CONFIRM_PASSWORD_STEP === currentIndex){
//                 console.log("Current stp is the profile details step");
//                 //form.submit();
// //                return isValid && changePassword(username);
//             }
//             return form.valid();
        },
        onFinishing: function (event, currentIndex)
        {
            //form.validate().settings.ignore = ":disabled";
            return form.valid();
        },
        onFinished: function (event, currentIndex)
        {
//            alert("Submitted!");
            $("#reg-form").submit();
        }
    });

