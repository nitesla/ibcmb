package longbridge.utils;

import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Created by Fortune on 4/6/2017.
 */
public class MyModelMapper<S,D> {

    private static ModelMapper instance;

    private MyModelMapper(){}

    public static ModelMapper getInstance(){
        if(instance == null){
            instance = new ModelMapper();
        }
        return instance;
    }

//    public D convertEntityToDTO(S s){
//        return getInstance().map(s, Class < D >);
//    }

//    private Role convertDTOToEntity(RoleDTO roleDTO){
//        return  instance.map(roleDTO,Role.class);
//    }
//
//    private Iterable<RoleDTO> convertEntitiesToDTOs(Iterable<Role> roles){
//        List<RoleDTO> roleDTOs = new ArrayList<>();
//        for(Role role: roles){
//            RoleDTO roleDTO = instance.map(role,RoleDTO.class);
//            roleDTOs.add(roleDTO);
//        }
//        return roleDTOs;
//    }

}
