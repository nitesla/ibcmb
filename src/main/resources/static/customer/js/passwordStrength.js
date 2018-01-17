var m_strNumber = "0123456789";
function checkPassword(strPassword,minLength,m_noOfDigits,m_strCharacters,noOfSpecChar)
{
    // Reset combination count
    var nScore = 0;

    // Password length
    // -- Less than 4 characters
    if (strPassword.length < minLength)
    {
        nScore += 2;
    }
    else if (strPassword.length >= minLength)
    {
        nScore += 25;
    }

    // Numbers
    var nNumberCount = countContain(strPassword, m_strNumber);
    // -- 1 number
    if (nNumberCount < m_noOfDigits)
    {
        nScore += 0;
    }
    // -- 3 or more numbers
    if (nNumberCount >= m_noOfDigits)
    {
        nScore += 25;
    }

    // Characters
    var nCharacterCount = countContain(strPassword, m_strCharacters);
    // -- 1 character
    if (nCharacterCount < noOfSpecChar)
    {
        nScore += 0;
    }
    // -- More than 1 character
    if (nCharacterCount >= 1)
    {
        nScore += 25;
    }

    return nScore;
}

// Runs password through check and then updates GUI
function runPassword(strPassword,minLength,m_noOfDigits,m_strCharacters,noOfSpecChar)
{
    // Check password
//        console.log("p "+strPassword);
    var nScore = checkPassword(strPassword,minLength,m_noOfDigits,m_strCharacters,noOfSpecChar);

    // Get controls
    var ctlBar = document.getElementById('passwordStrength');
    var ctlText = document.getElementById('passwordDescription');
    if (!ctlBar || !ctlText) return;

    // Set new width
    ctlBar.style.width = (nScore*1.25>100)?100:nScore*1.25 + "%";

    // Color and text
    // -- Very Secure
    /*if (nScore >= 90)
     {
     var strText = "Very Secure";
     var strColor = "#0ca908";
     }
     // -- Secure
     else if (nScore >= 80)
     {
     var strText = "Secure";
     vstrColor = "#7ff67c";
     }
     // -- Very Strong
     else
     */
    if (nScore >= 75)
    {
        var strText = "Strong";
        var strColor = "#006000";
    }else if(nScore >= 50){
        var strText = "Medium";
        var strColor = "#e3cb00";
    }
    else
    {
        var strText = "Weak";
        var strColor = "#e71a1a";
    }
    if(strPassword.length == 0)
    {
        ctlText.innerHTML =  "password field is empty";
    }
    else
    {
//            console.log("password strenth "+strPassword.length);
        ctlBar.style.background = strColor;
        ctlText.innerHTML =  strText;
    }
}

// Checks a string for a list of characters
function countContain(strPassword, strCheck)
{
    // Declare variables
    var nCount = 0;

    for (i = 0; i < strPassword.length; i++)
    {
        if (strCheck.indexOf(strPassword.charAt(i)) > -1)
        {
            nCount++;
        }
    }

    return nCount;
}