package longbridge.controllers.operations;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.UnitDTO;
import longbridge.models.Unit;
import longbridge.services.CodeService;
import longbridge.services.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



/**
 * Created by Fortune on 4/26/2017.
 */

@Controller
@RequestMapping("/ops/units")

public class OpsUnitController {

    @Autowired
    private CodeService codeService;

    @Autowired
    UnitService unitService;


    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @GetMapping("/new")
    public String addUnit( Model model){
        model.addAttribute("unit", new Unit());
        Iterable<CodeDTO> units = codeService.getCodesByType("UNIT");
        model.addAttribute("units",units);
        return "ops/unit/add";
    }

    @PostMapping
    public String createUnit(@ModelAttribute("unit") UnitDTO unit, BindingResult result, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            return "ops/unit/add";
        }
        String unitName = codeService.getByTypeAndCode("UNIT",unit.getCode()).getDescription();
        unit.setName(unitName);
        unitService.addUnit(unit);
        redirectAttributes.addFlashAttribute("message","Unit personnel created successfully");
        return "redirect:/ops/units";
    }

    @GetMapping
    public String getUnits(Model model){
        return "ops/unit/view";
    }

    @GetMapping("/all")
    public @ResponseBody DataTablesOutput<UnitDTO> getUnits(DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<UnitDTO> units = unitService.getUnits(pageable);
        DataTablesOutput<UnitDTO> out = new DataTablesOutput<UnitDTO>();
        out.setDraw(input.getDraw());
        out.setData(units.getContent());
        out.setRecordsFiltered(units.getTotalElements());
        out.setRecordsTotal(units.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/edit")
    public String  editUnit(@PathVariable Long id, Model model){
        UnitDTO unit = unitService.getUnit(id);
        Iterable<CodeDTO> units = codeService.getCodesByType("UNIT");
        model.addAttribute("unit",unit);
        model.addAttribute("units",units);
        return "/ops/unit/edit";

    }

    @PostMapping("/update")
    public String updateUnit(@ModelAttribute("unit") UnitDTO unit, BindingResult result, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()) {
            return "ops/unit/new";
        }

        String unitName = codeService.getByTypeAndCode("UNIT",unit.getCode()).getDescription();
        unit.setName(unitName);
        unitService.updateUnit(unit);
        logger.info("Units received at server: {]",unit.toString());
        redirectAttributes.addFlashAttribute("message", "Unit updated successfully");
        return "redirect:/ops/units";
    }



    @GetMapping("/{id}/delete")
    public String deleteUnit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        unitService.deleteUnit(id);
        redirectAttributes.addFlashAttribute("message", "Unit deleted successfully");
        return "redirect:/ops/units";
    }


}


