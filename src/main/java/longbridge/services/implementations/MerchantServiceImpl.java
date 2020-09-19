package longbridge.services.implementations;

import longbridge.dtos.CategoryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.Merchant;
import longbridge.models.Product;
import longbridge.repositories.MerchantRepo;
import longbridge.repositories.ProductRepo;
import longbridge.services.MerchantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class MerchantServiceImpl implements MerchantService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MerchantRepo merchantrepo;

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private MessageSource messageSource;

	private final Locale locale = LocaleContextHolder.getLocale();



	@Override
	@Transactional
	public String addMerchant(Merchant merchant) throws InternetBankingException {

		try {
			prepare(merchant);
			merchantrepo.save(merchant);
			logger.info("Added new Merchant {} of category {}", merchant.getName(),
					merchant.getCategory());
			return messageSource.getMessage("merchant.add.success", null, locale);
		} catch (VerificationInterruptedException e) {
			return e.getMessage();
		} catch (Exception e) {
			throw new InternetBankingException(
					messageSource.getMessage("merchant.add.failure", null, locale), e);
		}
	}
	
	private void prepare(Merchant merchant) {
		if (merchant.getProducts() != null) {
			merchant.getProducts().removeIf(product -> product.getId() == null);
			merchant.getProducts().forEach(product -> {
				product.setMerchant(merchant);
				if (product.getId() < 0)
					product.setId(null);
			});
		}
	}

	@Override
	@Transactional
	public String deleteMerchant(Long id) throws InternetBankingException {
		try {
			Merchant merchant = merchantrepo.findOneById(id);
			merchantrepo.delete(merchant);
			logger.info("Merchant {} has been deleted", id);
			return messageSource.getMessage("merchant.delete.success", null, locale);
		} catch (VerificationInterruptedException e) {
			return e.getMessage();
		} catch (InternetBankingException e) {
			throw e;
		} catch (Exception e) {
			throw new InternetBankingException(
					messageSource.getMessage("merchant.delete.failure", null, locale), e);
		}
	}

	@Override
	public Merchant getMerchant(Long id) {
		return merchantrepo.findOneById(id);
	}

	@Override
	public List<Merchant> getMerchantsByCategory(String category) {
		return merchantrepo.findByCategoryAndEnabled(category,true);
	}

	@Override
	@Transactional
	public String updateMerchant(Merchant merchant) throws InternetBankingException {
		try {
			prepare(merchant);
			merchantrepo.save(merchant);
			logger.info("Updated Merchant with Id {}", merchant.getId());
			return messageSource.getMessage("merchant.update.success", null, locale);
		} catch (VerificationInterruptedException e) {
			return e.getMessage();
		} catch (InternetBankingException e) {
			throw e;
		} catch (Exception e) {
			throw new InternetBankingException(
					messageSource.getMessage("merchant.update.failure", null, locale), e);
		}

	}



	@Override
	public Page<Merchant> getMerchantsByCategory(String category, Pageable pageDetails) {
		return merchantrepo.findByCategory(category, pageDetails);
	}

	@Override
	public Page<CategoryDTO> getMerchantCategories(Pageable pageDetails) {
		List<String> categories = merchantrepo.findAllCategories();
		
		ArrayList<CategoryDTO> lst = new ArrayList<>();
		for(String category : categories) {
			lst.add(new CategoryDTO(category));
		}
        return new PageImpl<CategoryDTO>(lst);
		//return merchantrepo.findCategoriesOnly(pageDetails);
	}

	@Override
	public Page<Merchant> getMerchants(Pageable pageDetails) {
		return merchantrepo.findAll(pageDetails);
	}

	@Override
	public Iterable<Merchant> getMerchants() {
		return merchantrepo.findAll();
	}

	@Override
	public Product getProduct(Long id) {
		return productRepo.findOneById(id);
	}

	@Override
	public Iterable<String> getMerchantCategories() {
		return  merchantrepo.findAllCategories();
	}

	@Override @Transactional
	public void updateMerchantStatus(Merchant merchant) {
		merchantrepo.setEnabledFlag(merchant.getId(), merchant.isEnabled());
	}


}
