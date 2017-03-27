package longbridge.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Showboy on 27/03/2017.
 */
@RestController
public class MainController {

    @RequestMapping("/")
    public String home(){
        return "welcome";
    }
}
