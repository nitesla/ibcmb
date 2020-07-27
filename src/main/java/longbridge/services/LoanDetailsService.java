package longbridge.services;

import longbridge.dtos.LoanDetailsDTO;


public interface LoanDetailsService {

    String sendLoanDetails(String recipient, String name,LoanDetailsDTO loans);




}
