package longbridge.dtos;

/**
 * Created by Longbridge on 04/08/2017.
 */
public class BulkStatusDTO {
    private String label;
    private long value;

    public BulkStatusDTO() {
    }

    public BulkStatusDTO(String label, long value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
