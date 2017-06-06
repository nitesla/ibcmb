/**
 * Created by ayoade_farooq@yahoo.com on 5/7/2017.
 */

$(document).ready(function () {
    var acct = $('#source').val();
    getDestAccounts(acct)
    getSourceCurrency(acct);
});

$('#source').on('change',function(){
    var acct = $('#source').val();
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
           // $("#destination").get(0).options[0] = new Option("Select Account", "-1");
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
function goBack() {
    window.history.back();
}
var sortSelect = function (select, attr, order) {
    if(attr === 'text'){
        if(order === 'asc'){
            $(select).html($(select).children('option').sort(function (x, y) {
                return $(x).text().toUpperCase() < $(y).text().toUpperCase() ? -1 : 1;
            }));
            $(select).get(0).selectedIndex = 0;
            //e.preventDefault();
        }// end asc
        if(order === 'desc'){
            $(select).html($(select).children('option').sort(function (y, x) {
                return $(x).text().toUpperCase() < $(y).text().toUpperCase() ? -1 : 1;
            }));
            $(select).get(0).selectedIndex = 0;
            // e.preventDefault();
        }// end desc
    }

};