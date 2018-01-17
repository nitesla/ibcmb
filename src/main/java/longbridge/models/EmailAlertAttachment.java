package longbridge.models;

import java.io.File;

/**
 * Created by LB-PRJ-020 on 4/7/2017.
 */
public class EmailAlertAttachment {

    private File attachedFile;
    private String description;
    private String name;
    private boolean isAttachment;

    public File getAttachedFile() {
        return attachedFile;
    }

    public void setAttachedFile(File attachedFile) {
        this.attachedFile = attachedFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAttachment() {
        return isAttachment;
    }

    public void setAttachment(boolean attachment) {
        isAttachment = attachment;
    }
}
