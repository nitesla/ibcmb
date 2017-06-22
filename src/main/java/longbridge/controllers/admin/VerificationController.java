package longbridge.controllers.admin;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.sun.javafx.sg.prism.NGShape;
//import longbridge.dtos.PendingDTO;
import longbridge.dtos.PendingVerification;
import longbridge.dtos.VerificationDTO;
import longbridge.models.MakerChecker;
import longbridge.utils.verificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import longbridge.models.AdminUser;
import longbridge.models.Verification;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
import longbridge.services.VerificationService;

import java.security.Principal;
import java.util.List;

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


	@GetMapping(path = "/all")
	public @ResponseBody
	DataTablesOutput<PendingVerification> getAllPending(DataTablesInput input, Principal principal)
	{
		AdminUser createdBy = adminUserService.getUserByName(principal.getName());
		Pageable pageable = DataTablesUtils.getPageable(input);
		Page<PendingVerification> codes = verificationService.getPendingVerifications(createdBy,pageable);
		DataTablesOutput<PendingVerification> out = new DataTablesOutput<PendingVerification>();
		out.setDraw(input.getDraw());
		out.setData(codes.getContent());
		out.setRecordsFiltered(codes.getTotalElements());
		out.setRecordsTotal(codes.getTotalElements());
		return out;
	}


	@GetMapping(path = "/allverification")
	public @ResponseBody
	DataTablesOutput<Verification> getAllVerification(DataTablesInput input, Principal principal)
	{
		AdminUser createdBy = adminUserService.getUserByName(principal.getName());
		Pageable pageable = DataTablesUtils.getPageable(input);
		List<Verification> codes = verificationService.getVerificationForUser(createdBy);
		DataTablesOutput<Verification> out = new DataTablesOutput<Verification>();
		out.setDraw(input.getDraw());
		out.setData(codes);
		out.setRecordsFiltered(codes.size());
		out.setRecordsTotal(codes.size());
		return out;
	}




//	@GetMapping(path = "/all")
//	public @ResponseBody
//	DataTablesOutput<VerificationDTO> getAllCodes(DataTablesInput input,Principal principal)
//	{
//		AdminUser createdBy = adminUserService.getUserByName(principal.getName());
//		//String user=userCreatedBy.getUserName();
//		Pageable pageable = DataTablesUtils.getPageable(input);
//		Page<VerificationDTO> codes = verificationService.getMakerCheckerPending(pageable,createdBy);
//		DataTablesOutput<VerificationDTO> out = new DataTablesOutput<VerificationDTO>();
//		out.setDraw(input.getDraw());
//		out.setData(codes.getContent());
//		out.setRecordsFiltered(codes.getTotalElements());
//		out.setRecordsTotal(codes.getTotalElements());
//		return out;
//	}

	@GetMapping("/pendingops")
	public String getPendingVerification(Model model,Principal principal)
	{
		AdminUser createdBy = adminUserService.getUserByName(principal.getName());
		int verificationNumber=verificationService.getTotalNumberForVerification(createdBy);
		long totalPending=verificationService.getTotalNumberPending(createdBy);
		model.addAttribute("totalPending",totalPending);
		model.addAttribute("verificationNumber",verificationNumber);
		return "adm/makerchecker/pending";
	}


	@GetMapping("/verificationops")
	public String getVerification(Model model, Principal principal)
	{

		AdminUser createdBy=adminUserService.getUserByName(principal.getName());
		int verificationNumber=verificationService.getTotalNumberForVerification(createdBy);
		long totalPending=verificationService.getTotalNumberPending(createdBy);
		model.addAttribute("verificationNumber", verificationNumber);
		model.addAttribute("totalPending",totalPending);
		return "adm/makerchecker/checker";
	}

}
