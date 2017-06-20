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



//    public MakerChecker convertDTOToEntity(MakerCheckerDTO makerCheckerDTO){
//        return  modelMapper.map(makerCheckerDTO,Code.class);
//    }


    public VerificationDTO convertEntityToDTO(Verification verification)
    {
        return  modelMapper.map(verification,VerificationDTO.class);
    }

    public List<VerificationDTO> convertEntitiesToDTOs(Iterable<Verification> verifications)
    {
        List<VerificationDTO> verificationDTOArrayList = new ArrayList<>();
        for(Verification verification: verifications){
            VerificationDTO verificationDTO = convertEntityToDTO(verification);
            verificationDTOArrayList.add(verificationDTO);
        }
        return verificationDTOArrayList;
    }

//    public List<MakerCheckerDTO> convertEntitiesToDTOs(Iterable<Code> codes){
//        List<MakerCheckerDTO> codeDTOList = new ArrayList<>();
//        for(Code code: codes){
//            MakerCheckerDTO makerCheckerDTO = convertEntityToDTO(code);
//            codeDTOList.add(codeDTO);
//        }
//        return codeDTOList;
//    }

    @Override
    public Page<VerificationDTO> getMakerCheckerPending(Pageable pageDetails)
    {
        Page<Verification> page =verificationRepo.findByStatus(verificationStatus.PENDING,pageDetails);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<VerificationDTO> pageImpl = new PageImpl<VerificationDTO>(dtOs,pageDetails,t);
        return pageImpl;

    }


//    @Override
//    public Page<MakerCheckerDTO> getCodes(Pageable pageDetails) {
//        Page<Code> page = codeRepo.findAll(pageDetails);
//        List<CodeDTO> dtOs = convertEntitiesToDTOs(page.getContent());
//        long t = page.getTotalElements();
//
//        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
//        Page<CodeDTO> pageImpl = new PageImpl<CodeDTO>(dtOs,pageDetails,t);
//        return pageImpl;
//    }
}
