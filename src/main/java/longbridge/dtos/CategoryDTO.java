package longbridge.dtos;

public class CategoryDTO {

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
