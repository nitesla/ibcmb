package longbridge.controllers.corporate;

import longbridge.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Showboy on 02/07/2017.
 */
@Controller
@RequestMapping("/corporate")
public class CorpSetupController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageService messageService;


}
