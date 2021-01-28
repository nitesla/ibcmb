package longbridge.dtos;

 import java.io.Serializable;

public class CategoryDTO implements Serializable {

    public CategoryDTO() {
    }

    public CategoryDTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

}
