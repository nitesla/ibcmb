package longbridge.services.implementations;


import longbridge.dtos.BillerDTO;
import longbridge.models.Biller;
import longbridge.models.PaymentItem;
import longbridge.repositories.BillersRepo;
import longbridge.repositories.PaymentItemRepo;
import longbridge.services.BillerService;
import longbridge.services.IntegrationService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;


@Service
public class BillerServiceImpl implements BillerService {

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private BillersRepo billerRepo;

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
            Biller biller = new Biller();
            biller.setBillerName(billerDto.get(i).getBillername());
            biller.setBillerId(billerDto.get(i).getBillerid());
            biller.setCategoryName(billerDto.get(i).getCategoryname());
            biller.setCategoryDescription(billerDto.get(i).getCategorydescription());
            biller.setCategoryId(billerDto.get(i).getCategoryid());
            biller.setCurrencySymbol(billerDto.get(i).getCurrencySymbol());
            biller.setCustomerField1(billerDto.get(i).getCustomerfield1());
            biller.setCustomerField2(billerDto.get(i).getCustomerfield2());
            biller.setLogoUrl(billerDto.get(i).getLogoUrl());
            biller.setDeleteValue("Y");
            billerRepo.save(biller);
        }
        return "Successfully updated";
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
	public List<PaymentItem> getPaymentItem(String billers) {
		return paymentItemRepo.findByPaymentItemName(billers);
	}

	@Override
	public List<Biller> getBillersCategories() {
		return  billerRepo.findAll();
	}



}
