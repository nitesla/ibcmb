package longbridge.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Where(clause = "delFlag='N'")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NeftPayment extends AbstractEntity {

    private String appId;

    private String bankCode;

    private String totalValue;

    private String msgId;

    private LocalDateTime date = LocalDateTime.now();

    private int itemCount;


    @Column(name = "set_time_f")
    private LocalDateTime settlementTimeF;

    private String status;
    @OneToMany
    @JoinTable(name = "neftpayment_itemdatastore")
    private List<ItemDataStore> itemDataStores;

    public NeftPayment() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(String totalValue) {
        this.totalValue = totalValue;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public LocalDateTime getSettlementTimeF() {
        return settlementTimeF;
    }

    public void setSettlementTimeF(LocalDateTime settlementTimeF) {
        this.settlementTimeF = settlementTimeF;
    }

    public List<ItemDataStore> getItemDataStores() {
        return itemDataStores;
    }

    public void setItemDataStores(List<ItemDataStore> itemDataStores) {
        this.itemDataStores = itemDataStores;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NEFTPayment{" +
                "appId='" + appId + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", totalValue='" + totalValue + '\'' +
                ", msgId='" + msgId + '\'' +
                ", date=" + date +
                ", itemCount=" + itemCount +
                ", settlementTimeF=" + settlementTimeF +
                ", pfItemDataStores=" + itemDataStores +
                '}';
    }
}
