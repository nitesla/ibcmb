package longbridge.services.implementations;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.DirectDebitDTO;
import longbridge.dtos.PaymentDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.TransferRuleException;
import longbridge.models.*;
import longbridge.repositories.CorpDirectDebitRepo;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.DirectDebitRepo;
import longbridge.repositories.PaymentRepo;
import longbridge.services.*;
import longbridge.utils.DateUtil;
import longbridge.utils.StatusCode;
import longbridge.utils.TransferType;
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
public class DirectDebitServiceImpl implements DirectDebitService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private DirectDebitRepo directDebitRepo;

	@Autowired
	private CorpDirectDebitRepo corpDirectDebitRepo;

	@Autowired
	private PaymentRepo paymentRepo;
	
	@Autowired
	private TransferService transferService;

	@Autowired
	private IntegrationService integrationService;

	@Autowired
	private FinancialInstitutionService financialInstitutionService;

	@Autowired
	private CorpTransferRequestRepo corpTransferRequestRepo ;

	@Autowired
	private CorporateService corporateService ;

	@Autowired
	private MessageSource messageSource;

	private final Locale locale = LocaleContextHolder.getLocale();

	@Value("${bank.code}")
	private String bankCode;


	@Override
	public String addDirectDebit(RetailUser user, DirectDebitDTO directDebitDTO) {

		try{
			logger.info("direct debit setup {}", directDebitDTO.toString());
		LocalDate now = LocalDate.now();
		DirectDebit directDebit = convertDTOToEntity(directDebitDTO);
		directDebit.setDebitAccount(directDebit.getDebitAccount());
		directDebit.setDateCreated(now.toDate());
		directDebit.setRetailUser(user);
		directDebit.setStartDate(DateUtil.convertStringToDate(directDebitDTO.getStart()));
		directDebit.setEndDate(DateUtil.convertStringToDate(directDebitDTO.getEnd()));
		directDebit.setNextDebitDate(now.plusDays(directDebit.getIntervalDays()).toDate());

		generatePaymentsForDirectDebit(directDebitRepo.save(directDebit));
		return messageSource.getMessage("directdebit.add.success", null, locale);
	     }catch (Exception e) {
			e.printStackTrace();
			throw new InternetBankingException(e.getMessage(),e);
		}
	}

    @Override
    public String addCorpDirectDebit(CorporateUser user, DirectDebitDTO directDebitDTO) {
        try{
            logger.info("direct debit setup {}", directDebitDTO.toString());
            LocalDate now = LocalDate.now();
            CorpDirectDebit directDebit = convertDToToEntityCorp(directDebitDTO);
            directDebit.setDateCreated(now.toDate());
            directDebit.setCorporateUser(user);
            directDebit.setStartDate(DateUtil.convertStringToDate(directDebitDTO.getStart()));
            directDebit.setEndDate(DateUtil.convertStringToDate(directDebitDTO.getEnd()));
            directDebit.setNextDebitDate(now.plusDays(directDebit.getIntervalDays()).toDate());
            directDebit.setCorporate(user.getCorporate().getId());
			if ("SOLE".equals(user.getCorporate().getCorporateType())) {
				generatePaymentsForDirectDebit(corpDirectDebitRepo.save(directDebit));
			}else{
				logger.info("seems corporate is multi .... about to send request for authorization");

				CorpDirectDebit savedDebit = corpDirectDebitRepo.save(directDebit) ;
				CorpTransRequest transferRequest  = new CorpTransRequest();
				transferRequest.setUserReferenceNumber("CORP_"+user.getId().toString());
				transferRequest.setBeneficiaryAccountName(savedDebit.getCorpLocalBeneficiary().getAccountName());
				transferRequest.setBeneficiaryAccountNumber(savedDebit.getCorpLocalBeneficiary().getAccountNumber());
				FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByCode(savedDebit.getCorpLocalBeneficiary().getBeneficiaryBank());
				transferRequest.setFinancialInstitution(financialInstitution);
				transferRequest.setAmount(savedDebit.getAmount());
				transferRequest.setCustomerAccountNumber(savedDebit.getDebitAccount());

				transferRequest.setNarration("Direct Debit:" + savedDebit.getNarration());
				transferRequest.setRemarks(savedDebit.getNarration());


				logger.info("beneficary bank is  {}", savedDebit.toString());

				if(savedDebit.getCorpLocalBeneficiary().getBeneficiaryBank().equals(bankCode)){
					transferRequest.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
				}else{
					transferRequest.setTransferType(TransferType.INTER_BANK_TRANSFER);
				}
				transferRequest.setStatus(StatusCode.PENDING.toString());
				transferRequest.setStatusDescription("Pending Authorization");
				transferRequest.setCorpDirectDebit(savedDebit);
				transferRequest.setCorporate(user.getCorporate());

				CorpTransferAuth transferAuth = new CorpTransferAuth();
				transferAuth.setStatus("P");
				transferRequest.setTransferAuth(transferAuth);

				if (corporateService.getApplicableTransferRule(transferRequest) == null) {
					throw new TransferRuleException(messageSource.getMessage("rule.unapplicable", null, locale));
				}
				CorpTransRequest corpTransRequest = corpTransferRequestRepo.save(transferRequest);
				logger.info("Transfer request saved for authorization");
				return  "standing order has gone for authorization";

			}
            return messageSource.getMessage("directdebit.add.success", null, locale);
        }catch (Exception e) {
            e.printStackTrace();
            throw new InternetBankingException(e.getMessage(),e);
        }
    }

    @Override
	public String deleteDirectDebit(Long directDebitId) {
		DirectDebit directDebit = getDirectDebit(directDebitId);
		try{
			directDebitRepo.delete(directDebit);
			logger.info("Successfully deleted DirectDebit");
			return messageSource.getMessage("directdebit.delete.success", null, locale);
		}catch(Exception e){
			logger.error("Error", e);
			throw new InternetBankingException(messageSource.getMessage("directdebit.delete.failure", null, locale), e);
		}

	}


	@Override
	public DirectDebit getDirectDebit(Long directDebitId) {
		return directDebitRepo.getOne(directDebitId);
	}


	@Override
	public void performDirectDebitPayment(Payment payment)  {
		DirectDebit directDebit = payment.getDirectDebit();

		if (directDebit.getCorporate() == null) {
			TransferRequestDTO transferRequest = new TransferRequestDTO();
			transferRequest.setAmount(directDebit.getAmount());
			transferRequest.setCustomerAccountNumber(directDebit.getDebitAccount());
			transferRequest.setNarration("Direct Debit:" + directDebit.getNarration());
			transferRequest.setRemarks(directDebit.getNarration());
			transferRequest.setBeneficiaryAccountName(directDebit.getBeneficiary().getAccountName());
			transferRequest.setBeneficiaryAccountNumber(directDebit.getBeneficiary().getAccountNumber());
			FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByCode(directDebit.getBeneficiary().getBeneficiaryBank());
			transferRequest.setFinancialInstitution(financialInstitution);

			if (directDebit.getBeneficiary().getBeneficiaryBank().equals(bankCode)) {
				transferRequest.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
			} else {
				transferRequest.setTransferType(TransferType.INTER_BANK_TRANSFER);
			}
			if (transferService.validateDirectDebitTransfer(transferRequest)) {
				TransRequest transRequest = transferService.makeBackgroundTransfer(transferRequest, directDebit);
				directDebit.proceedToNextDebitDate();
				if (transRequest.getStatus().equals("000") || transRequest.getStatus().equals("00")) {
					payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
					paymentRepo.save(payment);
					logger.info("Complete Execution of direct debits for today: {} ", LocalDate.now());
				} else {
					payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
					paymentRepo.save(payment);
				}
				directDebitRepo.save(directDebit);
			} else {
				logger.info("Failed Direct Debit transfer id {}", payment.getDirectDebit().getId());
			}


		} else if (directDebit.getCorporate() != null) {

			CorpDirectDebit corpDirectDebit = corpDirectDebitRepo.findOneById(directDebit.getId());
			CorpTransferRequestDTO transferRequest = new CorpTransferRequestDTO();
			transferRequest.setAmount(directDebit.getAmount());
			transferRequest.setCustomerAccountNumber(directDebit.getDebitAccount());
			transferRequest.setNarration("Direct Debit:" + directDebit.getNarration());
			transferRequest.setRemarks(directDebit.getNarration());

			transferRequest.setBeneficiaryAccountName(corpDirectDebit.getCorpLocalBeneficiary().getAccountName());
			transferRequest.setBeneficiaryAccountNumber(corpDirectDebit.getCorpLocalBeneficiary().getAccountNumber());
			FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByCode(corpDirectDebit.getCorpLocalBeneficiary().getBeneficiaryBank());
			transferRequest.setFinancialInstitution(financialInstitution);
			if (corpDirectDebit.getCorpLocalBeneficiary().getBeneficiaryBank().equals(bankCode)) {
				transferRequest.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
			} else {
				transferRequest.setTransferType(TransferType.INTER_BANK_TRANSFER);
			}
			BigDecimal balance = integrationService.getAvailableBalance(transferRequest.getCustomerAccountNumber());
			if (balance != null) {
				if (!(balance.compareTo(transferRequest.getAmount()) == 0 || (balance.compareTo(transferRequest.getAmount()) > 0))) {
					logger.info("Account Balance is insufficient for this transfer {}", transferRequest.getCustomerAccountNumber());
					logger.info("Failed Direct Debit transfer,id {},", payment.getDirectDebit().getId());
				} else {


					TransRequest transRequest = transferService.makeBackgroundTransfer(transferRequest, directDebit);
					directDebit.proceedToNextDebitDate();
					if (transRequest.getStatus().equals("000") || transRequest.getStatus().equals("00")) {
						payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
						paymentRepo.save(payment);
						logger.info("Complete Execution of direct debits for today: {} ", LocalDate.now());
					} else {
						payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
						paymentRepo.save(payment);
					}
					directDebitRepo.save(directDebit);
				}
			}
		}
	}



	private DirectDebit convertDTOToEntity(DirectDebitDTO directDebitDTO) {
		DirectDebit directDebit = new DirectDebit();
		directDebit.setId(directDebitDTO.getId());
		return modelMapper.map(directDebitDTO, DirectDebit.class);
	}

	private CorpDirectDebit convertDToToEntityCorp(DirectDebitDTO directDebitDTO){
        return modelMapper.map(directDebitDTO, CorpDirectDebit.class);
    }

	@Override
	public List<DirectDebit> getDueDirectDebits() {
		return directDebitRepo.findByNextDebitDateBetween(DateUtil.getStartOfDay(new Date()), DateUtil.getEndOfDay(new Date()));
	}

	@Override
	public List<Payment> getDuePayments(){
	 return paymentRepo.findByDebitDateBetween(DateUtil.getStartOfDay(new Date()), DateUtil.getEndOfDay(new Date()));

	}

	/*@Override
	public Page<DirectDebit> getUserDirectDebits(RetailUser user, Pageable pageable) {
		return directDebitRepo.findByRetailUser(user,pageable);
	}
*/
	@Override
	public Page<DirectDebitDTO> getUserDirectDebitDTOs(RetailUser user, Pageable pageable) {
		Page<DirectDebit> directDebits=directDebitRepo.findByRetailUser(user,pageable);
		List<DirectDebitDTO> directDebitDTOS = new ArrayList<>();
		for (DirectDebit directDebit : directDebits) {
			DirectDebitDTO directDebitDTO = modelMapper.map(directDebit, DirectDebitDTO.class);
			directDebitDTOS.add(directDebitDTO);
		}
		long t = directDebits.getTotalElements();
		return new PageImpl<>(directDebitDTOS, pageable, t);
	}

	@Override
	public Page<DirectDebitDTO> getCorpUserDirectDebitDTOS(CorporateUser corporateUser, Pageable pageable){
		Page<DirectDebit> directDebits=directDebitRepo.findByCorporate(corporateUser.getCorporate().getId(),pageable);
		List<DirectDebitDTO> directDebitDTOS = new ArrayList<>();
		for (DirectDebit directDebit : directDebits) {
			DirectDebitDTO directDebitDTO = modelMapper.map(directDebit, DirectDebitDTO.class);
			directDebitDTOS.add(directDebitDTO);
		}
		long t = directDebits.getTotalElements();
		return new PageImpl<>(directDebitDTOS, pageable, t);

	}

	/*@Override
    public List<CorpDirectDebit> getCorpUserDirectDebits(CorporateUser corporateUser) {
        return corpDirectDebitRepo.findByCorporate(corporateUser.getCorporate().getId());
    }
*/
	/*@Override
	public List<CorpDirectDebit> getByCOrporate(Long cordId) {
		return corpDirectDebitRepo.findByCorporate(cordId);
	}
*/

	@Override
	public void generatePaymentsForDirectDebit(DirectDebit directDebit) {
		logger.info("about to generate payments for standing order {}",directDebit.toString());
		boolean checker = true ;

		Collection<Payment> directDebitPayments = new ArrayList<>();
		Date startDate = directDebit.getStartDate() ;
		Date endDate = directDebit.getEndDate();
		Date nextDebit = startDate ;
        Payment firstpay =  new Payment();
        firstpay.setDebitDate(startDate);
        firstpay.setDirectDebit(directDebit);
        directDebitPayments.add(firstpay);
		logger.info("end date is {} ", endDate);
		while (checker){
             logger.info("next date is currently........{}",nextDebit);
			nextDebit = DateUtil.addDaysToDate(nextDebit , directDebit.getIntervalDays());
			logger.info("checking date ......{}", nextDebit);

			if(DateUtil.isBeforeDay(nextDebit , endDate)){

				Payment payment =  new Payment();
				payment.setDebitDate(nextDebit);
				payment.setDirectDebit(directDebit);
			    directDebitPayments.add(payment);
			}else {
				logger.info("date now out of bound ! {}",nextDebit);
				checker = false ;
			}


		}
		directDebit.setPayments(directDebitPayments);
		directDebitRepo.save(directDebit);
	}

	@Override
	public String modifyPayment(PaymentDTO paymentDTO) {
		return null;
	}

	@Override
	public String deletePayment(Long id) {
		Payment payment =  paymentRepo.getOne(id);
		try{
			paymentRepo.delete(payment);
			logger.info("Successfully deleted Payment");
			return messageSource.getMessage("directdebit.delete.success", null, locale);
		}catch(Exception e){
			logger.error("Error", e);
			throw new InternetBankingException(messageSource.getMessage("directdebit.delete.failure", null, locale), e);
		}
	}

	@Override
	public DirectDebit getPaymentsDirectDebit(Long paymentId) {
		Payment payment =  paymentRepo.getOne(paymentId);
		return payment.getDirectDebit();
	}

	@Override
	public Collection<Payment> debitsPayments(DirectDebit directDebit) {
		return paymentRepo.findByDirectDebit(directDebit);
	}

    @Override
	public Collection<PaymentDTO>  getPayments(DirectDebit directDebit){
        Collection<Payment> payments = paymentRepo.findByDirectDebit(directDebit);
        Collection<PaymentDTO> result = new ArrayList<>();
        payments.forEach( p -> {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setId(p.getId());
            paymentDTO.setAmount(directDebit.getAmount().toString());
            paymentDTO.setDate(p.getDebitDate().toString());
            paymentDTO.setStatus(p.getPaymentStatus().toString());
            result.add(paymentDTO) ;
            if( directDebit instanceof CorpDirectDebit){
                CorpDirectDebit corpDirectDebit = (CorpDirectDebit) directDebit ;
                paymentDTO.setBeneficiary(corpDirectDebit.getCorpLocalBeneficiary().getAccountName());
            }else{
                paymentDTO.setBeneficiary(directDebit.getBeneficiary().getAccountName());
            }
        });
	    return  result ;
    }




}
