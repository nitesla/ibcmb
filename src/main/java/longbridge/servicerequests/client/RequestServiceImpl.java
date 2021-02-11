package longbridge.servicerequests.client;

import longbridge.exception.InternetBankingException;
import longbridge.models.Email;
import longbridge.models.OperationsUser;
import longbridge.models.User;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.servicerequests.config.RequestConfig;
import longbridge.servicerequests.config.RequestConfigService;
import longbridge.services.UserGroupMessageService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class RequestServiceImpl implements RequestService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RequestConfigService requestConfigService;
    private final TemplateEngine templateEngine;
    private final RequestRepository requestRepo;
    private final UserGroupMessageService groupMessageService;
    private final String SRTEMPLATE = "srtemplate";


    public RequestServiceImpl(RequestConfigService requestConfigService, TemplateEngine templateEngine, @Qualifier("msgTemplate") SpringResourceTemplateResolver templateResolver, RequestRepository requestRepo, UserGroupMessageService groupMessageService) {
        this.requestConfigService = requestConfigService;
        this.requestRepo = requestRepo;
        this.groupMessageService = groupMessageService;
        this.templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }


    @Override
    public void addRequest(AddRequestCmd request) {
        RequestConfig requestConfig = requestConfigService.getRequestConfig(request.getServiceReqConfigId());
        User currentUser = getCurrentUser();
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setRequestName(requestConfig.getName());
        Date reqDate = new Date();
        serviceRequest.setDateRequested(reqDate);
        serviceRequest.setCurrentStatus("PENDING");
        serviceRequest.setBody(request.getBody());
        logger.info("Saving service request {} {} ", requestConfig.getName(), currentUser.getUserName());
        requestRepo.save(serviceRequest);
        logger.trace("{}", serviceRequest);
        Context context = new Context();
        context.setVariable("date", reqDate);
        context.setVariable("name", requestConfig.getName());
        context.setVariable("user", currentUser);
        context.setVariable("request", requestDetails(request.getBody()));

        String message = templateEngine.process(SRTEMPLATE, context);
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

    private Map<String, String> requestDetails(String json) {
        JSONObject jObject = new JSONObject(json);
        Map map = new HashMap();
        for (Iterator<String> it = jObject.keys(); it.hasNext(); ) {
            String key = it.next();
            map.put(key, jObject.get(key));
        }
        return map;
    }

    @Override
    public ServiceRequestDTO getRequest(Long id) {
        return requestRepo.findById(id).map(ServiceRequestDTO::new).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public ServiceRequestDTO getRequestByName(String name) {
        return requestRepo.findFirstByRequestName(name).map(ServiceRequestDTO::new).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Page<ServiceRequestDTO> getUserRequests(Pageable pageDetails) {
        User currentUser = getCurrentUser();
        return requestRepo.findForUser(currentUser.getUserType(), currentUser.getId()).map(ServiceRequestDTO::new);
    }

    @Override
    public Page<ServiceRequestDTO> getOpRequests(OperationsUser opsUser, Pageable pageDetails) {
        return null;
    }
}
