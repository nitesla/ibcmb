package longbridge.response;

import longbridge.dtos.QuicktellerBankCodeDTO;

import java.util.List;

public class BankCodeResponse {

    private List<QuicktellerBankCodeDTO> codes = null;


    public List<QuicktellerBankCodeDTO> getCodes() {
        return codes;
    }

    public void setCodes(List<QuicktellerBankCodeDTO> codes) {
        this.codes = codes;
    }
}
