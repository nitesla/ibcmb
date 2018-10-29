package longbridge.models;

public class Commands {
    private String commandCode;
    private String commandDesc;

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
