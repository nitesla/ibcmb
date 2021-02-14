package longbridge.servicerequests.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.config.IbankingContext;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.servicerequests.config.RequestConfig;
import longbridge.servicerequests.config.RequestConfigInfo;
import longbridge.servicerequests.config.RequestConfigService;
import longbridge.services.UserGroupMessageService;
import longbridge.services.UserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RequestConfigService requestConfigService;
    private final TemplateEngine templateEngine;
    private final RequestRepository requestRepo;
    private final UserGroupMessageService groupMessageService;
    private final UserGroupService groupService;
    private final String SRTEMPLATE = "srtemplate";
    private final ObjectMapper objectMapper;


    public RequestServiceImpl(RequestConfigService requestConfigService, RequestRepository requestRepo, UserGroupMessageService groupMessageService, UserGroupService groupService, ObjectMapper objectMapper) {
        this.requestConfigService = requestConfigService;
        this.requestRepo = requestRepo;
        this.groupMessageService = groupMessageService;
        this.groupService = groupService;
        this.objectMapper = objectMapper;
        this.templateEngine = initEngine();

    }

    private TemplateEngine initEngine() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(IbankingContext.getApplicationContext());
        templateResolver.setPrefix("classpath:/messaging/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        TemplateEngine t = new SpringTemplateEngine();
        t.setTemplateResolver(templateResolver);
        return t;
    }

    @Override
    public void addRequest(AddRequestCmd request) {
        RequestConfig requestConfig = requestConfigService.getRequestConfig(request.getServiceReqConfigId());
        User currentUser = getCurrentUser();
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setRequestName(requestConfig.getName());
        if (currentUser.getUserType() == UserType.RETAIL) {
            serviceRequest.setEntityId(currentUser.getId());
            serviceRequest.setRequester(currentUser.getUserName());
            serviceRequest.setUserType(UserType.RETAIL);
        } else if (currentUser.getUserType() == UserType.CORPORATE) {
            CorporateUser corporateUser = (CorporateUser) currentUser;
            serviceRequest.setEntityId(corporateUser.getCorporate().getId());
            serviceRequest.setRequester(corporateUser.getUserName());
            serviceRequest.setUserType(UserType.CORPORATE);
        }
        serviceRequest.setServiceReqConfigId(request.getServiceReqConfigId());

        Date reqDate = new Date();
        serviceRequest.setDateRequested(reqDate);
        try {
            serviceRequest.setData(objectMapper.writeValueAsString(request.getBody()));
        } catch (JsonProcessingException e) {
            logger.error("Error serializing request {}", e.getMessage());
            throw new ApplicationContextException("Could not complete the request");
        }
        logger.info("Saving service request {} {} ", requestConfig.getName(), currentUser.getUserName());

        logger.trace("{}", serviceRequest);
        Context context = new Context();
        context.setVariable("date", reqDate);
        context.setVariable("user", currentUser);
        context.setVariable("request", request.getBody());
        context.setVariable("config", requestConfig);
        String message = templateEngine.process(SRTEMPLATE, context);
        serviceRequest.setFormatted(message);
        requestRepo.save(serviceRequest);

        Email email = new Email.Builder()
                .setSubject(serviceRequest.getRequestName())
                .setBody(message)
                .build();
        if (requestConfig.getGroupId() != null) {
            groupMessageService.send(requestConfig.getGroupId(), email);
        }
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();
            return currentUser.getUser();
        }

        throw new InternetBankingException("Unauthorized user");
    }


    @Override
    public ServiceRequestDTO getRequest(Long id) {
        return requestRepo.findById(id).map(ServiceRequestDTO::new).orElseThrow(EntityNotFoundException::new);
    }


    @Override
    public Page<ServiceRequestDTO> getUserRequests(Pageable pageDetails) {
        User currentUser = getCurrentUser();
        Long entId = null;
        if (currentUser.getUserType() == UserType.RETAIL) {
            entId = currentUser.getId();
        } else if (currentUser.getUserType() == UserType.CORPORATE) {
            CorporateUser corporateUser = (CorporateUser) currentUser;
            entId = corporateUser.getCorporate().getId();
        }
        return requestRepo.findForUser(currentUser.getUserType(), entId, pageDetails).map(ServiceRequestDTO::new);
    }

    @Override
    public Page<ServiceRequestDTO> getOpRequests(OperationsUser opsUser, Pageable pageDetails) {
        List<Long> groups = groupService.getGroups(opsUser);
        List<Long> configIds = requestConfigService.getRequestConfigByGroup(groups).stream()
                .map(RequestConfigInfo::getId).collect(Collectors.toList());
        return requestRepo.findByConfigIds(configIds,pageDetails).map(ServiceRequestDTO::new);
    }

    @Override
    public RequestStats getOpRequestStats(OperationsUser opsUser) {
        List<Long> groups = groupService.getGroups(opsUser);
        List<Long> configIds = requestConfigService.getRequestConfigByGroup(groups).stream()
                .map(RequestConfigInfo::getId).collect(Collectors.toList());

        Integer all = requestRepo.allRequest(configIds);
        Integer untreated = requestRepo.unattendRequest(configIds);
        return new RequestStats(all,untreated);
    }

    @Override
    public ServiceRequestDTO.CommentDTO addRequestComment(AddCommentCmd commentCmd) {
        OperationsUser currentUser = (OperationsUser)getCurrentUser();
        ServiceRequest request = requestRepo.getOne(commentCmd.getRequestId());
        Comment comment = new Comment();
        comment.setComments(commentCmd.getComments());
        comment.setCreatedOn(new Date());
        comment.setStatus(commentCmd.getStatus());
        comment.setCreatedBy(currentUser);
        request.addComments(comment);
        request.setCurrentStatus(commentCmd.getStatus());
        requestRepo.save(request);
        return ServiceRequestDTO.makeComment(comment);
    }
}
