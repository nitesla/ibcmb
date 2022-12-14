var customerId = "null";

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
var VALIDATE_GEN_PASS = 1;
var VALIDATE_PASSWORD_STEP = 2;
var TOKEN_AUTH = 3;
var CHANGE_PASSWORD_STEP = 4;

//var condition = [[${success}]];

//    $("#wizard-t-2").get(0).click();
form.children("div").steps({
    headerTag: "h3",
    bodyTag: "section",
    transitionEffect: "slideLeft",
    onStepChanging: function (event, currentIndex, newIndex)
    {

        form.validate().settings.ignore = ":disabled,:hidden";
        //console.log(currentIndex);
        var isValid = form.valid();

        if(SECURITY_QUESTION_STEP === currentIndex){
            console.log("Current step is the account details step");
            var noOfQs = $('#noOfQuestion').val();
            var i = 0;
            var secAnswer ="";
            for(var i = 0;i<parseInt(noOfQs);i++){
                // console.log("answer "+$('#securityAnswer'+i).val());
                if(i ===0){
                    secAnswer+=$('#securityAnswer'+i).val();
                }else{
                    secAnswer +=','+$('#securityAnswer'+i).val();

                }
            }
            console.log("answer 2 "+secAnswer);
            return isValid && validateSecAnswer(secAnswer);
        }
        if(VALIDATE_GEN_PASS === currentIndex){
            //console.log("Current step is the account details step");
            return isValid && validateGenPassword();
        }
        if(VALIDATE_PASSWORD_STEP === currentIndex){
            //console.log("Current step is the change password step");
            //form.submit();
            var confirm = $('#confirm').val();

            return isValid && validatePassword(confirm);
        }
        if(TOKEN_AUTH === currentIndex){
            //console.log("Current step is the account details step");
            return isValid && validateToken() && changePassword();
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


function validateSecAnswer(secAnswers){
    $('#myLoader').modal('show');
    var secAnswer = secAnswers;
    var sent = "";
    var result = "";
    var username = $('#username').val();
    username = username.trim();
    // console.log("validating "+secAnswer);
    // var url = "/rest/secAns";
    // var data = "username="+username+"&secAnswers="+secAnswer;
    // var http = new XMLHttpRequest();
    // http.open("POST", url, false);
    // http.setRequestHeader("content-type","application/x-www-form-urlencoded");
    // http.onreadystatechange = function() {//Call a function when the state changes.
    //     if(http.readyState == 4 && http.status == 200) {
    //         console.log("http output "+http.responseText);
    //         result = String(http.responseText);
    //         // result = ''+data;
    //         console.log("the result 1 "+result);
    //         if(data === 'true'){
    //             //$('input[name=username]').val(result);
    //         }else{
    //             //invalid account number
    //             //alert("Account number not found");
    //             $('#errorMess').text(result);
    //             $('#myLoader').modal('hide');
    //             $('#myModalError').modal('show');
    //         }
    //     }else{
    //         // console.log("http output 2 "+http.responseText);
    //         $('#myLoader').modal('hide');
    //         $('#errorMess').text("Service not available, please try again later");
    //         $('#myModalError').modal('show');
    //     }
    // };
    // http.send(data);
    $.ajax({
        type:'POST',
        url:"/rest/secAns",
        data: {username : username,secAnswers:secAnswer},
        async:false,
        cache:false,
        success:function(data){
            result = ''+data;
            console.log("the result 1 "+result);
            if(data === 'true'){
                //$('input[name=username]').val(result);
            }else{
                //invalid account number
                //alert("Account number not found");
                $('#errorMess').text(result);
                $('#myLoader').modal('hide');
                $('#myModalError').modal('show');
            }
        },error:function (data) {
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    });
    // console.log("the result "+result);
    if(result == "true"){
        console.log("tyyey");
        sent = sendGenPassword();
        console.log("the sent reponse "+sent);
        if(String(sent) == "true"){
            return true;
        }else {
            return false;
        }
    }else{
        $('#myLoader').modal('hide');
        return false;
    }
}

function sendGenPassword() {
    var username = $('input[name="username"]').val();
    username = username.trim();
    console.log("the username "+username);
    var result="";
    $.ajax({
        type:'POST',
        url:"/rest/sendGenPass/username",
        data:{username:username},
        async:false,
        cache:false,
        success:function(data1){
            result = ''+String(data1);
            console.log("the temp "+result);
            if(result == "true"){
                $('#successMess').text("A reset code has been sent to your email address.");
                $('#myLoader').modal('hide');
                $('#myModalSuccess').modal('show');

            }else{
                //invalid account number
                //alert("Account number not found");
                $('#myLoader').modal('hide');
                $('#errorMess').text(result);
                $('#myModalError').modal('show');
            }
        },error:function (data) {
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    });

    if(result == "true"){
        return true;
    }else{
        return false;
    }
}

function validateGenPassword() {
    $('#myLoader').modal('show');
    // console.log("p** fff");
    var result;
    var username = $('#username').val();
    var genpassword = $('#genpassword').val();
    // console.log("p** "+genpassword);
    genpassword = genpassword.trim();

    $.ajax({
        type:'POST',
        data:{genpassword:genpassword,username:username},
        url:"/rest/verGenPass/username/genpassword",
        async:false,
        cache:false,
        success:function(data1){
            result = ''+String(data1);
            //console.log("the result "+result);
            if(result == "true"){
                $('#myLoader').modal('hide');
            }else{
                //invalid account number
                //alert("Account number not found");
                $('#myLoader').modal('hide');
                $('#errorMess').text(result);
                $('#myModalError').modal('show');
            }
        },error:function (data) {
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    });

    if(result == "true"){
        return true;
    }else{
        return false;
    }
}

function validatePassword(password){
    password =  password.trim();
    var username = $('#username').val();
// console.log("username "+username+" confirm "+password);
    var res;
    $('#myLoader').modal('show');
    $.ajax({
        type:'POST',
        cache:false,
        data:{password:password, username:username},
        url:"/rest/password/check",
        async:false,
        success:function(data1){
            res = ''+String(data1);
            if(res === 'true'){
                //success
                $('#myLoader').modal('hide');
            }else{
                $('#myLoader').modal('hide');
                $('#errorMess').text(res);
                $('#myModalError').modal('show');

            }
        },error:function (data) {
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    });

    if(res === 'true'){
        //username is valid and available
        return true;
    }else{
        return false;
    }
}

function validateToken(){
    $('#myLoader').modal('show');
    var username = $('input[name="username"]').val();
    var token = $('input[name="token"]').val();
    var result;
    $.ajax({
        type:'POST',
        url:"/rest/tokenAuth/username/token",
        data:{username:username,token:token},
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
        },error:function (data) {
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    });

    if(result == "true"){
        return true;
    }else{
        return false;
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

                    document.getElementById("errorMess").textContent=data;
                    $('#myModalError').modal('show');

                }
            },error:function (data) {
                $('#myLoader').modal('hide');
                $('#errorMess').text("Service not available, please try again later");
                $('#myModalError').modal('show');
            }
        });
    });

    $('#reg-form').submit();
    $('#myLoader').modal('hide');
    returnValue = $('#returnValue').val();
    //alert(returnValue);
    return Boolean(returnValue);
}




