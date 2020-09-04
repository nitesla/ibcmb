
package longbridge.response;

import longbridge.dtos.BillerDTO;

import java.util.List;

public class BillerResponse {

    private List<BillerDTO> billers = null;

    public List<BillerDTO> getBillers() {
        return billers;
    }

    public void setBillers(List<BillerDTO> billers) {
        this.billers = billers;
    }

}
