package longbridge.forms;

import javax.validation.constraints.NotEmpty;

/**
 * Created by Fortune on 5/8/2017.
 */
public class OperatorForm {
    private Long groupId;
    @NotEmpty(message = "username")
    private String username;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
