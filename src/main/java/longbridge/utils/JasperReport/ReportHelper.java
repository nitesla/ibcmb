package longbridge.utils.JasperReport;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

public class ReportHelper {

    private final static String reportPath = "jasperreports/";
    private final static String reportExt = ".jasper";
    private final static String templateExt = ".jrxml";

    public static JasperReport getJasperReport(String reportName) throws Exception {

        ClassPathResource classPathResource = new ClassPathResource(reportPath + reportName + reportExt);
        JasperReport jasperReport = null;
        if (classPathResource.exists()) {
            jasperReport = (JasperReport) JRLoader.loadObject(classPathResource.getInputStream());
        } else {

            InputStream inputStream = new ClassPathResource(reportPath + reportName + templateExt).getInputStream();
            jasperReport = JasperCompileManager.compileReport(inputStream);

        }
        return jasperReport;
    }




}
