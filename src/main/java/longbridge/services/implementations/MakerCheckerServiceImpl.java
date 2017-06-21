package longbridge.services.implementations;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.MakerCheckerDTO;
import longbridge.dtos.VerificationDTO;
import longbridge.models.Code;
import longbridge.models.MakerChecker;
import longbridge.models.Verification;
import longbridge.repositories.MakerCheckerRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.MakerCheckerService;
import longbridge.utils.verificationStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chiomarose on 19/06/2017.
 */
@Service
public class MakerCheckerServiceImpl implements MakerCheckerService {


   @Autowired
   private VerificationRepo verificationRepo;


    @Autowired
    private ModelMapper modelMapper;








}
