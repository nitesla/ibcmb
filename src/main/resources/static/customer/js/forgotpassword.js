    var customerId = "null";

    function validateSecAnswer(secAnswer){
        var result;
        $.ajax({
            type:'GET',
            url:"/rest/secAns/"+secAnswer,
            async:false,
            success:function(data1){
                result = ''+String(data1);
                if(result == "" || result === null){
                    //invalid account number
                    $.notify({
                        title: '<strong>Oops!</strong>',
                        message: 'Wrong Answer!'
                    },{
                        type: 'danger'
                    });
                    //alert("Account number not found");
                }else{
                    //valid account number
                    $('input[name=username]').val(result);
                }
            }
        });

        if(result == "" || result === null){
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
                            message: 'Password Reset Failed, the entered password might not meet the set password policy'
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
    
    var SECURITY_QUESTION_STEP = 0;
    var CHANGE_PASSWORD_STEP = 1;

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
            // Allways allow previous action even if the current form is not valid!
            if (currentIndex > newIndex)
            {
                return true;
            }

            // Needed in some cases if the user went back (clean up)
            if (currentIndex < newIndex)
            {
                // To remove error styles
                form.find(".body:eq(" + newIndex + ") label.error").remove();
                form.find(".body:eq(" + newIndex + ") .error").removeClass("error");
            }

            if(SECURITY_QUESTION_STEP === currentIndex){
                console.log("Current step is the account details step");
                var secAnswer = $('input[name="securityAnswer"]').val();
                return isValid && validateSecAnswer(secAnswer);
            }
            if(CHANGE_PASSWORD_STEP === currentIndex){
                console.log("Current step is the change password step");
                //form.submit();
               return isValid && changePassword();
            }

            form.validate().settings.ignore = ":disabled,:hidden";
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

