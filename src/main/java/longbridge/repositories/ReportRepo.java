package longbridge.repositories;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.dtos.ReportDTO;
import longbridge.models.CronJobExpression;
import longbridge.models.Permission;
import longbridge.models.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by Longbridge on 6/15/2017.
 */
@Repository
public interface ReportRepo extends CommonRepo<Report,Long> {
    Report findByReportNameIgnoreCase(String reportName);
    Report findReportById(Long id);
    @Query("select r from Report r  where r.permission in (:permissionList)")
    List<Report> findbyPermission(@Param("permissionList") Collection<Permission> permissions);
    @Query("select r from Report r  where r.permission in (:permissionList) or r.permission = null")
    Page<Report> findbyPermission(@Param("permissionList") Collection<Permission> permissions, Pageable pageable);

    @Query("select r from Report r  where (r.permission in (:permissionList) or r.permission = null) and upper(r.reportName) like %:search%")
    Page<Report> findbyReportBySearch(@Param("permissionList") Collection<Permission> permissions, Pageable pageable, @Param("search") String search);


}
