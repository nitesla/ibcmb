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

	function changePassword(){
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
                            message: 'Password Reset Failed'
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
    var CHANGE_PASSWORD_STEP = 2;

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
            if(CHANGE_PASSWORD_STEP === currentIndex){
                console.log("Current step is the change password step");
                //form.submit();
               return isValid && changePassword();
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
           //alert("Submitted!");
           window.location.href = "/login/retail";
//             $("#reg-form").submit();
        }
    });

