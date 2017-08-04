// var customerId = "null";
// /** This validates the input account number.
//  *
//  * @param accountNumber the account number to check
//  */
// function validateAccountNo(accountNumber){
// console.log("jhvbjk "+accountNumber);
//     var customerId;
//     var secQues;
//     $.ajax({
//         type:'GET',
//         url:"/rest/retail/accountname/"+accountNumber,
//         async:false,
//         success:function(data1){
//             customerId = ''+String(data1);
//             console.log("customerId "+data1);
//             if(customerId == ""){
//                 //invalid account number
//                 document.getElementById("errorMess").textContent="Ensure you put in a valid account number.";
//                 $('#myModalError').modal('show');
//                 //alert("Account number not found");
//             }else{
//                 //valid account number
//                 //alert("Customer Id: " + customerId);
//                 $('input[name=customerId]').val(customerId);
//             }
//         }
//     });
//
//     console.log(customerId);
//
//     if(customerId == "" || customerId === null){
//         return false;
//     }else{
//         $.ajax({
//             url: "/rest/secQues/"+customerId,
//             type: 'GET',
//             async: false,
//             success:function(data2){
//                 secQues = ''+String(data2);
//                 // console.log(secQues);
//                 if(data2 == null ){
//                     document.getElementById("errorMess").textContent="Could not get Security Question from server, please try again.";
//                     $('#myModalError').modal('show');
//
//                 }else{
//                     // $('input[name=securityQuestion]').val(secQues);
//                     // console.log(data2);
//                     var container = document.getElementById("secQuestionsDiv");
//                     container.innerHTML = "";
//                     for (i = 0; i < data2.length; i++) {
//                         container.innerHTML += "<div class='form-group'>";
//                         container.innerHTML += "<label>" + data2[i] + "</label>";
//                         container.innerHTML += "</div>";
//                         container.innerHTML += "<div class='form-group'>";
//                         container.innerHTML += "<input type='text' required name='securityAnswer" + i + "' id='securityAnswer" + i + "' class='my-select required' placeholder='Security Answer'/>";
//                         container.innerHTML += "</div>";
//                     }
//                     console.log("just "+data2.length);
//                     $('input[name=noOfSecQn]').val(data2.length);
//                     console.log("the noOfSecQs "+$('#noOfSecQn').val())
//                 }
//             },
//         })
//     }
//
//     // console.log(customerId);
//
//     if(customerId == "" || customerId === null || secQues == "" || secQues === null){
//         return false;
//     }else{
//         return true;
//     }
// }
//
// function validateSecAnswer(secAnswer){
//     var customerId = $('#customerId').val();
//     // console.log('customer id {}'+customerId);
//     var result;
//     $.ajax({
//         type:'GET',
//         url:"/rest/secAns/cifId",
//         data: {customerId : customerId,secAnswers:secAnswer},
//         async:false,
//         success:function(data1){
//             result = ''+String(data1);
//             if(result === "true"){
//                 //invalid account number
//                 // console.log("whether "+data1);
//                 $('input[name=username]').val(result);
//
//
//             }else{
//                 //valid account number
//                 // console.log("whether ERROR"+data1);
//                 document.getElementById("errorMess").textContent=data1;
//                 $('#myModalError').modal('show');
//             }
//         }
//     });
//
//     if(result == "true"){
//         result = sendUsername();
//         return result;
//     }else{
//         return false;
//     }
// }
//
// function sendUsername(){
//     var returnValue;
//     $('#reg-form').submit(function(e){
//         e.preventDefault();
//
//         $.ajax({
//             url: '',
//             async:false,
//             type: "POST",
//             data: $(this).serialize(),
//             success: function(data)
//             {
//                 returnValue = ''+String(data);
//                 //alert(data+" return ");
//                 //callback methods go right here
//                 if(data==="true"){
//                     $('#returnValue').val(returnValue);
//                     returnValue = true;
//                 }else {
//                     document.getElementById("errorMess").textContent="Failed to send username, please try again later.";
//                     $('#myModalError').modal('show');
//                     $('#returnValue').val(returnValue);
//                     returnValue= false;
//                 }
//             }
//         });
//     });
//     $('#reg-form').submit();
//     //returnValue = $('#returnValue').val();
//     //alert(returnValue);
//     //return Boolean(returnValue);
//     if(returnValue == "" || returnValue === null || returnValue == false){
//         return false;
//     }else{
//         return true;
//     }
// }
//
//
//
//
// //steps with form
// var map = {};
// var form = $("#reg-form");
// form.validate({
//     errorPlacement: function errorPlacement(error, element) {
//         element.before(error);
//     },
//     rules: {
//         confirm: {
//             equalTo: "#password"
//         }
//     }
// });
//
// var ACCOUNT_DETAILS_STEP = 0;
// var SEND_USERNAME_STEP = 1;
//
// //var condition = [[${success}]];
//
// //    $("#wizard-t-2").get(0).click();
// form.children("div").steps({
//     headerTag: "h3",
//     bodyTag: "section",
//     transitionEffect: "slideLeft",
//     onStepChanging: function (event, currentIndex, newIndex)
//     {
//         form.validate().settings.ignore = ":disabled,:hidden";
//         console.log(currentIndex);
//         var isValid = form.valid();
//         // Allways allow previous action even if the current form is not valid!
//         if (currentIndex > newIndex)
//         {
//             return true;
//         }
//
//         // Needed in some cases if the user went back (clean up)
//         if (currentIndex < newIndex)
//         {
//             // To remove error styles
//             form.find(".body:eq(" + newIndex + ") label.error").remove();
//             form.find(".body:eq(" + newIndex + ") .error").removeClass("error");
//         }
//
//         if(ACCOUNT_DETAILS_STEP === currentIndex){
//             console.log("Current step is the account details step");
//             var accountNumber = $('input[name="acct"]').val();
//             return isValid && validateAccountNo(accountNumber);
//         }
//         if(SEND_USERNAME_STEP === currentIndex){
//             console.log("Current step is the change password step");
//             //form.submit();
//             var noOfQs = $('#noOfSecQn').val();
//             var i = 0;
//             var secAnswer ="";
//             for(var i = 0;i<parseInt(noOfQs);i++){
//                 // console.log("answer "+$('#securityAnswer'+i).val());
//                 if(i ===0){
//                     secAnswer+=$('#securityAnswer'+i).val();
//                 }else{
//                     secAnswer +=','+$('#securityAnswer'+i).val();
//
//                 }
//             }
//             // console.log("sec answers are "+secAnswer);
//             return isValid && validateSecAnswer(secAnswer);
//         }
//         return form.valid();
//     },
//     onFinishing: function (event, currentIndex)
//     {
//         //form.validate().settings.ignore = ":disabled";
//         return form.valid();
//     },
//     onFinished: function (event, currentIndex)
//     {
//         //alert("Submitted!");
//         window.location.href = "/login/retail";
// //             $("#reg-form").submit();
//     }
// });
//
//
