package longbridge.servicerequests.config;

import java.io.Serializable;

public class SRFieldDTO implements Serializable {
    private Long id;
    private String name;
    private String label;
    private RequestField.Type type;
    private String data;

    public SRFieldDTO() {
    }


    public SRFieldDTO(RequestField field) {
        this.name = field.getName();
        this.label = field.getLabel();
        this.type = field.getType();
        this.data = field.getData();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public RequestField.Type getType() {
        return type;
    }

    public void setType(RequestField.Type type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
