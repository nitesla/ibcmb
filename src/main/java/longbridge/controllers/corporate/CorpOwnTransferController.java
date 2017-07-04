package longbridge.controllers.corporate;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.exception.TransferException;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.validator.transfer.TransferValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 */
@Controller
@RequestMapping("/corporate/transfer/ownaccount")
public class CorpOwnTransferController {

	private CorporateUserService corporateUserService;
	private IntegrationService integrationService;
	private CorpTransferService corpTransferService;
	private AccountService accountService;
	private MessageSource messages;
	private LocaleResolver localeResolver;
	private CorpLocalBeneficiaryService corpLocalBeneficiaryService;
	private TransferValidator validator;
	private FinancialInstitutionService financialInstitutionService;
	private ApplicationContext appContext;
	private TransferErrorService errorService;

	private String page = "corp/transfer/ownaccount/";
	@Value("${bank.code}")
	private String bankCode;

	@Autowired
	public CorpOwnTransferController(CorporateUserService corporateUserService, IntegrationService integrationService,
			CorpTransferService corpTransferService, AccountService accountService, MessageSource messages,
			LocaleResolver localeResolver, CorpLocalBeneficiaryService corpLocalBeneficiaryService,
			TransferValidator validator, FinancialInstitutionService financialInstitutionService,
			ApplicationContext appContext) {
		this.corporateUserService = corporateUserService;
		this.integrationService = integrationService;
		this.corpTransferService = corpTransferService;
		this.accountService = accountService;
		this.messages = messages;
		this.localeResolver = localeResolver;
		this.corpLocalBeneficiaryService = corpLocalBeneficiaryService;
		this.validator = validator;
		this.financialInstitutionService = financialInstitutionService;
		this.appContext = appContext;
	}

	@GetMapping("")
	public ModelAndView index() throws Exception {

		ModelAndView view = new ModelAndView();

		CorpTransferRequestDTO corptransferRequestDTO = new CorpTransferRequestDTO();
		corptransferRequestDTO
				.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode("bankCode"));
		view.addObject("transferRequest", corptransferRequestDTO);
		view.setViewName(page + "pagei");
		return view;

	}

	/*
	 * @PostMapping("") public String
	 * makeTransfer(@ModelAttribute("corpTransferRequestDTO") @Valid
	 * CorpTransferRequestDTO corpTransferRequestDTO, RedirectAttributes
	 * redirectAttributes, Locale locale, HttpServletRequest request, Principal
	 * principal, Model model) throws TransferException { try { String token =
	 * request.getParameter("token");
	 * 
	 * // boolean tokenOk =
	 * integrationService.performTokenValidation(principal.getName(), token);
	 * boolean tokenOk = !token.isEmpty();
	 * 
	 * if (!tokenOk) { redirectAttributes.addFlashAttribute("message",
	 * messages.getMessage("auth.token.failure", null, locale)); // return
	 * "redirect:" }
	 * corpTransferService.addTransferRequest(corpTransferRequestDTO);
	 * 
	 * 
	 * // redirectAttributes.addFlashAttribute("message",
	 * messages.getMessage("transac tion.success", null, locale));
	 * model.addAttribute("transferRequest", request); return page + "pageiv";
	 * 
	 * } catch (InternetBankingTransferException exception)
	 * 
	 * { String errorMessage = exception.getMessage();
	 * redirectAttributes.addFlashAttribute("error", errorMessage); return page
	 * + "pagei";
	 * 
	 * }
	 * 
	 * }
	 */

	@PostMapping("/auth")
	public String processTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO,
			Model model) throws Exception {

		model.addAttribute("transferRequest", transferRequestDTO);
		return page + "pageiii";

	}

	@PostMapping("/summary")
	public String transferSummary(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO request,
			Locale locale, BindingResult result, Model model, RedirectAttributes redirectAttributes,
			HttpServletRequest servletRequest) {
		try {
			request.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
			request.setBeneficiaryAccountName(
					accountService.getAccountByAccountNumber(request.getBeneficiaryAccountNumber()).getAccountName());
			model.addAttribute("corpTransferRequest", request);
			// validator.validate(request, result);
			// if (result.hasErrors()) {
			// return page + "pagei";
			// }
			request.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
			// corpTransferService.validateTransfer(request);
			model.addAttribute("corpTransferRequest", request);
			servletRequest.getSession().setAttribute("corpTransferRequest", request);

			return page + "pageii";

		} catch (InternetBankingTransferException exception)

		{
			return page + "pagei";

		}

	}

	@RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
	public ModelAndView report(@PathVariable Long id) {

		JasperReportsPdfView view = new JasperReportsPdfView();
		view.setUrl("classpath:pdf/");
		view.setApplicationContext(appContext);

		Map<String, Object> params = new HashMap<>();
		params.put("datasource", corpTransferService.getTransfer(id));

		return new ModelAndView(view, params);
	}
}
