 function showPop (item)
    {
         
        $('input.form-control').attr('data-toggle', 'popover');
        var err;
        var arr_err = [];
        $('.'+item).each(function(i, obj){
            
             err = $(this).text();
            
             if (err !== '')
            {
                
                
                $(this).hide();

                arr_err.push(err);
               
            }
        });

        var disp_err;

        $('input.form-control').each(function(i, obj)
        {
            disp_err = "<div style='text-align:left;padding:20px;font-size:12px'><i class='fa fa-exclamation text-danger' style='font-size:20px'></i> <span>"+arr_err[i]+"!!!</span></div>";
            $(this).attr('data-content', disp_err);
             $('[data-toggle="popover"]').popover({html:"true"}).popover('show');
        });

        


        
        
        
        
      

       
        
      
       
        
    }   