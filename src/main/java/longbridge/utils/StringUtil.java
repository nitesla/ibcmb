package longbridge.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Longbridge on 7/19/2017.
 */
public class StringUtil {
    public static List<String> splitByComma(String word){
       return Arrays.asList(word.split(","));
    }
    public static String compareAnswers(List<String>answers,List<String>entAnswers ){
        int noOfMismatch = 0;
        if((answers.size()>0)&&(entAnswers.size()>0)) {
            for(int i =0; i<answers.size();i++){
                if(!answers.get(i).equalsIgnoreCase(entAnswers.get(i))){
                    noOfMismatch++;
                    System.out.println("my answer "+answers.get(i)+" entrust "+entAnswers.get(i));
                }
            }
            System.out.println("no of mis match is "+noOfMismatch);
            if(noOfMismatch==0){
                return "true";
            }
        }
        return "false";
    }
    public static String compareAnswers(WebRequest webRequest, List<String>entAnswers ){
        int noOfMismatch = 0;
        List<String> providedAnswers = Arrays.asList(webRequest.getParameter("secAnswer").split(","));
        int noOfSecQn = Integer.parseInt(webRequest.getParameter("noOfSecQn"));
        for (int i=0;i<noOfSecQn;i++){
            if(!entAnswers.get(i).equalsIgnoreCase(providedAnswers.get(i))){
                noOfMismatch++;
            }
        }
            if(noOfMismatch==0){
                return "true";
            }
        return "false";
    }
    public static String convertFieldToTitle(String fieldName){
        StringBuilder builder = new StringBuilder();
        char[] fields = fieldName.toCharArray();
        int counter = 0;
        for (char field:fields) {
            if(counter == 0){
                builder.append(Character.toUpperCase(field));
            }else {
                if(Character.isUpperCase(field)){
                    builder.append(" ");
                    builder.append(field);
                }else {
                    builder.append(field);
                }
            }
            counter++;
        }
        return builder.toString();
    }
    public static String extractedFieldName(String genericFieldName){
        return genericFieldName.substring(genericFieldName.lastIndexOf('.') + 1, genericFieldName.length());
    }
    public static JSONObject convertToJSON(String json)  {
        JSONObject jsonObject = new JSONObject();
        JsonFactory factory = new JsonFactory();

        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<Map.Entry<String,JsonNode>> fieldsIterator = rootNode.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String,JsonNode> field = fieldsIterator.next();
            jsonObject.put(field.getKey(),field.getValue());
//            System.out.println("Key: " + field.getKey() + "\tValue:" + field.getValue());
        }
        return jsonObject;
    }
    public static List<String> userDetials(){
        String[] userDetails = new String[]{"userName","firstName","lastName","email","phoneNumber","status"};
        return Arrays.asList(userDetails);
    }
}
