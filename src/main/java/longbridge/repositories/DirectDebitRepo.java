package longbridge.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import longbridge.models.DirectDebit;
import longbridge.models.RetailUser;

@Repository
public interface DirectDebitRepo extends CommonRepo<DirectDebit,Long> {
	List<DirectDebit> findByNextDebitDate(Date date);
	
	List<DirectDebit> findByRetailUser(RetailUser user);
}
