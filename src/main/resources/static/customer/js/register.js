
    function loadPhishingImages(){
		console.log("in load phishing imges");
		var listOfImages = [];
		$('#phishing').load("/rest/json/phishingimages");
	}

	var accountName = "null";

	function validateAccountNumber(accountNumber){
		var accountName;
		$.ajax({
            type:'GET',
            url:"/rest/accountname/"+accountNumber,
            async:false,
            success:function(data1){
            	accountName = ''+String(data1);
				if(accountName == 'null'){
					//invalid account number
					alert("Account name not found");
				}else{
					//valid account number
					alert("Account name: " + accountName);
				}
            }
        });

        if(accountName===accountNumber){
        	return false;
        }else{
        	return true;
        }
        
	}

	function validateUsername(username){
        var result;
		$.ajax({
            type:'GET',
            url:"/rest/username/check/"+username,
            async:false,
            success:function(data1){
            	result = ''+String(data1);
				if(result == 'false'){
					//invalid account number
					alert("user name not found");
				}else{
					//valid account number
					alert("user name: " + result);
				}
            }
        });
        
        if(result === 'true'){
            //username is valid and available
        	return true;
        }else{
        	return false;
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
    form.children("div").steps({
        headerTag: "h3",
        bodyTag: "section",
        transitionEffect: "slideLeft",
        onStepChanging: function (event, currentIndex, newIndex)
        {
            form.validate().settings.ignore = ":disabled,:hidden";
			console.log(currentIndex);
            var isValid = form.valid();
            if(ACCOUNT_DETAILS_STEP === currentIndex){
                console.log("Current step is the account details step");
                var accountNumber = $('input[name="acct"]').val();
                return isValid && validateAccountNumber(accountNumber);
            }
            if(PROFILE_DETAILS_STEP === currentIndex){
                console.log("Current stp is the profile details step");
                var username = $('input[name="username"]').val();
                return isValid && validateUsername(username);
            }
            return form.valid();
        },
        onFinishing: function (event, currentIndex)
        {
            
            form.validate().settings.ignore = ":disabled";
            return form.valid();
        },
        onFinished: function (event, currentIndex)
        {
//            alert("Submitted!");
            $("#reg-form").submit();
        }
    });

