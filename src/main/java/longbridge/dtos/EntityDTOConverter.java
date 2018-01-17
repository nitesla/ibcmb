package longbridge.dtos;

/**
 * Created by Fortune on 4/10/2017.
 */
public interface EntityDTOConverter<S,T> {

    T entityToDTO(S s);

    S DTOToEntity(T t);

}
