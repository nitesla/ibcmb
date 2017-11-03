package longbridge.models;

import java.util.List;

/**
 * Created by LB-PRJ-020 on 4/7/2017.
 */
public class Email {

    private String senderEmail;
    private String receiverEmail;
    private String[] receiverEmails;
    private String messageSubject;
    private String messageBody;
    private String[] ccList;
    private List<EmailAlertAttachment> emailAttachments;
    private String templateName;



    public Email(){}

    public Email(Builder builder)
    {
        this.setSenderEmail(builder.senderEmail);
        this.setReceiverEmail(builder.receiverEmail);
        this.setReceiverEmails(builder.receiverEmails);
        this.setMessageSubject(builder.messageSubject);
        this.setMessageBody(builder.messageBody);
        this.setCcList(builder.ccList);
        this.setEmailAttachments(builder.emailAttachments);
        this.setTemplateName(builder.templateName);
    }

    public static class Builder
    {

        private String senderEmail;
        private String receiverEmail;
        private String[] receiverEmails;
        private String messageSubject;
        private String messageBody;
        private String templateName;
        private String[] ccList;
        private List<EmailAlertAttachment> emailAttachments;

        public Builder setSender(String sender)
        {
            this.senderEmail=sender;
            return this;
        }
        public Builder setRecipient(String recipient)
        {

            this.receiverEmail=recipient;
            return this;
        }



        public Builder setRecipients(String[] receiverEmails) {
            this.receiverEmails = receiverEmails;
            return this;
        }

        public Builder setSubject(String subject)
        {
            this.messageSubject = subject;
            return this;
        }

        public Builder setBody(String body) {
            this.messageBody = body;
            return this;
        }

        public Builder setCCList(String[] ccList) {
            this.ccList = ccList;
            return this;
        }

        public Builder setAttachments(List<EmailAlertAttachment> attachments) {

            this.emailAttachments=attachments;
            return this;
        }



        public Builder setTemplateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public Email build(){
            return  new Email(this);
        }
    }


    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String[] getReceiverEmails() {
        return receiverEmails;
    }

    public void setReceiverEmails(String[] receiverEmails) {
        this.receiverEmails = receiverEmails;
    }

    public String getMessageSubject()
    {
        return messageSubject;
    }

    public void setMessageSubject(String messageSubject)
    {
        this.messageSubject = messageSubject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String[] getCcList() {
        return ccList;
    }

    public void setCcList(String[] ccList) {
        this.ccList = ccList;
    }

    public List<EmailAlertAttachment> getEmailAttachments() {
        return emailAttachments;
    }

    public void setEmailAttachments(List<EmailAlertAttachment> emailAttachments) {
        this.emailAttachments = emailAttachments;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
