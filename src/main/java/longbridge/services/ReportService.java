package longbridge.services;

import longbridge.dtos.ReportDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.FinancialInstitution;
import longbridge.models.Report;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by mac on 30/01/2018.
 */
public interface ReportService {
    ReportDTO extractParameters(InputStream inputStream, String reportName, String username) throws JRException,IOException, InternetBankingException;
    ReportDTO extractParameters(String reportName, Long permissionId, String username, String sysFileName, String originalFileName,MultipartFile file ) throws JRException,IOException, InternetBankingException;
    byte[] getReportsInBytes(String sysFileName, MultipartFile file, String format) throws IOException, InternetBankingException;
    void saveReport(String sysFileName, MultipartFile file, String format) throws IOException, InternetBankingException;
    boolean isFileValid(String fileName);
    Report convertDTOToEntity(ReportDTO reportDTO);
    ReportDTO convertEntitytoDTO(Report report);
    @PreAuthorize("hasAuthority('ADD_REPORT')")
    String addReport(ReportDTO reportDTO) throws VerificationInterruptedException;
    String saveReport(Report report) throws VerificationInterruptedException;
    List<ReportDTO> getReportsForuser();
    Page<ReportDTO> getPageableReportsForuser(Pageable pageDetails);
    Page<ReportDTO> searchReportsForuser(Pageable pageDetails, String search);
    Page<ReportDTO> getReports(Pageable pageDetails);
    Page<ReportDTO> findReports(String search, Pageable pageDetails);
    boolean reportAlreadyExist(String reportName) throws InternetBankingException;
    ReportDTO getReportById(Long id);
    boolean isUserAuthorized(ReportDTO reportDTO);
    String editReport(ReportDTO reportDTO, WebRequest webRequest) throws VerificationInterruptedException;
    String deleteReport(Long id) throws InternetBankingException;
    String deleteReport(Report report) throws InternetBankingException;
    List<FinancialInstitution> getFinancialInstutions();

    ReportDTO updateReportParameters(ReportDTO reportDTO, Long permissionId, String name, String originalFilename, MultipartFile file) throws IOException, JRException;
//    InputStream sendFileAsAttachement(FileType fileType, Date date);



}
