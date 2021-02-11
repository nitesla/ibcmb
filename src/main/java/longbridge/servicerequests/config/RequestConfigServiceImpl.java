package longbridge.servicerequests.config;

import longbridge.dtos.UserGroupDTO;
import longbridge.services.UserGroupService;
import longbridge.utils.Verifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestConfigServiceImpl implements RequestConfigService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RequestConfigRepository configRepo;
    private final UserGroupService groupService;


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
        return configRepo.save(update(config, cmd));
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
    public List<RequestConfig> getRequestConfigs() {
        return configRepo.findAll();
    }

    @Override
    public Page<RequestConfig> getRequestConfigs(String pattern, Pageable pageDetails) {
        return configRepo.findUsingPattern(pattern, pageDetails);
    }

}
