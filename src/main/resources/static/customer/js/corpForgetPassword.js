/**
 * Created by Longbridge on 7/24/2017.
 */
// function loadPasswordPage(){
//     console.log("username "+$('#username').val());
//     var username = $('#username').val();
//     var corporateId = $('#corporateId').val();
//     console.log("corporateId "+$('#corporateId').val());
//     $.ajax({
//         type: 'GET',
//         url: '/forgot/password/corporate',
//         asyn:false,
// //					dataType: 'String',
//         data: {username:username,corporateId:corporateId},
//         success: function (data2) {
//             console.log(data2);
// //             if (reponse === 'success') {
// // //                    $("#tokenmodal").hide();
// // //                    $("#otpmodal").show();
// // //                    $('#loading-icon').hide();
// //                 $('#successMess').show();
// //                 document.getElementById("successMess").textContent = "Successfully sent";
// //
// //             }else{
// // //                    $("#tokenModalCorp").show();
// // //                    $("#otpmodalCorp").hide();
// //                 if(reponse==="empty"){
// //                     $('#loading-icon').hide();
// //                     $('#errorMess').show();
// //                     document.getElementById("errorMess").textContent = "Invalid username";
// //                 }else{
// //                     $('#loading-icon').hide();
// //                     $('#errorMess').show();
// //                     document.getElementById("errorMess").textContent = reponse;
// //                 }
// //             }
//         },error: function (data) {
//             // $('#loading-icon').hide();
//             // alert("failure");
// //                $('#sub').disabled();
//             console.log("error")
//         }
//     });
// //        $.ajax({options});
// }
