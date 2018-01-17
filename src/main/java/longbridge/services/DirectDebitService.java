package longbridge.services;

import longbridge.dtos.DirectDebitDTO;
import longbridge.exception.TransferException;
import longbridge.models.DirectDebit;
import longbridge.models.RetailUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DirectDebitService {
	
	/** Adds a new direct debit for the specified user and beneficiary
	 * 
	 * @param user the customer
	 * @return A message detailing the success or failure
	 */
	String addDirectDebit(RetailUser user, DirectDebitDTO directDebitDTO);
	
	/** Deletes the specified directDebit for the current user
	 * 
	 * @param directDebitId the direct debit id
	 */
	void deleteDirectDebit(Long directDebitId);
	
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
	void performDirectDebit(DirectDebit directDebit) throws TransferException;

	/** This fetches all the direct debits that are due to be 
	 * performed today.
	 * @return a {@link List} of {@code DirectDebit}s that are due to be 
	 * performed today
	 */
	List<DirectDebit> getDueDirectDebits();
	
	/** This fetches all the direct debits for the specified user
	 * 
	 * @param user the user containing the direct debits
	 * @return a {@link List} of {@code DirectDebit}s that are owned by the user
	 */
	List<DirectDebit> getUserDirectDebits(RetailUser user);
	
}
