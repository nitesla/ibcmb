package longbridge.jobs;

/**
 * Created by Longbridge on 8/11/2017.
 */
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

public class CompileJasper {

    public static void compile() {
        String sourceFileName = "/Users/mac/Documents/Resources/rpt_account-excel-statement.jrxml";

        System.out.println("Compiling Report Design ...");
        try {
            /**
             * Compile the report to a file name same as
             * the JRXML file name
             */
            JasperCompileManager.compileReportToFile(sourceFileName);
        } catch (JRException e) {
            e.printStackTrace();
        }
        System.out.println("Done compiling!!! ...");
    }
}
