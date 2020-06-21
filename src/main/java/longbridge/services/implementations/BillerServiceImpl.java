package longbridge.services.implementations;

import longbridge.billerresponse.Biller;
import longbridge.dtos.BillerDTO;
import longbridge.models.Billers;
import longbridge.repositories.BillerRepo;
import longbridge.services.BillerService;
import longbridge.services.IntegrationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BillerServiceImpl implements BillerService {

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private BillerRepo billerRepo;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public String updateBillersTable(){
       int u =  billerRepo.deleteAllByDeleteValue("Y");
        List<BillerDTO> billerDto = integrationService.getBillers();
        for (int i = 0; i < billerDto.size(); i++){
            Billers billers = new Billers();
            billers.setBillerName(billerDto.get(i).getBillername());
            billers.setBillerId(billerDto.get(i).getBillerid());
            billers.setCategoryName(billerDto.get(i).getCategoryname());
            billers.setCategoryDescription(billerDto.get(i).getCategorydescription());
            billers.setCategoryId(billerDto.get(i).getCategoryid());
            billers.setCurrencySymbol(billerDto.get(i).getCurrencySymbol());
            billers.setCustomerField1(billerDto.get(i).getCustomerfield1());
            billers.setCustomerField2(billerDto.get(i).getCustomerfield2());
            billers.setLogoUrl(billerDto.get(i).getLogoUrl());
            billers.setDeleteValue("Y");
            billerRepo.save(billers);
        }
        return "Successfully updated";
    }
//    @Override
//    public BillerDTO convertEntityToDTO(Billers verification) {
//        return modelMapper.map(verification, BillerDTO.class);
//    }
//    @Override
//    public List<BillerDTO> convertEntitiesToDTOs(Iterable<Billers> verifications) {
//        List<BillerDTO> verificationDTOArrayList = new ArrayList<>();
//        for (Billers verification : verifications) {
//            BillerDTO verificationDTO = convertEntityToDTO(verification);
//            verificationDTOArrayList.add(verificationDTO);
//        }
//        return verificationDTOArrayList;
//    }
//
//    @Override
//    public Page<BillerDTO> getBillers(Pageable pageable) {
//        Page<Billers> page = (Page<Billers>) billerRepo.findAll();
//        List<BillerDTO> dtOs = convertEntitiesToDTOs(page.getContent());
//        long t = page.getTotalElements();
//        Page<BillerDTO> pageImpl = new PageImpl<BillerDTO>(dtOs, pageable, t);
//        return pageImpl;
//
//    }




}
