package longbridge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import longbridge.models.AbstractEntity;
import org.json.simple.JSONObject;

import static longbridge.utils.StringUtil.convertToJSON;

/**
 * Created by Longbridge on 10/12/2017.
 */
public class SerializeUtil {
    public static JSONObject getPrettySerialJSON(AbstractEntity abstractEntity){
        JSONObject jsonObject = null;
        try {
            ObjectMapper prettyMapper = new ObjectMapper();
            JsonSerializer<Object> serializer = ((PrettySerializer) (abstractEntity)).getAuditSerializer();
            SimpleModule module = new SimpleModule();
            module.addSerializer(abstractEntity.getClass(), serializer);
            prettyMapper.registerModule(module);
//            System.out.println("Registering Pretty serializer for " + abstractEntity.getClass().getName());
            String writeValueAsString = prettyMapper.writeValueAsString(abstractEntity);
//            System.out.println("the serialized data {}"+ writeValueAsString);
            jsonObject = convertToJSON(writeValueAsString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

