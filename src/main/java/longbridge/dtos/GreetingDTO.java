package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class GreetingDTO {


    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    @NotEmpty(message = "eventName")
    private String eventName;
    @NotEmpty(message = "type")
    private String type;
    @NotEmpty(message="message")
    private String message;
    private boolean recurringDate;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date executedOn;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date expiredOn;
    private String accountNumber;
    private String accountName;
    @NotEmpty(message="imageLink")
   // @Pattern(regexp="([a-zA-Z0-9_-]+[.]jpg|[a-zA-Z0-9_-]+[.]png)$", message="Image is not valid")
    private String imageLink;
    @NotNull(message="duration")
    private Integer duration;
    private String userId;

    private String userType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName.toUpperCase();
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getRecurringDate() {
        return recurringDate;
    }

    public void setRecurringDate(boolean recurringDate) {
        this.recurringDate = recurringDate;
    }

    public Date getExecutedOn() {
        return executedOn;
    }

    public void setExecutedOn(Date executedOn) {
        this.executedOn = executedOn;
    }

    public Date getExpiredOn() {
        return expiredOn;
    }

    public void setExpiredOn(Date expiredOn) {
        this.expiredOn = expiredOn;
    }
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }


    @Override
    public String toString() {
        return "GreetingDTO{" +
                "eventName='" + eventName + '\'' +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", recurringDate=" + recurringDate +
                ", executedOn=" + executedOn +
                ", expiredOn=" + expiredOn +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountName='" + accountName + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", duration=" + duration +
                ", userId='" + userId + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
    