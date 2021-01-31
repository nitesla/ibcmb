package longbridge.services.implementations;

import longbridge.dtos.BillPaymentDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.TransferErrorService;
import longbridge.models.*;
import longbridge.repositories.BillPaymentRepo;
import longbridge.repositories.BillerRepo;
import longbridge.repositories.PaymentItemRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.IntegrationService;
import longbridge.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Fortune on 7/9/2018.
 */

@Service
public class PaymentServiceImpl implements PaymentService {

    private final BillPaymentRepo billPaymentRepo;
    @Autowired
    private IntegrationService integrationService;
    private final MessageSource messageSource;
    private final BillerRepo billersRepo;
    private final PaymentItemRepo paymentItemRepo;
    private final static Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final Locale locale = LocaleContextHolder.getLocale();
    private final TransferErrorService transferErrorService;

    @Autowired
    public PaymentServiceImpl(BillPaymentRepo billPaymentRepo,  MessageSource messageSource, BillerRepo billersRepo, PaymentItemRepo paymentItemRepo, TransferErrorService transferErrorService) {
        this.billPaymentRepo = billPaymentRepo;
        this.messageSource = messageSource;
        this.billersRepo = billersRepo;
        this.paymentItemRepo = paymentItemRepo;
        this.transferErrorService = transferErrorService;
    }

    @Override
    public String addBillPayment(BillPaymentDTO paymentDTO) {

        logger.debug("Adding bill payment {} for user [{}]", paymentDTO, getCurrentUser().getUserName());

        try {
            logger.info("Print   333---->{}", paymentDTO.getCustomerAccountNumber());
            BillPayment payment1 = convertPaymentDTOToEntity(paymentDTO);
            BillPayment billPayment = integrationService.billPayment(payment1);
//            billPayment = billPaymentRepo.save(billPayment);
//            billPaymentRepo.save(payment1);
            logger.info("Added Bill payment {}", billPayment);
            billPayment = integrationService.checkBillPaymentTransaction(billPayment);

            logger.info("Checked Query Transaction details {}", billPayment);
            billPayment = billPaymentRepo.save(billPayment);

            if (billPayment.getStatus().equalsIgnoreCase("94")) {
                return messageSource.getMessage(billPayment.getResponseDescription(), null, locale);


            }else if (billPayment.getResponseCode().equalsIgnoreCase("9000")) {
                return messageSource.getMessage("Payment Successful", null, locale);

            }else {

                return messageSource.getMessage(billPayment.getResponseDescription(), null, locale);
            }

            }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new InternetBankingException(messageSource.getMessage("Payment Failure",null,locale));
        }


    }

    @Override
    public String addCorpBillPayment(BillPaymentDTO paymentDTO) {

        logger.debug("Adding bill payment {} for user [{}]",paymentDTO, getCurrentCorpUser().getUserName());

        try {
            BillPayment payment1 = convertCorpPaymentDTOToEntity(paymentDTO);
            BillPayment billPayment = integrationService.billPayment(payment1);
//            billPayment = billPaymentRepo.save(billPayment);
//            billPaymentRepo.save(payment1);
            logger.info("Added Bill payment {}", billPayment);
            billPayment = integrationService.checkBillPaymentTransaction(billPayment);
            logger.info("Checked Query Transaction details {}", billPayment);
            billPayment = billPaymentRepo.save(billPayment);

            if (billPayment.getStatus().equalsIgnoreCase("94")) {
                return messageSource.getMessage(billPayment.getResponseDescription(), null, locale);


            }else if (billPayment.getResponseCode().equalsIgnoreCase("9000")) {
                return messageSource.getMessage("Payment Successful", null, locale);

            }else {

                return messageSource.getMessage(billPayment.getResponseDescription(), null, locale);
            }
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new InternetBankingException(messageSource.getMessage("Payment Failure",null,locale));
        }

    }


    @Override
    public BillPayment getBillPayment(Long id){
        return billPaymentRepo.findById(id).get();
    }


    @Override
    public Page<BillPaymentDTO> getBillPayments(Pageable pageDetails) {

        logger.debug("Retrieving completed payments");
        RetailUser user = getCurrentUser();
        Page<BillPayment> page = billPaymentRepo.findByCustomerIdAndStatusNotNullOrderByCreatedOnDesc(user.getId().toString(), pageDetails);
        List<BillPaymentDTO> dtOs = convertPaymentEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);

    }

    @Override
    public Page<BillPaymentDTO> getCorpPayments(Pageable pageDetails) {

        logger.debug("Retrieving completed payments");
        CorporateUser user = getCurrentCorpUser();
        Page<BillPayment> page = billPaymentRepo.findByCustomerIdAndStatusNotNullOrderByCreatedOnDesc(user.getId().toString(), pageDetails);
        List<BillPaymentDTO> dtOs = convertPaymentEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);

    }

    @Override
    public Page<BillPaymentDTO> getBillPayments(String pattern, Pageable pageDetails) {

        logger.debug("Retrieving completed payments");
        RetailUser user = getCurrentUser();
        Page<BillPayment> page = billPaymentRepo.findUsingPattern(user.getId().toString(),pattern, pageDetails);
        List<BillPaymentDTO> dtOs = convertPaymentEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);

    }

    @Override
    public Page<BillPaymentDTO> getCorpPayments(String pattern, Pageable pageable) {

        logger.debug("Retrieving completed CORP payments");
        CorporateUser user = getCurrentCorpUser();
        Page<BillPayment> page = billPaymentRepo.findUsingPattern(user.getId().toString(),pattern, pageable);
        List<BillPaymentDTO> dtOs = convertPaymentEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageable, t);

    }


    private BillPayment convertPaymentDTOToEntity(BillPaymentDTO paymentDTO){

        logger.debug("Converting Bill payment DTO to entity");

        Random rand = new Random();
        int upperbound = 9999999;
        int random = rand.nextInt(upperbound);

        BillPayment payment = new BillPayment();
        payment.setCustomerAccountNumber(paymentDTO.getCustomerAccountNumber());
//        payment.setPaymentItemId(Long.parseLong(paymentDTO.getPaymentItemId()));
//        payment.setBillerId(Long.parseLong(paymentDTO.getBillerId()));
        payment.setAmount(new BigDecimal(paymentDTO.getAmount()));
        payment.setPhoneNumber(paymentDTO.getPhoneNumber());
        payment.setEmailAddress(paymentDTO.getEmailAddress());
        payment.setCustomerId(getCurrentUser().getId().toString());
        payment.setPaymentCode(paymentDTO.getPaymentCode());
        payment.setPaymentItemName(paymentDTO.getPaymentItemName());
        payment.setBillerName(paymentDTO.getBillerName());
        payment.setCategoryName(paymentDTO.getCategoryName());
        payment.setRequestReference("1453" + random);
        return payment;
    }

    private BillPayment convertCorpPaymentDTOToEntity(BillPaymentDTO paymentDTO){

        logger.debug("Converting Bill payment DTO to entity");

        Random rand = new Random();
        int upperbound = 9999999;
        int random = rand.nextInt(upperbound);

        BillPayment payment = new BillPayment();
        logger.info("Print   333---->{}", paymentDTO.getCustomerAccountNumber());
        payment.setCustomerAccountNumber(paymentDTO.getCustomerAccountNumber());
//        payment.setPaymentItemId(Long.parseLong(paymentDTO.getPaymentItemId()));
//        payment.setBillerId(Long.parseLong(paymentDTO.getBillerId()));
        payment.setAmount(new BigDecimal(paymentDTO.getAmount()));
        payment.setPhoneNumber(paymentDTO.getPhoneNumber());
        payment.setEmailAddress(paymentDTO.getEmailAddress());
        payment.setCustomerId(getCurrentCorpUser().getId().toString());
        payment.setPaymentCode(paymentDTO.getPaymentCode());
        payment.setPaymentItemName(paymentDTO.getPaymentItemName());
        payment.setBillerName(paymentDTO.getBillerName());
        payment.setCategoryName(paymentDTO.getCategoryName());
        payment.setRequestReference("1453" + random);
        return payment;
    }

    private BillPaymentDTO convertPaymentEntityToDTO(BillPayment payment){

        BillPaymentDTO paymentDTO = new BillPaymentDTO();
        Biller biller = billersRepo.findByBillerId(payment.getBillerId());
        PaymentItem paymentItem = paymentItemRepo.findByPaymentItemId(payment.getPaymentItemId());

        paymentDTO.setAmount(payment.getAmount().toString());
        paymentDTO.setStatus(payment.getStatus());
        paymentDTO.setCreatedOn(payment.getCreatedOn());
        paymentDTO.setTransactionRef(payment.getTransactionRef());
        paymentDTO.setResponseDescription(payment.getResponseDescription());
        paymentDTO.setCategoryName(payment.getCategoryName());
        paymentDTO.setBillerName(payment.getBillerName());
        paymentDTO.setPaymentItemName(payment.getPaymentItemName());
        paymentDTO.setId(payment.getId());
        return paymentDTO;
    }

    public List<BillPaymentDTO> convertPaymentEntitiesToDTOs(Iterable<BillPayment> payments) {
        List<BillPaymentDTO> billPaymentDTOs = new ArrayList<>();
        for (BillPayment billPayment : payments) {
            BillPaymentDTO billPaymentDTO = convertPaymentEntityToDTO(billPayment);
            billPaymentDTOs.add(billPaymentDTO);
        }
        return billPaymentDTOs;
    }

    private RetailUser getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (RetailUser) principal.getUser();
    }

    private CorporateUser getCurrentCorpUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (CorporateUser) principal.getUser();
    }


}

