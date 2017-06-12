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
        var loader = document.getElementById('loader');
        loader.style.display = "block";

        form.validate().settings.ignore = ":disabled,:hidden";
        console.log(currentIndex);
        var isValid = form.valid();

        // Allways allow previous action even if the current form is not valid!
        if (currentIndex > newIndex)
        {
            $("#myspan").hide();
            loader.style.display = "none";
            return true;
        }

        // Needed in some cases if the user went back (clean up)
        if (currentIndex < newIndex)
        {    $("#myspan").hide();
            // To remove error styles
            form.find(".body:eq(" + newIndex + ") label.error").remove();
            form.find(".body:eq(" + newIndex + ") .error").removeClass("error");
        }

        if(ACCOUNT_DETAILS_STEP === currentIndex){

            console.log("Current step is the account details step");
            var accountNumber = $('input[name="accountNumber"]').val();

            var email = $('input[name="email"]').val();
            console.log(email);
            var birthDate = $('input[name="birthDate"]').val();
            return isValid && validateAccountDetails(accountNumber, email, birthDate);

        }
        if(PROFILE_DETAILS_STEP === currentIndex){
            console.log("Current stp is the profile details step");
            var username = $('input[name="userName"]').val();
            var confirm = $('input[name="confirm"]').val();
            var regCode = $('input[name="regCode"]').val();
            return validateUsername(username) && validatePassword(confirm) && validateRegCode(regCode);
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
        window.location.href = "/login/retail";
    }
});


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
    var loader = document.getElementById('loader');
    loader.style.display = "block";
    if(email == ""){
        email = "ib@coronationmb.com"
    }
    if(birthDate == ""){
        birthDate = "11-12-1970"
    }
    var customerId;
    $.ajax({
        type:'GET',
        url:"/rest/accountdetails/"+accountNumber+"/"+email+"/"+birthDate,
        async:false,
        success:function(data1){
            customerId = ''+String(data1);

            if(customerId == "" || customerId === null){

                loader.style.display = "none";
                //invalid account number
                // $.notify({
                //     title: '<strong></strong>',
                //     message: 'Invalid Account Number, Contact the bank'
                // },{
                //     type: 'danger'
                // });

                document.getElementById("myspan").textContent="Invalid Accouunt Credentials, Contact the bank";
                $("#myspan").show();
                loader.style.display = "none";

                //alert("Account number not found");
            }else{
                loader.style.display = "none";

                //valid account number
                //alert("Customer Id: " + customerId);
                $('input[name=customerId]').val(customerId);
            }
        }
    });

    if(customerId == "" || customerId == null ){
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
                // $.notify({
                //     title: '<strong></strong>',
                //     message: 'Username already exists'
                // },{
                //     type: 'danger'
                // });

                document.getElementById("myspan1").textContent="Username already exists";
                $("#myspan1").show();
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
                // $.notify({
                //     title: '<strong></strong>',
                //     message: 'The entered password might not meet the set password policy'
                // },{
                //     type: 'danger'
                // });

                document.getElementById("myspan1").textContent="The entered password might not meet the set password policy";
                $("#myspan").show();

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
                // $.notify({
                //     title: '<strong></strong>',
                //     message: 'Enter the Registration code sent to your mobile'
                // },{
                //     type: 'danger'
                // });

                document.getElementById("myspan").textContent="Enter the Registration code sent to your mobile";
                $("#myspan").show();

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
    if(email == ""){
        email = "ib@coronationmb.com"
    }
    var birthDate = $('input[name="birthDate"]').val();
    if(birthDate == ""){
        birthDate = "11-12-1970"
    }
    var result;

    var loader = document.getElementById('load');
    loader.style.display = "block";






    $.ajax({
        type:'GET',
        url:"/rest/regCode/"+accountNumber+"/"+email+"/"+birthDate,
        async:false,
        success:function(data1){
            result = ''+String(data1);
            if(result === 'false' || result=== '' || result === null){
                loader.style.display = "none";
                //invalid account number
                //alert("user name not found");
                // $.notify({
                //     title: '<strong></strong>',
                //     message: 'Failed to send registration code. Please try again.'
                // },{
                //     type: 'danger'
                // });
                document.getElementById("myspan1").textContent="Failed to send registration code. Please try again.";
                $("#myspan2").show();



            }else{
                loader.style.display = "none";
                $("#myspan2").hide();
                var showreg = document.getElementById('regcodebox');
                showreg.style.display = "block";



                //valid account number
                //alert("code sent: " + result);
                console.log(result);
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
                    // $.notify({
                    //     title: '<strong></strong>',
                    //     message: 'Self Registration Failed'
                    // },{
                    //     type: 'danger'
                    // });

                    document.getElementById("myspan").textContent="Self Registration Failed";
                    $("#myspan").show();
                }
            }
        });
    });
    $('#reg-form').submit();
    returnValue = $('#returnValue').val();
    //alert(returnValue);
    return Boolean(returnValue);
}