var customerId = "null";
var entityDetails =[5];
entityDetails[0] = "";
entityDetails[1] = "";
entityDetails[2] = "";
entityDetails[3] = "";
entityDetails[4] = "";
var noOfQs = 0;
var secAnswer ="";
var email = "";

/** This validates the input account number.
 *
 * @param accountNumber the account number to check
 */
function validateAccountNo(corporateId,email){
    $('#myLoader').modal('show');
    var secQues;
    var email1 = email;
    // console.log("the corporateId "+corporateId);
    $.ajax({
        type:'POST',
        url:"/rest/corporate/email/corporateId",
        data:{email:email1,corporateId:corporateId},
        async:false,
        success:function(data1){
            entityDetails[0] = data1[0];
            entityDetails[1] = data1[1];
            entityDetails[2] = data1[2];
            entityDetails[3] = data1[3];
            entityDetails[4] = data1[4];
            // console.log("the data "+data1);
            // console.log("the entityDetails "+entityDetails);
            if(data1[0] == '' && data1[1] == ''){
                //invalid account number
                document.getElementById("errorMess").textContent="Please supply valid Corporate ID. and email";
                $('#myModalError').modal('show');
                $('#myLoader').modal('hide');
                //alert("Account number not found");
            }else{
                $('#myLoader').modal('hide');
                //valid account number
                //alert("Customer Id: " + customerId);
                // $('input[name=customerId]').val(customerId);
            }
        },error:function (data) {
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    });

    // console.log("available "+entityDetails[0]);
var fetchQuestion = "";
    if(entityDetails[0] == '' && entityDetails[1] == ''){
        return false;
        $('#myLoader').modal('hide');
    }else{
        $('#myLoader').modal('hide');
        $.ajax({
            url: "/rest/corporate/secQues/"+entityDetails,
            type: 'GET',
            async: false,
            success:function(data2){
                // console.log("kjhb "+data2);
                secQues = ''+String(data2);
                console.log("first time login ",entityDetails[4]);
                if(data2 == null || data2 ==''){
                    if(entityDetails[4] == "Y"){
                        document.getElementById("errorMess").textContent="Please complete registration by providing credentials sent to your email or contact the bank.";
                        $('#myModalError').modal('show');
                        $(".btn-link").on("click", function()
                        {                            window.location.href = "/login/corporate";
                        });
                    }else{
                        document.getElementById("errorMess").textContent="Could not get Security Question from server, please try again later.";
                        $('#myModalError').modal('show');

                    }

                }else{
                    fetchQuestion = "true";
                    // $('input[name=securityQuestion]').val(secQues);
                    // console.log(data2.length);
                    noOfQs = data2.length;
                    console.log("the noOfQs "+noOfQs);
                    var container = document.getElementById("secQuestionsDiv");
                    container.innerHTML = "";
                    for (i = 0; i < data2.length; i++) {
                        container.innerHTML += "<div class='form-group'>";
                        container.innerHTML += "<label>" + data2[i] + "</label>";
                        container.innerHTML += "</div>";
                        container.innerHTML += "<div class='form-group'>";
                        container.innerHTML += "<input type='text' autocomplete='off' required name='corpSecurityAnswer" + i + "' id='corpSecurityAnswer" + i + "' class='my-select required' placeholder='Security Answer'/>";
                        container.innerHTML += "</div>";
                    }
                    $('input[name=noOfSecQn]').val(data2.length);
                }
            },error:function (data) {
                $('#myLoader').modal('hide');
                $('#errorMess').text("Service not available, please try again later");
                $('#myModalError').modal('show');
            }
        })
    }

    // console.log("fetchQuestion "+fetchQuestion);

    if(fetchQuestion === "true"){
        return true ;
    }else{
        return false;
    }
}

function validateSecAnswer(secAnswer){
    // console.log("the sec details "+entityDetails);
    // console.log("the sec answer "+secAnswer);
    // var customerId = $('#customerId').val();
    $('#myLoader').modal('show');
    // console.log('customer id {}'+customerId);
    var result;
    $.ajax({
        type:'GET',
        url: "/rest/corporate/validate/secAns/"+entityDetails,
        data: {secAnswers:secAnswer},
        async:false,
        success:function(data1){
            result = ''+String(data1);
            console.log("the outcome "+result);
            if(result === "true"){

                //invalid account number
                // console.log("whether "+data1);
                $('input[name=username]').val(result);


            }else{
                //valid account number
                // console.log("whether ERROR"+data1);
                document.getElementById("errorMess").textContent=data1;
                $('#myModalError').modal('show');
            }
        },error:function (data) {
            $('#myLoader').modal('hide');
            $('#errorMess').text("Service not available, please try again later");
            $('#myModalError').modal('show');
        }
    });


    if(result == "true"){
        result = sendUsername();
        $('#myLoader').modal('hide');
        return result;
    }else{
        $('#myLoader').modal('hide');
        return false;
    }

}

function sendUsername(){

    var returnValue;
    $('#reg-form').submit(function(e){
        e.preventDefault();

        $.ajax({
            url: '',
            async:false,
            type: "POST",
            data: {entityId:entityDetails[0],entityGroup:entityDetails[1],secAnswer:secAnswer,email:email,firstName:entityDetails[2],userName:entityDetails[3],noOfSecQn:noOfQs},
            success: function(data)
            {
                returnValue = ''+String(data);
                //alert(data+" return ");
                //callback methods go right here
                if(data==="true"){
                    $('#returnValue').val(returnValue);
                    returnValue = true;
                }else {
                    document.getElementById("errorMess").textContent="Failed to send username, please try again later later.";
                    $('#myModalError').modal('show');
                    $('#returnValue').val(returnValue);
                    returnValue= false;
                }
            },error:function (data) {
                $('#myLoader').modal('hide');
                $('#errorMess').text("Service not available, please try again later");
                $('#myModalError').modal('show');
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
            return false;
        }

        // Needed in some cases if the user went back (clean up)
        // if (currentIndex < newIndex)
        // {
        //     // To remove error styles
        //     form.find(".body:eq(" + newIndex + ") label.error").remove();
        //     form.find(".body:eq(" + newIndex + ") .error").removeClass("error");
        // }

        if(ACCOUNT_DETAILS_STEP === currentIndex){
            console.log("Current step is the account details step");
            var corporateId = $('#corporateId').val();
            email = $('#email').val();
            console.log("email "+email);
            return isValid && validateAccountNo(corporateId,email);
        }
        if(SEND_USERNAME_STEP === currentIndex){
            console.log("Current step is the change password step");
            //form.submit();
            var i = 0;
            secAnswer = "";
            // console.log("noOfQuestion "+noOfQs);
            for(var i = 0;i<parseInt(noOfQs);i++){
                // console.log("answer are"+$('#corpSecurityAnswer'+i).val());
                if(i ===0){
                    secAnswer+=$('#corpSecurityAnswer'+i).val();
                }else{
                    secAnswer +=','+$('#corpSecurityAnswer'+i).val();

                }
                // alert(secAnswer);
            }
            // console.log("sec answers are "+secAnswer);
            return isValid && validateSecAnswer(secAnswer);
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
        window.location.href = "/login/corporate";
//             $("#reg-form").submit();
    }
});


