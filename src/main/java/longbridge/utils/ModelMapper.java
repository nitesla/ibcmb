package longbridge.utils;

/**
 * Created by Fortune on 4/6/2017.
 */
public class ModelMapper {

    private ModelMapper instance = null;

    private ModelMapper(){}

    public ModelMapper getInstance(){
        if(instance == null){
            instance = new ModelMapper();
        }
        return instance;
    }

}
