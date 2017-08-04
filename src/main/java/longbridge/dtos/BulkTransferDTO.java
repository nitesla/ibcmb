package longbridge.dtos;

/**
 * Created by Longbridge on 14/06/2017.
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Date;

public class BulkTransferDTO {
    @JsonProperty("DT_RowId")
    private Long id;
    private String customerAccountNumber;
    private Date tranDate;
    private String refCode;
    private  String status;
    private Long batchId;


    public BulkTransferDTO() {
    }

    public BulkTransferDTO(String customerAccountNumber, Date tranDate, String refCode, String status, Long batchId) {
        this.customerAccountNumber = customerAccountNumber;
        this.tranDate = tranDate;
        this.refCode = refCode;
        this.status = status;
        this.batchId = batchId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
