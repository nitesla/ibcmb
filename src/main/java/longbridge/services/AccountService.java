package longbridge.services;

import longbridge.models.Account;
import longbridge.repositories.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Showboy on 28/03/2017.
 */
@Service
public class AccountService {
    @Autowired
    private AccountRepo accountRepository;

    public Account findByAccountNumber(String acctNumber){
        return accountRepository.findByAccountNumber(acctNumber);
    }
}
