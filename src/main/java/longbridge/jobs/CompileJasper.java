package longbridge.jobs;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

/**
 * Created by Longbridge on 8/11/2017.
 */
public class CompileJasper {

    public static void compile() {
        String sourceFileName = "/Users/mac/Desktop/Bulk Transfer/rpt_bulk_transfer_excel.jrxml";

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
