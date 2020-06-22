package longbridge.services;

import longbridge.dtos.BillerDTO;
import longbridge.models.Billers;

import longbridge.dtos.CategoryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Billers;
import longbridge.models.Merchant;
import longbridge.models.PaymentItem;
import longbridge.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface BillerService {
    public String updateBillersTable();

	public String disableBillerService(BillerDTO billerDTO);
	public String enableBillerService(BillerDTO billerDTO);
//    public List<Billers> getAllBillerList();
//    public Page<BillerDTO> getBillers(Pageable pageable);
//    List<BillerDTO> convertEntitiesToDTOs(Iterable<Billers> verifications);
//    public BillerDTO convertEntityToDTO(Billers verification);


	String addBiller(Billers biller) throws InternetBankingException;

	String deleteBiller(Long id) throws InternetBankingException;

	Billers getBiller(Long id);

	List<Billers> getBillersByCategory(String category);

	String updateBiller(Billers biller) throws InternetBankingException;

	Page<Billers> getBillersByCategory(String category, Pageable pageDetails);

	Page<CategoryDTO> getBillerCategories(Pageable pageDetails);

	Iterable<String> getBillerCategories();

	Page<Billers> getBillers(Pageable pageDetails);

	Iterable<Billers> getBillers();

	PaymentItem getPaymentItem(Long id);

	void updateBillerStatus(Billers biller);


}
