function checkAndInvalidateSession() {
    console.log("test session");
    $.ajax({
        type:'GET',
        url:"/retail/account/validate/session",
        async:true,
        success:function(data1){
            console.log("the session response "+data1);
            result = ''+String(data1);
            if(result == "invalid"){
                window.location.href = "/retail/logout";
            }else {
                console.log("valid session");
            }
        },error:function (data1) {
            console.log("error");
            window.location.href = "/retail/logout";
        }
    });
}