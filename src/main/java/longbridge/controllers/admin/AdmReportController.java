package longbridge.controllers.admin;

import longbridge.config.IbankingContext;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.PermissionDTO;
import longbridge.dtos.ReportDTO;
import longbridge.dtos.ReportParameterDTO;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.FinancialInstitution;
import longbridge.services.CodeService;
import longbridge.services.MakerCheckerService;
import longbridge.services.ReportService;
import longbridge.services.RoleService;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.ReportUtils;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.io.*;
import java.net.URLConnection;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by mac on 29/01/2018.
 */
@Controller
@RequestMapping("admin/report")
public class AdmReportController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ReportUtils reportUtils;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private CodeService codeService;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Value("${report.logo.url}")
    private String imagePath;

    @GetMapping("/view")
    public String reportList(Model model) {
        return "adm/report/view";
    }

    @GetMapping("/view/data")
    public @ResponseBody
    DataTablesOutput<ReportDTO> getAllReports(DataTablesInput input, Model model, @RequestParam("csearch") String csearch) {
        logger.info("TO search {}", csearch);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ReportDTO> reportDTOS = null;
        if (csearch == null || csearch.equalsIgnoreCase("")) {
            reportDTOS = reportService.getReports(pageable);

        } else {
            reportDTOS = reportService.findReports(csearch, pageable);
        }
        DataTablesOutput<ReportDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(reportDTOS.getContent());
        out.setRecordsFiltered(reportDTOS.getTotalElements());
        out.setRecordsTotal(reportDTOS.getTotalElements());
        return out;
    }

    @GetMapping("/add")
    public String newReport(Model model) {
        List<PermissionDTO> permissionDTOS = roleService.findPermisionsByCategory("REPORT");
        model.addAttribute("permissionDTOS", permissionDTOS);
        return "adm/report/add";
    }

    @PostMapping("/add")
    public String newReport(@RequestParam("transferFile") MultipartFile file, WebRequest webRequest, Model model, Locale locale, Principal principal, HttpSession session) {
        List<PermissionDTO> permissionDTOS = roleService.findPermisionsByCategory("REPORT");
        model.addAttribute("permissionDTOS", permissionDTOS);
        if (file.isEmpty()) {
            model.addAttribute("failure", messageSource.getMessage("file.require", null, locale));
            return "adm/report/add";
        }
        logger.info("the file name {}", file.getOriginalFilename());

        if (file.getSize() >= 100000) {
            logger.info("the file size {}", file.getSize());
            model.addAttribute("failure", messageSource.getMessage("file.size.failed", null, locale));
            return "adm/report/add";
        }
        if (!reportService.isFileValid(file.getOriginalFilename())) {
            logger.debug("invalid file fomat");
            model.addAttribute("failure", messageSource.getMessage("file.format.failure", null, locale));
            return "adm/report/add";
        }

        String reportName = webRequest.getParameter("reportName");
        if (reportService.reportAlreadyExist(reportName)) {
            model.addAttribute("failure", messageSource.getMessage("report.already.exist", null, locale));
            return "adm/report/add";
        }
        long permissionId = NumberUtils.createLong(webRequest.getParameter("permissionId") );
        String sysFileName = ReportUtils.generateFileName();
        logger.info("the report name {} done by {} and permission {}", reportName, principal.getName(), permissionId);
        try {
            ReportDTO reportDTO = reportService.extractParameters(reportName, permissionId, principal.getName(), sysFileName, file.getOriginalFilename(), file);
            logger.info("the report detials {}", reportDTO);
            session.removeAttribute("reportDTO");
            session.setAttribute("reportDTO", reportDTO);
            model.addAttribute("reportDTO", reportDTO);
            return "adm/report/addParameter";
        } catch (JRException e) {
            model.addAttribute("failure", messageSource.getMessage("report.added.failed", null, locale));
            logger.error(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("failure", messageSource.getMessage("file.upload.failure", null, locale));
            logger.error(e.getMessage(), e);
        }

        return "adm/report/add";
    }

    @PostMapping("/update/file")
    public String updateReportWithFile(@RequestParam("transferFile") MultipartFile file, @ModelAttribute("reportDTO") @Valid ReportDTO reportDTO, BindingResult result,
                                       RedirectAttributes redirectAttributes, Locale locale, WebRequest webRequest, Model model, Principal principal, HttpSession session) {

        if (file.isEmpty()) {
            model.addAttribute("failure", messageSource.getMessage("file.require", null, locale));
            return "adm/report/add";
        }
        logger.info("the file name {}", file.getOriginalFilename());

        if (file.getSize() >= 100000) {
            logger.info("the file size {}", file.getSize());
            model.addAttribute("failure", messageSource.getMessage("file.size.failed", null, locale));
            return "adm/report/add";
        }


        if (!reportService.isFileValid(file.getOriginalFilename())) {
            logger.debug("invalid file fomat");
            model.addAttribute("failure", messageSource.getMessage("file.format.failure", null, locale));
            return "adm/report/add";
        }
        logger.info("report about update");
        logger.info("report dto {}", reportDTO);
        if (result.hasErrors()) {//TODO
            logger.info("an error occur {}", result.getAllErrors());
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.require", null, locale)));
            return "adm/report/view";
        }
        try {
            logger.info("is maker checker enabled {}", makerCheckerService.isEnabled("ADD_REPORT"));
            logger.info("the file saved temp");

            reportDTO.setJrxmlFile(file.getBytes());
            long permissionId = NumberUtils.createLong(webRequest.getParameter("permissionId"));
            reportDTO = reportService.updateReportParameters(reportDTO,permissionId , principal.getName(), file.getOriginalFilename(), file);
            logger.info("the updated report detials {}", reportDTO);
            session.removeAttribute("reportDTO");
            session.setAttribute("reportDTO", reportDTO);
            model.addAttribute("reportDTO", reportDTO);
            return "adm/report/addParameter";
        } catch (Exception e) {
            logger.error("Error loading file {}", e.getMessage());
            model.addAttribute("failure", messageSource.getMessage("file.upload.failure", null, locale));
        }
        return "adm/report/view";
    }

    @PostMapping("/new")
    public String newReport(RedirectAttributes redirectAttributes, Locale locale, WebRequest webRequest, HttpSession session, Model model) {
        ReportDTO reportDTO = null;
        if (session.getAttribute("reportDTO") == null) {
            model.addAttribute("failure", messageSource.getMessage("report.added.failed", null, locale));
            List<PermissionDTO> permissionDTOS = roleService.findPermisionsByCategory("REPORT");
            model.addAttribute("permissionDTOS", permissionDTOS);
            return "adm/report/upload";
        } else {
            reportDTO = (ReportDTO) session.getAttribute("reportDTO");
        }
        logger.info("the dto {}", reportDTO);
        List<ReportParameterDTO> parameterDTOS = new ArrayList<>();
        reportDTO.getReportParameters().forEach(reportParameterDTO -> {
            reportParameterDTO.setParameterDesc(webRequest.getParameter(reportParameterDTO.getParameterName()));
            parameterDTOS.add(reportParameterDTO);
        });
        reportDTO.setReportParameters(parameterDTOS);
        try {
            String message = reportService.addReport(reportDTO);
            session.removeAttribute("reportDTO");
            redirectAttributes.addFlashAttribute("message", message);
            logger.info("the report descriptions {}", parameterDTOS);
//            if(reportDTO.getId() != null){
//                return "redirect:/admin/report/view";
//            }
        } catch (VerificationInterruptedException e) {
            e.printStackTrace();
            logger.info("the error due to verification is {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            logger.info("the error due to unexpected is {}", e);
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/admin/report/view";

    }

    @GetMapping("/generate/index")
    public String generateReportIndex(Model model) {
        return "adm/report/generateIndex";
    }

    @GetMapping("/generate/index/data")
    public @ResponseBody
    DataTablesOutput<ReportDTO> getAllRevisedEntity(DataTablesInput input, Model model, @RequestParam("csearch") String csearch) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ReportDTO> reportDTOS = null;
        if (csearch != null && !csearch.equalsIgnoreCase("")) {
            reportDTOS = reportService.searchReportsForuser(pageable, csearch);
        } else {
            reportDTOS = reportService.getPageableReportsForuser(pageable);
        }
        DataTablesOutput<ReportDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(reportDTOS.getContent());
        out.setRecordsFiltered(reportDTOS.getTotalElements());
        out.setRecordsTotal(reportDTOS.getTotalElements());
        return out;
    }

    @GetMapping("/generate/{id}")
    public String generateReport(Model model, @PathVariable Long id, Locale locale) {
        logger.info("the report id {}", id);
        ReportDTO reportById = reportService.getReportById(id);
        boolean userAuthorized = false;
        if (reportById != null) {
//            logger.info("the report parameters {}",reportById.getReportParameters());
            if (reportById.getPermission() == null) {
                userAuthorized = true;
            } else {
                userAuthorized = reportService.isUserAuthorized(reportById);
            }
        }

        if (userAuthorized) {
            List<FinancialInstitution> financialInstitutions = reportService.getFinancialInstutions();


            reportById.getReportParameters().forEach(r -> {
                if (r.getDatatype().equals("CODE")) {
                    logger.info("the code value {}", r.getCode());
                    List<CodeDTO> reportCodes = codeService.getCodesByType(r.getCode());
                    r.setCodeValues(reportCodes);

                }
            });

            model.addAttribute("financialInstitutions", financialInstitutions);
            model.addAttribute("reportDTO", reportById);
            return "adm/report/generate";
        }

        model.addAttribute("failure", messageSource.getMessage("report.generate.unauthosized", null, locale));
        return "adm/report/generateIndex";
    }


    @GetMapping("/{id}/edit/file")
    public String editReportFile(@PathVariable Long id, Model model, Locale locale) {
        logger.debug("the id {}", id);
        ReportDTO dto = reportService.getReportById(id);
        List<PermissionDTO> permissionDTOS = roleService.findPermisionsByCategory("REPORT");
        model.addAttribute("permissionDTOS", permissionDTOS);
        if (dto == null) {
            model.addAttribute("failure", messageSource.getMessage("report.exist.fail", null, locale));
            return "adm/report/view";
        }
        model.addAttribute("reportDTO", dto);
        return "adm/report/re-add";
    }

    @GetMapping("/{id}/edit")
    public String editReport(@PathVariable Long id, Model model, Locale locale) {
        logger.debug("the id {}", id);
        ReportDTO dto = reportService.getReportById(id);
        if (dto == null) {
            model.addAttribute("failure", messageSource.getMessage("report.exist.fail", null, locale));
            return "adm/report/view";
        }
        List<PermissionDTO> permissionDTOS = roleService.findPermisionsByCategory("REPORT");
        model.addAttribute("reportDTO", dto);
        model.addAttribute("permissionDTOS", permissionDTOS);
        return "adm/report/edit";
    }

    @PostMapping("/update")
    public String updateReport(@ModelAttribute("reportDTO") @Valid ReportDTO reportDTO, BindingResult result,
                               RedirectAttributes redirectAttributes, Locale locale, WebRequest webRequest) {
        logger.info("report about update");
        logger.info("report dto {}", reportDTO);
        if (result.hasErrors()) {//TODO
            logger.info("an error occur {}", result.getAllErrors());
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.require", null, locale)));
            return "adm/report/view";
        }
        String message = reportService.editReport(reportDTO, webRequest);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/admin/report/view";
    }

    @GetMapping("/{id}/delete")
    public String deleteReport(Model model, @PathVariable Long id, Locale locale, RedirectAttributes redirectAttributes) {
        logger.info("the report to delete  id {}", id);
        try {
            String message = reportService.deleteReport(id);
            redirectAttributes.addFlashAttribute("message", message);
            logger.info("the report deleted {}", message);
        } catch (VerificationInterruptedException e) {
            e.printStackTrace();
            logger.info("the error due to verification is {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            logger.info("the error due to verification is {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/admin/report/view";
    }

    @GetMapping("/{id}/download")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        ReportDTO reportDTO = reportService.getReportById(id);
        logger.trace("downloading report");
        InputStream stream = new ByteArrayInputStream(reportDTO.getJrxmlFile());
        String mimeType = URLConnection.guessContentTypeFromName(reportDTO.getOrigFileName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        System.out.println("mimetype : " + mimeType);
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + reportDTO.getOrigFileName() + "\"");
//        response.setContentLength(String.valueOf(pdfReportStream.size()));
        InputStream inputStream = new BufferedInputStream(stream);
        //Copy bytes from source to destination(outputstream in this example), closes both streams.
        FileCopyUtils.copy(inputStream, response.getOutputStream());
        inputStream.close();
    }

    @PostMapping("/generate")
    public void generateReport(Model model, WebRequest webRequest, Locale locale, HttpServletResponse response) {
        String reportName = webRequest.getParameter("reportName");
        String reportType = webRequest.getParameter("reportType");
        String id = webRequest.getParameter("id");
        logger.trace("the about to validate report id {}", id);
        logger.trace("selected  reportType {}", reportType);
        ReportDTO reportDTO = reportService.getReportById(Long.parseLong(id));
        logger.info("report {}", reportDTO);

        if (reportDTO == null) {
            model.addAttribute("failure", messageSource.getMessage("report.generate.error", null, locale));
//            return modelAndView;
        } else {
            Map<String, Object> modelMap = reportUtils.convertUsingDataTypes(webRequest, reportDTO);
            logger.info("the parameters {}", modelMap);
            try {
                modelMap.put("logo", imagePath);
                ApplicationContext context = IbankingContext.getApplicationContext();
                DataSource dataSource = context.getBean(DataSource.class);
//                Properties properties = new Properties();
//                properties.setProperty("Content-Disposition", String.format("inline; filename=\"" + reportDTO.getReportName() + "\""));
                InputStream stream = new ByteArrayInputStream(reportDTO.getJrxmlFile());
                JasperDesign design = JRXmlLoader.load(stream);
                JasperReport jasperReport = JasperCompileManager.compileReport(design);
                jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
                JasperPrint print = JasperFillManager.fillReport(jasperReport, modelMap, dataSource.getConnection());
                if (reportType.equalsIgnoreCase("excel")) {
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    exporter.setExporterInput(new SimpleExporterInput(print));
                    ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));
                    exporter.exportReport();
                    response.setHeader("Content-Length", String.valueOf(pdfReportStream.size()));
                    response.setContentType("application/vnd.ms-excel");
                    response.addHeader("Content-Disposition", "inline; filename=\"" + reportDTO.getReportName() + ".xlsx\"");
                    OutputStream responseOutputStream = response.getOutputStream();
                    responseOutputStream.write(pdfReportStream.toByteArray());

                    responseOutputStream.close();
                    pdfReportStream.close();
                    responseOutputStream.flush();
                } else {
                    if (reportDTO.getJrxmlFile() != null) {
                        response.setContentType("application/pdf");
//                        response.setHeader("Content-Length", String.valueOf(pdfReportStream.size()));
                        response.addHeader("Content-Disposition", "attachment; filename=\"" + reportDTO.getReportName() + ".pdf\"");

                        JasperExportManager.exportReportToPdfStream(print, response.getOutputStream());

                    }
                }

            } catch (IOException e) {
                logger.info("cannot generate report because {}", e);

            } catch (Exception e) {
                logger.info("cannot generate report because {}", e);
                ModelAndView modelAndView = new ModelAndView("redirect:/admin/report/generate/index");
                model.addAttribute("failure", messageSource.getMessage("report.generate.error", null, locale));
            }
        }
    }
}
