package longbridge.servicerequests.client;

import longbridge.models.UserType;

import javax.persistence.Lob;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceRequestDTO {

    private  String data;
    private  String formatted;
    private Long id;
    private List<CommentDTO> comments;
    private long entityId;
    private UserType userType;
    private String requestName;
    private String requester;
    @Lob
    private String body;
    private String currentStatus;
    private Date dateRequested;
    private Long serviceReqConfigId;

    public ServiceRequestDTO() {
    }

    public ServiceRequestDTO(ServiceRequest request) {
        this.formatted = request.getFormatted();
        this.data = request.getData();
        this.currentStatus = request.getCurrentStatus();
        this.entityId = request.getEntityId();
        this.userType = request.getUserType();
        this.requestName = request.getRequestName();
        this.serviceReqConfigId = request.getServiceReqConfigId();
        this.dateRequested = request.getDateRequested();
        this.requester = request.getRequester();
        this.serviceReqConfigId = request.getServiceReqConfigId();
        this.id = request.getId();
        this.comments= request.getComments().stream()
                .map(this::makeComment).collect(Collectors.toList());
    }

    private CommentDTO makeComment(Comment cmt){
        CommentDTO dto = new CommentDTO();
        dto.comments = cmt.getComments();
        dto.createdBy = cmt.getCreatedBy().getUserName();
        dto.status = cmt.getStatus();
        dto.createdOn = cmt.getCreatedOn();
        return dto;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public Long getServiceReqConfigId() {
        return serviceReqConfigId;
    }

    public void setServiceReqConfigId(Long serviceReqConfigId) {
        this.serviceReqConfigId = serviceReqConfigId;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    class CommentDTO {
        private String status;
        private String comments;
        private String createdBy;
        private Date createdOn;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
        }
    }
}
