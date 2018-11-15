package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Commands{

    @JsonProperty("CommandCode")
    public String commandCode;

    @JsonProperty("CommandDesc")
    public String commandDesc;

    public Commands() {
    }

    public String getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(String commandCode) {
        this.commandCode = commandCode;
    }

    public String getCommandDesc() {
        return commandDesc;
    }

    public void setCommandDesc(String commandDesc) {
        this.commandDesc = commandDesc;
    }

    @Override
    public String toString() {
        return "Command{" +
                "commandCode='" + commandCode + '\'' +
                ", commandDesc='" + commandDesc + '\'' +
                '}';
    }
}
