package longbridge.services;

import longbridge.dtos.CategoryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Biller;
import longbridge.models.Merchant;
import longbridge.models.PaymentItem;
import longbridge.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface BillerService {

	String addBiller(Biller biller) throws InternetBankingException;

	String deleteBiller(Long id) throws InternetBankingException;

	Biller getBiller(Long id);

	List<Biller> getBillersByCategory(String category);

	String updateBiller(Biller biller) throws InternetBankingException;

	Page<Biller> getBillersByCategory(String category, Pageable pageDetails);

	Page<CategoryDTO> getBillerCategories(Pageable pageDetails);

	Iterable<String> getBillerCategories();

	Page<Biller> getBillers(Pageable pageDetails);

	Iterable<Biller> getBillers();

	PaymentItem getPaymentItem(Long id);
	
	void updateBillerStatus(Biller biller);

}
