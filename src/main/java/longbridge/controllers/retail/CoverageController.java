package longbridge.controllers.retail;


import longbridge.dtos.CoverageDetailsDTO;
import longbridge.services.CoverageService;
import longbridge.services.RetailUserService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/retail/coverage")
public class CoverageController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RetailUserService retailUserService;
    @Autowired
    private CoverageService coverageService;

    @GetMapping("/view/{customerId}/{coverageName}")
    public String viewCoverageDetails(@PathVariable String customerId, @PathVariable String coverageName, Principal principal, Model model) {

        Map<String, List<String>> coverageDetails = coverageService.getCoverageDetails(coverageName, customerId);

//        model.addAttribute("coverageHeaders",coverageDetails.keySet());
//        List<String> coverageValues = coverageDetails.values().stream().map(values -> values.stream().collect(Collectors.joining("\n\n\n\n"))).collect(Collectors.toList());
        model.addAttribute("coverageDetails", coverageDetails);
        model.addAttribute("coverageName", coverageName.toUpperCase());
        return "cust/coverage/index";
    }



//    @GetMapping("/getViewData/{customerId}")
//    @ResponseBody
//    public DataTablesOutput<CoverageDetailsDTO> getViewDetails(@PathVariable String customerIds, String coverage, DataTablesInput input){
//        Pageable pageable = DataTablesUtils.getPageable(input);
//        Page<CoverageDetailsDTO> coverageDetailsDTO = null;
//        coverageDetailsDTO=coverageService.getCoverages(coverage,customerIds,pageable);
//        DataTablesOutput<CoverageDetailsDTO> out = new DataTablesOutput<>();
//        out.setDraw(input.getDraw());
//        out.setData(coverageDetailsDTO.getContent());
//        out.setRecordsFiltered(coverageDetailsDTO.getTotalElements());
//        out.setRecordsTotal(coverageDetailsDTO.getTotalElements());
//        return out;
//    }
//
}
