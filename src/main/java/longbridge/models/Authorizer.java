package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Created by Fortune on 7/29/2017.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class Authorizer extends AbstractEntity {

    private String name;
    private int level;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
