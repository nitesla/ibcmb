package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Created by Longbridge on 22/06/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class PhishingImage extends AbstractEntity{

    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
