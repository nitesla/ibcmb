package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Fortune on 3/29/2017.
 * 
 * Code is lookup table (or enumeration) predicate
 */

@Entity
public class Code extends AbstractEntity {

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


	@Override
    public String toString() {
        return "Code [id=" + id + ", code=" + code + ", description="
                + description + ", type=" + type + "]";
    }

    
}
