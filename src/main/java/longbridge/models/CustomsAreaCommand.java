package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CustomsAreaCommand{

    @JsonProperty("Commands")
    public List<Commands> commands;
    @JsonProperty("Code")
    public String code;
    @JsonProperty("Message")
    public String message;

    public List<Commands> getCommands() {
        return commands;
    }

    public void setCommands(List<Commands> commands) {
        this.commands = commands;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CustomsAreaCommand{" +
                "commands=" + commands +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
