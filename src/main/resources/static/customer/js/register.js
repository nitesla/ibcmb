//steps with form
var summaryEmail = "";
var summaryUsername = "";
var summaryConfirm = "";
var summarySecQuestion = "";
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
        //console.log(currentIndex);
        var isValid = form.valid();

        // Always allow previous action even if the current form is not valid!
        if (currentIndex > newIndex)
        {
            // $('#myModalSuccess').modal('hide');
            $('#myLoader').modal('hide');
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

        if(ACCOUNT_DETAILS_STEP === currentIndex){

            //console.log("Current step is the account details step");
            var accountNumber = $('input[name="accountNumber"]').val();

            var email = $('input[name="email"]').val();
            summaryEmail = email;
            //console.log(email);
            var birthDate = $('input[name="birthDate"]').val();
            $("#register-info").hide();
            return isValid && validateAccountDetails(accountNumber, email, birthDate) && validateExists(accountNumber, email, birthDate);

        }
        if(PROFILE_DETAILS_STEP === currentIndex){

            //console.log("Current stp is the profile details step");
            var username = $('input[name="userName"]').val();
            summaryUsername = username;
            var confirm = $('input[name="confirm"]').val();
            summaryConfirm = confirm;
            var regCode = $('input[name="regCode"]').val();
            return isValid && validateRegCode(regCode) && validateUsername(username) && validatePassword(confirm);
        }
        if(SECURITY_QUESTION_STEP === currentIndex){
            
            //console.log("Current Step is the security question step");
            //$("#reg-form").submit();

            return isValid;
        }
        if(PHISHING_IMAGE_STEP === currentIndex){
            //console.log("Current Step is the phishing image step");
            //$("#reg-form").submit();
            getSummary();
            return isValid && checkImage();
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
        // $('#myLoader').modal('show');
        //form.validate().settings.ignore = ":disabled";
        return form.valid() && registerUser();
    },
    onFinished: function (event, currentIndex)
    {
//            alert("Submitted!");
        //window.location.href = "/login/retail";
        return redirectUser();
    }
});
function getSummary() {
    var noOfQuestions = $('#noOfQuestions').val();
    //console.log("noOfQuestions "+noOfQuestions);a
    var imgPath =  $('#imgPaths').val();
 
    var phishing = $("input[name='phishing']:checked"). val();
    var container = document.getElementById("regSummary");
console.log("phishing "+phishing);
    container.innerHTML = "";
    container.innerHTML += "<p style='text-transform: none'><h1>Self-Registration Summary</h1></p> <br/>";
    container.innerHTML += "<p style='text-transform: none'>Please find below a summary of the details you have entered for your registration</p>";
    container.innerHTML += "<p style='text-transform: none'>Email Address: "+summaryEmail+"</p>";
    container.innerHTML += "<p style='text-transform: none'>Username: "+summaryUsername+"</p>";
    container.innerHTML += "<p style='text-transform: none'>Password: **********</p>";
    for (i = 0; i < noOfQuestions; i++) {
        container.innerHTML += "<p style='text-transform: none'>Security Question: "+(i+1)+"  "+$('#securityQuestion'+i).val()+"</p>";
    }
    var imgP = imgPath+phishing;
    container.innerHTML += "<p style='text-transform: none'>Phishing Image: <br/><img src='"+imgP +"' width='100px' height='100px' style='padding: 5px;'/></p>";
    // container.innerHTML +="<table>" +
    //         "<tbody>" +
    //         "<tr><td>Email address:</td><td>"+summaryEmail+"</td></tr>"+
    //         "<tr><td>Username:</td><td>"+summaryUsername+"</td></tr>"+
    //         "<tr><td>Password:</td><td>**********</td></tr>";
    //         "<tr><td>Phishing Image: </td><td><img src='"+imgP +"' width='100px' height='100px' style='padding: 5px;'/></td></tr>";
    // for (i = 0; i < noOfQuestions; i++) {
    //     container.innerHTML +="<tr><td>Security Question:</td> <td>"+$('#securityQuestion'+i).val()+"</td></tr>";
    // }
    // container.innerHTML +=  "</tbody>"+
    //     "</table>"
    
}

// function loadPhishingImages(){
//     console.log("in load phishing imges");
//     var listOfImages = [];
//     $('#phishing').load("/rest/json/phishingimages");
// }

var customerId = "null";



/** This validates the input account number.
 *
 * @param accountNumber the account number to check
 */
function validateAccountDetails(accountNumber, email, birthDate){

     $('#myLoader').modal('show');

    if(email == ""){
        email = "ib@coronationmb.com"
    }
    if(birthDate == ""){
        birthDate = "19-20-1970"
    }
    var customerId;

    accountNumber = accountNumber.trim();
    email = email.trim();
    birthDate = birthDate.trim();
    var url = "/rest/accountdetails";
    var data = "accountNumber="+accountNumber+"&email="+email+"&birthDate="+birthDate;

    var http = new XMLHttpRequest();
    http.open("POST", url, false);
    http.setRequestHeader("content-type","application/x-www-form-urlencoded");
    http.onreadystatechange = function() {//Call a function when the state changes.
        if(http.readyState == 4 && http.status == 200) {
            console.log("http output "+http.responseText);
            customerId = String(http.responseText);
            if(customerId == "true" ){
                        }else {
                            $('#errorMess').text(customerId);
                            $('#myModalError').modal('show');
                            $('#myLoader').modal('hide');
                        }
        }else{
            console.log("http output 2 "+http.responseText);
            $('#myLoader').modal('hide');
                    $('#errorMess').text("Service not available, please try again later");
                    $('#myModalError').modal('show');
        }
    };
    http.send(data);

    // const response =  $.ajax({
    //     type:'POST',
    //     url:"/rest/accountdetails",
    //     cache:false,
    //     data:{accountNumber:accountNumber, email:email, birthDate:birthDate},
    //     async:true,
    //     success:function(data1){
    //         customerId = ''+String(data1);
    //         if(customerId == "true" ){
    //
    //         }else {
    //             $('#errorMess').text(customerId);
    //             $('#myModalError').modal('show');
    //             $('#myLoader').modal('hide');
    //         }
    //         // if(customerId == "" || customerId === null){
    //         //
    //         //     $('#errorMess').text(customerId);
    //         //     $('#myModalError').modal('show');
    //         //
    //         // }else{
    //         //     $('input[name=customerId]').val(customerId);
    //         // }
    //     },error:function (data) {
    //         $('#myLoader').modal('hide');
    //         $('#errorMess').text("Service not available, please try again later");
    //         $('#myModalError').modal('show');
    //     }
    // });
        console.log("the customer response "+customerId);
        if(customerId === "true"){
            return true;
        }else{
            $('#myLoader').modal('hide');
            return false;
        }
}

function validateExists(accountNumber, email, birthDate){
    console.log("validate ");

    if(email == ""){
        email = "ib@coronationmb.com"
    }
    if(birthDate == ""){
        birthDate = "19-20-1970"; // just a place holder
    }
    var cif;
    accountNumber = accountNumber.trim();
    email = email.trim();
    birthDate = birthDate.trim();

    var url = "/rest/accountexists";
    var data = "accountNumber="+accountNumber+"&email="+email+"&birthDate="+birthDate;

    var http = new XMLHttpRequest();
    http.open("POST", url, false);
    http.setRequestHeader("content-type","application/x-www-form-urlencoded");
    http.onreadystatechange = function() {//Call a function when the state changes.
        if(http.readyState == 4 && http.status == 200) {
            console.log("http output "+http.responseText);
            cif = String(http.responseText);
            if(cif == "" || cif === null){

                //invalid account number
                $('#errorMess').text("This account already exists on our internet banking platform, Please try logging in.");
                $('#myModalError').modal('show');
                $('#myLoader').modal('hide');

                //alert("Account number not found");
            }else{
                $('input[name=customerId]').val(cif);
                // sendRegCode();
            }
        }else{
            console.log("http output 2 "+http.responseText);
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    };
    http.send(data);
    if(cif == "" || cif == null ){
        $('#myLoader').modal('hide');
        return false;

    }else{
        sendRegCode();
        $('#myLoader').modal('hide');
        return true;
    }
    // const response = $.ajax({
    //     type:'POST',
    //     url:"/rest/accountexists",
    //     cache:false,
    //     data:{accountNumber:accountNumber, email:email, birthDate:birthDate},
    //     async:true,
    //     success:function(data1){
    //         cif = ''+String(data1);
    //
    //         if(cif == "" || cif === null){
    //
    //             //invalid account number
    //             $('#errorMess').text("This account already exists on our internet banking platform, Please try logging in.");
    //             $('#myModalError').modal('show');
    //             $('#myLoader').modal('hide');
    //
    //             //alert("Account number not found");
    //         }else{
    //             $('input[name=customerId]').val(cif);
    //             // sendRegCode();
    //         }
    //     },error:function (data) {
    //         $('#myLoader').modal('hide');
    //         $('#errorMess').text("Service not available, please try again later");
    //         $('#myModalError').modal('show');
    //     }
    // });

}

function validateUsername(username){
    var result;
     $('#myLoader').modal('show');
    username = username.trim();
    var url = "/rest/username/check";
    var data = "username="+username;

    var http = new XMLHttpRequest();
    http.open("POST", url, false);
    http.setRequestHeader("content-type","application/x-www-form-urlencoded");
    http.onreadystatechange = function() {//Call a function when the state changes.
        if(http.readyState == 4 && http.status == 200) {
            console.log("http output "+http.responseText);
            result = String(http.responseText);
            if(result == 'false'){
                //invalid account number
                $('#errorMess').text("Username already exists.");
                $('#myModalError').modal('show');
                $('#myLoader').modal('hide');
            }else{
                //valid account number
                //alert("user name: " + result);
            }
        }else{
            console.log("http output 2 "+http.responseText);
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    };
    http.send(data);
    // $.ajax({
    //     type:'POST',
    //     url:"/rest/username/check",
    //     cache:false,
    //     data:{username:username},
    //     async:false,
    //     success:function(data1){
    //         result = ''+String(data1);
    //         if(result == 'false'){
    //             //invalid account number
    //             $('#errorMess').text("Username already exists.");
    //             $('#myModalError').modal('show');
    //             $('#myLoader').modal('hide');
    //         }else{
    //             //valid account number
    //             //alert("user name: " + result);
    //         }
    //     },error:function (data) {
    //         $('#myLoader').modal('hide');
    //         $('#errorMess').text("Service not available, please try again later");
    //         $('#myModalError').modal('show');
    //     }
    // });

    if(result === 'true'){
        //username is valid and available
        
        return true;
    }else{
         $('#myLoader').modal('hide');
        return false;
    }
    
}

function validatePassword(password){
    var result;
    password = password.trim();
    // var url = "/rest/password/password";
    // var data = "password="+password;
    //
    // var http = new XMLHttpRequest();
    // http.open("POST", url, false);
    // http.setRequestHeader("content-type","application/x-www-form-urlencoded");
    // http.onreadystatechange = function() {//Call a function when the state changes.
    //     if(http.readyState == 4 && http.status == 200) {
    //         console.log("http output "+http.responseText);
    //         result = String(http.responseText);
    //         if(result === 'true'){
    //             //success
    //             $('#myLoader').modal('hide');
    //         }else{
    //             $('#errorMess').text(result);
    //             $('#myModalError').modal('show');
    //             $('#myLoader').modal('hide');
    //         }
    //     }else{
    //         $('#myLoader').modal('hide');
    //         $('#errorMess').text("Service not available, please try again later");
    //         $('#myModalError').modal('show');
    //     }
    // };
    // http.send(data);
    $.ajax({
        type:'POST',
        data:{password:password},
        url:"/rest/password/password",
        async:false,
        cache:false,
        success:function(data1){
            result = ''+String(data1);
            if(result === 'true'){
                //success
                $('#myLoader').modal('hide');
            }else{
                $('#errorMess').text(result);
                $('#myModalError').modal('show');
                $('#myLoader').modal('hide');
            }
        },error:function (data) {
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
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
    code = code.trim();
    var url = "/rest/regCode/check";
    var data = "code="+code;

    var http = new XMLHttpRequest();
    http.open("POST", url, false);
    http.setRequestHeader("content-type","application/x-www-form-urlencoded");
    http.onreadystatechange = function() {//Call a function when the state changes.
        if(http.readyState == 4 && http.status == 200) {
            console.log("http output "+http.responseText);
            result = String(http.responseText);
            if(result == 'true'){
                //invalid account number


            }else{
                $('#errorMess').text(result);
                $('#myModalError').modal('show');
            }
        }else{
            console.log("http output 2 "+http.responseText);
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    };
    http.send(data);
    // $.ajax({
    //     type:'POST',
    //     url:"/rest/regCode/check",
    //     cache:false,
    //     data:{code:code},
    //     async:false,
    //     success:function(data1){
    //         result = ''+String(data1);
    //         // console.log("error "+result);
    //         if(result == 'true'){
    //             //invalid account number
    //
    //
    //         }else{
    //             $('#errorMess').text(result);
    //             $('#myModalError').modal('show');
    //         }
    //     },error:function (data) {
    //         console.log("error "+data);
    //         $('#myLoader').modal('hide');
    //         $('#errorMess').text("Service not available, please try again later");
    //         $('#myModalError').modal('show');
    //     }
    // });

    if(result === 'true'){
        //username is valid and available
         $('#myLoader').modal('hide');
        return true;
    }else{
         $('#myLoader').modal('hide');
        return false;
    }
}


function sendRegCode(){

    var accountNumber = $('input[name="accountNumber"]').val();
    var email = $('input[name="email"]').val();
    if(email == ""){
        email = "ib@coronationmb.com";
    }
    var birthDate = $('input[name="birthDate"]').val();
    if(birthDate == ""){
        birthDate = "19-20-1970";
    }
    var result;
    accountNumber = accountNumber.trim();
    email = email.trim();
    birthDate = birthDate.trim();
    // var url = "/rest/regCode";
    // var data = "accountNumber="+accountNumber+"&email="+email+"&birthDate="+birthDate;
    // var http = new XMLHttpRequest();
    // http.open("POST", url, false);
    // http.setRequestHeader("content-type","application/x-www-form-urlencoded");
    // http.onreadystatechange = function() {//Call a function when the state changes.
    //     if(http.readyState == 4 && http.status == 200) {
    //         console.log("http output "+http.responseText);
    //         result = String(http.responseText);
    //         if(result === 'false' || result=== '' || result === null){
    //             //invalid account number
    //             $("#errorMess").text("Failed to send registration code. Please try again.");
    //             $('#myModalError').modal('show');
    //
    //
    //         }else if(result ==="noPhoneNumber"){
    //             $("#errorMess").text("No registered phone number to send reg code to, please contact the bank");
    //             $('#myModalError').modal('show');
    //             $(".btn-link").on("click", function()
    //             {
    //                 window.location.href = "/login/corporate";
    //             });
    //             $("#myModalError").on("hidden.bs.modal", function () {
    //                 window.location.href = "/login/corporate";
    //
    //             });
    //         }
    //         else{
    //
    //             $('#successMess').text("Registration code has been successfully sent. If you do not receive a message after 3 minutes, please retry.");
    //             $('#myModalSuccess').modal('show');
    //
    //             // var showreg = document.getElementById('regcodebox');
    //             // showreg.style.display = "block";
    //
    //
    //             //valid account number
    //             //alert("code sent: " + result);
    //             //console.log(result);
    //         }
    //     }else{
    //         // console.log("http output 2 "+http.responseText);
    //         $('#myLoader').modal('hide');
    //         $('#errorMess').text("Service not available, please try again later");
    //         $('#myModalError').modal('show');
    //     }
    // };
    $.ajax({
        type:'POST',
        url:"/rest/regCode",
        cache:false,
        data:{accountNumber:accountNumber, email:email, birthDate:birthDate},
        async:false,
        success:function(data1){
            result = ''+String(data1);
            if(result === 'false' || result=== '' || result === null){
                //invalid account number
                $("#errorMess").text("Failed to send registration code. Please try again.");
                $('#myModalError').modal('show');


            }else if(result ==="noPhoneNumber"){
                $("#errorMess").text("No registered phone number to send reg code to, please contact the bank");
                $('#myModalError').modal('show');
                $(".btn-link").on("click", function()
                {
                    window.location.href = "/login/corporate";
                });
                $("#myModalError").on("hidden.bs.modal", function () {
                    window.location.href = "/login/corporate";

                });
            }
            else{

                $('#successMess').text("Registration code has been sent to your registered phone number. If you do not receive a message after 3 minutes, please retry.");
                $('#myModalSuccess').modal('show');

                // var showreg = document.getElementById('regcodebox');
                // showreg.style.display = "block";


                //valid account number
                //alert("code sent: " + result);
                //console.log(result);
            }
        },error:function (data) {
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    });
}


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

function registerUser(){
    console.log("to register");
    // $('.actions > ul').hide();
    var time = 500;
    $('#myLoader').modal('show');
        var returnValue = false;
        var url = '/register';
        var data = $("#reg-form").serialize();
    setTimeout(function(){
        var http = new XMLHttpRequest();
        http.open("POST", url, false);
        http.setRequestHeader("content-type","application/x-www-form-urlencoded");
        http.onreadystatechange = function() {//Call a function when the state changes.
            if(http.readyState == 4 && http.status == 200) {
                console.log("http output "+http.responseText);
                data = String(http.responseText);
                if(data==="true"){
                    $('#returnValue').val(true);
                    redirectUser();
                }else {
                    // $('.actions > ul').show();
                    $('#myLoader').modal('hide');
                    $('#errorMess').text(data);
                    $('#myModalError').modal('show');
                }
            }else{
                // hideSpinner();
                console.log("http output 2 "+http.responseText);
                $('#myLoader').modal('hide');
                $('#errorMess').text("Service not available, please try again later");
                $('#myModalError').modal('show');
            }
        };
        http.send(data);
        returnValue = $('#returnValue').val();
        console.log("final response "+returnValue);
        if(returnValue === 'true'){
            //username is valid and available
            return true;
        }else{
            // $('#myLoader').modal('hide');
            hideSpinner();
            return false;
        }
    }, time );

    //     $.ajax({
    //         url: '',
    //         async:false,
    //         type: "POST",
    //         data: $(this).serialize(),
    //         success: function(data)
    //         {
    //             //alert(data+" return ");
    //             //callback methods go right here
    //             if(data==="true"){
    //                 $('#returnValue').val(true);
    //             }else {
    //                 $('#errorMess').text(data);
    //                 $('#myModalError').modal('show');
    //             }
    //         },error:function (data) {
    //             $('#myLoader').modal('hide');
    //             $('#errorMess').text("Service not available, please try again later");
    //             $('#myModalError').modal('show');
    //         }
    //     });
    // $('#reg-form').submit();
    //  $('#myLoader').modal('hide');

}

function redirectUser() {
    window.location.href = '/rest/redirect/login';
}

