package longbridge.dtos;

import org.modelmapper.ModelMapper;

/**
 * Created by Fortune on 4/10/2017.
 */
public abstract class AbstractEntityDTOConverter<S,T> implements EntityDTOConverter<S,T>{

    private ModelMapper modelMapper = new ModelMapper();

//    @Override
//    public T entityToDTO(S s) {
//       // T t = modelMapper.createTypeMap();
//    }

    @Override
    public S DTOToEntity(T t) {
        return null;
    }
}
