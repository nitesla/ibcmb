package longbridge.services;

import longbridge.dtos.BillerDTO;
import longbridge.models.Biller;
import longbridge.models.BillerCategory;
import longbridge.models.PaymentItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface BillerService {

    void enableOrDisableCategory(Long id,Boolean value);

    void enableOrDisableBiller(Long id,Boolean value);

    String refreshPaymentItems(Long id);

//    public void runJobForBillers();

    Page<Biller> findEntities(String pattern, Pageable pageDetails);

    Page<Biller> getEntities(Pageable pageDetails);

    void enablePaymentItems(Long id, Boolean value);

    List<PaymentItem> getPaymentItemsForBiller(Long id);

    void refreshBiller();
    void RefreshAll();

    void readOnlyAmount(Long id, Boolean value);

    Biller addBiller(BillerDTO billerDto) ;

    String deleteBiller(Long id) ;

    Biller getBiller(Long id);

    Page<Biller> getBillers(Pageable pageDetails);

    Page<Biller> getBillers(String search, Pageable pageDetails);

    Page<Biller> getBillersByCategory(String category, Pageable pageDetails);

    Page<Biller> getBillerByCategory(String search, String category, Pageable pageDetails);


    Biller updateBiller(BillerDTO biller) ;

    Page<BillerCategory> getBillerCategories(Pageable pageDetails);

    Page<BillerCategory> getBillerCategories(String search, Pageable pageDetails);

    List <BillerCategory> getCategory();

    List<PaymentItem> getPaymentItems(Long id);

    void updateBillerStatus(Biller biller);

    List<Biller> getBillersByCategory(String category);

    List<Biller> getBillersCategories();

    Iterable<Biller> getBillers();

    PaymentItem getPaymentItem(Long id);

    Biller getBillerName(Long id);

    Page<Biller> findSearch(String categoryname, String search, Pageable pageable);
}
