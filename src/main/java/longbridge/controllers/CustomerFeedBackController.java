package longbridge.controllers;

import longbridge.dtos.CustomerFeedBackDTO;
import longbridge.models.CustomerFeedBack;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.services.CustomerFeedBackService;
import longbridge.services.RetailUserService;
import longbridge.utils.DateFormatter;
import longbridge.utils.StringUtil;
import longbridge.utils.TransferType;
import longbridge.utils.UserType;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;


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
