package longbridge.services.implementations;

import longbridge.dtos.BillPaymentDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.BillPaymentRepo;
import longbridge.repositories.BillerRepo;
import longbridge.repositories.PaymentItemRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
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
            BillPayment payment = convertPaymentDTOToEntity(paymentDTO);
            billPaymentRepo.save(payment);
            logger.info("Added payment {}",payment);
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new InternetBankingException(messageSource.getMessage("payment.add.failure",null,locale));
        }

        return messageSource.getMessage("payment.add.success",null,locale);

    }

    @Override
    public String updatePaymentStatus(String status) {
        return null;
    }

    @Override
    public Page<BillPaymentDTO> getBillPayments(Pageable pageable) {

        logger.debug("Retrieving completed payments");
        Page<BillPayment> page = billPaymentRepo.findByUserId(getCurrentUser().getId(),pageable);
        List<BillPaymentDTO> dtOs = convertPaymentEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<BillPaymentDTO> pageImpl = new PageImpl<BillPaymentDTO>(dtOs, pageable, t);
        return pageImpl;

    }

    @Override
    public Page<BillPaymentDTO> getCorpPayments(Pageable pageable) {

        logger.debug("Retrieving completed payments");
        Page<BillPayment> page = billPaymentRepo.findByUserId(getCurrentCorpUser().getId(),pageable);
        List<BillPaymentDTO> dtOs = convertPaymentEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<BillPaymentDTO> pageImpl = new PageImpl<BillPaymentDTO>(dtOs, pageable, t);
        return pageImpl;

    }


    private BillPayment convertPaymentDTOToEntity(BillPaymentDTO paymentDTO){

        logger.debug("Converting Bill payment DTO to entity");

        BillPayment payment = new BillPayment();
        payment.setPaymentItemId(Long.parseLong(paymentDTO.getPaymentItemId()));
        payment.setBillerId(Long.parseLong(paymentDTO.getBillerId()));
        payment.setCustomerAccountNumber(paymentDTO.getCustomerAccountNumber());
        payment.setAmount(new BigDecimal(paymentDTO.getAmount()));
        payment.setPhoneNumber(paymentDTO.getPhoneNumber());
        payment.setEmailAddress(paymentDTO.getEmailAddress());
        payment.setCustomerIdentifier(paymentDTO.getCustomerIdentifier());
        payment.setUserId(getCurrentUser().getId());
        payment.setStatus("SUBMITTED");
        return payment;
    }

    private BillPaymentDTO convertPaymentEntityToDTO(BillPayment payment){

        BillPaymentDTO paymentDTO = new BillPaymentDTO();
        Biller biller = billersRepo.findOneById(payment.getBillerId());
        PaymentItem paymentItem = paymentItemRepo.findOneById(payment.getPaymentItemId());
        paymentDTO.setBillerName(biller.getBillerName());
        paymentDTO.setPaymentItemName(paymentItem.getPaymentItemName());
        paymentDTO.setAmount(payment.getAmount().toString());
        paymentDTO.setStatus(payment.getStatus());
        paymentDTO.setCreatedOn(payment.getCreatedOn());
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

