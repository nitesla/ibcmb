$(document).ready(function() {
    initnotices();
});
function initnotices(){
    $("#fielderrors li").each(function(index, value) {
        var tt = $(this).text();
        $("[name='" + tt + "']").closest(".form-group").addClass("has-error");
    });

    $('.actionMessage li').each(function(index, value) {
        var tt = $(this).text();
        $('#myModalSuccess').modal('show');
        var err = document.getElementById('successMess');
        err.textContent = tt;
        //var notify = $.notify({message:tt}, { allow_dismiss: true ,type: 'success'});
    });

    $('.actionError li').each(function(index, value) {
        var tt = $(this).text();
        $('#myModalError').modal('show');
        var err = document.getElementById('errorMess');
        err.textContent = tt;
        //var notify = $.notify({message:tt}, { allow_dismiss: true ,type: 'danger'});
    });

    $('.fieldError li').each(function(index, value) {
        var tt = $(this).text();
        var notify = $.notify({message:tt}, { allow_dismiss: true ,type: 'danger'});
    });
}