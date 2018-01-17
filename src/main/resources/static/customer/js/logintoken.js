function sendOtp() {
    //        console.log([[${username}]]);
    $('#loading-icon').show();
    $('#errorMess').hide();
    //make sure alert message is not empty
    //        alert("send otp");
    //        console.log(acct);
    $.ajax({
        type: 'GET',
        url: '/otp/send',
        asyn: false,
        //					dataType: 'String',
        data: {
            username: $('#username').val()
        },
        error: function(data) {
            $('#loading-icon').hide();
            // alert("failure");
            //                $('#sub').disabled();
        },
        success: function(reponse) {
            if (reponse === 'success') {
                $('#loading-icon').hide();
                $("#tokenmodal").hide();
                $("#otpmodal").show();
                $('#successMess').show();
                document.getElementById("successMess").textContent = "Successfully sent";

            } else {
                $("#tokenModal").show();
                $("#otpmodal").hide();
                if (reponse === "empty") {
                    $('#loading-icon').hide();
                    $('#errorMess').show();
                    document.getElementById("errorMess").textContent = "Invalid username";
                } else {
                    $('#loading-icon').hide();
                    $('#errorMess').show();
                    document.getElementById("errorMess").textContent = reponse;
                }
            }
        }
    });
    //        $.ajax({options});
}