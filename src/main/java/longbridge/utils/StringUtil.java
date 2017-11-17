package longbridge.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.config.audits.RevisedEntitiesUtil;
import longbridge.dtos.AuditSearchDTO;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Longbridge on 7/19/2017.
 */
public class StringUtil {
    static Logger logger = LoggerFactory.getLogger(StringUtil.class);
    private static final String PACKAGE_NAME = "longbridge.models.";
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
//        System.out.println("the datatble field "+builder.toString());
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
    public static Map<String,List<String>> addSupperClassFieldsIfNeccassary(Class<?> superclass, List<String> headers, List<String> classFields){
        Map<String,List<String>> map = new HashMap<>();
        if (superclass.toString().contains("User") ||superclass.toString().contains("Beneficiary")||superclass.toString().contains("TransRequest")){
            for (Field field: superclass.getDeclaredFields()) {
                String fieldName = StringUtil.extractedFieldName(field.toString());
                if(superclass.toString().contains("User") ) {
                    if (StringUtil.userDetials().contains(fieldName)) {
                        headers.add(convertFieldToTitle(fieldName));
                        classFields.add("fullEntity." + fieldName);
                    }
                }else {
                    Annotation[] annotations = field.getAnnotations();
                    boolean fieldDiplay = true;
                    for (Annotation annotation: annotations) {
                        if(annotation.toString().contains("ManyToOne")||annotation.toString().contains("OneToOne")||annotation.toString().contains("ManyToMany")||annotation.toString().contains("OneToMany")){
                            fieldDiplay = false;
                            break;
                        }
                    }
                    if(!fieldDiplay){
                        continue;
                    }
                    headers.add(convertFieldToTitle(fieldName));
                    classFields.add("fullEntity." + fieldName);
                }
            }

        }
        map.put("headers",headers);
        map.put("classFields",classFields);
        return map;
    }
    public static Map<String,List<String>> addSupperClassFields(Class<?> superclass, List<String> headers, List<String> classFields){
        Map<String,List<String>> map = new HashMap<>();
        if (!superclass.toString().contains("AbstractEntity")) {
            for (Field field : superclass.getDeclaredFields()) {
                Annotation[] annotations = field.getAnnotations();
                String fieldName = StringUtil.extractedFieldName(field.toString());
                Map<String, Object> fieldsNameAfterCheck = getFieldsNameAfterCheck(field, fieldName);
                if(!(boolean) fieldsNameAfterCheck.get("ignoreField")) {
                    headers.add(convertFieldToTitle(fieldName));
                    classFields.add("fullEntity." + fieldsNameAfterCheck.get("field"));
                }

            }
        }

        map.put("headers",headers);
        map.put("classFields",classFields);
        return map;
    }
    public static String convertFromKermelCaseing(String enttyname){
        StringBuilder builder = new StringBuilder();
        for(int y = 0; y < enttyname.length(); y++){
            if(Character.isUpperCase(enttyname.charAt(y)) && y != 0){
                builder.append("_");
                builder.append(Character.toUpperCase(enttyname.charAt(y)));
            }else{
                builder.append(Character.toUpperCase(enttyname.charAt(y)));
            }
        }
        return builder.toString();
    }
    public static String getFieldsFrom(Class<?> className, String search,String initial){
        Field[] declaredFields = className.getDeclaredFields();
        StringBuilder stringBuilder =  new StringBuilder();
        String searchIdentity = " like '%"+search+"%' ";
        int counter = 0;
        for (Field field:declaredFields) {

            String fieldName = StringUtil.extractedFieldName(field.toString());

            Annotation[] annotations = field.getAnnotations();
            boolean fieldDiplay = true;

            if(fieldName.equalsIgnoreCase("serialVersionUID")) {
                continue;
            }
            boolean ignoreField =false;

            String datatableField = StringUtil.convertFromKermelCaseing(fieldName);
            for (Annotation annotation: annotations) {
                if(annotation.toString().contains("ManyToOne")||annotation.toString().contains("OneToOne")||annotation.toString().contains("ManyToMany")||annotation.toString().contains("OneToMany")){
                    datatableField += "_ID";
                    if(annotation.toString().contains("OneToMany")){
                        ignoreField = true;
                    }
                    break;
                }
            }
            if(ignoreField){
                continue;
            }if(counter == 0){
                stringBuilder.append(initial+datatableField+searchIdentity);
            }else {
                stringBuilder.append("or "+initial+datatableField+searchIdentity);
            }
            counter++;
        }
        Class<?> superclass = className.getSuperclass();
        if (!superclass.toString().contains("AbstractEntity")) {
            for (Field field : superclass.getDeclaredFields()) {
                Annotation[] annotations = field.getAnnotations();
                String fieldName = StringUtil.extractedFieldName(field.toString());
                String datatableField = StringUtil.convertFromKermelCaseing(fieldName);
                boolean ignoreField = false;
                for (Annotation annotation : annotations) {
                    if (annotation.toString().contains("ManyToOne") || annotation.toString().contains("OneToOne") || annotation.toString().contains("ManyToMany") || annotation.toString().contains("OneToMany")) {
                        datatableField += "_ID";
                        ignoreField = true;
                        break;
                    }
                }
                if(ignoreField){
                    continue;
                }
                if(counter == 0){
                    stringBuilder.append(initial+datatableField+searchIdentity);
                }else {
                    stringBuilder.append("or "+initial+datatableField+searchIdentity);
                }
                counter++;
            }
        }
        return stringBuilder.toString();

    }
    public static Map<String, Object> getFieldsNameAfterCheck(Field field, String fieldName){
        Map<String,Object> fields = new HashMap<>();
        Annotation[] annotations = field.getAnnotations();
        String datatableField = StringUtil.convertFromKermelCaseing(fieldName);
        boolean ignoreField = false;
        for (Annotation annotation: annotations) {
            if(annotation.toString().contains("ManyToOne")||annotation.toString().contains("OneToOne")||annotation.toString().contains("ManyToMany")||annotation.toString().contains("OneToMany")){
                datatableField += "_ID";

                if(annotation.toString().contains("OneToMany")){
                    ignoreField = true;
                }
                break;
            }
        }
        fields.put("field",datatableField);
        fields.put("ignoreField",ignoreField);
        return fields;
    }
    public static String searchModifiedEntityTypeEntity(AuditSearchDTO auditSearchDTO, boolean queryFieldDirectly){
        String timeStamp = "";String className = ""; String lastChangedBy = ""; String revsionField = ""; String ipAddress ="";
        logger.info("the auditSearchDTO is {}",auditSearchDTO);
        if(queryFieldDirectly){
            revsionField = "revision.id";
            timeStamp = "revision.timestamp";
            lastChangedBy = "revision.lastChangedBy";
            ipAddress = "revision.ipAddress";
            className = "entityClassName";
        }else {
            revsionField = "revision_id";
            timeStamp = "c.timestamp";
            lastChangedBy = "c.last_Changed_By";
            ipAddress = "c.ip_Address";
            className = "entity_Class_Name";
        }
        StringBuilder builder = new StringBuilder("");
        String dayBeforeComparator = " <=";
        boolean fromDateNotEmpty = auditSearchDTO.getFromDate() !=0;
        boolean endDateNotEmpty = auditSearchDTO.getEndDate() !=0;
        if(fromDateNotEmpty && auditSearchDTO.getEndDate() ==0){
            logger.info("the from date {}",auditSearchDTO.getFromDate());
            auditSearchDTO.setEndDate(DateUtil.convertDateToLong(DateUtil.nextDate(auditSearchDTO.getFromDate())));
        }
        if(endDateNotEmpty){
            dayBeforeComparator = " < ";
        }
        Collection<Integer> revisionId = null;
        String replaceDetails = "''";
        boolean idNotEmpty = !StringUtils.isEmpty(auditSearchDTO.getId());
        boolean ipAddressNotEmpty = !StringUtils.isEmpty(auditSearchDTO.getIpAddress());
        boolean classNameNotEmpty = !StringUtils.isEmpty(auditSearchDTO.getEntityClassName());
        boolean lastChangeByNotEmpty = !StringUtils.isEmpty(auditSearchDTO.getLastChangeBy());
        if(idNotEmpty){
            revisionId = RevisedEntitiesUtil.getRevIdsFromId(auditSearchDTO,"");
            if(revisionId !=null) {
                replaceDetails = revisionId.toString().replace("[", "").replace("]", "");
            }
        }
        logger.info("search for id {} ip {} startDate {} enddate {} className {}",idNotEmpty,ipAddressNotEmpty,fromDateNotEmpty,endDateNotEmpty,classNameNotEmpty);
        logger.info("the auditSearch DTO {}",auditSearchDTO);
        logger.info("is class empty {}",StringUtils.isEmpty(auditSearchDTO.getEntityClassName()));

        if(fromDateNotEmpty && !idNotEmpty && ipAddressNotEmpty && classNameNotEmpty && lastChangeByNotEmpty){
            builder.append(" where "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator+auditSearchDTO.getEndDate());
            builder.append(" and "+ipAddress+" = "+auditSearchDTO.getIpAddress());
            builder.append(" and "+lastChangedBy+" = '"+auditSearchDTO.getLastChangeBy()+"' ");
        }
        else if(classNameNotEmpty && idNotEmpty && !fromDateNotEmpty &&  !ipAddressNotEmpty&&!lastChangeByNotEmpty){
            builder.append(" where m."+revsionField+" in ("+ replaceDetails +")");
            builder.append(" and m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
        }
        else if(classNameNotEmpty && idNotEmpty && fromDateNotEmpty  && !ipAddressNotEmpty && !lastChangeByNotEmpty){
            builder.append(" where m."+revsionField+" in ("+ replaceDetails +")");
            builder.append(" and m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
            builder.append(" and "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator
                    +auditSearchDTO.getEndDate());
        }
        else if(classNameNotEmpty && idNotEmpty && fromDateNotEmpty && ipAddressNotEmpty &&!lastChangeByNotEmpty){
            builder.append(" where m."+revsionField+" in ("+ replaceDetails +")");
            builder.append(" and m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
            builder.append(" and "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator+auditSearchDTO.getEndDate());
            builder.append(" and "+ipAddress+" = "+auditSearchDTO.getIpAddress());
        }
        else if(classNameNotEmpty && idNotEmpty && fromDateNotEmpty && ipAddressNotEmpty && lastChangeByNotEmpty){
            builder.append(" where m."+revsionField+" in ("+ replaceDetails +")");
            builder.append(" and m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
            builder.append(" and "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator+auditSearchDTO.getEndDate());
            builder.append(" and "+ipAddress+" = "+auditSearchDTO.getIpAddress());
            builder.append(" and "+lastChangedBy+" = '"+auditSearchDTO.getLastChangeBy()+"' ");
        }else if(classNameNotEmpty && idNotEmpty && fromDateNotEmpty && !ipAddressNotEmpty &&lastChangeByNotEmpty){
            builder.append(" where m."+revsionField+" in ("+ replaceDetails +")");
            builder.append(" and m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
            builder.append(" and "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator+auditSearchDTO.getEndDate());
            builder.append(" and "+lastChangedBy+" = '"+auditSearchDTO.getLastChangeBy()+"' ");
        }
        else if(classNameNotEmpty && idNotEmpty && fromDateNotEmpty && !ipAddressNotEmpty &&lastChangeByNotEmpty){
            builder.append(" where m."+revsionField+" in ("+ replaceDetails +") ");
            builder.append(" and m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
            builder.append(" and "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator+auditSearchDTO.getEndDate());
            builder.append(" and "+lastChangedBy+" = '"+auditSearchDTO.getLastChangeBy()+"' ");
        }
        else if(classNameNotEmpty && idNotEmpty && !fromDateNotEmpty && !ipAddressNotEmpty &&lastChangeByNotEmpty){
            builder.append(" where m."+revsionField+" in ("+ replaceDetails +") ");
            builder.append(" and m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
            builder.append(" and "+lastChangedBy+" = '"+auditSearchDTO.getLastChangeBy()+"' ");
        }
        else if(classNameNotEmpty && idNotEmpty && fromDateNotEmpty && !ipAddressNotEmpty &&!lastChangeByNotEmpty){
            builder.append(" where m."+revsionField+" in ("+ replaceDetails +")");
            builder.append(" and m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
            builder.append(" and "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator+auditSearchDTO.getEndDate());
        }else if(classNameNotEmpty && !idNotEmpty && fromDateNotEmpty && !ipAddressNotEmpty &&!lastChangeByNotEmpty){
            builder.append(" where m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
            builder.append(" and "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator+auditSearchDTO.getEndDate());
        }else if(classNameNotEmpty && !idNotEmpty && !fromDateNotEmpty && !ipAddressNotEmpty &&!lastChangeByNotEmpty){
            builder.append(" where m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
        }else if(!classNameNotEmpty && !idNotEmpty && fromDateNotEmpty && !ipAddressNotEmpty &&!lastChangeByNotEmpty){
            builder.append(" where "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator+auditSearchDTO.getEndDate());
        }else if(classNameNotEmpty && !idNotEmpty && fromDateNotEmpty && !ipAddressNotEmpty &&!lastChangeByNotEmpty){
            builder.append(" where m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
            builder.append(" and "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator+auditSearchDTO.getEndDate());
        }else if(classNameNotEmpty && !idNotEmpty && !fromDateNotEmpty && !ipAddressNotEmpty &&lastChangeByNotEmpty){
            builder.append(" where m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"'");
            builder.append(" and "+lastChangedBy+" = '"+auditSearchDTO.getLastChangeBy()+"' ");
        }
        else if(classNameNotEmpty && !idNotEmpty && fromDateNotEmpty && !ipAddressNotEmpty &&!lastChangeByNotEmpty){
            builder.append(" where m."+className+" = '"+PACKAGE_NAME+auditSearchDTO.getEntityClassName()+"' ");
        }
        else if(!classNameNotEmpty && !idNotEmpty && fromDateNotEmpty && ipAddressNotEmpty &&lastChangeByNotEmpty){
            builder.append(" where "+timeStamp+" >= "+auditSearchDTO.getFromDate()+" and "+timeStamp+dayBeforeComparator+auditSearchDTO.getEndDate());
            builder.append(" and "+lastChangedBy+" = '"+auditSearchDTO.getLastChangeBy()+"'");
            builder.append(" and "+ipAddress+" = "+auditSearchDTO.getIpAddress());
        }
        else if(!classNameNotEmpty && !idNotEmpty && !fromDateNotEmpty && ipAddressNotEmpty &&lastChangeByNotEmpty){
            builder.append(" where "+lastChangedBy+" = '"+auditSearchDTO.getLastChangeBy()+"'");
            builder.append(" and "+ipAddress+" = "+auditSearchDTO.getIpAddress());
        }
        else if(!classNameNotEmpty && !idNotEmpty && !fromDateNotEmpty && !ipAddressNotEmpty &&lastChangeByNotEmpty){
            builder.append(" where "+lastChangedBy+" = '"+auditSearchDTO.getLastChangeBy()+"' ");
        }
        else if(!classNameNotEmpty && !idNotEmpty && !fromDateNotEmpty && ipAddressNotEmpty && !lastChangeByNotEmpty){
            builder.append(" where "+ipAddress+" = "+auditSearchDTO.getIpAddress());
        }
        logger.info("the appended query is {}",builder.toString());
        return builder.toString();
    }
}
