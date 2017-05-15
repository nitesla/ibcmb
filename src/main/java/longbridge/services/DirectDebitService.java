package longbridge.services;

import java.util.List;

import org.springframework.stereotype.Service;

import longbridge.dtos.DirectDebitDTO;
import longbridge.models.DirectDebit;
import longbridge.models.RetailUser;

@Service
public interface DirectDebitService {
	
	/** Adds a new direct debit for the specified user and beneficiary
	 * 
	 * @param user the customer
	 * @param beneficiary the beneficiary of the direct debit
	 * @param account the account to be debitted for the payments
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
	void performDirectDebit(DirectDebit directDebit);

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
