package longbridge.controllers.admin;


import longbridge.dtos.CorporateDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.GreetingDTO;
import longbridge.dtos.RetailUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.services.CodeService;
import longbridge.services.CorporateUserService;
import longbridge.services.GreetingService;
import longbridge.services.RetailUserService;
import longbridge.utils.DataTablesUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

@Controller
@RequestMapping("/admin/greetings")
public class AdmGreetingController  {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GreetingService greetingService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CodeService codeService;

    @Autowired
    RetailUserService retailUserService;

    @Autowired
    CorporateUserService corporateUserService;

    @Value("greetingImage.path")
    private String GREETING_IMAGE_FOLDER;
    
    private static String INVALID="invalid";

    String addGreetingPage = "adm/greeting/add";

    String editGreetingPage = "adm/greeting/edit";
    String greetingViewPage = "redirect:/admin/greetings";

    @GetMapping()
    public String listGreetings()
    {
        return "adm/greeting/view";
    }


    @GetMapping(path = "/all")
    @ResponseBody
    public DataTablesOutput<GreetingDTO> getAllGreetings(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<GreetingDTO> greetingDto = greetingService.getGreetingDTOs(pageable);
        DataTablesOutput<GreetingDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(greetingDto.getContent());
        out.setRecordsFiltered(greetingDto.getTotalElements());
        out.setRecordsTotal(greetingDto.getTotalElements());
        return out;
    }


    @GetMapping("/new")
    public String addGreeting(@ModelAttribute("greetingDTO") GreetingDTO greetingDTO) {
       return addGreetingPage;
    }

    @PostMapping
    public String addGreeting(@ModelAttribute("greetingDTO")@Valid GreetingDTO greetingDTO, BindingResult result,
                              @RequestParam("greetingImage") MultipartFile greetingImage, RedirectAttributes redirectAttributes, Locale locale) throws IOException{


        if(greetingImage.isEmpty()){
            result.addError(new ObjectError("INVALID",messageSource.getMessage("form.fields.required.image",null,locale)));
            return addGreetingPage;

        }
        else if(greetingImage.getSize()>(1000000000)){
            result.addError(new ObjectError("INVALID",messageSource.getMessage("form.fields.image.size",null,locale)));
            return addGreetingPage;
        }

       else if(result.hasErrors()){
           logger.info("all errors {} ",result.getAllErrors());
            result.addError(new ObjectError("INVALID",messageSource.getMessage("form.fields.required",null,locale)));
            return addGreetingPage;
        }else {

            try {
                logger.info("Here");

                byte[] bytes = greetingImage.getBytes();
//                    Path path = Paths.get(GREETING_IMAGE_FOLDER + greetingImage.getOriginalFilename());
                String path = greetingImage.getOriginalFilename();
                logger.info("Here {}", path);
//                String mimeType = Files.probeContentType(path);
                String mimeType = path.substring(path.lastIndexOf(".") + 1);

                logger.info("here2 {}", mimeType);
                if((mimeType.equalsIgnoreCase("png"))||(mimeType.equals("jpg"))) {
//                    Files.write(path, bytes)
                    String addStatus = greetingService.addGreeting(greetingDTO);
                    redirectAttributes.addFlashAttribute("message", addStatus);
                    return "redirect:/admin/greetings/";
                }else{
                    result.addError(new ObjectError("INVALID",messageSource.getMessage("form.fields.image.type",null,locale)));
                    return addGreetingPage;
                }
            } catch (InternetBankingException | IOException i) {
                result.addError(new ObjectError("error", i.getMessage()));
                logger.error("Error creating greeting", i);
                return addGreetingPage;
            }
        }
    }


    @GetMapping("/{id}/edit")
    public String editGreeting(@PathVariable Long id, Model model) {
        GreetingDTO dto = greetingService.getGreetingDTO(id);
        model.addAttribute("greetingDTO", dto);
        return editGreetingPage;
    }

    @GetMapping("/{id}/delete")
    public String deleteGreeting(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String message = greetingService.deleteGreeting(id);
            redirectAttributes.addFlashAttribute("message", message);

        }
        catch (InternetBankingException ibe){
            logger.error("Error deleting setting",ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

        }
        return greetingViewPage;
    }


    @PostMapping("/update")
    public String updateGreeting(@ModelAttribute("greetingDTO") @Valid GreetingDTO greetingDTO,
                                 @RequestParam("greetingImage") MultipartFile greetingImage,BindingResult result,RedirectAttributes redirectAttributes,Locale locale) {

        if(greetingImage.getSize()>10000000){
    result.addError(new ObjectError("INVALID",messageSource.getMessage("form.fields.image.size",null,locale)));
    return editGreetingPage;

    }else if (result.hasErrors()){
           result.addError(new ObjectError("INVALID",messageSource.getMessage("form.fields.required",null,locale)));
           return editGreetingPage;

       }
        try {
            if(!greetingImage.isEmpty()){

                    byte[] bytes = greetingImage.getBytes();
                    Path path = Paths.get(GREETING_IMAGE_FOLDER + greetingImage.getOriginalFilename());
                String mimeType = Files.probeContentType(path);
                if((mimeType.equals("image/png"))||(mimeType.equals("image/jpg"))) {
                    Files.write(path, bytes);
                    String updateStatus = greetingService.updateGreeting(greetingDTO);
                    redirectAttributes.addFlashAttribute("message", updateStatus);
                    return greetingViewPage;
                }else{
                    result.addError(new ObjectError("INVALID",messageSource.getMessage("form.fields.image.type",null,locale)));
                    return addGreetingPage;
                }
            }else{
                String updateStatus = greetingService.updateGreeting(greetingDTO);
                redirectAttributes.addFlashAttribute("message", updateStatus);
                return greetingViewPage;


            }


        }
        catch (InternetBankingException | IOException ibe){
            logger.error("Error updating greeting {}",ibe);
            result.addError(new ObjectError("exception",ibe.getMessage()));
            return editGreetingPage;
       }
    }

    @GetMapping("/find")
    public String findRetailUser(Model model) {
        RetailUserDTO user = new RetailUserDTO();
        model.addAttribute("retailUser", user);
        return "/adm/greeting/add_retail";
    }

    @RequestMapping(path= "/find/users")
    public
    @ResponseBody
    DataTablesOutput<RetailUserDTO> getRetailUsers(DataTablesInput input, RetailUserDTO user) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<RetailUserDTO> retailUsers = retailUserService.findUsers(user,pageable);
        DataTablesOutput<RetailUserDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(retailUsers.getContent());
        logger.info("Users found: {}", retailUsers.getContent());
        out.setRecordsFiltered(retailUsers.getTotalElements());
        out.setRecordsTotal(retailUsers.getTotalElements());

        return out;
    }

    @GetMapping("/find/corp")
    public String findCorporateUser(Model model) {
        CorporateDTO user = new CorporateDTO();
        model.addAttribute("corporateDTO", user);
        return "/adm/greeting/add_corpuser";
    }

    @RequestMapping(path= "/find/corpusers")
    public
    @ResponseBody
    DataTablesOutput<CorporateUserDTO> getCorporateUsers(DataTablesInput input, CorporateDTO user) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorporateUserDTO> corporateUserDTOs = corporateUserService.getUsers(user,pageable);
        DataTablesOutput<CorporateUserDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(corporateUserDTOs.getContent());
        logger.info("Users found: {}", corporateUserDTOs.getContent());
        out.setRecordsFiltered(corporateUserDTOs.getTotalElements());
        out.setRecordsTotal(corporateUserDTOs.getTotalElements());

        return out;
    }


}
