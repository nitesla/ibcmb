package longbridge.utils;


import longbridge.dtos.apidtos.MobileRetailUserDTO;
import longbridge.models.RetailUser;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;

@Service
public class Converters {


    ModelMapper mm = new ModelMapper();
    Converter<RetailUser,MobileRetailUserDTO> userConverter = new Converter<RetailUser, MobileRetailUserDTO>() {
        @Override
        public MobileRetailUserDTO convert(MappingContext<RetailUser, MobileRetailUserDTO> context) {

            RetailUser retailUser = context.getSource();
            MobileRetailUserDTO mobileRetailUserDTO = context.getDestination();
            mobileRetailUserDTO.setUserName(retailUser.getUserName());
            mobileRetailUserDTO.setBvn(retailUser.getBvn());
            mobileRetailUserDTO.setLastLoginDate(retailUser.getLastLoginDate().toString());
            mobileRetailUserDTO.setFirstName(retailUser.getFirstName());
            mobileRetailUserDTO.setLastName(retailUser.getLastName());

            return mobileRetailUserDTO;
        }
    };


//    PropertyMap<RetailUser, MobileRetailUserDTO> mymap = new PropertyMap<RetailUser, MobileRetailUserDTO>()
//    {
//        protected void configure()
//        {
//            // Note: this is not normal code. It is "EDSL" so don't get confused
//            map(source.getName()).setName(null);
//            using(userConverter).map(source.getMass()).setLarge(false);
//        }
//    };
}
