package longbridge.controllers;

import longbridge.dtos.CustomerFeedBackDTO;
import longbridge.services.CustomerFeedBackService;
import longbridge.services.RetailUserService;
import longbridge.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/feedback")
public class CustomerFeedBackController {

    @Autowired
    private CustomerFeedBackService customerFeedBackService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    RetailUserService retailUserService;

    @PostMapping("/save")
    @ResponseBody
    public String addFeedBack(@ModelAttribute("feedBackDTO") CustomerFeedBackDTO feedBackDTO){
        String response=null;
        if(feedBackDTO!=null) {
            String sanitizedMessage= StringUtil.sanitizeString(feedBackDTO.getComments());
            feedBackDTO.setComments(sanitizedMessage);
            logger.info("feedBackDTO is {}",feedBackDTO.getComments());
            customerFeedBackService.addFeedBack(feedBackDTO);
        }
        else{
            logger.info("feedback error is {}", feedBackDTO);
        }
       return response;

    }




}
