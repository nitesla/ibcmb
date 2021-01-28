package longbridge.dtos;

 import java.io.Serializable;

public class CodeTypeDTO implements Serializable {

	private String type;

	public CodeTypeDTO(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
