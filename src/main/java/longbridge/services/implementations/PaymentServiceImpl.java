package longbridge.services.implementations;

import longbridge.dtos.BillPaymentDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.BillPaymentRepo;
import longbridge.repositories.BillerRepo;
import longbridge.repositories.PaymentItemRepo;
import longbridge.repositories.RetailUserRepo;
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

/**
 * Created by Fortune on 7/9/2018.
 */

@Service
public class PaymentServiceImpl implements PaymentService {

    private final BillPaymentRepo billPaymentRepo;
    private RetailUserRepo retailUserRepo;
    @Autowired
    private IntegrationService integrationService;
    private final MessageSource messageSource;
    private final BillerRepo billersRepo;
    private final PaymentItemRepo paymentItemRepo;
    private final static Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public PaymentServiceImpl(BillPaymentRepo billPaymentRepo, MessageSource messageSource, BillerRepo billersRepo, PaymentItemRepo paymentItemRepo) {
        this.billPaymentRepo = billPaymentRepo;
        this.messageSource = messageSource;
        this.billersRepo = billersRepo;
        this.paymentItemRepo = paymentItemRepo;
    }

    @Override
    public String addBillPayment(BillPaymentDTO paymentDTO) {

        logger.debug("Adding bill payment {} for user [{}]",paymentDTO, getCurrentUser().getUserName());

        try {
            BillPayment payment1 = convertPaymentDTOToEntity(paymentDTO);
            BillPayment billPayment = integrationService.billPayment(payment1);
            billPayment = billPaymentRepo.save(billPayment);
//            billPaymentRepo.save(payment1);
            logger.info("Added payment {}",billPayment);

        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new InternetBankingException(messageSource.getMessage("Payment Failure",null,locale));
        }

        return messageSource.getMessage("Payment Successful",null,locale);

    }

    @Override
    public String addCorpBillPayment(BillPaymentDTO paymentDTO) {

        logger.debug("Adding bill payment {} for user [{}]",paymentDTO, getCurrentCorpUser().getUserName());

        try {
            BillPayment payment1 = convertCorpPaymentDTOToEntity(paymentDTO);
            BillPayment billPayment = integrationService.billPayment(payment1);

            billPayment = billPaymentRepo.save(billPayment);
            logger.info("Added payment {}",billPayment);
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new InternetBankingException(messageSource.getMessage("Payment Failure",null,locale));
        }

        return messageSource.getMessage("Payment Successful",null,locale);

    }


    @Override
    public BillPayment getBillPayment(Long id){
        return billPaymentRepo.findById(id).get();
    }


    @Override
    public Page<BillPaymentDTO> getBillPayments(Pageable pageDetails) {

        logger.debug("Retrieving completed payments");
        RetailUser user = getCurrentUser();
        Page<BillPayment> page = billPaymentRepo.findByRequestReferenceAndCreatedOnNotNullOrderByCreatedOnDesc("RET_" + user.getId(), pageDetails);
        List<BillPaymentDTO> dtOs = convertPaymentEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<BillPaymentDTO> pageImpl = new PageImpl<BillPaymentDTO>(dtOs, pageDetails, t);
        return pageImpl;

    }

    @Override
    public Page<BillPaymentDTO> getCorpPayments(Pageable pageDetails) {

        logger.debug("Retrieving completed payments");
        CorporateUser user = getCurrentCorpUser();
        Page<BillPayment> page = billPaymentRepo.findByRequestReferenceAndCreatedOnNotNullOrderByCreatedOnDesc("COP_" + user.getId(), pageDetails);
        List<BillPaymentDTO> dtOs = convertPaymentEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<BillPaymentDTO> pageImpl = new PageImpl<BillPaymentDTO>(dtOs, pageDetails, t);
        return pageImpl;

    }

    @Override
    public Page<BillPaymentDTO> getBillPayments(String pattern, Pageable pageDetails) {

        logger.debug("Retrieving completed payments");
        RetailUser user = getCurrentUser();
        Page<BillPayment> page = billPaymentRepo.findUsingPattern("RET_" + user.getId(),pattern, pageDetails);
        List<BillPaymentDTO> dtOs = convertPaymentEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<BillPaymentDTO> pageImpl = new PageImpl<BillPaymentDTO>(dtOs, pageDetails, t);
        return pageImpl;

    }

    @Override
    public Page<BillPaymentDTO> getCorpPayments(String pattern, Pageable pageable) {

        logger.debug("Retrieving completed CORP payments");
        CorporateUser user = getCurrentCorpUser();
        Page<BillPayment> page = billPaymentRepo.findUsingPattern("COP_" + user.getId(),pattern, pageable);
        List<BillPaymentDTO> dtOs = convertPaymentEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<BillPaymentDTO> pageImpl = new PageImpl<BillPaymentDTO>(dtOs, pageable, t);
        return pageImpl;

    }


    private BillPayment convertPaymentDTOToEntity(BillPaymentDTO paymentDTO){

        logger.debug("Converting Bill payment DTO to entity");

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
        payment.setRequestReference("RET_" + getCurrentUser().getId());
        return payment;
    }

    private BillPayment convertCorpPaymentDTOToEntity(BillPaymentDTO paymentDTO){

        logger.debug("Converting Bill payment DTO to entity");

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
        payment.setRequestReference("COP_" + getCurrentCorpUser().getId());
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
        RetailUser retailUser = (RetailUser) principal.getUser();
        return retailUser;
    }

    private CorporateUser getCurrentCorpUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CorporateUser corporateUser = (CorporateUser) principal.getUser();
        return corporateUser;
    }


}

