package longbridge.controllers.retail;


import longbridge.dtos.LoanDTO;
import longbridge.dtos.LoanDetailsDTO;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.LoanDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/retail/loan")
public class RetailLoanController {

    @Autowired
    private LoanDetailsService loanDetailsService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @PostMapping("/email")
    @ResponseBody
    public ResponseEntity<HttpStatus> sendLoanDetailsinMail(@RequestBody List<LoanDTO> loans) throws IOException {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String recipient = principal.getUser().getEmail();
        String name = principal.getUser().getFirstName();
        LoanDetailsDTO detailsDTO = new LoanDetailsDTO();
        detailsDTO.setLoanList(loans);
        loanDetailsService.sendLoanDetails(recipient,name,detailsDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }


}
