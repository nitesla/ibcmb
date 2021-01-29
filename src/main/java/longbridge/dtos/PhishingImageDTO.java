package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Longbridge on 03/07/2017.
 */
public class PhishingImageDTO implements Serializable {

    @JsonProperty("DT_RowId")
    private Long id;

    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PhishingImageDTO(Long id, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
    }

    public PhishingImageDTO() {
    }
}
