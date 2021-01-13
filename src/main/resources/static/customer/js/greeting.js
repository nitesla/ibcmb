"use strict";
function setGreetings(result) {
    if(result!=""){
        console.log("this is it : "+ result);
        createDiv(result);
    }
}


    function createDiv(result){
        var div="";
        for(var i=0;i<result.length;i++){
            console.log("the link : " + result[i].imageLink);
            div+="<li><a style='text-decoration:none;'><br/><span  class=" + "genGreet>" + result[i].message + "</span><br/>"
                 +"<button value=" + result[i].imageLink + "" +
                " class=" + "\" gvalue btn btn-link token-btn\"" +
                "" + ">" + "view image " + "</button></a><li>"
        }
        $(".cnt").text(result.length);
        $(".specialGreeting").html(div);
        if($(".specialGreeting").text().length>0){
            $(".nav_menu").show();
        }
    }
function getRetailGreetings() {
    $.ajax({
        dataType: 'json',
        url: "/retail/account/greetings/current",
        type: "GET",
        success: function (result) {
           setGreetings(result);
        },
        error: function () {
            console.log("error loading retail greetings");
        }
    });

}
function getCorporateGreetings() {
    $.ajax({
        dataType: 'json',
        url: "/corporate/account/greetings/current",
        type: "GET",
        success: function (result) {

           setGreetings(result);
        },
        error: function () {
            console.log("error loading corporate greetings");
        }
    });
}



function greetingUtility() {

    $(".specialGreeting").on('click', '.gvalue', function getGreetingImage() {
        var imageName = $(this).val();

       console.log("here"+imageName);
        $(".imageVal").html("<img style=\"width:600px;height:280px\"  alt=\"Special Greeting\" src=" + " /customer/img/" + imageName + " />")

        $('.greetingImageModal').modal({});
    });


    $("#greetingImage").change(function (fname) {
        var filename = fname.target.files[0].name;
        $("#imageLink").val(filename);

    });
    var n = $("#duration").val();

    $("#duration").change(function () {
        n = $("#duration").val();
        if (n === ""||n===0) {
            $("#duration").val(1);
            n = 1;
        }
    });
    if ($("#duration").change(function () {
        var duration = parseInt($("#duration").val());
        if (duration === ""||duration===0) {
           duration = 1;
           $("#duration").val(1);
        }
        var execute = $("#execute").val();
        var executeDate = moment(execute + "00:00:00", "YYYY-MM-DD HH:mm:ss").format("YYYY-MM-DD HH:mm:ss");
        var date = new Date(executeDate);
        date = date.setDate(date.getDate() + (duration-1));
        var expireDateWrapper = moment(date);
        $("#expire").val(expireDateWrapper.format('YYYY-MM-DD'));
    }))
        if ($("#execute").change(function () {
            var duration = parseInt($("#duration").val());
            if (duration === ""||duration===0) {
                duration = 1;
                $("#duration").val(1);
            }
            var execute = $("#execute").val();
            var executeDate = moment(execute + "00:00:00", "YYYY-MM-DD HH:mm:ss").format("YYYY-MM-DD HH:mm:ss");
            var date = new Date(executeDate);
            date = date.setDate(date.getDate() + (duration-1));
            var expireDateWrapper = moment(date);
            $("#expire").val(expireDateWrapper.format('YYYY-MM-DD'));
        }))




            $(".btn-find").on("click", function (e) {
                e.preventDefault();
                $("#retailUserBtn #displayBtn").text("Retail");

                var disp = "";
                var userIds = "";
                var options = {
                    url: "/admin/greetings/find",
                    title: 'Select ',
                    size: eModal.size.lg,
                    subtitle: 'Retail User',
                    buttons: [{
                        text: 'Ok',
                        style: 'info',
                        close: false,
                        class: 'btn btn-primary addRetail',
                        click: function () {
                            var table = $('#retail').DataTable();
                            table.rows({
                                selected: true
                            }).data().each(function (d) {
                                disp += d['firstName'] + " " + d['lastName'] + ", ";
                                userIds += d['DT_RowId'] + ",";

                            })
                            $("#indi2").val(userIds);
                            $("#indi").val(disp);
                            $("#userType").val("Retail");

                        }

                    }, {
                        text: 'Close',
                        style: 'default',
                        close: true,
                        class: " btn btn-primary closemodal",
                    }],
                };
                eModal.ajax(options);

            });

    //find corporate user
    $("#retailUserBtn .btn-find-corpuser").on("click", function (e) {
        e.preventDefault();
        $("#retailUserBtn #displayBtn").text("Corporate");

        var disp = "";
        var userIds = "";
        var options = {
            url: "/admin/greetings/find/corp",
            title: 'Select ',
            size: eModal.size.lg,
            subtitle: 'Corporate User',
            buttons: [{
                text: 'Ok',
                style: 'info',
                close: false,
                class: 'btn btn-primary addCorporate',
                click: function () {
                    var table = $('#corp').DataTable();
                    table.rows({
                        selected: true
                    }).data().each(function (d) {
                        disp += d['firstName'] + " " + d['lastName'] + ", ";
                        userIds += d['DT_RowId'] + ",";
                    })
                    $("#indi2").val(userIds);
                    $("#indi").val(disp);
                    $("#userType").val("Corporate");
                }

            },
                {
                    text: 'Close',
                    style: 'default',
                    close: true,
                    class: " btn btn-primary closemodal",
                }

            ],
        };
        eModal.ajax(options);

    });

    // $("#parameter").on("click",function(){
    //     var caretPos = $("#message")[0].selectionStart;
    //     var existingText= $("#message").val();
    //     var txtToAdd = $("#parameter").val();
    //     $("#message").val(existingText.substring(0, caretPos) + txtToAdd + existingText.substring(caretPos) );
    // });

    $("#parameter").change(function(){
        var caretPos = $("#message")[0].selectionStart;
        var existingText= $("#message").val();
        var txtToAdd = $("#parameter").val();
        $("#message").val(existingText.substring(0, caretPos) + txtToAdd + existingText.substring(caretPos) );
    });

    if ( $('[type="date"]').prop('type') != 'date' ) {
        $('[type="date"]').datepicker();
    }

    $("#message").on("blur",function() {
        var str = $("#message").val();
        var res = str.match(/\${(?!\w+})|\$(?!{)/g);
        if (res !== null) {
            $("#messageError").text("'" +res +"'"+ "  tag is not properly closed");
            $("#messageError").show();
            $("#submit").attr("disabled","disabled");

        }else {
            $("#messageError").hide();
            $("#submit").removeAttr("disabled");
        }
    });



}

