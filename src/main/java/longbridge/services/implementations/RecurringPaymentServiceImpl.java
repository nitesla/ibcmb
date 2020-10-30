package longbridge.services.implementations;

import longbridge.dtos.CorpQuicktellerRequestDTO;
import longbridge.dtos.PaymentStatDTO;
import longbridge.dtos.QuicktellerRequestDTO;
import longbridge.dtos.RecurringPaymentDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.*;
import longbridge.repositories.CorpRecurringPaymentRepo;
import longbridge.repositories.PaymentStatRepo;
import longbridge.repositories.RecurringPaymentRepo;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.services.RecurringPaymentService;
import longbridge.utils.DateUtil;
import org.joda.time.LocalDate;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class RecurringPaymentServiceImpl implements RecurringPaymentService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CorpRecurringPaymentRepo corpRecurringPaymentRepo;

	@Autowired
	private RecurringPaymentRepo recurringPaymentRepo;

	@Autowired
	private PaymentStatRepo paymentStatRepo;

	@Autowired
	private RecurringPaymentService recurringPaymentService;

	@Autowired
	private IntegrationService integrationService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private MessageSource messageSource;

	private final Locale locale = LocaleContextHolder.getLocale();

	@Value("${bank.code}")
	private String bankCode;


	@Override
	public String addRecurringPayment(RetailUser user, RecurringPaymentDTO recurringPaymentDTO) {

		try{
			logger.info("Recurring Payment setup {}", recurringPaymentDTO.toString());
			LocalDate now = LocalDate.now();
			RecurringPayment recurringPayment = convertDToToEntity(recurringPaymentDTO);
			recurringPayment.setDateCreated(now.toDate());
			recurringPayment.setRetailUser(user);
			recurringPayment.setRequestReference("RET_" + user.getId());
			recurringPayment.setCustomerId(user.getId().toString());
			recurringPayment.setPaymentCode(recurringPaymentDTO.getPaymentCode());
			recurringPayment.setStartDate(DateUtil.convertStringToDate(recurringPaymentDTO.getStart()));
			recurringPayment.setEndDate(DateUtil.convertStringToDate(recurringPaymentDTO.getEnd()));
			recurringPayment.setNextDebitDate(now.plusDays(recurringPayment.getIntervalDays()).toDate());
			generatePaymentsForRecurringPayment(recurringPaymentRepo.save(recurringPayment));
			return messageSource.getMessage("recurringpayment.add.success", null, locale);
		}catch (Exception e) {
			e.printStackTrace();
			throw new InternetBankingException(e.getMessage(),e);
		}
	}

	@Override
	public String addCorpRecurringPayment(CorporateUser user, RecurringPaymentDTO recurringPaymentDTO) {
		try{
			logger.info("Recurring Payment setup {}", recurringPaymentDTO.toString());
			LocalDate now = LocalDate.now();
			CorpRecurringPayment recurringPayment = convertDToToEntityCorp(recurringPaymentDTO);
			recurringPayment.setDateCreated(now.toDate());
			recurringPayment.setCorporateUser(user);
			recurringPayment.setRequestReference("COP_" + user.getCorporate().getId());
			recurringPayment.setCustomerId(user.getCorporate().getId().toString());
			recurringPayment.setStartDate(DateUtil.convertStringToDate(recurringPaymentDTO.getStart()));
			recurringPayment.setEndDate(DateUtil.convertStringToDate(recurringPaymentDTO.getEnd()));
			recurringPayment.setNextDebitDate(now.plusDays(recurringPayment.getIntervalDays()).toDate());
			recurringPayment.setCorporate(user.getCorporate().getId());
            generatePaymentsForRecurringPayment(corpRecurringPaymentRepo.save(recurringPayment));
			return messageSource.getMessage("recurringpayment.add.success", null, locale);
		}catch (Exception e) {
			e.printStackTrace();
			throw new InternetBankingException(e.getMessage(),e);
		}
	}

    @Override
	public String deleteRecurringPayment(Long recurringPaymentId) {
		RecurringPayment recurringPayment = getRecurringPayment(recurringPaymentId);
		try{
			recurringPaymentRepo.delete(recurringPayment);
			logger.info("Successfully deleted Recurring Payment");
			return messageSource.getMessage("recurringpayment.delete.success", null, locale);
		}catch(Exception e){
			logger.error("Error", e);
			throw new InternetBankingException(messageSource.getMessage("recurringpayment.delete.failure", null, locale), e);
		}

	}


	@Override
	public RecurringPayment getRecurringPayment(Long recurringPaymentId) {
		return recurringPaymentRepo.getOne(recurringPaymentId);
	}

	@Override
	public Collection<PaymentStat> recurringPayments(RecurringPayment recurringPayment) {
		return paymentStatRepo.findByRecurringPayment(recurringPayment);
	}

	@Override
	public String deletePayment(Long id) {
		PaymentStat paymentStat =  paymentStatRepo.getOne(id);
		try{
			paymentStatRepo.delete(paymentStat);
			logger.info("Successfully deleted Payment");
			return messageSource.getMessage("recurringpayment.delete.success", null, locale);
		}catch(Exception e){
			logger.error("Error", e);
			throw new InternetBankingException(messageSource.getMessage("recurringpayment.delete.failure", null, locale), e);
		}
	}

    @Override
    public Collection<PaymentStatDTO>  getPayments(RecurringPayment recurringPayment){
        Collection<PaymentStat> payments = paymentStatRepo.findByRecurringPayment(recurringPayment);
        Collection<PaymentStatDTO> result = new ArrayList<>();
        payments.forEach( p -> {
            PaymentStatDTO paymentDTO = new PaymentStatDTO();
            paymentDTO.setId(p.getId());
            paymentDTO.setAmount(recurringPayment.getAmount().toString());
            paymentDTO.setDate(p.getDebitDate().toString());
            paymentDTO.setStatus(p.getPaymentStatus().toString());
            paymentDTO.setPaymentItem(( recurringPayment.getPaymentItemName()));
            result.add(paymentDTO);
        });
        return  result ;
    }

    @Override
    public RecurringPayment getPaymentsRecurringPayment(Long paymentId) {
        PaymentStat payment =  paymentStatRepo.getOne(paymentId);
        return payment.getRecurringPayment();
    }


	@Override
	public void performRecurringPayment(PaymentStat paymentStat)  {
		RecurringPayment recurringPayment = paymentStat.getRecurringPayment();

		if (recurringPayment.getCorporate() == null) {
			QuicktellerRequestDTO quicktellerRequestDTO = new QuicktellerRequestDTO();
			quicktellerRequestDTO.setAmount(recurringPayment.getAmount());
			quicktellerRequestDTO.setCustomerAccountNumber(recurringPayment.getCustomerAccountNumber());
			quicktellerRequestDTO.setEmailAddress(recurringPayment.getEmailAddress());
			quicktellerRequestDTO.setCustomerId(recurringPayment.getCustomerId());
			quicktellerRequestDTO.setPhoneNumber(recurringPayment.getPhoneNumber());
			quicktellerRequestDTO.setPaymentCode((recurringPayment.getPaymentCode()));
			quicktellerRequestDTO.setRequestReference(recurringPayment.getRequestReference());
			quicktellerRequestDTO.setNarration("Recurring Payment:" + recurringPayment.getNarration());
			quicktellerRequestDTO.setRemarks(recurringPayment.getNarration());

			if (recurringPaymentService.validateRecurringPayment(quicktellerRequestDTO)) {
				RecurringPayment recurringPayment1 = recurringPaymentService.makeBackgroundTransfer(quicktellerRequestDTO, recurringPayment);
				recurringPayment.proceedToNextDebitDate();
				if (recurringPayment1.getStatus().equals("SUCCESSFUL")) {
					paymentStat.setPaymentStatus(PaymentStat.PaymentStatus.COMPLETED);
					paymentStatRepo.save(paymentStat);
					logger.info("Complete Execution of Recurring Payment for today: {} ", LocalDate.now());
				} else {
					paymentStat.setPaymentStatus(PaymentStat.PaymentStatus.PENDING);
					paymentStatRepo.save(paymentStat);
				}
				recurringPaymentRepo.save(recurringPayment);
			} else {
				logger.info("Failed Recurring Payment id {}", paymentStat.getRecurringPayment().getId());
			}


		} else if (recurringPayment.getCorporate() != null) {

			CorpRecurringPayment corpRecurringPayment = corpRecurringPaymentRepo.findOneById(recurringPayment.getId());
			CorpQuicktellerRequestDTO corpQuicktellerRequestDTO = new CorpQuicktellerRequestDTO();

			corpQuicktellerRequestDTO.setAmount(corpRecurringPayment.getAmount());
			corpQuicktellerRequestDTO.setCustomerAccountNumber(corpRecurringPayment.getCustomerAccountNumber());
			corpQuicktellerRequestDTO.setEmailAddress(corpRecurringPayment.getEmailAddress());
			corpQuicktellerRequestDTO.setCustomerId(corpRecurringPayment.getCustomerId());
			corpQuicktellerRequestDTO.setPhoneNumber(corpRecurringPayment.getPhoneNumber());
			corpQuicktellerRequestDTO.setPaymentCode((corpRecurringPayment.getPaymentCode()));
			corpQuicktellerRequestDTO.setRequestReference(corpRecurringPayment.getRequestReference());
			corpQuicktellerRequestDTO.setNarration("Recurring Payment:" + corpRecurringPayment.getNarration());
			corpQuicktellerRequestDTO.setRemarks(corpRecurringPayment.getNarration());

			BigDecimal balance = integrationService.getAvailableBalance(corpQuicktellerRequestDTO.getCustomerAccountNumber());
			if (balance != null) {
				if (!(balance.compareTo(corpQuicktellerRequestDTO.getAmount()) == 0 || (balance.compareTo(corpQuicktellerRequestDTO.getAmount()) > 0))) {
					logger.info("Account Balance is insufficient for this transfer {}", corpQuicktellerRequestDTO.getCustomerAccountNumber());
					logger.info("Failed Recurring Payment transfer,id {},", paymentStat.getRecurringPayment().getId());
				} else {


					RecurringPayment recurringPayment1 = recurringPaymentService.makeBackgroundTransfer(corpQuicktellerRequestDTO, recurringPayment);
					recurringPayment.proceedToNextDebitDate();
					if (recurringPayment1.getStatus().equals("SUCCESSFUL")) {
						paymentStat.setPaymentStatus(PaymentStat.PaymentStatus.COMPLETED);
						paymentStatRepo.save(paymentStat);
						logger.info("Complete Execution of Recurring Payment for today: {} ", LocalDate.now());
					} else {
						paymentStat.setPaymentStatus(PaymentStat.PaymentStatus.PENDING);
						paymentStatRepo.save(paymentStat);
					}
					recurringPaymentRepo.save(recurringPayment);
				}
			}
		}
	}

	@Override
	public boolean validateRecurringPayment(QuicktellerRequestDTO dto) throws InternetBankingTransferException {



		String cif = accountService.getAccountByAccountNumber(dto.getCustomerAccountNumber()).getCustomerId();
		boolean acctPresent = accountService.getAccountsForDebit(cif).stream()
				.anyMatch(i -> i.getAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber()));


		if (!acctPresent) {
			logger.info("Account is flagged for NO-DEBIT {} ",dto.getCustomerAccountNumber());
			return false;
		}

		BigDecimal balance = integrationService.getAvailableBalance(dto.getCustomerAccountNumber());
		if (balance != null) {
			if (!(balance.compareTo(dto.getAmount()) == 0 || (balance.compareTo(dto.getAmount()) > 0))) {
				logger.info("Account Balance is insufficient for this transfer {}",dto.getCustomerAccountNumber());
				return false;
			}
		}
		return true;
	}

	public RecurringPayment makeBackgroundTransfer(QuicktellerRequestDTO quicktellerRequestDTO, RecurringPayment recurringPayment) {
		logger.info("Initiating a Background Bill Payment", quicktellerRequestDTO);
		RecurringPayment recurringPayment1 = integrationService.recurringPayment(convertDToToEntityCorp(quicktellerRequestDTO));
		logger.trace("Recurring Payment Details: {} ", recurringPayment1);
		recurringPaymentRepo.save(recurringPayment1);
		return recurringPayment1;
	}



	private RecurringPayment convertDToToEntity(RecurringPaymentDTO recurringPaymentDTO) {
		RecurringPayment recurringPayment = new RecurringPayment();
		recurringPayment.setId(recurringPaymentDTO.getId());
		return modelMapper.map(recurringPaymentDTO, RecurringPayment.class);
	}

	private CorpRecurringPayment convertDToToEntityCorp(RecurringPaymentDTO recurringPaymentDTO){
		return modelMapper.map(recurringPaymentDTO, CorpRecurringPayment.class);
	}

	private CorpRecurringPayment convertDToToEntityCorp(QuicktellerRequestDTO quicktellerRequestDTO){
		return modelMapper.map(quicktellerRequestDTO, CorpRecurringPayment.class);
	}

	@Override
	public List<RecurringPayment> getDueRecurringPayment() {
		return recurringPaymentRepo.findByNextDebitDateBetween(DateUtil.getStartOfDay(new Date()), DateUtil.getEndOfDay(new Date()));
	}

	@Override
	public List<PaymentStat> getDuePayments(){
	 return paymentStatRepo.findByDebitDateBetween(DateUtil.getStartOfDay(new Date()), DateUtil.getEndOfDay(new Date()));

	}


	@Override
	public Page<RecurringPaymentDTO> getUserRecurringPaymentDTOs(RetailUser user, Pageable pageable) {
		Page<RecurringPayment> recurringPayments=recurringPaymentRepo.findByRetailUserOrderByStartDateDesc(user,pageable);
		List<RecurringPaymentDTO> recurringPaymentDTOS = new ArrayList<>();
		for (RecurringPayment recurringPayment : recurringPayments) {
			RecurringPaymentDTO directDebitDTO = modelMapper.map(recurringPayment, RecurringPaymentDTO.class);
			recurringPaymentDTOS.add(directDebitDTO);
		}
		long t = recurringPayments.getTotalElements();
		return new PageImpl<>(recurringPaymentDTOS, pageable, t);
	}

	@Override
	public Page<RecurringPaymentDTO> getCorpUserRecurringPaymentDTOS(CorporateUser corporateUser, Pageable pageable){
		Page<RecurringPayment> recurringPayments=recurringPaymentRepo.findByCorporateOrderByStartDateDesc(corporateUser.getCorporate().getId(),pageable);
		List<RecurringPaymentDTO> recurringPaymentDTOS = new ArrayList<>();
		for (RecurringPayment recurringPayment : recurringPayments) {
            RecurringPaymentDTO recurringPaymentDTO = modelMapper.map(recurringPayment, RecurringPaymentDTO.class);
            recurringPaymentDTOS.add(recurringPaymentDTO);
		}
		long t = recurringPayments.getTotalElements();
		return new PageImpl<>(recurringPaymentDTOS, pageable, t);

	}




	public void generatePaymentsForRecurringPayment(RecurringPayment recurringPayment) {
		logger.info("about to generate payments for recurring payment {}",recurringPayment.toString());
		boolean checker = true ;

		Collection<PaymentStat> recurringPayments = new ArrayList<>();
		Date startDate = recurringPayment.getStartDate() ;
		Date endDate = recurringPayment.getEndDate();
		Date nextDebit = startDate ;
		PaymentStat firstpay =  new PaymentStat();
		firstpay.setDebitDate(startDate);
		firstpay.setRecurringPayment(recurringPayment);
		recurringPayments.add(firstpay);
		logger.info("end date is {} ", endDate);
		while (checker){
			logger.info("next date is currently........{}",nextDebit);
			nextDebit = DateUtil.addDaysToDate(nextDebit , recurringPayment.getIntervalDays());
			logger.info("checking date ......{}", nextDebit);

			if(DateUtil.isBeforeDay(nextDebit , endDate)){

				PaymentStat payment =  new PaymentStat();
				payment.setDebitDate(nextDebit);
				payment.setRecurringPayment(recurringPayment);
				recurringPayments.add(payment);
			}else {
				logger.info("date now out of bound ! {}",nextDebit);
				checker = false ;
			}


		}
		recurringPayment.setPayments(recurringPayments);
		recurringPaymentRepo.save(recurringPayment);
	}

}
