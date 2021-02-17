package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class DateRange {
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date fromDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date toDate;

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

}
