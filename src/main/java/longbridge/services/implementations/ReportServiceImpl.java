package longbridge.services.implementations;

import longbridge.config.SpringContext;
import longbridge.dtos.ReportDTO;
import longbridge.dtos.ReportParameterDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.*;
import longbridge.repositories.FinancialInstitutionRepo;
import longbridge.repositories.ReportRepo;
import longbridge.repositories.TransferCodeRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.DateUtil;
import longbridge.utils.ReportUtils;
import longbridge.utils.Verifiable;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.context.Context;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by mac on 30/01/2018.
 */
@Service
public class ReportServiceImpl implements ReportService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${report.path}")
    private String REPORT_PATH;
    @Value("${report.temp.path}")
    private String TEMP_REPORT_PATH;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    ReportRepo reportRepo;
    @Autowired
    ReportUtils reportUtils;
    @Autowired
    FinancialInstitutionRepo financialInstitutionRepo;
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    MailService mailService;
    private Locale locale = LocaleContextHolder.getLocale();
    @Value("${jrxmlImage.path}")
    private String imagePath;
    @Autowired
    private ConfigurationService configurationService;

    @Override
    public ReportDTO extractParameters(InputStream inputStream, String reportName, String username) throws JRException,IOException, InternetBankingException {
        ReportDTO reportDTO = new ReportDTO();
        List<ReportParameterDTO> reportParamsDTO =  new ArrayList<>();
        reportDTO.setReportName(reportName);
        reportDTO.setCreatedOn(new Date());
        reportDTO.setCreatedBy(username);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
        JRParameter[] params = jasperReport.getParameters();
        for(JRParameter param : params) {
            if(!param.isSystemDefined() && param.isForPrompting()){
                ReportParameterDTO parameterDTO = new ReportParameterDTO();
                param.getName();
                param.getDescription();
                param.getDefaultValueExpression();
                param.getNestedTypeName();
                parameterDTO.setParameterName(param.getName());
                logger.info("param "+param.getValueClass());
                logger.info("param 2"+param.getValueClassName());
                reportParamsDTO.add(parameterDTO);

            }
        }
        reportDTO.setReportParameters(reportParamsDTO);
        logger.info("the number of parameters is {} parameters {}",reportParamsDTO.size(),reportParamsDTO);

        return reportDTO;
    }
    @Override
    public ReportDTO extractParameters(String reportName, String permissionCode, String username, String sysFileName, String origFileName) throws JRException,IOException, InternetBankingException {
        String jasperfileName  = TEMP_REPORT_PATH+sysFileName+".jasper";
        Permission permission = roleService.findPermisionsByCode(permissionCode);
        File file = new File(jasperfileName);
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setPermission(permission);
        List<ReportParameterDTO> reportParamsDTO =  new ArrayList<>();
        reportDTO.setReportName(reportName);
        reportDTO.setCreatedOn(new Date());
        reportDTO.setCreatedBy(username);
        reportDTO.setOrigFileName(origFileName);
        reportDTO.setSysFileName(sysFileName);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
        JRParameter[] params = jasperReport.getParameters();
        for(JRParameter param : params) {
            if(!param.isSystemDefined() && param.isForPrompting()){
                ReportParameterDTO parameterDTO = new ReportParameterDTO();
                param.getName();
                param.getDescription();
                param.getDefaultValueExpression();
                param.getNestedTypeName();
                parameterDTO.setParameterName(param.getName());
                parameterDTO.setDatatype(param.getValueClassName());

                if(param.hasProperties() ){
                    if(param.getPropertiesMap().containsProperty("CODE")) {
                        logger.info("param "+param.getName());
                        logger.info("param "+param.getValueClass());
                        parameterDTO.setCode(param.getPropertiesMap().getProperty("CODE"));
                        if (parameterDTO.getCode() != null && !parameterDTO.getCode().equals("")) {
                            parameterDTO.setDatatype("CODE");
                        }
                        logger.info("param in property {}", param.getPropertiesMap().getProperty("CODE"));
                    }
                }
                reportParamsDTO.add(parameterDTO);

            }
        }
        reportDTO.setReportParameters(reportParamsDTO);
        logger.info("the number of parameters is {} parameters {}",reportParamsDTO.size(),reportParamsDTO);

        return reportDTO;
    }
    @Override
    public ReportDTO extractParameters(String reportName, String permissionCode, String username, String sysFileName, String origFileName,MultipartFile file) throws JRException,IOException, InternetBankingException {
//        String jasperfileName  = TEMP_REPORT_PATH+sysFileName+".jasper";
//        JasperReport jasperReport1 = JasperCompileManager.compileReport(file.getInputStream());
        Permission permission = roleService.findPermisionsByCode(permissionCode);
//        File file1 = new File(jasperfileName);
//        InputStream stream = new ByteArrayInputStream(file.getBytes());
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setPermission(permission);
        List<ReportParameterDTO> reportParamsDTO =  new ArrayList<>();
        reportDTO.setReportName(reportName);
        reportDTO.setCreatedOn(new Date());
        reportDTO.setCreatedBy(username);
        reportDTO.setOrigFileName(origFileName);
        reportDTO.setSysFileName(sysFileName);
        reportDTO.setJrxmlFile(file.getBytes());
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getInputStream());
        JRParameter[] params = jasperReport.getParameters();
        for(JRParameter param : params) {
            if(!param.isSystemDefined() && param.isForPrompting()){
                ReportParameterDTO parameterDTO = new ReportParameterDTO();
                param.getName();
                param.getDescription();
                param.getDefaultValueExpression();
                param.getNestedTypeName();
                parameterDTO.setParameterName(param.getName());
                parameterDTO.setDatatype(param.getValueClassName());

                if(param.hasProperties() ){
                    if(param.getPropertiesMap().containsProperty("CODE")) {
                        logger.info("param "+param.getName());
                        logger.info("param "+param.getValueClass());
                        parameterDTO.setCode(param.getPropertiesMap().getProperty("CODE"));
                        if (parameterDTO.getCode() != null && !parameterDTO.getCode().equals("")) {
                            parameterDTO.setDatatype("CODE");
                        }
                        logger.info("param in property {}", param.getPropertiesMap().getProperty("CODE"));
                    }
                }
                reportParamsDTO.add(parameterDTO);

            }
        }
        reportDTO.setReportParameters(reportParamsDTO);
        logger.info("the number of parameters is {} parameters {}",reportParamsDTO.size(),reportParamsDTO);

        return reportDTO;
    }
    @Override
    public ReportDTO updateReportParameters(ReportDTO reportDTO, String permissionCode, String username, String origFileName, MultipartFile file) throws JRException,IOException, InternetBankingException {
//        String jasperfileName  = TEMP_REPORT_PATH+reportDTO.getSysFileName()+".jasper";
//        if(makerCheckerService.isEnabled("ADD_REPORT")){
//            jasperfileName  = TEMP_REPORT_PATH+reportDTO.getSysFileName()+".jasper";
//        }
        Permission permission = roleService.findPermisionsByCode(permissionCode);
//        File file = new File(jasperfileName);
        reportDTO.setPermission(permission);
        List<ReportParameterDTO> reportParamsDTO =  new ArrayList<>();
        reportDTO.setReportName(reportDTO.getReportName());
        reportDTO.setCreatedOn(new Date());
        reportDTO.setCreatedBy(username);
        reportDTO.setOrigFileName(origFileName);
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getInputStream());
        JRParameter[] params = jasperReport.getParameters();
        for(JRParameter param : params) {
            if(!param.isSystemDefined() && param.isForPrompting()){
                ReportParameterDTO parameterDTO = new ReportParameterDTO();
                parameterDTO.setParameterName(param.getName());
                parameterDTO.setDatatype(param.getValueClassName());
                if(param.hasProperties() ){
                    if(param.getPropertiesMap().containsProperty("CODE")) {
                        logger.info("param "+param.getName());
                        parameterDTO.setCode(param.getPropertiesMap().getProperty("CODE"));
                        if (parameterDTO.getCode() != null && !parameterDTO.getCode().equals("")) {
                            parameterDTO.setDatatype("CODE");
                        }
                        logger.info("param in property {}", param.getPropertiesMap().getProperty("CODE"));
                    }
                }
                logger.info("param "+param.getValueClass());
                logger.info("param 2"+param.getValueClassName());
                reportParamsDTO.add(parameterDTO);

            }
        }
        reportDTO.setReportParameters(reportParamsDTO);
        logger.info("the number of parameters is {} parameters {}",reportParamsDTO.size(),reportParamsDTO);

        return reportDTO;
    }

    @Override
    public ReportDTO getFileAndResaved(String reportName) throws JRException, IOException, InternetBankingException {
        File file = new File(reportName);
        ReportDTO reportDTO = new ReportDTO();
        List<String> reportParams =  new ArrayList<>();
        reportDTO.setReportName(reportName);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
        JRParameter[] params = jasperReport.getParameters();
        for(JRParameter param : params) {
            if(!param.isSystemDefined() && param.isForPrompting()){
                param.getName();
                param.getDescription();
                param.getDefaultValueExpression();
                param.getNestedTypeName();
                reportParams.add(param.getName());
                logger.info("the parameter name is {} ",param.getName(),param.getDescription());
            }
        }
//        reportDTO.setParameterName(reportParams);
//        reportDTO.setParameterDesc(reportParams);

        return reportDTO;
    }

    @Override
    public void saveReportTemp(String reportName, MultipartFile file,String format) throws IOException, InternetBankingException {

        String fileName  = TEMP_REPORT_PATH+reportName+"."+format;
        reportUtils.deleteReportIfExist(true,reportName);
        try (
                FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] bytes = file.getBytes();
            fos.write(bytes);
            fos.close();
        }catch (FileNotFoundException e){
            logger.info("file not found {}", e);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public byte[] getReportsInBytes(String reportName, MultipartFile file,String format) throws IOException, InternetBankingException {

        String fileName  = TEMP_REPORT_PATH+reportName+"."+format;
        reportUtils.deleteReportIfExist(true,reportName);
        try (
                FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] bytes = file.getBytes();return bytes;
//            fos.write(bytes);
//            fos.close();
        }catch (FileNotFoundException e){
            logger.info("file not found {}", e);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void saveReport(String reportName, MultipartFile file,String format) throws IOException, InternetBankingException {
        String fileName  = REPORT_PATH+reportName+"."+format;
        try (
                FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] bytes = file.getBytes();
            fos.write(bytes);
            fos.close();
        }catch (FileNotFoundException e){
            logger.info("file not found {}", e);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isFileValid(String fileName) {
        if(fileName!=null && !StringUtils.isEmpty(fileName)){
            int lastIndexOfDot = fileName.lastIndexOf('.');
            if(lastIndexOfDot != -1) {
                String fileExtension = fileName.substring(lastIndexOfDot, fileName.length());
                logger.info("the file extension {}", fileExtension);
                if (StringUtils.equals(fileExtension, ".jrxml")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Report convertDTOToEntity(ReportDTO reportDTO) {
        return modelMapper.map(reportDTO,Report.class);
    }
    @Override
    public ReportDTO convertEntitytoDTO(Report report) {
        ReportDTO reportDTO = modelMapper.map(report, ReportDTO.class);
        reportDTO.setId(report.getId());
        return reportDTO;
    }

    @Override
    @Verifiable(operation = "ADD_REPORT", description = "Add Report")
    public String addReport(ReportDTO reportDTO){
        Report report = convertDTOToEntity(reportDTO);
        try{
            reportRepo.save(report);
            logger.info("the report to be saved {}",report);
            return messageSource.getMessage("report.added.success", null, locale);
        }catch (VerificationInterruptedException e){
            return e.getMessage();
        }catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("report.added.failed", null, locale), e);
        }

    }
    @Override
    public String saveReport(Report report){
        try{
            reportRepo.save(report);
            logger.info("the report to be saved {}",report);
            return messageSource.getMessage("report.added.success", null, locale);
        }catch (VerificationInterruptedException e){
            return e.getMessage();
        }catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("report.added.failed", null, locale), e);
        }
    }

    @Override
    public List<ReportDTO> getReportsForuser() {
        User user = getCurrentUser();
        List<ReportDTO> reportDTOS =  new ArrayList<>();
        if(user != null) {
            logger.info("get Permissions for {}",user.getUserName());
            Collection<Permission> permissions = user.getRole().getPermissions();
            logger.info("the permission list {}",permissions.size());
            List<Report> reports = reportRepo.findbyPermission(permissions);
            logger.info("the report list {}",reports.size());

            reports.forEach(report->reportDTOS.add(convertEntitytoDTO(report)));
        }
        logger.info("the reportDTO list {}",reportDTOS.size());
        return reportDTOS;
    }
    @Override
    public Page<ReportDTO> getPageableReportsForuser(Pageable pageDetails) {
        User user = getCurrentUser();
        List<ReportDTO> reportDTOS =  new ArrayList<>();
        Collection<Permission> permissions =  new ArrayList<>();
        if(user != null) {
            permissions = user.getRole().getPermissions();
            logger.debug("the permission list {}",permissions.size());
        }
        Page<Report> reports = reportRepo.findbyPermission(permissions,pageDetails);
        long t = reports.getTotalElements();
        logger.trace("the number of report {}",t);
        reports.forEach(report->reportDTOS.add(convertEntitytoDTO(report)));
        logger.info("the dto {}",reportDTOS.size());
        return new PageImpl<ReportDTO>(reportDTOS, pageDetails, t);
    }
    @Override
    public Page<ReportDTO> searchReportsForuser(Pageable pageDetails, String search) {
        User user = getCurrentUser();
        List<ReportDTO> reportDTOS =  new ArrayList<>();
        Collection<Permission> permissions =  new ArrayList<>();
        if(user != null) {
            permissions = user.getRole().getPermissions();
            logger.debug("the permission list {}",permissions.size());
        }
        Page<Report> reports = reportRepo.findbyReportBySearch(permissions,pageDetails,search.toUpperCase());
        long t = reports.getTotalElements();
        logger.trace("the number of report {}",t);
        reports.forEach(report->reportDTOS.add(convertEntitytoDTO(report)));
        logger.info("the dto {}",reportDTOS.size());
        return new PageImpl<ReportDTO>(reportDTOS, pageDetails, t);
    }
    @Override
    public Page<ReportDTO> getReports(Pageable pageDetails) {
        List<ReportDTO> reportDTOS =  new ArrayList<>();
        Page<Report> reports = reportRepo.findAll(pageDetails);
        long t = reports.getTotalElements();
        logger.info("the number of report {}",t);
        reports.forEach(report->reportDTOS.add(convertEntitytoDTO(report)));
        logger.info("the dto size {}",reportDTOS.size());
        return new PageImpl<ReportDTO>(reportDTOS, pageDetails, t);
    }
    @Override
    public Page<ReportDTO> findReports(String search, Pageable pageDetails) {
        List<ReportDTO> reportDTOS =  new ArrayList<>();
        Page<Report> reports = reportRepo.findUsingPattern(search,pageDetails);
        long t = reports.getTotalElements();
        logger.info("the number of report  search {}",t);
        reports.forEach(report->reportDTOS.add(convertEntitytoDTO(report)));
        logger.info("the search dto size {}",reportDTOS.size());
        return new PageImpl<ReportDTO>(reportDTOS, pageDetails, t);
    }


    @Override
    public boolean reportAlreadyExist(String reportName) throws InternetBankingException {
        Report report = reportRepo.findByReportNameIgnoreCase(reportName);
        if(report != null)
            return true;
        else
            return false;
    }

    @Override
    public ReportDTO getReportById(Long id) {
        Report report = reportRepo.findReportById(id);
        ReportDTO reportDTO = null;
        if(report != null) {
            reportDTO = convertEntitytoDTO(report);
        }
        return reportDTO;
    }

    @Override
    public boolean isUserAuthorized(ReportDTO reportDTO) {
        User user = getCurrentUser();
        List<ReportDTO> reportDTOS =  new ArrayList<>();
        if(user != null) {
            logger.info("check Permissions of {}",user.getUserName());
            Collection<Permission> permissions = user.getRole().getPermissions();
            boolean validUser = permissions.contains(reportDTO.getPermission());
            return validUser;
//            reports.forEach(report->reportDTOS.add(convertEntitytoDTO(report)));
        }
        return false;
    }


    @Override
    @Verifiable(operation = "EDIT_REPORT", description = "Edit Report")
    public String editReport(ReportDTO reportDTO, WebRequest webRequest) {
        try{
            Report report = reportRepo.findOneById(reportDTO.getId());
            Report report1 = convertDTOToEntity(reportDTO);
            report.setReportParameters(report1.getReportParameters());
            report.setReportName(report1.getReportName());
            Permission permission = roleService.findPermisionsByCode(webRequest.getParameter("permissionCode"));
            report.setPermission(permission);

            reportRepo.save(report);
            logger.info("the report to be saved {}",report);
            return messageSource.getMessage("report.update.success", null, locale);
        }catch (VerificationInterruptedException e){
            return e.getMessage();
        }catch (InternetBankingException e){
            return e.getMessage();
        }catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("report.update.failure", null, locale), e);
        }

    }

    @Override
    @Verifiable(operation = "DELETE_REPORT", description = "Delete Report")
    public String deleteReport(Long id) throws InternetBankingException {
        try {
            logger.info("the report id {}",id);
            Report report = reportRepo.findOneById(id);
            reportRepo.delete(report);
            if(!makerCheckerService.isEnabled("DELETE_REPORT")) {
                logger.info("the file name to deleted {}", report.getSysFileName());
                reportUtils.deleteReportIfExist(false, report.getSysFileName());
            }
            return messageSource.getMessage("report.delete.success", null, locale);
        }catch (VerificationInterruptedException e){
            return e.getMessage();
        }catch (InternetBankingException e){
            return e.getMessage();
        }catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("report.delete.failure", null, locale), e);
        }

    }
    @Override
    public String deleteReport(Report report) throws InternetBankingException {
        reportRepo.delete(report);
        logger.info("is maker checker enable {}",makerCheckerService.isEnabled("DELETE_REPORT"));
        logger.info("the file name to deleted {}", report.getSysFileName());
        reportUtils.deleteReportIfExist(false, report.getSysFileName());
        return messageSource.getMessage("report.delete.success", null, locale);
    }


    @Override
    public List<FinancialInstitution> getFinancialInstutions() {
        return financialInstitutionRepo.findAll();
    }


    private User getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUser();
    }


}
