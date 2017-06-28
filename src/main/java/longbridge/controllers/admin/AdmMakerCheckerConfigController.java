package longbridge.controllers.admin;

import longbridge.dtos.MakerCheckerDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.MakerChecker;
import longbridge.services.MakerCheckerServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Created by chiomarose on 16/06/2017.
 */
@Controller
@RequestMapping(value = "/admin/checker")
public class AdmMakerCheckerConfigController {

    @Autowired
    private MessageSource messageSource;

//    @Autowired
//    private RoleService roleService;

    @Autowired
    MakerCheckerServiceConfig makerCheckerService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping()
    public String listModel(Model model)
    {

        return "adm/makercheckerconfig/view";
    }


    @GetMapping("/new")
    public String addMakerChecker(Model model)
    {
      //  model.addAttribute("checker", new MakerCheckerDTO());
        return "adm/makercheckerconfig/add";
    }



    @PostMapping("/save")
    public String createPermission(@ModelAttribute("checker") MakerCheckerDTO makerChecker, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors())
        {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "adm/makercheckerconfig/add";
        }
        try {
            String message = makerCheckerService.saveMakerChecker(makerChecker);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/checker";
        }
        catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", messageSource.getMessage(ibe.getMessage(),null, locale)));
            logger.error("Error creating maker checker configuration", ibe);
            return "adm/makercheckerconfig/add";
        }
    }
//    @GetMapping("/{id}/edit")
//    public String editSetting(@PathVariable Long id, Model model) {
////        SettingDTO dto = makerCheckerService.
////        model.addAttribute("setting", dto);
////        return "adm/setting/edit";
//    }


    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<MakerChecker> getAllEntities(DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<MakerChecker> makerCheckers= makerCheckerService.getEntities(pageable);
        DataTablesOutput<MakerChecker> out = new DataTablesOutput<MakerChecker>();
        out.setDraw(input.getDraw());
        out.setData(makerCheckers.getContent());
        out.setRecordsFiltered(makerCheckers.getTotalElements());
        out.setRecordsTotal(makerCheckers.getTotalElements());
        return out;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<HttpStatus> changeMakerCheckerEntry(@RequestBody MakerChecker makerChecker)
    {
        makerCheckerService.updateMakerChecker(makerChecker);
        ResponseEntity<HttpStatus> resp = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return resp;
    }





}
