package longbridge.billerresponse;

import longbridge.dtos.BillerCategoryDTO;
import longbridge.dtos.CategoryDTO;

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
