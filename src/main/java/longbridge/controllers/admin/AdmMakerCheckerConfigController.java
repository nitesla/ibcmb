package longbridge.controllers.admin;

import longbridge.models.MakerChecker;
import longbridge.services.MakerCheckerServiceConfig;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import longbridge.utils.DataTablesUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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






    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<MakerChecker> getAllEntities(DataTablesInput input, @RequestParam("csearch") String search){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<MakerChecker> makerCheckers= null;
        if (StringUtils.isNoneBlank(search)) {
        	makerCheckers= makerCheckerService.findEntities(search,pageable);
		}else{
			makerCheckers= makerCheckerService.getEntities(pageable);
		}
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
        makerCheckerService.configureMakerChecker(makerChecker);
        ResponseEntity<HttpStatus> resp = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return resp;
    }





}
