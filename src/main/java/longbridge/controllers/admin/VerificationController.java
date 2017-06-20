package longbridge.controllers.admin;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import longbridge.utils.verificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import longbridge.models.AdminUser;
import longbridge.models.Verification;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
import longbridge.services.VerificationService;

@Controller
@RequestMapping("/admin/verifications")
public class VerificationController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private VerificationService verificationService;
	@Autowired
	private VerificationRepo verificationRepo;

	@Autowired 
	private AdminUserService adminUserService;
	
    @PersistenceContext
    private EntityManager eman;

	
	
	@GetMapping("/")
	public String getVerifications(Model model){
		return "adm/admin/verification/view";
	}
	
	@PostMapping("/{id}/verify")
	public String verify(@PathVariable Long id) {

		logger.info("id {}", id); 
        //TODO check verifier role
        AdminUser adminUser = adminUserService.getUser(1l);
        Verification verification = verificationRepo.findOne(id);
        logger.info("log {}", verification);

        if (verification == null || verificationStatus.PENDING != verification.getStatus())
            return "Verification not found";

        //TODO check if this verification has a dependency
        if(verification.getDependency() != null){
        	return "Verification has a dependency";
        }
        
        verificationService.verify(id);

		return "adm/admin/verification/confirm";
	}
	
	@PostMapping("/{id}/decline")
	public String decline(@PathVariable Long verificationId){
		//TODO check verifier role
        AdminUser adminUser = adminUserService.getUser(1l);
        Verification verification = verificationService.getVerification(verificationId);

        if (verification == null || verificationStatus.PENDING != verification.getStatus())
            return "Verification not found";

        //TODO check if this verification has a dependency
        if(verification.getDependency() != null){
        	return "Verification has a dependency";
        }
        
        verificationService.verify(verificationId);
		return "adm/admin/verification/decline";
	}

}
