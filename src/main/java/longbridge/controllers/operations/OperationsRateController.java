package longbridge.controllers.operations;


import longbridge.dtos.InvestmentRateDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.InvestmentRate;
import longbridge.services.InvestmentRateService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Controller
    @RequestMapping("ops/rate")
    public class OperationsRateController{

    @Autowired
    MessageSource messageSource;

    @Autowired
    InvestmentRateService rateService;
    final Logger logger= LoggerFactory.getLogger(this.getClass());


        @GetMapping()
        public String getRateView() {
            return "ops/rate/view";
        }

        @GetMapping(path = "/all")
        public @ResponseBody
        DataTablesOutput<InvestmentRateDTO> getAllRates(DataTablesInput input) {

            Pageable pageable = DataTablesUtils.getPageable(input);
            List<String> investmentName=rateService.getDistinctInvestments();
            List<InvestmentRateDTO> rateDTO=new ArrayList<>();
            investmentName.forEach(i-> {
                InvestmentRateDTO rate=new InvestmentRateDTO();
                rate.setInvestmentName(i);
                rateDTO.add(rate);
            });
            DataTablesOutput<InvestmentRateDTO> out = new DataTablesOutput<>();
            PageImpl<InvestmentRateDTO> page=new PageImpl<>(rateDTO,pageable,rateDTO.size());
            out.setDraw(input.getDraw());
            out.setData(page.getContent());
            out.setRecordsFiltered(page.getTotalElements());
            out.setRecordsTotal(page.getTotalElements());
            return out;
        }


        @GetMapping("/new")
        public String addRate(InvestmentRateDTO investmentRateDTO) {
            return "ops/rate/add";
        }

    @PostMapping("/add")
    public String createRate(@ModelAttribute("investmentRateDTO") @Valid InvestmentRateDTO investmentRateDTO, BindingResult result, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "ops/rate/add";
        }


        try {
            String message = rateService.addRate(investmentRateDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/rate";
        }
        catch (InternetBankingException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            logger.error("Error creating rate",ibe);
            return "ops/rate/add";
        }
    }

    @GetMapping("/type/{type}")
    public String getInvestmentType(@PathVariable String type, Model model) {
        model.addAttribute("investmentNameDTO", type);
        return "ops/rate/type-rate-view";
    }

    @GetMapping(path = "/{type}")
    public @ResponseBody DataTablesOutput<InvestmentRate> getAllRateType(DataTablesInput input,@PathVariable String type) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<InvestmentRate> investmentRates = rateService.getAllRatesByInvestmentName(type,pageable);
        DataTablesOutput<InvestmentRate> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(investmentRates.getContent());
        out.setRecordsFiltered(investmentRates.getTotalElements());
        out.setRecordsTotal(investmentRates.getTotalElements());
        return out;
    }

    @GetMapping("/type/{type}/new")
    public String addInvestmentRate(@PathVariable String type, Model model) {
        InvestmentRateDTO investmentRateDTO = new InvestmentRateDTO();
        investmentRateDTO.setInvestmentName(type);
        model.addAttribute("investmentRateDTO", investmentRateDTO);
        return "ops/rate/add";

    }

    @GetMapping("/{id}/edit")
    public String editCode(@PathVariable Long id, Model model) {
        InvestmentRateDTO investmentRateDTO= rateService.getInvestmentRate(id);
        model.addAttribute("investmentRateDTO", investmentRateDTO);
        return "ops/rate/edit";
    }

    @PostMapping("/update")
    public String updateRate(@ModelAttribute("investmentRateDTO") @Valid InvestmentRateDTO investmentRateDTO, BindingResult result, Principal principal,RedirectAttributes redirectAttributes,Locale locale) {
        if(result.hasErrors()){
            logger.error("Error occurred creating rate{}", result.toString());
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "ops/rate/edit";

        }
        try {
            String message = rateService.updateRate(investmentRateDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/rate";
        }
        catch (InternetBankingException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            logger.error("Error updating Rate",ibe);
            return "ops/rate/edit";

        }
    }

    @GetMapping("/{rateId}/delete")
    public String deleteCode(@PathVariable Long rateId, RedirectAttributes redirectAttributes) {
        try {
            String message = rateService.deleteRate(rateId);
            redirectAttributes.addFlashAttribute("message", message);
        }
        catch (InternetBankingException ibe){
            logger.error("Error deleting Code",ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

        }
        return "redirect:/ops/rate/all";
    }

}


