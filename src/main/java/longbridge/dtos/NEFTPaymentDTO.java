package longbridge.dtos;

import longbridge.models.ItemDataStore;


import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

public class NEFTPaymentDTO {
    private String appId;
    private String bankCode;
    private String totalValue;
    private String msgId;
    private LocalDateTime date = LocalDateTime.now();
    private int itemCount;
    private LocalDateTime settlementTimeF;
    @OneToMany
    private List<ItemDataStore> pfItemDataStores;

}
