package longbridge.services;

import longbridge.dtos.MakerCheckerDTO;
import longbridge.dtos.VerificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by chiomarose on 19/06/2017.
 */
public interface MakerCheckerService
 {
    Page<VerificationDTO> getMakerCheckerPending(Pageable pageDetails);

 }
