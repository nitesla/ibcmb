package longbridge.dtos;

import longbridge.models.AbstractEntity;

import javax.persistence.Entity;

/**
 * Created by Fortune on 4/5/2017.
 *
 */

public class CodeDTO {

    private String code;
    private String type;
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}
