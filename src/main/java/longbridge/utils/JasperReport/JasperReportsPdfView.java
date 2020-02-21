package longbridge.utils.JasperReport;

/**
 * Created by mac on 21/09/2018.
 */
import longbridge.utils.JasperReport.AbstractJasperReportsSingleFormatView;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/**
 * Implementation of {@code AbstractJasperReportsSingleFormatView}
 * that renders report results in PDF format.
 *
 * <p><b>This class is compatible with classic JasperReports releases back until 2.x.</b>
 * As a consequence, it keeps using the {@link net.sf.jasperreports.engine.JRExporter}
 * API which got deprecated as of JasperReports 5.5.2 (early 2014).
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.1.3
 */
@SuppressWarnings({"deprecation", "rawtypes"})
public class JasperReportsPdfView extends AbstractJasperReportsSingleFormatView {

    public JasperReportsPdfView() {
        setContentType("application/pdf");
    }

    @Override
    protected net.sf.jasperreports.engine.JRExporter createExporter() {
        return new JRPdfExporter();
    }

    @Override
    protected boolean useWriter() {
        return false;
    }

}
