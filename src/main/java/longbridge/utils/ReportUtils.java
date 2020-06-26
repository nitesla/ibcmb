package longbridge.utils;

import longbridge.dtos.ReportDTO;
import longbridge.exception.InternetBankingException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 30/01/2018.
 */
@Component
public class ReportUtils {
    @Value("${report.path}")
    private String REPORT_PATH;
    @Value("${report.temp.path}")
    private String TEMP_REPORT_PATH;
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    public static String generateFileName(){
        String reportName = "Rep_" +(new Date().getTime());
        System.out.println("the generated report name "+reportName);
        return reportName;
    }
    public Map convertUsingDataTypes(WebRequest webRequest, ReportDTO reportDTO){
        Map<String, Object> modelMap = new HashMap<>();

//            reportDTO.getReportParameters().forEach(parameter->reportParameters.add(parameter.getParameterName()));
        reportDTO.getReportParameters().forEach(reportParam-> System.out.println("the para values "+webRequest.getParameter(reportParam.getParameterName())));
        reportDTO.getReportParameters().forEach(reportParam-> {
if(reportParam.getDatatype() != null){
    SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
            switch (reportParam.getDatatype()) {

                case "java.util.Date":

                    Date date = null;
                    try {
                        date = new SimpleDateFormat("MM/dd/yyyy").parse(webRequest.getParameter(reportParam.getParameterName()));
                        String format1 = formatDate.format(date);
                        date = formatDate.parse(format1);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    modelMap.put(reportParam.getParameterName(), date);
                    break;
                case "java.sql.Date":
                    java.sql.Date date1 = null;
                    try {
                        Date date2 = new SimpleDateFormat("MM/dd/yyyy").parse(webRequest.getParameter(reportParam.getParameterName()));
                        System.out.println("the date before format "+date2);
                        String dateTemp = formatDate.format(date2);
                        System.out.println("the date after formate"+dateTemp);
                        date2 =  formatDate.parse(dateTemp);
                        System.out.println("the date 2 after format dat util "+dateTemp);
                        date1 = new java.sql.Date(date2.getTime());
                        System.out.println("the date in sql date "+date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    modelMap.put(reportParam.getParameterName(), date1);
                    break;
                case "java.lang.Double":
                    try {
                        modelMap.put(reportParam.getParameterName(), Double.parseDouble(webRequest.getParameter(reportParam.getParameterName())));
                    } catch (NumberFormatException e) {
                        modelMap.put(reportParam.getParameterName(), Double.parseDouble("0.00"));

                        e.printStackTrace();
                    }
                    break;
                case "java.lang.Float":
                    try {
                        modelMap.put(reportParam.getParameterName(), Float.parseFloat(webRequest.getParameter(reportParam.getParameterName())));
                    } catch (NumberFormatException e) {
                        modelMap.put(reportParam.getParameterName(), Float.parseFloat("0.00"));

                        e.printStackTrace();
                    }
                    break;
                case "java.lang.Integer":
                    try {
                        modelMap.put(reportParam.getParameterName(), Integer.parseInt(webRequest.getParameter(reportParam.getParameterName())));
                    } catch (NumberFormatException e) {
                        modelMap.put(reportParam.getParameterName(), Integer.parseInt("0"));

                        e.printStackTrace();
                    }
                    break;
                case "java.lang.Long":
                    try {
                        modelMap.put(reportParam.getParameterName(), Long.parseLong(webRequest.getParameter(reportParam.getParameterName())));
                    } catch (NumberFormatException e) {
                        modelMap.put(reportParam.getParameterName(), Long.parseLong("0"));

                        e.printStackTrace();
                    }
                    break;
                case "java.math.BigDecimal":
                    try {
                        modelMap.put(reportParam.getParameterName(), new BigDecimal(webRequest.getParameter(reportParam.getParameterName())));
                    } catch (NumberFormatException e) {
                        modelMap.put(reportParam.getParameterName(), new BigDecimal("0"));

                        e.printStackTrace();
                    }
                    break;
                case "java.lang.Boolean":
                    try {
                        modelMap.put(reportParam.getParameterName(), Boolean.valueOf(webRequest.getParameter(reportParam.getParameterName())));
                    } catch (NumberFormatException e) {
                        modelMap.put(reportParam.getParameterName(), false);

                        e.printStackTrace();
                    }
                    break;
                default:
                    modelMap.put(reportParam.getParameterName(),
                            webRequest.getParameter(reportParam.getParameterName()));

            }
        }
        });
        return modelMap;
    }
    public boolean deleteReportIfExist(boolean isFileTemp, String fileName) throws InternetBankingException {
        try {
            String jasperFileName = REPORT_PATH + fileName + ".jasper";
            String jrxmlFileName = REPORT_PATH + fileName + ".jrxml";
            if(isFileTemp) {
                jasperFileName = TEMP_REPORT_PATH + fileName + ".jasper";
                jrxmlFileName = TEMP_REPORT_PATH + fileName + ".jrxml";
            }
            Path path = Paths.get(jrxmlFileName);
            boolean isDeleted = Files.deleteIfExists(path);
            System.out.println("jasper file deleted succesfully "+isDeleted);
            path = Paths.get(jasperFileName);
            isDeleted = Files.deleteIfExists(path);
            System.out.println("jrxml file deleted succesfully "+isDeleted);
            return isDeleted;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public  void moveTempReportToPermLocation(String fileName){
        String jasperTempFileName  = TEMP_REPORT_PATH+fileName+".jasper";
        String jasperfileName  = REPORT_PATH+fileName+".jasper";
        String jrxmlfileName  = REPORT_PATH+fileName+".jrxml";
        String jrxmlTempfileName  = TEMP_REPORT_PATH+fileName+".jrxml";
        try {
            Path jasperPath = Files.move(Paths.get(jasperTempFileName),Paths.get(jasperfileName));
            Path jrxmlPath = Files.move(Paths.get(jrxmlTempfileName),Paths.get(jrxmlfileName));
//            Path temp = Files.move(Paths.get("/Users/mac/Documents/longbridge/latestIbanking/ibcmb/src/main/resources/tempReports/Rep_pp.jasper"),Paths.get("/Users/mac/Documents/longbridge/latestIbanking/ibcmb/src/main/resources/reports/jj.jasper"));
            if(jrxmlPath != null && jasperPath != null){
                logger.info("the files are moved");
            }else {
                logger.info("the file  not moved");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
