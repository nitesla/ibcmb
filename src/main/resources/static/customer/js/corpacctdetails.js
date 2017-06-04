/**
 * Created by ayoade_farooq@yahoo.com on 5/7/2017.
 */
$('#source').on('change',function(){
    var acct = $('#source').val();
    getDestAccounts(acct)
    getSourceCurrency(acct);
});
$('#destination').on('change',function(){
    var acct = $('#destination').val();
    getDestCurrency(acct)
});


function getDestAccounts(acct) {

    $.get("/corporate/transfer/dest/"+acct+"/accounts", function( data ) {
        $("#destination").empty();
        data.forEach(function(item, i) {
            $("#destination").get(0).options[0] = new Option("Select Account", "-1");
            var option = "<option  value = " + item + " >" + item +  "</option>";
            $("#destination").append(option);
        });
    });

}


function getSourceCurrency(acct) {

    $.get("/corporate/transfer/" + acct + "/currency", function (data) {
        document.getElementById("scurency").innerHTML = data;

    });
}
function getDestCurrency(acct) {

    $.get("/corporate/transfer/" + acct + "/currency", function (data) {
        document.getElementById("dcurency").innerHTML = data;

    });
}
function goBack() {
    window.history.back();
}