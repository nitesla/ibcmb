package longbridge.response;

import longbridge.dtos.BillerCategoryDTO;

import java.util.List;

public class BillerCategoryResponse {

    private List<BillerCategoryDTO> categorys = null;

    public List<BillerCategoryDTO> getCategorys() {
        return categorys;
    }
    public void setCategorys(List<BillerCategoryDTO> categorys) {
        this.categorys = categorys;
    }
}
