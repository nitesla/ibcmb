package longbridge.controllers;

import longbridge.exception.PasswordException;
import longbridge.exception.PasswordPolicyViolationException;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import longbridge.dtos.OperationsUserDTO;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordPolicyService;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

@Controller
@RequestMapping("/general/operations/users")
public class OperationsUserContoller {
	
	private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private  OperationsUserService operationsUserService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SecurityService securityService;


    @Autowired
    PasswordPolicyService passwordService;

	@RequestMapping(path = "/find" )
    public @ResponseBody DataTablesOutput<OperationsUserDTO> getUsers(DataTablesInput input, OperationsUserDTO user){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<OperationsUserDTO> operationsUsers = operationsUserService.findUsers(user, pageable);
        DataTablesOutput<OperationsUserDTO> out = new DataTablesOutput<OperationsUserDTO>();
        out.setDraw(input.getDraw());
        out.setData(operationsUsers.getContent());
        out.setRecordsFiltered(operationsUsers.getTotalElements());
        out.setRecordsTotal(operationsUsers.getTotalElements());

        return out;
    }

    @GetMapping("/password/reset/")
    public String getOpsUsername(){
        return "/ops/username";
    }


    @PostMapping("/password/reset/")
    public String validateOpsUsername(WebRequest request, Model model, Locale locale, HttpSession session) {
        String username = request.getParameter("username");
        if (username == null || "".equals(username)) {
            model.addAttribute("failure", messageSource.getMessage("form.fields.required", null, locale));
            return "/ops/username";
        }
        if (!operationsUserService.userExists(username)) {
            model.addAttribute("failure", messageSource.getMessage("username.invalid", null, locale));
            return "/ops/username";
        }
        boolean result = securityService.sendOtp(username);
        if (result) {
            session.setAttribute("username", username);
            session.setAttribute("url", "/password/reset");
            model.addAttribute("message",messageSource.getMessage("otp.send.failure",null,locale));
            return "/ops/ptoken";
        }
        model.addAttribute("failure", messageSource.getMessage("otp.send.failure", null, locale));
        return "/ops/username";
    }

    @GetMapping("/password/reset")
    public String resetOpsPassword(HttpServletRequest request, RedirectAttributes redirectAttributes){
        if(request.getSession().getAttribute("username")!=null){
            String username =(String)request.getSession().getAttribute("username");
            try {
                String message = operationsUserService.resetPassword(username);
                redirectAttributes.addFlashAttribute("message",message);
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("url");
                return "redirect:/login/ops";
            }
            catch (PasswordException pe){
                redirectAttributes.addFlashAttribute("failure",pe.getMessage());
            }
        }
        return "redirect:/password/reset/";
    }
}
