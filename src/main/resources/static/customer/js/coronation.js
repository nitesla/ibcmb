$(document).ready(function() {
    initnotices();
});
function initnotices(){
    $("#fielderrors li").each(function(index, value) {
        var tt = $(this).text();
        $("[name='" + tt + "']").closest(".form-group").addClass("has-error");
    });

    $('.actionMessage li').each(function(index, value) {
        var tt = $(this).text();
        $('#myModalSuccess').modal('show');
        var err = document.getElementById('successMess');
        err.textContent = tt;
        //var notify = $.notify({message:tt}, { allow_dismiss: true ,type: 'success'});
    });

    $('.actionError li').each(function(index, value) {
        var tt = $(this).text();
        $('#myModalError').modal('show');
        var err = document.getElementById('errorMess');
        err.textContent = tt;
        //var notify = $.notify({message:tt}, { allow_dismiss: true ,type: 'danger'});
    });

    $('.fieldError li').each(function(index, value) {
        var tt = $(this).text();
        var notify = $.notify({message:tt}, { allow_dismiss: true ,type: 'danger'});
    });
}

function currentBar (arg)
{
    var barTitleHolder = document.getElementById('bar');

    barTitleHolder.innerHTML = arg;
}


function breadCrumb (arg)
{
    
    
  var bcHolder = document.getElementById('bread-crumb');


  if (arg.constructor == Array)
{
    for (let i = 0; i < arg.length; i++)
    {
        var link = document.createElement('a');

        if (i == (arg.length-1))
        {
            var link = document.createElement('span');
        }
        for (let j = 0; j < arg[i].length; j++)
        {                 
            if (j == 0)
            {
                link.innerHTML =" " + arg[i][j];
            }
            else if (j == 1 && i != (arg.length-1))
            {
                link.setAttribute('href',arg[i][j]);
            }
            bcHolder.appendChild(link);                        
        }

        bcHolder.appendChild(link);
    }
}

else
    {
        var link = document.createElement('span');
        link.innerHTML = arg;
        bcHolder.appendChild(link);

    }

  
   
    
    
    
     

    //   var link = document.createElement('a');
    // link.setAttribute('href','google.com');
    // link.innerHTML = arg[0];
   
    

    
   
    // bcHolder.innerHTML = arg;
}