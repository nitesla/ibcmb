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
        console.log(currentIndex);
        var isValid = form.valid();

        if(SECURITY_QUESTION_STEP === currentIndex){
            console.log("Current step is the account details step");
            var customerId = $('#customerId').val();
            
            // console.log("answer 2 "+secAnswer);
            return isValid && fetchCorpDetails(customerId);
        }
        if(VALIDATE_GEN_PASS === currentIndex){
            console.log("Current step is the account details step");
            return isValid && validateGenPassword();
        }
        if(VALIDATE_PASSWORD_STEP === currentIndex){
            console.log("Current step is the change password step");
            //form.submit();
            var confirm = $('input[name="confirm"]').val();
            return isValid && validatePassword(confirm);
        }
        if(TOKEN_AUTH === currentIndex){
            console.log("Current step is the account details step");
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


function fetchCorpDetails(customerId){
    // var secAnswer = secAnswers;
    // var sent = "";
    // var result;
    // var username = $('input[name="username"]').val();
    // console.log("validating "+secAnswer);
    $.ajax({
        type:'GET',
        url:"/ops/corporates/users/corp/new",
        data: {customerId:customerId},
        async:false,

        success:function(data){
            result = ''+String(data);
            if(result != null){
                console.log("success");
                //$('input[name=username]').val(result);
                var container = document.getElementById("acountDetails");
                container.innerHTML = "";
                console.log("the data size "+data.get('accountNum').size);
                for (i = 0; i < data.get('accountNum').size; i++) {
                    container.innerHTML += "<label class='checkbox-inline'>";
                    container.innerHTML += "<input type='checkbox'' name='accounts"+i;
                    container.innerHTML += "' value='"+data.get('accountNum').get(i)+"'/>";
                    container.innerHTML += "<span  text='"+data.get('accountNum').get(i)+" - "+data.get('accountNum').get(i)+"'></span>";
                    container.innerHTML += "</label>";
                }
            }else{
                //invalid account number
                //alert("Account number not found");
                $('#errorMess').text(result);
                $('#myModalError').modal('show');
            }
        }
    });

    if(result != null){
        return true;
        // // sent = sendGenPassword();
        // if(String(sent) == "true"){
        //     return true;
        // }else {
        //     return false;
        // }
    }else{
        return false;
    }
}

function sendGenPassword() {
    var username = $('input[name="username"]').val();
    var result;
    $.ajax({
        type:'GET',
        url:"/rest/sendGenPass/"+username,
        async:false,
        success:function(data1){
            result = ''+String(data1);
            if(result == "true"){
                $('#successMess').text("A temporary password has been sent to your email address.");
                $('#myModalSuccess').modal('show');
            }else{
                //invalid account number
                //alert("Account number not found");
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

function validateGenPassword() {
    var result;
    var username = $('input[name="username"]').val();
    var genpassword = $('input[name="genpassword"]').val();
    $.ajax({
        type:'GET',
        url:"/rest/verGenPass/"+username+"/"+genpassword,
        async:false,
        success:function(data1){
            result = ''+String(data1);
            if(result == "true"){

            }else{
                //invalid account number
                //alert("Account number not found");
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

function validatePassword(password){
    var res;
    $.ajax({
        type:'GET',
        url:"/rest/password/check/"+password,
        async:false,
        success:function(data1){
            res = ''+String(data1);
            if(res === 'true'){
                //success
            }else{

                $('#errorMess').text(res);
                $('#myModalError').modal('show');

            }
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
    var username = $('input[name="username"]').val();
    var token = $('input[name="token"]').val();
    var result;
    $.ajax({
        type:'GET',
        url:"/rest/tokenAuth/"+username+"/"+token,
        async:false,
        success:function(data1){
            result = ''+String(data1);
            if(result == "true"){

            }else{
                //invalid account number
                //alert("Account number not found");
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
            }
        });
    });
    $('#reg-form').submit();
    returnValue = $('#returnValue').val();
    //alert(returnValue);
    return Boolean(returnValue);
}
	



