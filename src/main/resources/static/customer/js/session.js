var counter  = 0 ;
var sessionCheck = getCookie('time_out_time') ;

setInterval(function() {
    console.log('cookie value is'+getCookie('time_out_time'))
    count();
}, 1000);

function count(){
    counter ++ ;
    console.log('conter now '+ counter);
    if(counter > sessionCheck){
        location.href = '/';
        console.log('session to  be expired ')
    }else{
        console.log('still in session !')
    }
}

$('body').on('click',function () {
    console.log('body was clicked')
    refreshCount();
})

function refreshCount(){
    counter = 0 ;
}
function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}