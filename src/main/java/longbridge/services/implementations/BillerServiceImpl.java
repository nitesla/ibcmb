package longbridge.services.implementations;


import longbridge.dtos.BillerCategoryDTO;
import longbridge.dtos.BillerDTO;
import longbridge.dtos.PaymentItemDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Biller;
import longbridge.models.BillerCategory;
import longbridge.models.PaymentItem;
import longbridge.repositories.BillerCategoryRepo;
import longbridge.repositories.BillerRepo;
import longbridge.repositories.PaymentItemRepo;
import longbridge.services.BillerService;
import longbridge.services.IntegrationService;
import org.apache.commons.lang3.NotImplementedException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
@Transactional
public class BillerServiceImpl implements BillerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private BillerRepo billerRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PaymentItemRepo paymentItemRepo;
    @Autowired
    private BillerCategoryRepo billerCategoryRepo;
    @Autowired
    private MessageSource messageSource;
    private Locale locale = LocaleContextHolder.getLocale();




    @Override
    public void RefreshAll() {
        // fetch biller from quickteller
        updateBillers();
        // fetch categories from quickteller
        refreshCategories();
    }

    @Override
    public void refreshBiller(){
        updateBillers();
    }



    private void updateBillers() {
        logger.info("UPDATING BILLERS!!");
        List<BillerDTO> billerDTOList = integrationService.getBillers();
        List<Biller> updatedBillers = compareAndUpdateBillers(billerDTOList);
        billerRepo.removeObsolete(updatedBillers.stream().map(Biller::getId).collect(Collectors.toList()));
    }



    private List<Biller> compareAndUpdateBillers(List<BillerDTO> updatedBillers) {
        List<Biller> billers = new ArrayList<>();
        updatedBillers.forEach(updatedBiller -> {
            Biller storedBiller = billerRepo.findByBillerId(updatedBiller.getBillerid());
            if (storedBiller == null) {
                Biller biller = createBiller(updatedBiller);
                biller.setEnabled(true);
                billers.add(biller);
            } else {
                Biller biller = createBiller(updatedBiller);
                biller.setEnabled(storedBiller.isEnabled());
                biller.setId(storedBiller.getId());
                billers.add(biller);
            }
        });
        return billerRepo.saveAll(billers);
    }

    private Biller createBiller(BillerDTO dto) {
        Biller biller = new Biller();
        biller.setBillerName(dto.getBillername());
        biller.setBillerId(dto.getBillerid());
        biller.setCategoryName(dto.getCategoryname());
        biller.setCategoryDescription(dto.getCategorydescription());
        biller.setCategoryId(dto.getCategoryid());
        biller.setCurrencySymbol(dto.getCurrencySymbol());
        biller.setCustomerField1(dto.getCustomerfield1());
        biller.setCustomerField2(dto.getCustomerfield2());
        biller.setLogoUrl(dto.getLogoUrl());
        biller.setSupportemail(dto.getSupportemail());
        biller.setShortname(dto.getShortName());
        biller.setPaydirectInstitutionId(dto.getPaydirectInstitutionId());
        biller.setPaydirectProductId(dto.getPaydirectProductId());
        biller.setSurcharge(dto.getSurcharge());
        biller.setCurrencyCode(dto.getCurrencyCode());
        biller.setNarration(dto.getNarration());
        billerRepo.save(biller);
        return biller;
    }

    @Override
    public void enablePaymentItems(Long id, Boolean value) {
        if (value == false){
            Boolean newValue = true;
            paymentItemRepo.enablePaymentItem(id, newValue);
            logger.info("Item with id=[{}] is enabled = {}", id, newValue);
        }else{
            Boolean newValue = false;
            paymentItemRepo.enablePaymentItem(id, newValue);
            logger.info("Item with id=[{}] is enabled = {}", id, newValue);
        }


    }


    @Override
    public void readOnlyAmount(Long id, Boolean value){
        logger.info("readonly value = {}",value);
        if (value == false){
            Boolean newValue = true;
            paymentItemRepo.readOnly(id, newValue);
            logger.info("Item with id=[{}] set read-only = {}", id, newValue);
        }else{
            Boolean newValue = false;
            paymentItemRepo.readOnly(id, newValue);
            logger.info("Item with id=[{}] set read-only = {}", id, newValue);
        }

    }


    @Override
    public void enableOrDisableCategory(Long id,Boolean value) {
        if (value == false){
            Boolean newValue = true;
            billerCategoryRepo.enableOrDisableCategory(id, newValue);
            logger.info("Item with id=[{}] is enabled = {}", id, newValue);
        }else{
            Boolean newValue = false;
            billerCategoryRepo.enableOrDisableCategory(id, newValue);
            logger.info("Item with id=[{}] is enabled = {}", id, newValue);
        }

    }

    @Override
    public void enableOrDisableBiller(Long id,Boolean value) {
        if (value == false){
            Boolean newValue = true;
            billerRepo.enableOrDisableBiller(id, newValue);
            logger.info("Item with id=[{}] is enabled = {}", id, newValue);
        }else{
            Boolean newValue = false;
            billerRepo.enableOrDisableBiller(id, newValue);
            logger.info("Item with id=[{}] is enabled = {}", id, newValue);
        }
    }


    @Override
    public Page<Biller> findEntities(String pattern, Pageable pageDetails) {
        return billerRepo.findUsingPattern(pattern, pageDetails);
    }

    @Override
    public Page<Biller> getEntities(Pageable pageDetails) {
        return billerRepo.findAll(pageDetails);
    }


    @Override
    public Biller getBiller(Long id) {
        return billerRepo.findOneById(id);
    }

    @Override
    public Page<Biller> getBillers(Pageable pageDetails) {
        return null;
    }

    @Override
    public Page<Biller> getBillers(String search, Pageable pageDetails) {
        throw new NotImplementedException("Not yet done");
    }

    @Override
    public Page<Biller> getBillersByCategory(String category, Pageable pageDetails) {
        Page<Biller> categoryName = billerRepo.findByCategoryName(category, pageDetails);
        return categoryName;
    }

    @Override
    public Page<Biller> getBillerByCategory(String search, String category, Pageable pageDetails) {
        return null;
    }

    @Override
    public Biller updateBiller(BillerDTO biller) throws InternetBankingException {
        return null;
    }

    private BillerCategory createBillerCategory(BillerCategoryDTO billerCategory){
        BillerCategory category = new BillerCategory();
        category.setCategoryName(billerCategory.getCategoryname());
        category.setCategoryDescription(billerCategory.getCategorydescription());
        category.setCategoryId(billerCategory.getCategoryid());
        billerCategoryRepo.save(category);
        return category;
    }


    private List<BillerCategory> compareAndUpdateCategories(List<BillerCategoryDTO> updatedCategorys){
        List<BillerCategory> billerCategory = new ArrayList<>();
        updatedCategorys.forEach(updatedCategory -> {
            BillerCategory storedCategory = billerCategoryRepo.findByCategoryName(updatedCategory.getCategoryname());
            logger.info("STORED CATEGORY {}",storedCategory);
            if (storedCategory == null) {
                BillerCategory category = createBillerCategory(updatedCategory);
                category.setEnabled(true);
                billerCategory.add(category);
            } else {
                BillerCategory category = createBillerCategory(updatedCategory);
                category.setEnabled(storedCategory.isEnabled());
//                category.setId(storedCategory.getId());
                billerCategory.add(category);
            }
        });
            return billerCategoryRepo.saveAll(billerCategory);
    }
    

    private void refreshCategories(){
        logger.info("UPDATING CATEGORIES!!");
        List<BillerCategoryDTO> getBillerCategories = integrationService.getBillerCategories();
        List<BillerCategory> updatedCategories = compareAndUpdateCategories(getBillerCategories);
        billerCategoryRepo.removeObsolete(updatedCategories.stream().map(BillerCategory::getId).collect(Collectors.toList()));
    }



    @Override
    public Page<BillerCategory> getBillerCategories(Pageable pageDetails) {
        Page<BillerCategory> all = billerCategoryRepo.findAll(pageDetails);
        logger.info("All CATEGORIES {}",all);
       return all;
    }

    @Override
    public Page<BillerCategory> getBillerCategories(String search, Pageable pageDetails) {
        return billerCategoryRepo.findUsingPattern(search,pageDetails);
    }

    @Override
    public List<PaymentItem> getPaymentItems(Long id) {
        return paymentItemRepo.findByBillerIdAndEnabled(id, true);
    }

    @Override
    public PaymentItem getPaymentItem(Long id) {
        return paymentItemRepo.findByPaymentItemId(id);
    }

    @Override
    public Biller getBillerName(Long id) {
        return billerRepo.findByBillerId(id);
    }


    @Override
    public void updateBillerStatus(Biller biller) {

    }


    private void updatePaymentItems(Long billerId){
        logger.info("UPDATING PAYMENT ITEMS!!");
        List<PaymentItemDTO> getBillerPaymentItems = integrationService.getPaymentItems(billerId);
        List<PaymentItem> updatedPaymentItems = compareAndUpdatePaymentItems(getBillerPaymentItems);
        paymentItemRepo.removeObsolete(updatedPaymentItems.stream().map(PaymentItem::getId).collect(Collectors.toList()));
    }

    @Override
    public void refreshPaymentItems(Long billerId){
        updatePaymentItems(billerId);
    }


    @Override
    public List<PaymentItem> getPaymentItemsForBiller(Long id) {
        Biller biller = billerRepo.findOneById(id);
        List<PaymentItem> paymentItemList = paymentItemRepo.findByBillerId(biller.getBillerId());
        return paymentItemList;
    }




    @Override
    public Biller addBiller(BillerDTO billerDto) throws InternetBankingException {
        return null;
    }

    @Override
    public String deleteBiller(Long id) throws InternetBankingException {
        return null;
    }

    private PaymentItem createPaymentitem(PaymentItemDTO paymentItemDTO){
        PaymentItem newPaymentItem = new PaymentItem();
        newPaymentItem.setBillerId(paymentItemDTO.getBillerid());
        newPaymentItem.setAmount(paymentItemDTO.getAmount());
        newPaymentItem.setCode(paymentItemDTO.getCode());
        newPaymentItem.setPaymentItemId(paymentItemDTO.getPaymentitemid());
        newPaymentItem.setCurrencyCode(paymentItemDTO.getCurrencyCode());
        newPaymentItem.setIsAmountFixed(paymentItemDTO.getIsAmountFixed());
        newPaymentItem.setItemCurrencySymbol(paymentItemDTO.getItemCurrencySymbol());
        newPaymentItem.setPaymentCode(paymentItemDTO.getPaymentCode());
        newPaymentItem.setPaymentItemName(paymentItemDTO.getPaymentitemname());
        newPaymentItem.setReadonly(paymentItemDTO.getIsAmountFixed());
        paymentItemRepo.save(newPaymentItem);
        return newPaymentItem;
    }


    private List<PaymentItem> compareAndUpdatePaymentItems(List<PaymentItemDTO> paymentItems) {
        List<PaymentItem> items = new ArrayList<>();
        paymentItems.forEach(paymentItem -> {
            PaymentItem getStoredItem = paymentItemRepo.findByPaymentItemId(paymentItem.getPaymentitemid());
            if (getStoredItem == null) {
                PaymentItem newPaymentItem = createPaymentitem(paymentItem);
                newPaymentItem.setEnabled(true);
                items.add(newPaymentItem);
            } else {
                PaymentItem newPaymentItem = createPaymentitem(paymentItem);
                newPaymentItem.setEnabled(getStoredItem.isEnabled());
                newPaymentItem.setReadonly(getStoredItem.getReadonly());
                items.add(newPaymentItem);

            }
        });
        return paymentItemRepo.saveAll(items);
    }

    @Override
    public List<Biller> getBillersByCategory(String category) {
        return billerRepo.findByCategoryNameAndEnabled(category, true);
    }

    @Override
    public Iterable<Biller> getBillers() {
        return billerRepo.findAll();
    }



    @Override
    public List<Biller> getBillersCategories() {
        return  billerRepo.findAll();
    }

    @Override
    public List <BillerCategory> getCategory() {
        return billerCategoryRepo.findAll();
    }


    @Override
    public Page<Biller> findSearch(String categoryname, String search, Pageable pageable){
        Page<Biller> searchDetails = billerRepo.findByBillerNameContainsIgnoreCaseAndCategoryName(search,categoryname,pageable);
        return  searchDetails;
    }

}
