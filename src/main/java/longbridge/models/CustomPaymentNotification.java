package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;

import java.io.IOException;

public class CustomPaymentNotification implements PrettySerializer {

    @JsonProperty("PaymentRef")
    private String paymentRef;
    @JsonProperty("Code")
    private String code;
    @JsonProperty("Message")
    private String message;

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CustomPaymentNotification{" +
                "paymentRef='" + paymentRef + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override @JsonIgnore
    public JsonSerializer<CustomPaymentNotification> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(CustomPaymentNotification value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("Code", value.getCode());
                gen.writeStringField("Description", value.message);
                gen.writeStringField("Type", value.paymentRef);
                gen.writeEndObject();
            }
        };
    }

    @Override @JsonIgnore
    public JsonSerializer<CustomPaymentNotification> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(CustomPaymentNotification value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
//                if(value.id != null) {
//                    gen.writeStringField("id", value.id.toString());
//                }else {
//                    gen.writeStringField("id", "");
//                }
                gen.writeStringField("code", value.code);
                gen.writeStringField("description", value.message);
                gen.writeStringField("type", value.paymentRef);
                gen.writeEndObject();
            }
        };
    }
}
