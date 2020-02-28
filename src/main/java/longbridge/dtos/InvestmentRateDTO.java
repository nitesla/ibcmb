package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.utils.PrettySerializer;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InvestmentRateDTO {
    @JsonProperty("DT_RowId")
    private Long id;
    @NotEmpty(message="investmentName")
    private String investmentName;
    @NotNull(message="tenor")
    private int tenor;
    @NotNull(message = "value")
    private BigDecimal value;
    @NotNull(message="maxAmount")
    private int maxAmount;
    @NotNull(message="minAmount")
    private int minAmount;

    private int version;

    public String getInvestmentName() {
        return investmentName;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getTenor() {
        return tenor;
    }

    public void setTenor(int tenor) {
        this.tenor = tenor;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "InvestmentRate{" +
                "investmentName='" + investmentName + '\'' +
                ", tenor='" + tenor + '\'' +
                ", value='" + value + '\'' +
                ", maxAmount=" + maxAmount +
                ", minAmount=" + minAmount +
                '}';
    }
}