package longbridge.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.Date;


@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_flag='N'")
@Check(constraints = "duration >= 0")

public class Greeting extends AbstractEntity implements PrettySerializer {

    private String eventName;
    private String type;
    private String message;
    private Date createdOn=new Date();
    private Date executedOn;
    private Date expiredOn;
    private String createdBy;
    private boolean recurringDate;
    private String userId;
    private Integer duration;
    private String imageLink;
    private String userType;


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean getRecurringDate() {
        return recurringDate;
    }

    public void setRecurringDate(boolean recurringDate) {
        this.recurringDate = recurringDate;

    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "Greeting{" +
                "eventName='" + eventName + '\'' +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", createdOn=" + createdOn +
                ", executedOn=" + executedOn +
                ", expiredOn=" + expiredOn +
                ", createdBy='" + createdBy + '\'' +
                ", recurringDate=" + recurringDate +
                ", userId='" + userId + '\'' +
                ", duration=" + duration +
                ", imageLink='" + imageLink + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }

    @Override @JsonIgnore
    public JsonSerializer<Greeting> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Greeting value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("Event Name", value.eventName);
                if ("GNL".equals(value.type)) gen.writeStringField("Type", "General");
                else gen.writeStringField("Type", "Personal");
                gen.writeStringField("Message", value.message);
                gen.writeStringField("Creation Date", value.createdOn.toString());
                gen.writeStringField("Execution Date", value.executedOn.toString());
                gen.writeStringField("Expiration Date", value.expiredOn.toString());
                gen.writeStringField("Creator", value.createdBy);
                String recurring = "";
                if (value.recurringDate) recurring = "Yes";
                else recurring = "No";
                gen.writeStringField("Is Event Recurring?", recurring);
                gen.writeStringField("User Id", value.userId);
                gen.writeStringField("Number of Days", value.duration.toString());
                gen.writeStringField("Image Name", value.imageLink);
                gen.writeStringField("Type of User", value.userType);
                gen.writeEndObject();
            }
        };

    }

    @Override
    @JsonIgnore
    public JsonSerializer<Greeting> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Greeting value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("Event Name", value.eventName);
                if ("GNL".equals(value.type)) gen.writeStringField("Type", "General");
                else gen.writeStringField("Type", "Personal");
                gen.writeStringField("Message", value.message);
                gen.writeStringField("Type of User", value.userType);
                gen.writeStringField("Execution Date", value.executedOn.toString());
                gen.writeStringField("Number of Days", value.duration.toString());
                gen.writeStringField("Image Name", value.imageLink);

                String recurring = "";
                if (value.recurringDate) recurring = "Yes";
                else recurring = "No";
                gen.writeStringField("Is Event Recurring?", recurring);
                gen.writeEndObject();

            }
        };
    }

}
