package longbridge.servicerequests.config;

import longbridge.dtos.UserGroupDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.services.UserGroupService;
import longbridge.utils.Verifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class RequestConfigServiceImpl implements RequestConfigService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RequestConfigRepository configRepo;
    private final UserGroupService groupService;

    @Autowired
    private MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();


    @Autowired
    public RequestConfigServiceImpl(RequestConfigRepository configRepo, UserGroupService groupService) {
        this.configRepo = configRepo;
        this.groupService = groupService;
    }


    @Override
    @Verifiable(operation = "CREATE_REQUEST_CONFIG", description = "Create new Request Config")
    public RequestConfig add(AddRequestConfigCmd cmd) {

        return configRepo.save(update(new RequestConfig(), cmd));
    }

    @Override
    @Verifiable(operation = "MODIFY_REQUEST_CONFIG", description = "Modify existing Request Config")
    public RequestConfig updateRequestConfig(UpdateRequestConfigCmd cmd) {
        RequestConfig config = configRepo.findById(cmd.getId()).orElseThrow(EntityNotFoundException::new);
        if(config.isSystem()){
            cmd.setFields(Collections.emptyList());
            cmd.setType(config.getType());
            cmd.setName(config.getName());
        }
        return configRepo.save(update(config, cmd));
    }

    @Override
    @Verifiable(operation="DELETE_REQUEST_CONFIG",description="Delete existing Request Config")
    public String deleteRequest(Long id)  {
        try {
            RequestConfig config = configRepo.findById(id).orElseThrow(EntityNotFoundException::new);
            configRepo.delete(config);
            return messageSource.getMessage("request.delete.success", null, locale);
        }
        catch (VerificationInterruptedException e){
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("request.delete.failure", null, locale),e);
        }
    }

    private RequestConfig update(RequestConfig config, RequestConfigCmd cmd) {
        config.setName(cmd.getName());
        config.setType(cmd.getType());
        config.setAuthRequired(cmd.isAuthRequired());
        config.setDescription(cmd.getDescription());
        //check group
        UserGroupDTO group = groupService.getGroup(cmd.getGroupId());
        //
        config.setGroupId(cmd.getGroupId());
        List<RequestField> fieldSet = cmd.getFields().stream().map(this::convert).collect(Collectors.toList());
        config.setFields(fieldSet);
        return config;
    }

    private RequestField convert(SRFieldDTO dto){
        RequestField requestField = new RequestField();
        requestField.setType(dto.getType());
        requestField.setData(dto.getData());
        requestField.setLabel(dto.getLabel());
        requestField.setName(dto.getName());
        return requestField;
    }

    @Override
    public RequestConfig getRequestConfig(Long id) {
        return configRepo.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public RequestConfig getRequestConfigByName(String name) {
        return configRepo.findFirstByName(name);
    }

    @Override
    public Page<RequestConfig> getRequestConfigs(Pageable pageDetails) {
        return configRepo.findAll(pageDetails);
    }

    @Override
    public List<RequestConfigInfo> getRequestConfigs() {
        return configRepo.findAll().stream().map(cfg->
            new RequestConfigInfo(cfg.getId(),cfg.getName(),cfg.getDescription(),cfg.isSystem()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<RequestConfig> getRequestConfigs(String pattern, Pageable pageDetails) {
        return configRepo.findUsingPattern(pattern, pageDetails);
    }

    @Override
    public List<RequestConfigInfo> getRequestConfigByGroup(List<Long> groups) {
        return configRepo.findByGroupIdIn(groups).stream().map(cfg ->
                new RequestConfigInfo(cfg.getId(), cfg.getName(), cfg.getDescription(), cfg.isSystem()))
                .collect(Collectors.toList());
    }
}
