/**
 * Created by Showboy on 07/07/2017.
 */
var map = {};
var form = $("#setup-form");
form.validate({
    errorPlacement: function errorPlacement(error, element) { element.before(error); },
    rules: {
        confirm: {
            equalTo: "#password"
        }
    }
});
var PHISHING_IMAGE_STEP = 1;
form.children("div").steps({
    headerTag: "h3",
    bodyTag: "section",
    transitionEffect: "slideLeft",
    onStepChanging: function (event, currentIndex, newIndex)
    {
        form.validate().settings.ignore = ":disabled,:hidden";
        console.log(currentIndex);
        var isValid = form.valid();

        if(PHISHING_IMAGE_STEP === currentIndex){
            console.log("Current Step is the phishing image step");
            //$("#reg-form").submit();
            return isValid && updateUser();
        }

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
        window.location.href = "/corporate/reset_password";
    }
});

function updateUser(){

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
                    $('#errorMess').text("Profile update could not be completed due to service unavailability.");
                    $('#myModalError').modal('show');
                }
            },error:function (data) {
                $('#myLoader').modal('hide');
                $('#errorMess').text("Service not available, please try again later");
                $('#myModalError').modal('show');
            }
        });
    });
    $('#setup-form').submit();
    returnValue = $('#returnValue').val();
    //alert(returnValue);
    return Boolean(returnValue);
}