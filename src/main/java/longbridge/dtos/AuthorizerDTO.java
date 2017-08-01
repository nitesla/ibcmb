package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Fortune on 7/29/2017.
 */
public class AuthorizerDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String name;
    private  int level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

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

    @Override
    public String toString() {
        return "AuthorizerDTO{" +
                "id=" + id +
                ", version=" + version +
                ", name='" + name + '\'' +
                ", level=" + level +
                '}';
    }
}
