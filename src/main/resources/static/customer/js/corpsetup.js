/**
 * Created by Showboy on 06/07/2017.
 */

//steps with form
var map = {};
var form = $("#setup-form");
form.validate({
    errorPlacement: function errorPlacement(error, element) {
        element.before(error);
    }
});

var SECURITY_QUESTION_STEP = 0;
var PHISHING_IMAGE_STEP = 1;
var PASSWORD_RESET_STEP = 2;
var TOKEN_AUTH_STEP = 3;
form.children("div").steps({
    headerTag: "h3",
    bodyTag: "section",
    transitionEffect: "slideLeft",
    onStepChanging: function (event, currentIndex, newIndex)
    {

        form.validate().settings.ignore = ":disabled,:hidden";
        console.log(currentIndex);
        var isValid = form.valid();

        // Always allow previous action even if the current form is not valid!
        if (currentIndex > newIndex)
        {
            // $('#myModalSuccess').modal('hide');
            return true;
        }

        // Needed in some cases if the user went back (clean up)
        if (currentIndex < newIndex)
        {
            // To remove error styles
            form.find(".body:eq(" + newIndex + ") label.error").remove();
            form.find(".body:eq(" + newIndex + ") .error").removeClass("error");
            $('#myModalSuccess').modal('hide');
            $('#myModalError').modal('hide');
        }

        if(SECURITY_QUESTION_STEP === currentIndex){
            console.log("Current Step is the security question step");
            //$("#reg-form").submit();
            return isValid;
        }
        if(PHISHING_IMAGE_STEP === currentIndex){
            console.log("Current Step is the phishing image step");
            //$("#reg-form").submit();
            return isValid && checkImage();
        }
        if(PASSWORD_RESET_STEP === currentIndex){
            console.log("Current Step is the phishing image step");
            //$("#reg-form").submit();
            var confirm = $('#confirm').val();
            return isValid && validatePassword(confirm);
        }
        if(TOKEN_AUTH_STEP === currentIndex){
            console.log("Current Step is the phishing image step");
            //$("#reg-form").submit();
            return isValid && setup();
        }


        form.validate().settings.ignore = ":disabled,:hidden";


        return form.valid();


    },
    onStepChanged: function (event, currentIndex, priorIndex)
    {
        // Used to skip the "Warning" step if the user is old enough and wants to the previous step.
        if (currentIndex === 2 && priorIndex === 3)
        {
            form.steps("previous");
        }
        if (currentIndex === 1 && priorIndex === 2)
        {
            form.steps("previous");
        }
    },
    onFinishing: function (event, currentIndex)
    {
        //form.validate().settings.ignore = ":disabled";
        return form.valid();
    },
    onFinished: function (event, currentIndex)
    {
//            alert("Submitted!");
        window.location.href = "/corporate/logout";
    }
});


function checkImage() {
    var anyImageSelected = $('input[name="phishing"]:checked').length > 0;
    if(anyImageSelected != true){
        $('#errorMess').text("Please select phishing image.");
        $('#myModalError').modal('show');
        return false;
    }else{
        return true;
    }
}

function validatePassword(password){
    var result;
    $.ajax({
        type:'GET',
        url:"/rest/corp/password/"+password,
        async:false,
        success:function(data1){
            result = ''+String(data1);
            if(result === 'true'){
                //success

            }else{
                $('#errorMess').text(result);
                $('#myModalError').modal('show');
                $('#loading-icon').hide();
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

function validateToken(){
    $('#myLoader').modal('show');
    var token = $('input[name="token"]').val();
    var result;
    $.ajax({
        type:'GET',
        url:"/corporate/setup/tokenAuth/"+token,
        async:false,

        success:function(data1){
            result = ''+String(data1);
            if(result == "true"){

            }else{
                //invalid account number
                //alert("Account number not found");
                $('#myLoader').modal('hide');
                $('#errorMess').text(result);
                $('#myModalError').modal('show');
            }
        }
    });

    if(result == "true"){
        return true;
    }else{
        return false;
    }
}

function setup(){

    var returnValue = false;
    $('#setup-form').submit(function(e){
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
                    $('#errorMess').text(data);
                    $('#myModalError').modal('show');
                }
            }
        });
    });
    $('#setup-form').submit();
    returnValue = $('#returnValue').val();
    //alert(returnValue);
    return Boolean(returnValue);

}



