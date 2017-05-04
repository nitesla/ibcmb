package longbridge.models;

import java.util.List;

/**
 * Created by LB-PRJ-020 on 4/7/2017.
 */
public class EmailDetail {

    private String receiverEmail;
    private String subject;
    private String messageBody;
    private String ccList; //comma separated list of email addresses, null if empty
    private List<EmailAlertAttachment> emailAttachments;

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getCcList() {
        return ccList;
    }

    public void setCcList(String ccList) {
        this.ccList = ccList;
    }

    public List<EmailAlertAttachment> getEmailAttachments() {
        return emailAttachments;
    }

    public void setEmailAttachments(List<EmailAlertAttachment> emailAttachments) {
        this.emailAttachments = emailAttachments;
    }
}
