package longbridge.services;

import longbridge.models.AdminUser;
import longbridge.models.Verification;

public interface VerificationService {

	 void decline(Verification verification, AdminUser decliner, String declineReason); 

     void verify(Verification t, AdminUser verifier);
     
     Verification getVerification(Long id);
}
