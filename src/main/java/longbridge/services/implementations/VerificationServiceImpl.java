package longbridge.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.PendingVerification;
import longbridge.dtos.VerificationDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationException;
import longbridge.models.*;
import longbridge.repositories.VerificationRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.VerificationService;
import longbridge.utils.verificationStatus;
import org.modelmapper.ModelMapper;
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

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class VerificationServiceImpl implements VerificationService {

    private static final String PACKAGE_NAME = "longbridge.models.";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private VerificationRepo verificationRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ModelMapper modelMapper;

    Locale locale = LocaleContextHolder.getLocale();

    @Override
    public String decline(VerificationDTO dto) throws VerificationException {
        Verification verification = verificationRepo.findOne(dto.getId());

        String verifiedBy = getCurrentUserName();
        if(verifiedBy.equals(verification.getInitiatedBy())){
            throw new InternetBankingException("You cannot verify what you initiated");
        }

        if (!verificationStatus.PENDING.equals(verification.getStatus())) {
            throw new InternetBankingException("Verification is not pending for the operation");
        }
        verification.setId(dto.getId());
        verification.setVersion(dto.getVersion());
        verification.setDeclinedBy(getCurrentUserName());
        verification.setDeclinedOn(new Date());
        verification.setDeclineReason(dto.getComment());
        verificationRepo.save(verification);
        return messageSource.getMessage("verification.decline", null, locale);
    }


    @Override
    public String verify(Long id) throws VerificationException{
        VerificationDTO verificationDTO = getVerification(id);
        return verify(verificationDTO);
    }

    @Override
    public String verify(VerificationDTO dto) throws VerificationException {

        Verification verification = verificationRepo.findOne(dto.getId());
        String verifiedBy = getCurrentUserName();
        if(verifiedBy.equals(verification.getInitiatedBy())){
            throw new InternetBankingException("You cannot verify what you initiated");
        }

        if (!verificationStatus.PENDING.equals(verification.getStatus())) {
            throw new InternetBankingException("Verification is not pending for the operation");
        }

        ObjectMapper mapper = new ObjectMapper();


        try {
            Class<?> clazz  = Class.forName(PACKAGE_NAME + verification.getEntityName());
            Object object = mapper.readValue(verification.getOriginalObject(), clazz);

            entityManager.merge(object);
            verification.setId(dto.getId());
            verification.setVersion(dto.getVersion());
            verification.setVerifiedBy(getCurrentUserName());
            verification.setVerifiedOn(new Date());
            verification.setComments(dto.getComment());
            verification.setStatus(verificationStatus.VERIFIED);
            verificationRepo.save(verification);


        }
        catch (ClassNotFoundException|IOException e) {
            logger.error("Error verifying operation");
            throw new InternetBankingException("Failed to verify the operation");
        }
        return messageSource.getMessage("verification.verify", null, locale);
    }


    @Override
    public VerificationDTO getVerification(Long id) {
        return convertEntityToDTO(verificationRepo.findOne(id));
    }



    public VerificationDTO convertEntityToDTO(Verification verification) {
        return modelMapper.map(verification, VerificationDTO.class);
    }

    public List<VerificationDTO> convertEntitiesToDTOs(Iterable<Verification> verifications) {
        List<VerificationDTO> verificationDTOArrayList = new ArrayList<>();
        for (Verification verification : verifications) {
            VerificationDTO verificationDTO = convertEntityToDTO(verification);
            verificationDTOArrayList.add(verificationDTO);
        }
        return verificationDTOArrayList;
    }


    @Override
    public Page<VerificationDTO> getMakerCheckerPending(Pageable pageDetails, User createdBy) {
        Page<Verification> page = verificationRepo.findByStatusAndInitiatedBy(verificationStatus.PENDING, createdBy.getUserName(), pageDetails);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<VerificationDTO> pageImpl = new PageImpl<VerificationDTO>(dtOs, pageDetails, t);
        return pageImpl;

    }

    @Override
    public Page<VerificationDTO> getPendingOperations(String operation,User user,Pageable pageable) {
        Page<Verification> page = verificationRepo.findByOperationAndInitiatedByAndUserTypeAndStatus(operation,user.getUserName(),user.getUserType(),verificationStatus.PENDING,pageable);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<VerificationDTO> pageImpl = new PageImpl<VerificationDTO>(dtOs, pageable, t);
        return pageImpl;

    }

    @Override
    public long getTotalNumberPending(User user) {

        long totalNumberPending = verificationRepo.countByInitiatedByAndUserTypeAndStatus(user.getUserName(), user.getUserType(), verificationStatus.PENDING);
        return totalNumberPending;
    }


    @Override
    public int getTotalNumberForVerification(User user) {

        List<Verification> b = verificationRepo.findVerificationForUser(user.getUserName(), user.getUserType());
        return b.size();
    }



    @Override
    public Page<PendingVerification> getPendingVerifications(User user, Pageable pageable) {
        Page<Verification> verifications = verificationRepo.findByStatusAndInitiatedByAndUserType(verificationStatus.PENDING, user.getUserName(), user.getUserType(), pageable);
        Set<String> entities = new HashSet<>();
        List<PendingVerification> pendingVerifications = new ArrayList<>();
        for (Verification verification : verifications) {
            entities.add(verification.getOperation());
        }
        for (String entity : entities) {
            int countOp = 0;
            PendingVerification pendingVerification = new PendingVerification();

            for (Verification verification : verifications) {
                if (entities.equals(verification.getOperation())) {
                    countOp += 1;
                    pendingVerification.setId(verification.getId());
                }
            }
            pendingVerification.setEntityName(entity);
            pendingVerification.setNumPending(countOp);
            pendingVerifications.add(pendingVerification);
        }
        PageImpl<PendingVerification> pendVerifications = new PageImpl<PendingVerification>(pendingVerifications, pageable, verifications.getTotalElements());
        return pendVerifications;
    }


    public List<VerificationDTO> getVerificationsForUser(User user) {
        List<Verification> verifications = verificationRepo.findVerificationForUser(user.getUserName(), user.getUserType());
        return convertEntitiesToDTOs(verifications);

    }


    public List<VerificationDTO> getPendingForUser(User user) {
        List<Verification> verifications = verificationRepo.findByInitiatedByAndUserType(user.getUserName(), user.getUserType());
        return convertEntitiesToDTOs(verifications);

    }



    private String getCurrentUserName(){
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        return user.getUserName();
    }
}
