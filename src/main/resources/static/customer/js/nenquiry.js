/**
 * Created by LongBridge on 7/3/2017.
 */


function getAccountName() {
    $('#loading-icon').show();
    //make sure alert message is not empty
    var acct = $('#acct_num').val();
    var bank = $('#bank_code').val();

    if(( acct==null || acct=='' ) || ( bank==null || bank=='' )) {

        $('#loading-icon').hide();
        $('.acctError').show().css("color", "red");
        $('#loading-icon').hide();
        return false;
    }

    $.getJSON('/retail/transfer/' + acct + '/'+bank+'/nameEnquiry', function(jd) {
        var message = jd.message;
        var success =  jd.success;
        console.log(message);
        console.log(success);
        if (success==false){
            $('.acctError').text(message);
            $('.acctError').show().css("color", "red");
            $('#loading-icon').hide();
        }else{
            $('#acct_name').val(message);
            $('#localsave').show();
            $('#neq').hide();
            $('.acctError').hide()
            document.getElementById('loading-icon').style.display = 'none';
            $('#loading-icon').hide();
        }





    });

};

