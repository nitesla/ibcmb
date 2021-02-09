package longbridge.services;

import longbridge.dtos.CategoryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Merchant;
import longbridge.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 
 * @author Yemi Dalley
 */
public interface MerchantService {

	String addMerchant(Merchant merchant) ;

	String deleteMerchant(Long id) ;

	Merchant getMerchant(Long id);

	List<Merchant> getMerchantsByCategory(String category);

	String updateMerchant(Merchant merchant) ;

	Page<Merchant> getMerchantsByCategory(String category, Pageable pageDetails);

	Page<CategoryDTO> getMerchantCategories(Pageable pageDetails);

	Iterable<String> getMerchantCategories();

	Page<Merchant> getMerchants(Pageable pageDetails);

	Iterable<Merchant> getMerchants();

	Product getProduct(Long id);
	
	void updateMerchantStatus(Merchant merchant);

}
