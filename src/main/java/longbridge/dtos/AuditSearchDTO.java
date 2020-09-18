package longbridge.dtos;

import longbridge.config.audits.RevisedEntitiesUtil;
import longbridge.utils.DateUtil;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Longbridge on 10/26/2017.
 */

public class AuditSearchDTO {
    String id;
    String entityClassName;
    long fromDate;
    long endDate;
    String ipAddress;
    String lastChangeBy;
    String username;

    public AuditSearchDTO(String id,String entityClassName,String fromDate,String endDate,String ipAddress,String lastChangeBy){
        this.id = id;
        this.ipAddress = ipAddress;
        this.entityClassName =entityClassName;
        this.fromDate = DateUtil.convertDateToLong(fromDate);
        this.endDate = DateUtil.convertDateToLong(endDate);
        this.lastChangeBy = lastChangeBy;
    }
    public AuditSearchDTO(String id,String entityClassName,String fromDate,String endDate,String ipAddress,String lastChangeBy,String username){

        if(!StringUtils.isEmpty(username)){
            this.id = RevisedEntitiesUtil.getUserDetailsByUserName(entityClassName,username);
        }else {
            this.id = id;
        }
        this.ipAddress = ipAddress;
        this.entityClassName =entityClassName;
        this.fromDate = DateUtil.convertDateToLong(fromDate);
        this.endDate = DateUtil.convertDateToLong(endDate);
        this.lastChangeBy = lastChangeBy;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }


    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "AuditSearchDTO{" +
                "id='" + id + '\'' +
                ", entityClassName='" + entityClassName + '\'' +
                ", fromDate=" + fromDate +
                ", endDate=" + endDate +
                ", ipAddress='" + ipAddress + '\'' +
                ", lastChangeBy='" + lastChangeBy + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public String getLastChangeBy() {
        return lastChangeBy;
    }

    public void setLastChangeBy(String lastChangeBy) {
        this.lastChangeBy = lastChangeBy;
    }

}
