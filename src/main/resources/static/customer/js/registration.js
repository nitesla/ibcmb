
    function loadPhishingImages(){
		console.log("in load phishing imges");
		var listOfImages = [];
		$('#phishing').load("/rest/json/phishingimages");
	}

	function validateAccountNumber(accountNumber){
        var accountName = $.get("/rest/accountname/"+accountNumber);
        console.log(accountName);
        if(accountName == 'null'){
            //invalid account number
//
        }else{
            //valid account number
        }
	}

	function validateUsername(username){
        var result = $.get("/rest/username/check/"+username);
        if(result === 'true'){
            //username is valid and available
        }else{

        }
	}

    //datepicker
    $( function() {
        $( "#datepicker" ).datepicker(
            { dateformat : 'dd/mm/yy'}
        );

        //load the anti phishing images from the server
        loadPhishingImages();

        //setup event handler on select option
        $('#phishing').change(function(event){
        	console.log(this);
        	var url = $(this).val();
        	console.log(url);
        	$('#phishing-preview').attr('src', url);
        });
    } );


    //steps with form
    var map = {};
    var form = $("#reg-form");
    form.validate({
        errorPlacement: function errorPlacement(error, element) {
            element.before(error);
        },
        rules: {
            confirm: {
                equalTo: "#password"
            }
        }
    });
    var ACCOUNT_DETAILS_STEP = 0;
    var PROFILE_DETAILS_STEP = 1;
    form.children(".form-reg").steps({
        headerTag: "h3",
        bodyTag: "section",
        transitionEffect: "slideLeft",
        onStepChanging: function (event, currentIndex, newIndex)
        {
            form.validate().settings.ignore = ":disabled,:hidden";
            return form.valid();
        },
        onFinishing: function (event, currentIndex)
        {
            console.log(currentIndex);
            form.validate().settings.ignore = ":disabled";
            var isValid = form.valid();
            if(ACCOUNT_DETAILS_STEP === currentIndex){
                console.log("Current step is the account details step");
                return isValid && validateAccountNumber();
            }
            if(PROFILE_DETAILS_STEP === currentIndex){
                console.log("Current stp is the profile details step");
                return isValid && validateUsername();
            }
            return form.valid();
        },
        onFinished: function (event, currentIndex)
        {
//            alert("Submitted!");
            $("#reg-form").submit();
        }
    });

