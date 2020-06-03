package longbridge.controllers.operations;

import longbridge.config.SpringContext;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.ReportDTO;
import longbridge.models.FinancialInstitution;
import longbridge.services.CodeService;
import longbridge.services.ReportService;
import longbridge.utils.ReportUtils;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import longbridge.utils.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by mac on 13/02/2018.
 */
@Controller
@RequestMapping("/ops/report")
public class OpsReportController {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CodeService codeService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${report.path}")
    private String REPORT_PATH;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ReportUtils reportUtils;
    @Autowired
    private ApplicationContext appContext;

    @Value("${jrxmlImage.path}")
    private String imagePath;

    @GetMapping("/generate/index")
    public String generateReportIndex(Model model) {
        return "ops/report/generateIndex";
    }
    @GetMapping("/generate/index/data")
    public @ResponseBody
    DataTablesOutput<ReportDTO> getAllRevisedEntity(DataTablesInput input, @RequestParam("csearch") String csearch)
    {
        logger.info("TO search {}",csearch);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ReportDTO> reportDTOS = null;
        if(csearch!=null && !csearch.equalsIgnoreCase("")){
            reportDTOS = reportService.searchReportsForuser(pageable,csearch);
        }else {
            reportDTOS = reportService.getPageableReportsForuser(pageable);
        }
        DataTablesOutput<ReportDTO> out = new DataTablesOutput<ReportDTO>();
        out.setDraw(input.getDraw());
        out.setData(reportDTOS.getContent());
        out.setRecordsFiltered(reportDTOS.getTotalElements());
        out.setRecordsTotal(reportDTOS.getTotalElements());
        return out;
    }

    @GetMapping("/generate/{id}")
    public String generateReport(Model model, @PathVariable Long id, Locale locale) {
        logger.info("the report id {}",id);
        ReportDTO reportById = reportService.getReportById(id);
        boolean userAuthorized = false;
        if(reportById!=null){
            logger.info("the report parameters {}",reportById.getReportParameters());
            if(reportById.getPermission() == null){
                userAuthorized = true;
            }else {
                userAuthorized = reportService.isUserAuthorized(reportById);
            }
        }
        if(userAuthorized){
            List<FinancialInstitution> financialInstitutions = reportService.getFinancialInstutions();
            reportById.getReportParameters().forEach( r ->{
                //List<String> codes =  new ArrayList<>();

                if(r.getDatatype().equals("CODE")){

                    logger.info("userAuth {}",r.getCode());

                    List<CodeDTO>reportCodes = codeService.getCodesByType(r.getCode());
                    r.setCodeValues(reportCodes);
                }
            });
            model.addAttribute("financialInstitutions",financialInstitutions);
            model.addAttribute("reportDTO",reportById);
            return "ops/report/generate";
        }

        model.addAttribute("failure", messageSource.getMessage("report.generate.unauthosized", null, locale));
        return "ops/report/generateIndex";
    }

    @PostMapping("/generate")
    public ModelAndView generateReport(Model model, WebRequest webRequest, Locale locale, HttpServletResponse response) {
        String reportName = webRequest.getParameter("reportName");
        String reportType = webRequest.getParameter("reportType");
        String id = webRequest.getParameter("id");
        logger.trace("the about to validate report id {}",id);
        logger.trace("selected  reportType {}",reportType);
        ReportDTO reportDTO = reportService.getReportById(Long.parseLong(id));
        logger.info("report {}",reportDTO);

        if(reportDTO == null){
            ModelAndView modelAndView =  new ModelAndView("redirect:/ops/report/generate/index");
            model.addAttribute("failure", messageSource.getMessage("report.generate.error", null, locale));
            return modelAndView;
        }else{
            logger.info("about generating report");
            Map<String, Object> modelMap = reportUtils.convertUsingDataTypes(webRequest,reportDTO);
            logger.info("the parameters {}",modelMap);
            try {
                modelMap.put("logo",imagePath);
                ApplicationContext context = SpringContext.getApplicationContext();
                DataSource dataSource = context.getBean(DataSource.class);
                InputStream stream = new ByteArrayInputStream(reportDTO.getJrxmlFile());
                JasperDesign design = JRXmlLoader.load(stream);
                JasperReport jasperReport = JasperCompileManager.compileReport(design);

                JasperPrint print = JasperFillManager.fillReport(jasperReport, modelMap,dataSource.getConnection());
                if(reportType.equalsIgnoreCase("excel")) {
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    exporter.setExporterInput(new SimpleExporterInput(print));
                    ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));
                    exporter.exportReport();
                    response.setHeader("Content-Length", String.valueOf(pdfReportStream.size()));
                    response.setContentType("application/vnd.ms-excel");
                    response.addHeader("Content-Disposition", String.format("inline; filename=\"" + reportDTO.getReportName() + ".xls\""));
                    OutputStream responseOutputStream = response.getOutputStream();
                    responseOutputStream.write(pdfReportStream.toByteArray());

                    responseOutputStream.close();
                    pdfReportStream.close();
                    responseOutputStream.flush();
                }else {
                    if(reportDTO.getJrxmlFile() != null) {

                        JRPdfExporter pdfExporter = new JRPdfExporter();
                        pdfExporter.setExporterInput(new SimpleExporterInput(print));
                        ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
                        pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));
                        pdfExporter.exportReport();

                        response.setContentType("application/pdf");
                        response.setHeader("Content-Length", String.valueOf(pdfReportStream.size()));

                        response.addHeader("Content-Disposition", String.format("attachment; filename=\"" + reportDTO.getReportName() + ".pdf\""));

                        OutputStream responseOutputStream = response.getOutputStream();
                        responseOutputStream.write(pdfReportStream.toByteArray());
                        responseOutputStream.close();
                        pdfReportStream.close();
                        responseOutputStream.flush();


                    }
//                    JasperReportsPdfView view = new JasperReportsPdfView();
//                    view.setUrl("classpath:reports/"+reportDTO.getSysFileName()+".jrxml");
////                    view.setUrl(REPORT_PATH+reportDTO.getSysFileName()+".jrxml");
//                    view.setApplicationContext(appContext);
//                    view.setJdbcDataSource(dataSource);
//                    view.setHeaders(properties);
//                    modelAndView=new ModelAndView(view, modelMap);
//                    return modelAndView;
                }

            } catch (IllegalStateException e) {
//                e.printStackTrace();
//                ModelAndView modelAndView =  new ModelAndView("redirect:/admin/report/generate/index");
//                model.addAttribute("failure", messageSource.getMessage("report.generate.error", null, locale));
//                return modelAndView;

            }catch (IOException e) {
//                e.printStackTrace();
//                ModelAndView modelAndView =  new ModelAndView("redirect:/admin/report/generate/index");
//                model.addAttribute("failure", messageSource.getMessage("report.generate.error", null, locale));
//                return modelAndView;

            } catch (BeansException e) {
                e.printStackTrace();
                ModelAndView modelAndView =  new ModelAndView("redirect:/ops/report/generate/index");
                model.addAttribute("failure", messageSource.getMessage("report.generate.error", null, locale));
                return modelAndView;

            }catch (Exception e){
                e.printStackTrace();
                ModelAndView modelAndView =  new ModelAndView("redirect:/ops/report/generate/index");
                model.addAttribute("failure", messageSource.getMessage("report.generate.error", null, locale));
                return modelAndView;
            }
        }
        return new ModelAndView();
    }
}
