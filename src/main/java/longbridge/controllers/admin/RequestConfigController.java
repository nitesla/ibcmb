package longbridge.controllers.admin;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.UserGroupDTO;
import longbridge.exception.InternetBankingException;
import longbridge.servicerequests.config.*;
import longbridge.services.CodeService;
import longbridge.services.UserGroupService;
import longbridge.utils.DataTablesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/requests/config")
public class RequestConfigController {
    private final RequestConfigService configService;
    private final UserGroupService groupService;
    private final CodeService codeService;
    private final MessageSource messages;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public RequestConfigController(RequestConfigService configService, UserGroupService groupService, CodeService codeService, MessageSource messages) {
        this.configService = configService;
        this.groupService = groupService;
        this.codeService = codeService;
        this.messages = messages;
    }

    @GetMapping("{id}/view")
    public String getConfig(@PathVariable Long id, Model model) {
        RequestConfig cfg = configService.getRequestConfig(id);
        model.addAttribute("cfg", cfg);
        return "/adm/reqconfig/view";
    }

    @GetMapping("{id}/edit")
    public String editConfig(@PathVariable Long id, Model model) {
        RequestConfig cfg = configService.getRequestConfig(id);
        model.addAttribute("config", cfg);
        model.addAttribute("oldentity", true);
        return "/adm/reqconfig/edit";
    }

    @GetMapping("/new")
    public String newConfig(Model model) {
        RequestConfig cfg = new RequestConfig();
        model.addAttribute("config", cfg);
        return "/adm/reqconfig/edit";
    }

    @GetMapping
    public String getRequestCfgs() {
        return "/adm/reqconfig/view";
    }

    @GetMapping("/{id}/delete")
    public String deleteRequest(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            String message = configService.deleteRequest(id);
            logger.info("the Id is {}", id);
            redirectAttributes.addFlashAttribute("message", message);

        }
        catch (InternetBankingException ibe){
            logger.error("Error deleting setting",ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

        }
        return "redirect:/admin/requests/config";
    }


    @PostMapping("{id}")
    public String saveConfig(@ModelAttribute("config") @Valid UpdateRequestConfigCmd cmd, BindingResult result, RedirectAttributes redirectAttributes, Model model, Locale locale) {
        if (result.hasErrors()) {
            logger.trace("Error occurred creating code {}", result);
            model.addAttribute("oldentity", true);
            return "/adm/reqconfig/edit";
        }
        configService.updateRequestConfig(cmd);
        redirectAttributes.addFlashAttribute("message", messages.getMessage("request.config.modify.ok",null,locale));
        return "redirect:/admin/requests/config";
    }

    @PostMapping
    public String createConfig(@ModelAttribute("config") @Valid AddRequestConfigCmd cmd, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            logger.trace("Error occurred creating code {}", result);
            return "/adm/reqconfig/edit";
        }
        configService.add(cmd);
        redirectAttributes.addFlashAttribute("message", messages.getMessage("request.config.added.ok", null, locale));
        return "redirect:/admin/requests/config";
    }

    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<RequestConfig> getConfigs(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);

        Page<RequestConfig> cfgs = null;
        if (StringUtils.isNoneBlank(input.getSearch().getValue())) {
            cfgs = configService.getRequestConfigs(input.getSearch().getValue(), pageable);
        } else {
            cfgs = configService.getRequestConfigs(pageable);
        }
        DataTablesOutput<RequestConfig> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(cfgs.getContent());
        out.setRecordsFiltered(cfgs.getTotalElements());
        out.setRecordsTotal(cfgs.getTotalElements());
        return out;
    }


    @ModelAttribute
    public void init(Model model) {
        Iterable<CodeDTO> requestTypes = codeService.getCodesByType("REQUEST_TYPE");
        Iterable<UserGroupDTO> groups = groupService.getGroups();
        model.addAttribute("requestTypes", requestTypes);

        List<Pair<String, String>> fieldTypes = EnumSet.allOf(RequestField.Type.class).stream().map(this::convert).collect(Collectors.toList());
        model.addAttribute("fieldTypes", RequestField.Type.values());
        model.addAttribute("groups", groups);
    }

    private Pair<String,String> convert(RequestField.Type e){
        return Pair.of(e.name(),e.toString());
    }


}
