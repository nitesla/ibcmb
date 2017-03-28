package longbridge.services;

import longbridge.models.Customer;
import longbridge.repositories.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Showboy on 27/03/2017.
 */
@Service
public class CustomerService {
    @Autowired
    private CustomerRepo customerRepository;

    public Customer findByEmail(String email){
        return customerRepository.findByEmail(email);
    }
}
