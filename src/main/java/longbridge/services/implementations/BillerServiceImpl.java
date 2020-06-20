package longbridge.services.implementations;

import longbridge.dtos.CategoryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.Biller;
import longbridge.models.PaymentItem;
import longbridge.repositories.BillerRepo;
import longbridge.repositories.PaymentItemRepo;
import longbridge.services.BillerService;
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
public class BillerServiceImpl implements BillerService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BillerRepo billerRepo;

	@Autowired
	private PaymentItemRepo paymentItemRepo;

	@Autowired
	private MessageSource messageSource;

	private Locale locale = LocaleContextHolder.getLocale();



	@Override
	@Transactional
	public String addBiller(Biller biller) throws InternetBankingException {

		try {
			prepare(biller);
			billerRepo.save(biller);
			logger.info("Added new Biller {} of category {}", biller.getBillerName(),
					biller.getCategoryName());
			return messageSource.getMessage("biller.add.success", null, locale);
		} catch (VerificationInterruptedException e) {
			return e.getMessage();
		} catch (Exception e) {
			throw new InternetBankingException(
					messageSource.getMessage("biller.add.failure", null, locale), e);
		}
	}
	
	private void prepare(Biller biller) {
		if (biller.getPaymentItems() != null) {
			biller.getPaymentItems().removeIf(paymentItem -> {
				return paymentItem.getId() == null;
			});
			biller.getPaymentItems().stream().forEach(paymentItem -> {
				paymentItem.setBiller(biller);
				if (paymentItem.getId() < 0)
					paymentItem.setId(null);
			});
		}
	}

	@Override
	@Transactional
	public String deleteBiller(Long id) throws InternetBankingException {
		try {
			Biller biller = billerRepo.findOneById(id);
			billerRepo.delete(biller);
			logger.info("Biller {} has been deleted", id);
			return messageSource.getMessage("biller.delete.success", null, locale);
		} catch (VerificationInterruptedException e) {
			return e.getMessage();
		} catch (InternetBankingException e) {
			throw e;
		} catch (Exception e) {
			throw new InternetBankingException(
					messageSource.getMessage("biller.delete.failure", null, locale), e);
		}
	}

	@Override
	public Biller getBiller(Long id) {
		return billerRepo.findOneById(id);
	}

	@Override
	public List<Biller> getBillersByCategory(String category) {
		return billerRepo.findByCategoryAndEnabled(category,true);
	}

	@Override
	@Transactional
	public String updateBiller(Biller biller) throws InternetBankingException {
		try {
			prepare(biller);
			billerRepo.save(biller);
			logger.info("Updated Biller with Id {}", biller.getId());
			return messageSource.getMessage("biller.update.success", null, locale);
		} catch (VerificationInterruptedException e) {
			return e.getMessage();
		} catch (InternetBankingException e) {
			throw e;
		} catch (Exception e) {
			throw new InternetBankingException(
					messageSource.getMessage("biller.update.failure", null, locale), e);
		}

	}



	@Override
	public Page<Biller> getBillersByCategory(String category, Pageable pageDetails) {
		return billerRepo.findByCategory(category, pageDetails);
	}

	@Override
	public Page<CategoryDTO> getBillerCategories(Pageable pageDetails) {
		List<String> categories = billerRepo.findAllCategories();
		
		ArrayList<CategoryDTO> lst = new ArrayList<>();
		for(String category : categories) {
			lst.add(new CategoryDTO(category));
		}
		PageImpl<CategoryDTO> page = new PageImpl<CategoryDTO>(lst);
		return page;
	}

	@Override
	public Page<Biller> getBillers(Pageable pageDetails) {
		return billerRepo.findAll(pageDetails);
	}

	@Override
	public Iterable<Biller> getBillers() {
		return billerRepo.findAll();
	}

	@Override
	public PaymentItem getPaymentItem(Long id) {
		return paymentItemRepo.findOneById(id);
	}

	@Override
	public Iterable<String> getBillerCategories() {
		return  billerRepo.findAllCategories();
	}

	@Override @Transactional
	public void updateBillerStatus(Biller biller) {
		billerRepo.setEnabledFlag(biller.getId(), biller.isEnabled());
	}


}
