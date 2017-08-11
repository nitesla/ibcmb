 function showPop (errorClass)
    { 
         hideSibling('input',errorClass);
         

        //declare a font awesome tag to prepend to the existing error message
       var tag = "<i class='fa fa-exclamation text-danger' style='font-size:20px'></i> <span>";
       //add a new class to the existing element  and prepend the above variable to the class 
        $(errorClass).addClass('errors_div').prepend(tag);
        autoComplete('off');
       
       
       
    }   


function autoComplete(value)
{
     $('input').attr('autocomplete', value);
}


function hideSibling(first,second)
{
     $(first).on("click", function(){
    	$(this).siblings(second).hide();
        
    });


}



function errorLogin()
{      
    var err = [];
    var showErr = '';
    $('.erro').each(function(i,obj){
    if ($(this).text() !== '')
    {   
        $(this).hide();
        err.push($(this).text());
    }
    });
    if(err.length > 0)
    {    
        for (i = 0; i < err.length; i++)
        {
            showErr += "- "+err[i]+"<br>";
        }
         $('#errorMess').html(showErr);
         $('#myModalError').modal('show');
    }
    

    
        
       
       
        
    
}







