package longbridge.utils;

import longbridge.models.AbstractEntity;
import org.json.simple.JSONObject;

/**
 * Created by Longbridge on 10/12/2017.
 */
public class SerializeUtil {
    public static JSONObject getPrettySerialJSON(AbstractEntity abstractEntity){
        //        try {
//            ObjectMapper prettyMapper = new ObjectMapper();
//            JsonSerializer<Object> serializer = ((PrettySerializer) (abstractEntity)).getAuditSerializer();
//            SimpleModule module = new SimpleModule();
//            module.addSerializer(abstractEntity.getClass(), serializer);
//            prettyMapper.registerModule(module);
//            String writeValueAsString = prettyMapper.writeValueAsString(abstractEntity);
//            System.out.println("the serialized data "+ writeValueAsString);
//            jsonObject = convertToJSON(writeValueAsString);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        return null;
    }
}

