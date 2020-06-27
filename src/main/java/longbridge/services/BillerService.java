package longbridge.services;


import longbridge.models.Biller;
import longbridge.models.PaymentItem;

import java.util.List;


public interface BillerService {

	String updateBillersTable();
	
	List<Biller> getBillersByCategory(String category);

	List<Biller> getBillersCategories();

	Iterable<Biller> getBillers();

	List<PaymentItem> getPaymentItem(String billers);

}
