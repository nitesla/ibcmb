package longbridge.services.implementations;


import longbridge.dtos.BillerDTO;
import longbridge.dtos.PaymentItemDTO;
import longbridge.models.Biller;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultString;


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
    public void updateBillers(){
        List<BillerDTO> billerDto = integrationService.getBillers();
		logger.info("UPDATING BILLERS!!");
		List<Biller> updatedBillers = compareAndUpdateBillers(billerDto);
        List<Biller> obsoleteBillers = billerRepo.findByBillerIdNotIn(updatedBillers.stream().map(biller -> biller.getBillerId()).collect(Collectors.toList()));
        obsoleteBillers.forEach(biller -> {
            biller.setDelFlag("Y");
            billerRepo.save(biller);
        });
    }

    private List<Biller> compareAndUpdateBillers(List<BillerDTO> updatedBillers){
        List<Biller> billers = new ArrayList<>();
                updatedBillers.forEach(updatedBiller -> {
                    Biller existingBiller = billerRepo.findByBillerId(updatedBiller.getBillerid());
                    if (existingBiller == null) {
                        Biller biller = new Biller();
                        biller.setBillerName(updatedBiller.getBillername());
                        biller.setBillerId(updatedBiller.getBillerid());
                        biller.setCategoryName(updatedBiller.getCategoryname());
                        biller.setCategoryDescription(updatedBiller.getCategorydescription());
                        biller.setCategoryId(updatedBiller.getCategoryid());
                        biller.setCurrencySymbol(updatedBiller.getCurrencySymbol());
                        biller.setCustomerField1(updatedBiller.getCustomerfield1());
                        biller.setCustomerField2(updatedBiller.getCustomerfield2());
                        biller.setLogoUrl(updatedBiller.getLogoUrl());
                        billerRepo.save(biller);
                        billers.add(biller);
                    } else {
                        if (!defaultString(existingBiller.getBillerName()).trim().equals(defaultString(updatedBiller.getBillername()).trim())) {
                            existingBiller.setBillerName(updatedBiller.getBillername());
                        }
                        if (!defaultString(existingBiller.getCategoryName()).trim().equals(defaultString(updatedBiller.getCategoryname()).trim())) {
                            existingBiller.setCategoryName(updatedBiller.getCategoryname());
                        }
                        if (!defaultString(existingBiller.getCategoryDescription()).trim().equals(defaultString(updatedBiller.getCategorydescription()).trim())) {
                            existingBiller.setCategoryDescription(updatedBiller.getCategorydescription());
                        }
                        if (!defaultString(existingBiller.getCurrencySymbol()).trim().equals(defaultString(updatedBiller.getCurrencySymbol()).trim())) {
                            existingBiller.setCurrencySymbol(updatedBiller.getCurrencySymbol());
                        }
                        if (!defaultString(existingBiller.getLogoUrl()).trim().equals(defaultString(updatedBiller.getLogoUrl()).trim())) {
                            existingBiller.setLogoUrl(updatedBiller.getLogoUrl());
                        }
                        if (!defaultString(existingBiller.getCustomerField1()).trim().equals(defaultString(updatedBiller.getCustomerfield1()).trim())) {
                            existingBiller.setCustomerField1(updatedBiller.getCustomerfield1());
                        }
                        if (!defaultString(existingBiller.getCustomerField2()).trim().equals(defaultString(updatedBiller.getCustomerfield2()).trim())) {
                            existingBiller.setCustomerField2(updatedBiller.getCustomerfield2());
                        }
                        billers.add(existingBiller);
                        billerRepo.save(existingBiller);
                    }
                });
        return billers;
    }

    @Override
    public void authorizePaymentItems(Long id, Boolean value){
        logger.info("value "+value);
        if (value == true){
            int disableItem = paymentItemRepo.disablePaymentItem(id);
            logger.info("item disabled!!!");
        } else if (value == false){
            int enableItem = paymentItemRepo.enablePaymentItem(id);
            logger.info("item enabled!!!!");
        }

    }


    @Override
    public void disableBiller(Long id){
        int disableBiller = billerRepo.disableBiller(id);
        if (disableBiller < 0){
            throw new RuntimeException("Error disabling biller, Please try again!");
        }

    }

    @Override
    public void enableBiller(Long id){
        int enableBillers = billerRepo.enableBiller(id);
		logger.info("enabling biller service");
        if (enableBillers < 0){
            throw new RuntimeException("Error disabling biller, Please try again!");
        }
    }


    @Override
    public Page<Biller> findEntities(String pattern, Pageable pageDetails) {
        return billerRepo.findUsingPattern(pattern,pageDetails);
    }

    @Override
    public Page<Biller> getEntities(Pageable pageDetails)
    {
        return billerRepo.findAll(pageDetails);
    }



    @Override
    public Biller getBiller(Long id) {
        return billerRepo.findOneById(id);
    }


    	@Override
        public List<PaymentItem> getPaymentItemForBiller(Long billerid,Long id) {
        Biller biller = billerRepo.findOneById(id);
            Long billerId = biller.getBillerId();
            List<PaymentItemDTO> getBillerPaymentItems = integrationService.getPaymentItems(billerId);
            logger.info("Updating payment items");
            List<PaymentItem> updatedPaymentItems = compareAndUpdatePaymentItemTable(getBillerPaymentItems);
            List<PaymentItem> obsoleteItems = paymentItemRepo.findByPaymentItemIdNotIn(updatedPaymentItems.stream()
                    .map(updatedPaymentItem -> updatedPaymentItem.getPaymentItemId()).collect(Collectors.toList()));
            obsoleteItems.forEach(paymentItem -> {
                    paymentItem.setDelFlag("Y");
                paymentItemRepo.save(paymentItem);
            });
            return updatedPaymentItems;
	}


	public List<PaymentItem> compareAndUpdatePaymentItemTable(List<PaymentItemDTO> paymentItems){
        List<PaymentItem> items = new ArrayList<>();
        paymentItems.forEach(paymentItem -> {
            PaymentItem getItem = paymentItemRepo.findByPaymentItemId(paymentItem.getPaymentitemid());
            if (getItem == null){
                PaymentItem newPaymentItem = new PaymentItem();
                newPaymentItem.setBillerId(paymentItem.getBillerid());
                newPaymentItem.setAmount(paymentItem.getAmount());
                newPaymentItem.setCode(paymentItem.getCode());
                newPaymentItem.setPaymentItemId(paymentItem.getPaymentitemid());
                newPaymentItem.setCurrencyCode(paymentItem.getCurrencyCode());
                newPaymentItem.setIsAmountFixed(paymentItem.getIsAmountFixed());
                newPaymentItem.setItemCurrencySymbol(paymentItem.getItemCurrencySymbol());
                newPaymentItem.setPaymentCode(paymentItem.getPaymentCode());
                newPaymentItem.setPaymentItemName(paymentItem.getPaymentitemname());
                paymentItemRepo.save(newPaymentItem);
                items.add(newPaymentItem);
            } else {

                if (!defaultString(getItem.getPaymentItemName()).trim().equals(defaultString(paymentItem.getPaymentitemname()).trim())) {
                    getItem.setPaymentItemName(paymentItem.getPaymentitemname());
                }
                if (!defaultString(getItem.getItemCurrencySymbol()).trim().equals(defaultString(paymentItem.getItemCurrencySymbol()).trim())) {
                    getItem.setItemCurrencySymbol(paymentItem.getItemCurrencySymbol());
                }
                if (!getItem.getPaymentCode().equals(paymentItem.getPaymentCode())){
                    getItem.setPaymentCode(paymentItem.getPaymentCode());
                }
                if (!getItem.getCode().equals(paymentItem.getCode())){
                    getItem.setCode(paymentItem.getCode());
                }
                if (!getItem.getAmount().equals(paymentItem.getAmount())){
                    getItem.setAmount(paymentItem.getAmount());
                }
                if (!getItem.getIsAmountFixed().equals(paymentItem.getIsAmountFixed())){
                    getItem.setIsAmountFixed(paymentItem.getIsAmountFixed());
                }
                if (!getItem.getPaymentItemId().equals(paymentItem.getPaymentitemid())){
                    getItem.setPaymentItemId(paymentItem.getPaymentitemid());
                }
                if (!getItem.getCurrencyCode().equals(paymentItem.getCurrencyCode())){
                    getItem.setCurrencyCode(paymentItem.getCurrencyCode());
                }
                items.add(getItem);
                paymentItemRepo.save(getItem);
            }
        });
        return items;
    }

//    @Override
////    public BillerDTO convertEntityToDTO(Biller verification) {
////        return modelMapper.map(verification, BillerDTO.class);
////    }
////    @Override
////    public List<BillerDTO> convertEntitiesToDTOs(Iterable<Biller> verifications) {
////        List<BillerDTO> verificationDTOArrayList = new ArrayList<>();
////        for (Biller verification : verifications) {
////            BillerDTO verificationDTO = convertEntityToDTO(verification);
////            verificationDTOArrayList.add(verificationDTO);
////        }
////        return verificationDTOArrayList;
////    }
////
////    @Override
////    public Page<BillerDTO> getBillers(Pageable pageable) {
////        Page<Biller> page = (Page<Biller>) billerRepo.findAll();
////        List<BillerDTO> dtOs = convertEntitiesToDTOs(page.getContent());
////        long t = page.getTotalElements();
////        Page<BillerDTO> pageImpl = new PageImpl<BillerDTO>(dtOs, pageable, t);
////        return pageImpl;
////
////    }


//
//	@Override
//	@Transactional
//	public String addBiller(Biller biller) throws InternetBankingException {
//
//		try {
////			prepare(biller);
//			billerRepo.save(biller);
//			logger.info("Added new Biller {} of category {}", biller.getBillerName(),
//					biller.getCategoryName());
//			return messageSource.getMessage("biller.add.success", null, locale);
//		} catch (VerificationInterruptedException e) {
//			return e.getMessage();
//		} catch (Exception e) {
//			throw new InternetBankingException(
//					messageSource.getMessage("biller.add.failure", null, locale), e);
//		}
//	}
//
//	private void prepare(Biller biller) {
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

//	@Override
//	@Transactional
//	public String deleteBiller(Long id) throws InternetBankingException {
//		try {
//			Biller biller = billerRepo.findOneById(id);
//			billerRepo.delete(biller);
//			logger.info("Biller {} has been deleted", id);
//			return messageSource.getMessage("biller.delete.success", null, locale);
//		} catch (VerificationInterruptedException e) {
//			return e.getMessage();
//		} catch (InternetBankingException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new InternetBankingException(
//					messageSource.getMessage("biller.delete.failure", null, locale), e);
//		}
//	}



//	@Override
//	public List<Biller> getBillersByCategory(String category) {
//		return billerRepo.findByCategoryNameAndEnabled(category,true);
//	}

//	@Override
//	@Transactional
//	public String updateBiller(Biller biller) throws InternetBankingException {
//		try {
////			prepare(biller);
//			billerRepo.save(biller);
//			logger.info("Updated Biller with Id {}", biller.getId());
//			return messageSource.getMessage("biller.update.success", null, locale);
//		} catch (VerificationInterruptedException e) {
//			return e.getMessage();
//		} catch (InternetBankingException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new InternetBankingException(
//					messageSource.getMessage("biller.update.failure", null, locale), e);
//		}
//
//	}



//	@Override
//	public Page<Biller> getBillersByCategory(String category, Pageable pageDetails) {
//		return billerRepo.findByCategoryName(category, pageDetails);
//	}

//	@Override
//	public Page<CategoryDTO> getBillerCategories(Pageable pageDetails) {
//		List<String> categories = billerRepo.findAllCategories();
//
//		ArrayList<CategoryDTO> lst = new ArrayList<>();
//		for(String category : categories) {
//			lst.add(new CategoryDTO(category));
//		}
//		PageImpl<CategoryDTO> page = new PageImpl<CategoryDTO>(lst);
//		return page;
//	}
//
//	@Override
//	public Page<Biller> getBillers(Pageable pageDetails) {
//		return billerRepo.findAll(pageDetails);
//	}

//	@Override
//	public Iterable<Biller> getBillers() {
//		return billerRepo.findAll();
//	}


//
////
////	@Override
////	public Iterable<String> getBillerCategories() {
////		return  billerRepo.findAllCategories();
////	}
////
//
//	@Override @Transactional
//	public void updateBillerStatus(Biller biller) {
//		billerRepo.setEnabledFlag(biller.getId(), biller.isEnabled());
//	}



}