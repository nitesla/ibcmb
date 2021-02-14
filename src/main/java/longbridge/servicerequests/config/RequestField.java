package longbridge.servicerequests.config;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class RequestField implements Serializable {

    private String name;
    private String label;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String data;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public enum Type {
        TEXT("Text"),LARGE_TEXT("Text Area"), NUMBER("Number")
        ,DATE("Date"),CODE("Code"),LIST("List"),FILE("File")
        ,ACCOUNT("Account");

        private String description;

        Type(String description) {
            this.description = description;
        }


        @Override
        public String toString() {
            return description;
        }
    }
}
