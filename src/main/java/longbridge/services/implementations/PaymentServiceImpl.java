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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    public BillPaymentDTO addBillPayment(BillPaymentDTO paymentDTO) {

        logger.debug("Adding bill payment {} for user [{}]",paymentDTO, getCurrentUser().getUserName());

        try {
//
            BillPayment payment1 = convertPaymentDTOToEntity(paymentDTO);
            payment1 = persistPayment(convertPaymentEntityToDTO(payment1));

            BillPayment billPayment = integrationService.billPayment(payment1);

            billPayment = billPaymentRepo.save(billPayment);
//            billPaymentRepo.save(payment1);
            logger.info("Added payment {}",billPayment);

            if (billPayment.getStatus() != null) {
                if (billPayment.getStatus().equalsIgnoreCase("000") || billPayment.getStatus().equalsIgnoreCase("00"))
                    return convertPaymentEntityToDTO(billPayment);


            }
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new InternetBankingException(messageSource.getMessage("payment.add.failure",null,locale));
        }

//        return messageSource.getMessage("payment.add.success",null,locale);

        throw new InternetBankingException();

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
        payment.setCustomerId(paymentDTO.getCustomerId());
        payment.setUserId(getCurrentUser().getId());
        payment.setStatus("SUBMITTED");
        return payment;
    }

    private BillPaymentDTO convertPaymentEntityToDTO(BillPayment payment){

        BillPaymentDTO paymentDTO = new BillPaymentDTO();
        Biller biller = billersRepo.findByBillerId(payment.getBillerId());
        PaymentItem paymentItem = paymentItemRepo.findByPaymentItemId(payment.getPaymentItemId());
        paymentDTO.setBillerId(biller.getBillerId().toString());
        paymentDTO.setPaymentItemId(paymentItem.getPaymentItemId().toString());
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

    private BillPayment persistPayment(BillPaymentDTO billPaymentDTO) throws InternetBankingException {
        BillPayment billPayment = convertPaymentDTOToEntity(billPaymentDTO);
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUserName = authentication.getName();
                RetailUser user = retailUserRepo.findFirstByUserNameIgnoreCase(currentUserName);
                billPayment.setRequestReference("RET_" + user.getId());
            }
            billPayment = billPaymentRepo.save(billPayment);
            return billPayment;

        } catch (Exception e) {
            logger.error("Exception occurred saving transfer request", e);
        }
        return billPayment;
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

