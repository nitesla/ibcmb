package longbridge.services;

import longbridge.dtos.DirectDebitDTO;
import longbridge.dtos.PaymentDTO;
import longbridge.models.CorporateUser;
import longbridge.models.DirectDebit;
import longbridge.models.Payment;
import longbridge.models.RetailUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface DirectDebitService {
	
	/** Adds a new direct debit for the specified user and beneficiary
	 * 
	 * @param user the customer
	 * @return A message detailing the success or failure
	 */
	String addDirectDebit(RetailUser user, DirectDebitDTO directDebitDTO);

	String addCorpDirectDebit(CorporateUser user, DirectDebitDTO directDebitDTO);
	
	/** Deletes the specified directDebit for the current user
	 * 
	 * @param directDebitId the direct debit id
	 */
	String deleteDirectDebit(Long directDebitId);
	
	/** Returns the {@link DirectDebit} specified by directDebitId
	 * 
	 * @param directDebitId the direct debit id
	 * @return the DirectDebit specified by directDebitId
	 */
	DirectDebit getDirectDebit(Long directDebitId);
	
	/** This performs a Transfer from a DirectDebit object
	 * 
	 * @param directDebit the directDebit
	 */
	//void performDirectDebit(DirectDebit directDebit) throws TransferException;

	 void performDirectDebitPayment(Payment payment);

	/** This fetches all the direct debits that are due to be 
	 * performed today.
	 * @return a {@link List} of {@code DirectDebit}s that are due to be 
	 * performed today
	 */
	List<DirectDebit> getDueDirectDebits();

	List<Payment> getDuePayments();
	
	/** This fetches all the direct debits for the specified user
	 * 
	 * @param user the user containing the direct debits
	 * @return a {@link List} of {@code DirectDebit}s that are owned by the user
	 */
	Page<DirectDebitDTO> getCorpUserDirectDebitDTOS(CorporateUser user, Pageable pageable);

	//List<CorpDirectDebit> getCorpUserDirectDebits(CorporateUser corporateUser);

	//List<CorpDirectDebit> getByCOrporate(Long cordId);


	Page<DirectDebitDTO> getUserDirectDebitDTOs(RetailUser user, Pageable pageable);

	void generatePaymentsForDirectDebit(DirectDebit directDebit);

	String modifyPayment(PaymentDTO paymentDTO);

	String deletePayment(Long id);

	DirectDebit getPaymentsDirectDebit(Long paymentId);

	Collection<Payment> debitsPayments(DirectDebit directDebit);

	Collection<PaymentDTO>  getPayments(DirectDebit directDebit);


	
}
