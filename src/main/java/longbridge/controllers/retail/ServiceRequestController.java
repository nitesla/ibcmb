package longbridge.controllers.retail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;
import longbridge.dtos.ServiceRequestDTO;
import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.CodeService;
import longbridge.services.RequestService;
import longbridge.services.ServiceReqConfigService;
import longbridge.utils.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

/**
 * Created by Fortune on 4/5/2017.
 */

@Controller
@RequestMapping("/retail/requests")
public class ServiceRequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ServiceReqConfigService serviceReqConfigService;

    @Autowired
    private CodeService codeService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RetailUserRepo userRepo;

    private RetailUser retailUser = new RetailUser();//TODO user must be authenticated

    @GetMapping
    public String getServiceRequests(Model model) {
        Iterable<ServiceReqConfigDTO> requestList = serviceReqConfigService.getServiceReqConfigs();
        model.addAttribute("requestList", requestList);
        return "cust/servicerequest/list";
    }

    @PostMapping
    public String processRequest(@ModelAttribute("requestDTO") ServiceRequestDTO requestDTO, WebRequest httpRequest, RedirectAttributes redirectAttributes) {

        String requestBody = requestDTO.getRequestName();
        Long serviceReqConfigId =0L;
        ObjectMapper objectMapper = new ObjectMapper();
        ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO();
        try {
//            JsonNode jsonNode =objectMapper.readTree(requestBody);
            ArrayList<NameValue> myFormObjects = objectMapper.readValue(requestBody, new TypeReference<ArrayList<NameValue>>() {
            });
            Iterator<NameValue> iterator = myFormObjects.iterator();
            while (iterator.hasNext()) {
                NameValue nameValue = iterator.next();
                String name = nameValue.getName();
                String value = nameValue.getValue();
                if (name.equals("requestName")) {
                    serviceRequestDTO.setRequestName(value);
                    iterator.remove();
                }
                if(name.equals("serviceReqConfigId")){
                    serviceReqConfigId = Long.parseLong(nameValue.getValue());
                    iterator.remove();
                }
            }
            ServiceReqConfigDTO serviceReqConfigDTO = serviceReqConfigService.getServiceReqConfig(serviceReqConfigId);
            List<ServiceReqFormFieldDTO> formFieldDTOs = serviceReqConfigDTO.getFormFields();


            logger.info("My form size {}, fieldDTOs size {}",myFormObjects.size(),formFieldDTOs.size());


            if(myFormObjects.size()==formFieldDTOs.size()){
                int num=myFormObjects.size();

                logger.info("The form fields are equal with size {}",num);

                for(int i=0;i<num;i++){
                  if(myFormObjects.get(i).getName().equals(formFieldDTOs.get(i).getFieldName())){
                      myFormObjects.get(i).setName(formFieldDTOs.get(i).getFieldLabel());
                  }
              }
            }

            requestBody =  objectMapper.writeValueAsString(myFormObjects);

            retailUser = userRepo.findOne(1L);
            serviceRequestDTO.setBody(requestBody);
            serviceRequestDTO.setRequestStatus("S");
            serviceRequestDTO.setUser(retailUser);
            serviceRequestDTO.setDateRequested(new Date());
            requestService.addRequest(serviceRequestDTO);

           logger.info("The request body: {}",requestBody );
        } catch (Exception e) {
            throw new RuntimeException("Error adding request");
        }


        redirectAttributes.addFlashAttribute("message", "Request sent successfully");
//        logger.info("The received data: {}", requestDTO.getRequestName());
        return "redirect:/retail/requests";

    }

//    @PostMapping
//    public String createServiceRequest(@ModelAttribute("requestForm") ServiceRequestDTO requestDTO, BindingResult result, Model model){
//        if(result.hasErrors()){
//            return "cust/servicerequest/add";
//        }
//
//            retailUser = userRepo.findOne(1l);
//
//        logger.info(requestDTO.toString());
//        requestDTO.setUser(retailUser);
//        requestDTO.setDateRequested(new Date());
//        requestService.addRequest(requestDTO);
//        model.addAttribute("success", "Request added successfully");
//        return "redirect:/retail/requests";
//    }

    @GetMapping("/{reqId}")
    public String makeRequest(@PathVariable Long reqId, Model model) {
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfig(reqId);
        for (ServiceReqFormFieldDTO field : serviceReqConfig.getFormFields()) {
            if (field.getFieldType() != null && field.getFieldType().equals("CODE")) {
                field.setCodeDTOs(codeService.getCodesByType(field.getTypeData()));
            }

            if (field.getFieldType() != null && field.getFieldType().equals("LIST")) {
                String list = field.getTypeData();
                String myList[] = list.split(",");
                model.addAttribute("fixedList", myList);
            }
        }
        //System.out.println(serviceReqConfig);
        model.addAttribute("requestConfig", serviceReqConfig);
        return "cust/servicerequest/add";
    }

}
