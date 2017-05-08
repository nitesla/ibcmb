/**
 * Created by ayoade_farooq@yahoo.com on 5/7/2017.
 */
$('#source').on('change',function(){
    var acct = $('#source').val();
    alert('YAHOO YAHOO');
    getDestAccounts(acct)
    getSourceCurrency(acct);
});
$('#destination').on('change',function(){
    var acct = $('#destination').val();
    getDestCurrency(acct)
});


function getDestAccounts(acct) {

    $.get("/retail/transfer/dest/"+acct+"/accounts", function( data ) {
        $("#destination").empty();
        data.forEach(function(item, i) {
            $("#destination").get(0).options[0] = new Option("Select Account", "-1");
            var option = "<option  value = " + item + " >" + item +  "</option>";
            $("#destination").append(option);
        });
    });

}


function getSourceCurrency(acct) {

    $.get("/retail/transfer/" + acct + "/currency", function (data) {
        document.getElementById("scurency").innerHTML = data;

    });
}
function getDestCurrency(acct) {

    $.get("/retail/transfer/" + acct + "/currency", function (data) {
        document.getElementById("dcurency").innerHTML = data;

    });
}
