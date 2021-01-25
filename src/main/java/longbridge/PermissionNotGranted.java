package longbridge;

import org.springframework.security.access.AccessDeniedException;

public class PermissionNotGranted extends AccessDeniedException {
    private String detail;

    public PermissionNotGranted(String msg) {
        super(msg);
    }

    public PermissionNotGranted(String msg, Throwable t) {
        super(msg, t);
    }

    public PermissionNotGranted(String msg, String detail, Throwable t) {
        super(msg, t);
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
