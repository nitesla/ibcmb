package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fortune on 7/20/2017.
 */
public class CorporateRequestDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String corporateType;
    @NotEmpty(message = "customerId")
    private String customerId;
    private String createdOn;
    private List<CorporateUserDTO> corporateUsers = new ArrayList<>();
    private List<CorpTransferRuleDTO> corpTransferRules = new ArrayList<>();



}
