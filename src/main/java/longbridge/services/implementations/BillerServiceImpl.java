package longbridge.services.implementations;


import longbridge.dtos.BillerDTO;
import longbridge.dtos.CategoryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.Billers;
import longbridge.models.PaymentItem;
import longbridge.repositories.BillerRepo;
import longbridge.repositories.PaymentItemRepo;
import longbridge.services.BillerService;
import longbridge.services.IntegrationService;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private BillerRepo billerRepo;

    @Autowired
    private ModelMapper modelMapper;


	@Autowired
	private PaymentItemRepo paymentItemRepo;

	@Autowired
	private MessageSource messageSource;

	private Locale locale = LocaleContextHolder.getLocale();

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public String updateBillersTable(){
       int u =  billerRepo.deleteAllByDeleteValue("Y");
        List<BillerDTO> billerDto = integrationService.getBillers();
        for (int i = 0; i < billerDto.size(); i++){
            Billers billers = new Billers();
            billers.setBillerName(billerDto.get(i).getBillername());
            billers.setBillerId(billerDto.get(i).getBillerid());
            billers.setCategoryName(billerDto.get(i).getCategoryname());
            billers.setCategoryDescription(billerDto.get(i).getCategorydescription());
            billers.setCategoryId(billerDto.get(i).getCategoryid());
            billers.setCurrencySymbol(billerDto.get(i).getCurrencySymbol());
            billers.setCustomerField1(billerDto.get(i).getCustomerfield1());
            billers.setCustomerField2(billerDto.get(i).getCustomerfield2());
            billers.setLogoUrl(billerDto.get(i).getLogoUrl());
            billers.setDeleteValue("Y");
            billerRepo.save(billers);
        }
        return "Successfully updated";
    }


//    @Override
////    public BillerDTO convertEntityToDTO(Billers verification) {
////        return modelMapper.map(verification, BillerDTO.class);
////    }
////    @Override
////    public List<BillerDTO> convertEntitiesToDTOs(Iterable<Billers> verifications) {
////        List<BillerDTO> verificationDTOArrayList = new ArrayList<>();
////        for (Billers verification : verifications) {
////            BillerDTO verificationDTO = convertEntityToDTO(verification);
////            verificationDTOArrayList.add(verificationDTO);
////        }
////        return verificationDTOArrayList;
////    }
////
////    @Override
////    public Page<BillerDTO> getBillers(Pageable pageable) {
////        Page<Billers> page = (Page<Billers>) billerRepo.findAll();
////        List<BillerDTO> dtOs = convertEntitiesToDTOs(page.getContent());
////        long t = page.getTotalElements();
////        Page<BillerDTO> pageImpl = new PageImpl<BillerDTO>(dtOs, pageable, t);
////        return pageImpl;
////
////    }



	@Override
	@Transactional
	public String addBiller(Billers biller) throws InternetBankingException {

		try {
//			prepare(biller);
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
	
//	private void prepare(Billers biller) {
//		if (biller.getPaymentItems() != null) {
//			biller.getPaymentItems().removeIf(paymentItem -> {
//				return paymentItem.getId() == null;
//			});
//			biller.getPaymentItems().stream().forEach(paymentItem -> {
//				paymentItem.setBiller(biller);
//				if (paymentItem.getId() < 0)
//					paymentItem.setId(null);
//			});
//		}
//	}

	@Override
	@Transactional
	public String deleteBiller(Long id) throws InternetBankingException {
		try {
			Billers biller = billerRepo.findOneById(id);
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
	public Billers getBiller(Long id) {
		return billerRepo.findOneById(id);
	}

	@Override
	public List<Billers> getBillersByCategory(String category) {
		return billerRepo.findByCategoryNameAndEnabled(category,true);
	}

	@Override
	@Transactional
	public String updateBiller(Billers biller) throws InternetBankingException {
		try {
//			prepare(biller);
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
	public Page<Billers> getBillersByCategory(String category, Pageable pageDetails) {
		return billerRepo.findByCategoryName(category, pageDetails);
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
	public Page<Billers> getBillers(Pageable pageDetails) {
		return billerRepo.findAll(pageDetails);
	}

	@Override
	public Iterable<Billers> getBillers() {
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
	public void updateBillerStatus(Billers biller) {
		billerRepo.setEnabledFlag(biller.getId(), biller.isEnabled());
	}



}
