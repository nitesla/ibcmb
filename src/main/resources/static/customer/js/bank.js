$(document).ready(function () {
    initnotices();
});
function initnotices() {
    $("#fielderrors li").each(function (index, value) {
        var tt = $(this).text();
        $("[name='" + tt + "']").closest(".form-group").addClass("has-error");
    });

    $('.actionMessage li').each(function (index, value) {
        var tt = $(this).text();
        var transType=$(".transactionType").text();
        var feedStatus=$("#feedStatus").text();
        console.log("feedstatus"+feedStatus);
        var ref=$(".transactionRef").text();
        console.log(transType.length+transType+feedStatus);
        if((typeof transType!=="undefined") &&(transType.length>1) && (feedStatus==="EN")){
            $("#feedBackForm").show();
        }
        $('#myModalSuccess').modal('show');
        var err = document.getElementById('successMess');
        err.textContent = tt;
        //var notify = $.notify({message:tt}, { allow_dismiss: true ,type: 'success'});
    });


    $('.actionError li').each(function (index, value) {
        var tt = $(this).text();
        $('#myModalError').modal('show');
        var err = document.getElementById('errorMess');
        err.textContent = tt;
        //var notify = $.notify({message:tt}, { allow_dismiss: true ,type: 'danger'});
    });

    $('.fieldError li').each(function (index, value) {
        var tt = $(this).text();
        var notify = $.notify({message: tt}, {allow_dismiss: true, type: 'danger'});
    });
}

function currentBar(arg) {
    var barTitleHolder = document.getElementById('bar');

    barTitleHolder.innerHTML = arg;
}


function breadCrumb(arg) {


    var bcHolder = document.getElementById('bread-crumb');


    if (arg.constructor == Array) {
        for (var i = 0; i < arg.length; i++) {
            var link = document.createElement('a');

            if (i == (arg.length - 1)) {
                var link = document.createElement('span');
            }
            for (var j = 0; j < arg[i].length; j++) {
                if (j == 0) {
                    link.innerHTML = " " + arg[i][j];
                }
                else if (j == 1 && i != (arg.length - 1)) {
                    link.setAttribute('href', arg[i][j]);
                }
                bcHolder.appendChild(link);
            }

            bcHolder.appendChild(link);
        }
    }

    else {
        var link = document.createElement('span');
        link.innerHTML = arg;
        bcHolder.appendChild(link);

    }


    function naso() {
        alert();
    }


    function invalidateMySession() {
     window.open();

        $.ajax({
            url: "/invalidate",
            type: 'GET',
            async: false
//             success:function(jd){
//                 var message = jd.message;
//                 var success =  jd.success;
//                 console.log(message);
//                 console.log(success);
//                 if (success==false){
//                     document.getElementById("mylimit").textContent="Could not get limit from server, please try again.";
//                     $("#mylimit").show();
//                 }else{
// //                    $('input[name=availBal]').val(bal);
//                     var  bal=message;
//                     $('#limit').text("Limit: "+bal);
//
//                 }
//             }
        })



  }


    //   var link = document.createElement('a');
    // link.setAttribute('href','google.com');
    // link.innerHTML = arg[0];


    // bcHolder.innerHTML = arg;
}


function redirectFunc(arg) {
    window.location.href = arg;

}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}


function showSpinner() {
    console.log("Show spinner");
    $(".spinner-box").show();
    $('.spinner-box').css("display", 'block');
}

function hideSpinner() {
    // alert("hide");
    $(".spinner-box").hide();
    $('.spinner-box').css("display", 'none');
}
