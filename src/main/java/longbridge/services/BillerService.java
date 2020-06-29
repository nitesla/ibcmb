package longbridge.services;

import longbridge.dtos.BillerCategoryDTO;
import longbridge.dtos.BillerDTO;
import longbridge.dtos.CategoryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Biller;
import longbridge.models.BillerCategory;
import longbridge.models.PaymentItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface BillerService {

    void enableOrDisableCategory(Long id,Boolean value);

    void enableOrDisableBiller(Long id,Boolean value);

    void refreshPaymentItems(Long id);


    Page<Biller> findEntities(String pattern, Pageable pageDetails);

    Page<Biller> getEntities(Pageable pageDetails);

    void enablePaymentItems(Long id, Boolean value);

    List<PaymentItem> getPaymentItemsForBiller(Long id);

    void RefreshBiller();

    void readOnlyAmount(Long id, Boolean value);

    Biller addBiller(BillerDTO billerDto) throws InternetBankingException;

    String deleteBiller(Long id) throws InternetBankingException;

    Biller getBiller(Long id);

    Page<Biller> getBillers(Pageable pageDetails);

    Page<Biller> getBillers(String search, Pageable pageDetails);

    Page<Biller> getBillersByCategory(String category, Pageable pageDetails);

    Page<Biller> getBillerByCategory(String search, String category, Pageable pageDetails);


    Biller updateBiller(BillerDTO biller) throws InternetBankingException;

    Page<BillerCategory> getBillerCategories(Pageable pageDetails);

    Page<BillerCategory> getBillerCategories(String search, Pageable pageDetails);

    PaymentItem getPaymentItem(Long id);

    void updateBillerStatus(Biller biller);

    List<Biller> getBillersByCategory(String category);

    List<Biller> getBillersCategories();

    Iterable<Biller> getBillers();

    List<PaymentItem> getPaymentItem(String billers);



}
