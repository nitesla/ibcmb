/**
 * Created by user on 5/24/2017.
 */
var thisDate = new Date();
var year = thisDate.getFullYear();
var month = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
var maxDate = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
var days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
var weekArray = [];
var newOne = 0;

function showme() {
    document.getElementById("dateMonth").innerHTML = month[thisDate.getMonth()];
    document.getElementById("dateDay").innerHTML = days[thisDate.getDay()];
    document.getElementById("dateYear").innerHTML =year;


    var onejan = new Date(thisDate.getFullYear(),0,1);
    var today = new Date(thisDate.getFullYear(),thisDate.getMonth(),thisDate.getDate());
    var dayOfYear = ((today - onejan +1)/86400000);
    document.getElementById("dateWeek").innerHTML = "Week " + Math.ceil(dayOfYear/7);

    var day = thisDate.getDay();
    var firstDay = new Date(thisDate.getFullYear(), thisDate.getMonth(), thisDate.getDate() + (day == 0?-6:1)-day );
    weekValue = firstDay.getDate() - 1;
    var elementID = "day"
    var elementNum = 1

    for (i=0; i<7; i++) {
        weekValue = weekValue + 1;
        document.getElementById(elementID + elementNum.toString()).innerHTML = weekValue;
        if (weekValue == thisDate.getDate()) {
            document.getElementById(elementID + elementNum.toString()).style.backgroundColor = "#9F614B";
            document.getElementById(elementID + elementNum.toString()).style.color = "#fff"
        }
        elementNum = elementNum + 1;
        weekArray.push(weekValue);
    }

    //document.getElementById("week").innerHTML = weekArray;
    //document.getElementById("week").innerHTML = weekArray.join("&nbsp;&nbsp;&nbsp;&nbsp;");
}

function reduceWeek() {
    var newNumber = weekArray[0] - 1;
    newOne = weekArray[0] - 1
    var elementID = "day"
    var elementNum = 7

    if (newOne > 1) {
        weekArray = [];
    }

    for (i=0; i<7; i++) {

        if (Number(newOne) < 1) {
            break;
        }

        document.getElementById(elementID + elementNum.toString()).style.backgroundColor = "transparent";
        document.getElementById(elementID + elementNum.toString()).style.color = "#6a6d71"
        document.getElementById(elementID + elementNum.toString()).innerHTML = newNumber;
        elementNum = elementNum - 1;
        weekArray.push(newNumber);
        newOne = weekArray[0] - 1
        newNumber = newNumber - 1;
    }

    if (newOne > 1) {
        weekArray = weekArray.reverse()
        //document.getElementById("week").innerHTML = weekArray.join("&nbsp;&nbsp;&nbsp;&nbsp;");
    }
}

function increaseWeek() {
    var newNumber = weekArray[6] + 1;
    newOne = weekArray[weekArray.length - 1] + 1
    var maxDateNum = maxDate[thisDate.getMonth()];
    var elementID = "day"
    var elementNum = 1

    if (Number(newOne) <= Number(maxDateNum)) {
        weekArray = [];
    }

    for (i=0; i<7; i++) {

        if (Number(newOne) > Number(maxDateNum)) {
            //getNewMonth()
            break;
        }
        else{
            document.getElementById(elementID + elementNum.toString()).style.backgroundColor = "transparent";
            document.getElementById(elementID + elementNum.toString()).style.color = "#6a6d71"
            //document.getElementById(elementID + elementNum.toString()).innerHTML = newNumber;
            elementNum = elementNum + 1;
            weekArray.push(newNumber);
            newOne = Number(weekArray[weekArray.length - 1]) + 1
            newNumber = newNumber + 1;}
    }

    //document.getElementById("week").innerHTML = weekArray.join("&nbsp;&nbsp;&nbsp;&nbsp;");
}

function getNewMonth(currentMonth, methodName) {
    if (methodName == "increaseWeek") {
        var newMonth = currentMonth + 1;
    }
    else {
        var newMonth = currentMonth - 1;
    }
}

