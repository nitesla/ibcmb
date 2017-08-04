 function showPop ()
    { 
        //declare a font awesome tag to prepend to the existing error message
       var tag = "<i class='fa fa-exclamation text-danger' style='font-size:20px'></i> <span>";
       //add a new class to the existing element  and prepend the above variable to the class 
        $('.errors').addClass('errors_div').prepend(tag);
        autoComplete('off');
       
        hideSibling('input','.errors');

       
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

