package longbridge.models;

import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by chiomarose on 15/06/2017.
 */
@Entity
@Where(clause ="del_flag='N'")
public class MakerChecker extends AbstractEntity {

    private String enabled;
    private String operation;
    private String description;


    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
	public List<String> getDefaultSearchFields() {
		return Arrays.asList("operation","description");
	}

    @Override
    public String toString() {
        return new StringJoiner(", ", MakerChecker.class.getSimpleName() + "[", "]")
                .add("enabled='" + enabled + "'")
                .add("operation='" + operation + "'")
                .add("description='" + description + "'")
                .toString();
    }
}
