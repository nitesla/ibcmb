package longbridge.services;

import longbridge.models.Biller;
import longbridge.models.PaymentItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface BillerService {
     void updateBillers();

	 void disableBiller(Long id);
	 void enableBiller(Long id);

	Page<Biller> findEntities(String pattern, Pageable pageDetails);

	Page<Biller> getEntities(Pageable pageDetails);

	void authorizePaymentItems(Long id, Boolean value);

     List<PaymentItem> getPaymentItemForBiller(Long billerid, Long id);

//	Biller addBiller(BillerDTO billerDto) throws InternetBankingException;

//	String deleteBiller(Long id) throws InternetBankingException;
//
	Biller getBiller(Long id);
//
//	Page<Biller> getBillers(Pageable pageDetails);
//
//	Page<Biller> getBillers(String search, Pageable pageDetails);
//
//	Page<Biller> getBillersByCategory(String category, Pageable pageDetails);
//
//	Page<Biller> getBillersByCategory(String search, String category, Pageable pageDetails);
//
//
//	Biller updateBiller(BillerDTO biller) throws InternetBankingException;
//
//	Page<CategoryDTO> getBillerCategories(Pageable pageDetails);
//
//	Page<CategoryDTO> getBillerCategories(String search, Pageable pageDetails);
//
//	PaymentItem getPaymentItem(Long id);
//
//	void updateBillerStatus(Biller biller);


}