package longbridge.servicerequests.client;

public class RequestStats {

    public RequestStats() {
    }

    public RequestStats(Integer total, Integer unattended) {
        this.total = total;
        this.unattended = unattended;
    }

    private Integer total;
    private Integer unattended;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getUnattended() {
        return unattended;
    }

    public void setUnattended(Integer unattended) {
        this.unattended = unattended;
    }
}
