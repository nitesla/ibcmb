var customerId = "null";
var email = "";
var noOfQs = 0;
var secAnswer ="";
/** This validates the input account number.
 *
 * @param accountNumber the account number to check
 */

function validateAccountNo(accountNumber){
console.log("the num "+accountNumber);
    // var customerId;
    var secQues;
    $.ajax({
        type:'GET',
        url:"/rest/retail/accountname/"+accountNumber,
        async:false,
        success:function(data1){
            customerId = ''+String(data1);
            console.log("customerId "+data1);
            if(customerId == ""){
                //invalid account number
                document.getElementById("errorMess").textContent="Ensure you put in a valid account number.";
                $('#myModalError').modal('show');
                //alert("Account number not found");
            }else{
                //valid account number
                //alert("Customer Id: " + customerId);
                $('input[name=customerId]').val(customerId);
            }
        }
    });

    console.log(customerId);

    if(customerId == "" || customerId === null){
        return false;
    }else{
        $.ajax({
            url: "/rest/secQues/"+customerId,
            type: 'GET',
            async: false,
            cache:false,
            success:function(data2){
                secQues = ''+String(data2);
                // console.log(secQues);
                if(data2 == null ){
                    document.getElementById("errorMess").textContent="Could not get Security Question from server, please try again.";
                    $('#myModalError').modal('show');
                     $('#myLoader').modal('hide');

                }else{
                    // console.log('security questn 1 ' +$('#noOfSecQn').val());

                    var container = document.getElementById("secQuestionsDiv");
                    container.innerHTML = "";
                    for (i = 0; i < data2.length; i++) {
                        container.innerHTML += "<div class='form-group'>";
                        container.innerHTML += "<label>" + data2[i] + "</label>";
                        container.innerHTML += "</div>";
                        container.innerHTML += "<div class='form-group'>";
                        container.innerHTML += "<input type='text' required autocomplete='off' name='securityAnswer" + i + "' id='securityAnswer" + i + "' class='my-select required' placeholder='Security Answer'/>";
                        container.innerHTML += "</div>";
                    }
                    console.log('no of questn ' +data2.length);
                    noOfQs = data2.length;
                    $('input[name=noOfSecQn]').val(data2.length);
                    console.log('security questn ' +$('#noOfSecQn').val());
                     $('#myLoader').modal('hide');
                }
            }
        })
    }

    console.log(customerId);

    if(customerId == "" || customerId === null || secQues == "" || secQues === null){
        return false;
    }else{
        return true;
    }
}

function validateSecAnswer(secAnswer){
    // var customerId = $('#customerId').val();
 $('#myLoader').modal('show');
    // console.log('customer id '+customerId);
    // console.log('customer secAnswer '+secAnswer);
    var result = '';
    $.ajax({
        type:'GET',
        url:"/rest/secAns/cifId",
        cache:false,
        data: {customerId : customerId,secAnswers:secAnswer},
        async:false,
        success:function(data1){
            result = ''+String(data1);
            // console.log("the result after comparism "+result);
            if(result === "true"){
                //invalid account number
                // console.log("whether "+data1);
                $('input[name=username]').val(result);


            }else{
                //valid account number
                // console.log("whether ERROR"+data1);
                document.getElementById("errorMess").textContent=data1;
                 $('#myLoader').modal('hide');
                $('#myModalError').modal('show');
            }
        }
    });

    
    console.log("the result after comparism 2"+result);

    if(result == "true"){
        result = sendUsername();
        return result;
    }else{
        return false;
    }
}

function sendUsername(){
    var returnValue;
    $('#reg-form').submit(function(e){
        e.preventDefault();
// console.log("send  usernammec "+customerId);
        $.ajax({
            url: '',
            async:false,
            type: "POST",
            data: {customerId:customerId,noOfSecQn:noOfQs,secAnswer:secAnswer},
            success: function(data)
            {
                returnValue = ''+String(data);
                //alert(data+" return ");
                //callback methods go right here
                if(data==="true"){
                     $('#myLoader').modal('hide');
                    $('#returnValue').val(returnValue);
                    returnValue = true;
                }else {
                     $('#myLoader').modal('hide');
                    document.getElementById("errorMess").textContent="Failed to send username, please try again later.";
                    $('#myModalError').modal('show');
                    $('#returnValue').val(returnValue);
                    returnValue= false;
                }
            }
        });
    });
    
    $('#reg-form').submit();
    $('#myLoader').modal('hide');
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
            var accountNumber = $('input[name="acct"]').val();
            // email = $('input[name="email"]').val();
            // console.log("email "+email);
            return isValid && validateAccountNo(accountNumber);
            // console.log("Current step is the account details step");
            // var accountNumber = $('input[name="acct"]').val();
            // return isValid && validateAccountNo(accountNumber);
        }
        if(SEND_USERNAME_STEP === currentIndex){
            console.log("Current step is the change password step");
            //form.submit();
            var i = 0;
            secAnswer = "";
            for(var i = 0;i<parseInt(noOfQs);i++){
                // console.log("answer "+$('#securityAnswer'+i).val());
                if(i ===0){
                    secAnswer+=$('#securityAnswer'+i).val();
                }else{
                    secAnswer +=','+$('#securityAnswer'+i).val();

                }
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
        window.location.href = "/login/retail";
//             $("#reg-form").submit();
    }
});


//var entityDetails = [4];
// entityDetails[0] = "";
// entityDetails[1] = "";
//function validateAccountNo(accountNumber, email){
//  $('#myLoader').modal('show');
//     var secQues;
//     var emaill = email;
//     console.log("the email "+emaill)
//     $.ajax({
//         type:'GET',
//         url:"/rest/retail/"+emaill+"/"+accountNumber,
//         async:false,
//         cache:false,
//         success:function(data1){
//             entityDetails[0] = data1[0];
//             entityDetails[1] = data1[1];
//             customerId = entityDetails[0];
//             console.log("the userrrrrrr" + customerId)
//             email = entityDetails[1];
//             console.log("the email" + email)
//             if(customerId == "" && email == ""){
//                 //invalid account number
//                 document.getElementById("errorMess").textContent="Ensure you put in a valid account number. and email";
//                 $('#myModalError').modal('show');
//                 $('#myLoader').modal('hide');
//                 //alert("Account number not found");
//             }else{
//                 //valid account number
//                 //alert("Customer Id: " + customerId);
//                 //$('input[name=customerId]').val(customerId);
//             }
//         }
//     });