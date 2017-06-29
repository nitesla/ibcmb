package longbridge.models;

import org.hibernate.annotations.Where;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;

/**
 * Created by chiomarose on 15/06/2017.
 */
@Entity
@Where(clause ="del_flag='N'")
public class MakerChecker extends AbstractEntity {

    String enabled="N";

    String name;

    String code;

    String userType;

    @Override
    public String toString() {
        return "MakerChecker{" +
                "enabled='" + enabled + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
