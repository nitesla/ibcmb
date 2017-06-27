/**
 * Created by Showboy on 23/06/2017.
 */

/** This validates the input account number.
 *
 * @param accountNumber the account number to check
 */
function validateUsername(user){
    console.log(user);
    var username;
    var secQues;
    $.ajax({
        type:'GET',
        url:"/rest/username/"+user,
        async:false,
        success:function(data1){
            username = ''+String(data1);
            if(username == "false"){
                //invalid account number
                var error = document.getElementById("errorMess");
                error.textContent="Entered username doesn't exist on our platform.";
                $('#myModalError').modal('show');
            }else{
                //valid account number
                //alert("Customer Id: " + customerId);
            }
        }
    })

    console.log(username);

    if(username == "" || username === null || username == "false"){
        return false;
    }else{
        $.ajax({
            url: "/rest/getSecQues/"+username,
            type: 'GET',
            async: false,
            success:function(data2){
                secQues = ''+String(data2);
                if(secQues == "" || secQues === null){
                    var error = document.getElementById("errorMess");
                    error.textContent="Could not get Security Question from server, please try again.";
                    $('#myModalError').modal('show');
                }else{
                    $('input[name=securityQuestion]').val(secQues);
                }
            }
        })
    }

    console.log(secQues);

    if(username == "" || username === null || secQues == "" || secQues === null){
        return false;
    }else{
        return true;
    }
}


function validateSecAnswer(secAnswer, username){
    var result;
    var tokens;
    $.ajax({
        type:'GET',
        url:"/rest/secAnswer/"+secAnswer+"/"+username,
        async:false,
        success:function(data1){
            result = ''+String(data1);
            if(result == "" || result === null){
                //invalid account number
                var error = document.getElementById("errorMess");
                error.textContent="Wrong answer provided for security question.";
                $('#myModalError').modal('show');
            }else{
                //valid account number
                //$('input[name=username]').val(result);
            }
        }
    });

    console.log(result);

    if(result == "" || result === null || result == "false"){
        return false;
    }else{
        $.ajax({
            url: "/rest/getTokenSerials/"+username,
            type: 'GET',
            async: false,
            success:function(data2){
                tokens = data2;
                if(tokens == "" || tokens === null){
                    var error = document.getElementById("errorMess");
                    error.textContent="Could not get Token Serials.";
                    $('#myModalError').modal('show');
                }else{
                    setSelectOptions(tokens);
                }
            }
        })
    }



    if(result == "" || result === null || tokens == "" || tokens === null){
        return false;
    }else{
        return true;
    }
}

function setSelectOptions(data) {

    // Get select
    var select = document.getElementById('userTokens');

    // Add options
    for (var i in data) {
        $(select).append('<option value=' + data[i] + '>' + data[i] + '</option>');
    }

    // Set selected value
    $(select).val(data[0]);
}

function blockToken(){
    var returnValue;
    $('#reg-form').submit(function(e){
        e.preventDefault();

        $.ajax({
            url: '',
            async:false,
            type: "POST",
            data: $(this).serialize(),
            success: function(data)
            {
                returnValue = ''+String(data);
                //alert(data+" return ");
                //callback methods go right here
                if(data==="true"){
                    $('#returnValue').val(returnValue);
                    returnValue = true;
                }else {
                    var error = document.getElementById("errorMess");
                    error.textContent="Operation failed.";
                    $('#myModalError').modal('show');
                    $('#returnValue').val(returnValue);
                    returnValue= false;
                }
            }
        });
    });
    $('#reg-form').submit();
    //returnValue = $('#returnValue').val();
    //alert(returnValue);
    //return Boolean(returnValue);
    if(returnValue == "" || returnValue === null || returnValue == false){
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
var SEND_USERNAME_STEP = 1;
var BLOCK_TOKEN_STEP = 2;

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

        if(ACCOUNT_DETAILS_STEP === currentIndex){
            console.log("Current step is the account details step");
            var username = $('input[name="username"]').val();
            return isValid && validateUsername(username);
        }
        if(SEND_USERNAME_STEP === currentIndex){
            console.log("Current step is the change password step");
            //form.submit();
            var username = $('input[name="username"]').val();
            var secAnswer = $('input[name="securityAnswer"]').val();
            return isValid && validateSecAnswer(secAnswer, username);
        }
        if(BLOCK_TOKEN_STEP === currentIndex){
            console.log("Current step is the change password step");
            //form.submit();
            return isValid && blockToken();
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


