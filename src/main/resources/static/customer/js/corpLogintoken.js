function sendOtp() {
    //        console.log([[${username}]]);
    $('#loading-icon').show();
    $('#errorMess').hide();
    //make sure alert message is not empty
    //        alert("send otp");
    //        console.log(acct);
    $.ajax({
        type: 'GET',
        url: '/otp/send/corporate',
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
                $("#tokenModalCorp").hide();
                $("#otpmodalCorp").show();
                $('#successMess').show();
                document.getElementById("successMess").textContent = "Successfully sent";

            } else {
                $("#tokenModalCorp").show();
                $("#otpmodalCorp").hide();
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