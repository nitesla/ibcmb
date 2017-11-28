package longbridge.config.audits;

import longbridge.config.SpringContext;
import longbridge.dtos.AuditSearchDTO;
import longbridge.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

import static longbridge.utils.StringUtil.searchModifiedEntityTypeEntity;

/**
 * Created by chiomarose on 10/07/2017.
 */
@Transactional
public class RevisedEntitiesUtil {

    static Logger logger = LoggerFactory.getLogger(RevisedEntitiesUtil.class);

    private static final String PACKAGE_NAME = "longbridge.models.";

    @PersistenceContext
    EntityManager em;

    public static  List<Integer> revisedEntityDetails(String entityName,Integer revId)
    {
        if(entityName.contains("TransRequest")||entityName.contains("Transfer")){
            entityName  = "TransRequest";
        }
        List<Map<String ,Object>> mapList=null;
        List<Integer> revIds = new ArrayList<>();
        entityName = getOracleEntity(entityName);

        String auditEntity = entityName + "_AUD";
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select a.rev from "+ auditEntity +" a where a.id in (select ar.id from " + auditEntity + " ar where ar.rev = :revisionid) and rownum <1000";
        SqlParameterSource namedParameters = new MapSqlParameterSource("revisionid", revId);
        mapList= namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        if(!mapList.isEmpty()) {
            for (Map map : mapList) {
                revIds.add(Integer.parseInt(map.get("REV").toString()));
            }
        }

        return revIds;
    }

    public static String getOracleEntity(String enttyname){
        StringBuilder builder = new StringBuilder();
        for(int y = 0; y < enttyname.length(); y++){
            if(Character.isUpperCase(enttyname.charAt(y)) && y != 0){
                builder.append("_");
                builder.append(enttyname.charAt(y));
            }else{
                builder.append(enttyname.charAt(y));
            }
        }
        return builder.toString();
    }
    //Out dated method, kept in case of any necessity
    public static  Map<String, List<String>> getEntityPastDetails(String entityName,String[] revId)
    {
        String beneficiaryTableName = entityName;
        if(entityName.contains("TransRequest")||entityName.contains("Transfer")){
            entityName  = "TransRequest";
        }

        logger.info("the revision is {}",revId);
        List<Integer> refIds = new ArrayList<>();
        for (String rev:revId) {
            refIds.add(Integer.parseInt(rev));
        }

        List<String> itemIds =  new ArrayList<>();
        Map<String,List<String>> mergedDetails =  new HashMap<>();
        entityName = getOracleEntity(entityName);
        String auditEntity = entityName + "_AUD";
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select * from "+ auditEntity +" a where a.REV in (:revIdList)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("revIdList", refIds);
        List<Map<String ,Object>> entityDetails = namedParameterJdbcTemplate.queryForList(sql, namedParameters);

        if(!entityDetails.isEmpty())
        {
            if(entityName.contains("Beneficiary")) {
                entityDetails.set(0,getCurrentEntityDetails(beneficiaryTableName, (BigDecimal) entityDetails.get(0).get("ID")));
            }
            entityDetails = removeIrrelevantDetails(entityDetails);
            List<String> pastList =  new ArrayList<>();
                for (String item:entityDetails.get(0).keySet())
                {
                    if(entityDetails.get(0).get(item)!=null)
                    {
                        pastList.add(entityDetails.get(0).get(item).toString());
                        if(item.equalsIgnoreCase("id")){
                            itemIds.add(entityDetails.get(0).get(item).toString());
                        }
                    }

                    else
                    {
                        pastList.add("");

                    }
                }
//            logger.info("the pastDetails 2 {}",pastList);
            mergedDetails.put("pastDetails",pastList);
                if(entityDetails.size() >1){
                    List<String> currentList =  new ArrayList<>();
                    if(entityName.contains("Beneficiary")) {
                        entityDetails.set(1,getCurrentEntityDetails(beneficiaryTableName, (BigDecimal) entityDetails.get(1).get("ID")));
                    }
//                    logger.info("the after clear {}",pastList);
                for (String item:entityDetails.get(1).keySet())
                {
                    if(entityDetails.get(1).get(item)!=null)
                    {
                        if(item.equalsIgnoreCase("id")){
                            itemIds.add(entityDetails.get(1).get(item).toString());
                        }
                        currentList.add(entityDetails.get(1).get(item).toString());
                    }

                    else{
                        currentList.add("");
                    }

                }
//                    logger.info("the currentDetails 2 {}",currentList);
                mergedDetails.put("currentDetails",currentList);
                }else {
                    mergedDetails.put("currentDetails",null);
                }
                mergedDetails.put("keys",new ArrayList<>(entityDetails.get(0).keySet()));
            logger.info("the selectedItemId {}",itemIds);
                mergedDetails.put("selectedItemId",itemIds);

        }
        return mergedDetails;
    }
    public static Map<String, Object> getEntityDetailsById(String entityName, int rev)
    {

        if(entityName.contains("TransRequest")||entityName.contains("Transfer")){
            entityName  = "TransRequest";
        }
            Integer revId  = new Integer(rev);
            entityName = getOracleEntity(entityName);
            String auditEntity = entityName + "_AUD";
            ApplicationContext context = SpringContext.getApplicationContext();
            DataSource dataSource = context.getBean(DataSource.class);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            String sql = "select * from "+ auditEntity +" a where a.REV = :revId";
            SqlParameterSource namedParameters = new MapSqlParameterSource("revId", revId);
            List<Map<String ,Object>> entityDetails = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
//            if(!entityDetails.isEmpty())
//            {
//                entityDetails = removeIrrelevantDetails(entityDetails);
//    //            logger.info("this is the entity details {}",entityDetails);
//            }

            return entityDetails.get(0);
    }
    public static String getUserDetailsByUserName(String entityName, String username)
    {
            entityName = getOracleEntity(entityName);
//            String auditEntity = entityName + "_AUD";
            ApplicationContext context = SpringContext.getApplicationContext();
            DataSource dataSource = context.getBean(DataSource.class);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            String sql = "select a.id from "+ entityName +" a where a.USER_NAME = :username";
            SqlParameterSource namedParameters = new MapSqlParameterSource("username", username);
            List<Map<String ,Object>> entityDetails = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
if(entityDetails != null) {
    logger.info("the entityDetails {}",entityDetails.get(0));
    return entityDetails.get(0).get("ID").toString();
}else {
    return "";
}
    }

    private static List<Map<String, Object>> removeIrrelevantDetails(List<Map<String ,Object>> entityDetails){
        for (Map map:entityDetails) {
            for (Object itemDetail:map.keySet().toArray()) {
                if(itemDetail.toString().contains("_MOD")){
                    map.remove(itemDetail);
                }
            }
            map.remove("REV");
            map.remove("REVTYPE");
            map.remove("DEL_FLAG");
            map.remove("DELETE_ON");
            map.remove("VERSION");
        }
        return entityDetails;
    }


    public static  Map<String ,Object> getCurrentEntityDetails(String entityName,BigDecimal id)
    {
        entityName = StringUtil.convertFromKermelCaseing(entityName);
        System.out.println("converted entity name "+entityName);
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        List<Map<String ,Object>> mapList=null;
        String sql = "select * from " + entityName + " a where a.id=:id";
        SqlParameterSource namedParameters =  new MapSqlParameterSource("id",id);
        mapList = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        System.out.println("result details @@@@@ " + mapList);
        return mapList.get(0);
    }
    public static  Collection<Integer> getSearchedRevisedEntityID(String entityName, Class<?> clazz, String search)
    {
        String searchString  = StringUtil.getFieldsFrom(clazz,search,"a.");
        List<Map<String ,Object>> mapList=null;
        Collection<Integer> revIds = new ArrayList<>();
        entityName = getOracleEntity(entityName);
        String auditEntity = entityName + "_AUD";
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "";
        if(entityName.equalsIgnoreCase("TRANS_REQUEST")){
            sql = "select DISTINCT a.REV from "+ auditEntity +" a WHERE "+searchString+"or DTYPE like '%"+search+"%' ";
        }else {
            sql = "select DISTINCT a.REV from "+ auditEntity +" a WHERE "+searchString;

        }
        SqlParameterSource namedParameters = new MapSqlParameterSource();
        mapList= namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        if(!mapList.isEmpty()) {
            for (Map map : mapList) {
                revIds.add(Integer.parseInt(map.get("REV").toString()));
            }
        }else {
            revIds =  null;
        }
        return revIds;
    }
    public static  Collection<Integer> getSearchedAndMergedRevisedEntityID(String entityName, Class<?> clazz, String search)
    {
        String actualEntityName = StringUtil.convertFromKermelCaseing(entityName);
        String searchString  = StringUtil.getFieldsFrom(clazz,search,"e.");
        List<Map<String ,Object>> mapList=null;
        Collection<Integer> revIds = new ArrayList<>();
        entityName = getOracleEntity(entityName);
        logger.info("search using met "+search);
        String auditEntity = entityName + "_AUD";
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select DISTINCT a.REV from "+ auditEntity +" a, "+actualEntityName+" e WHERE "+searchString+" and e.id = a.id";
        SqlParameterSource namedParameters = new MapSqlParameterSource();
        mapList= namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        if(!mapList.isEmpty()) {
            for (Map map : mapList) {
                revIds.add(Integer.parseInt(map.get("REV").toString()));
            }
            logger.info("the revision id is {}",revIds);
        }else {
            logger.info("search list empty");
            revIds =  null;
        }

        return revIds;
    }
    public static Map<String, Object> getMergedEntityDetailsById(String entityName, int rev)
    {
        String actualEntityName = StringUtil.convertFromKermelCaseing(entityName);
        Integer revId  = new Integer(rev);
        entityName = getOracleEntity(entityName);
        String auditEntity = entityName + "_AUD";
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select * from "+ auditEntity +" a, "+actualEntityName+" e where a.REV = :revId and a.id = e.id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("revId", revId);
        List<Map<String ,Object>> entityDetails = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        if(!entityDetails.isEmpty())
        {
            entityDetails = removeIrrelevantDetails(entityDetails);
            logger.info("this is the entity details {}",entityDetails);
        }

        return entityDetails.get(0);
    }
    public static List<ModifiedEntityTypeEntity>  getSearchedModifiedEntity(AuditSearchDTO auditSearchDTO){
        String search = StringUtil.searchModifiedEntityTypeEntity(auditSearchDTO,false);
        List<ModifiedEntityTypeEntity> modifiedEntityTypeEntities = new ArrayList<>();
//        String actualEntityName = StringUtil.convertFromKermelCaseing(entityName);
//        String searchString  = StringUtil.getFieldsFrom(clazz,search,"e.");
        List<Map<String ,Object>> mapList=null;
        Collection<Integer> revIds = new ArrayList<>();
//        entityName = getOracleEntity(entityName);
//        String auditEntity = entityName + "_AUD";
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select m.ENTITY_CLASS_NAME as \"entityClassName\",c.TIMESTAMP as \"timestamp\", " +
                "c.IP_ADDRESS as \"ipAddress\",c.LAST_CHANGED_BY as \"lastChangedBy\" from  " +
                "Modified_Entity_Type_Entity m, Custom_Revision_Entity c "+search;
        SqlParameterSource namedParameters = new MapSqlParameterSource();
        logger.info("the search query {}",mapList);
        mapList= namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        if(!mapList.isEmpty()) {
            logger.info("the revision mapList is {}",mapList);
            for (Map map : mapList) {
                ModifiedEntityTypeEntity typeEntity = new ModifiedEntityTypeEntity();
                CustomRevisionEntity revisionEntity = new CustomRevisionEntity();
                typeEntity.setEntityClassName(map.get("entityClassName").toString());
                revisionEntity.setIpAddress(String.valueOf(map.get("ipAddress")));
                revisionEntity.setTimestamp(new BigDecimal(map.get("timestamp").toString()).longValue());
                revisionEntity.setLastChangedBy(String.valueOf(map.get("lastChangedBy")));
                typeEntity.setRevision(revisionEntity);
                modifiedEntityTypeEntities.add(typeEntity);

            }

        }else {
            logger.info("search list empty");
            revIds =  null;
        }
return modifiedEntityTypeEntities;
    }

    public static Long fetchModifiedEntity(AuditSearchDTO auditSearchDTO){
        String search  = searchModifiedEntityTypeEntity(auditSearchDTO,false);
        String auditedEntity = "";
        Long counter = Long.parseLong("0");
        List<Map<String ,Object>> mapList=null;
        Collection<Integer> revIds = new ArrayList<>();
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select count(*) from Modified_Entity_Type_Entity m, Custom_Revision_Entity c"+search+"and c.id = m.revision_id  " +
                "and c.last_changed_by <> 'Unknown' order by c.timestamp desc";
        SqlParameterSource namedParameters = new MapSqlParameterSource();
        logger.info("the select query {}",sql);
        counter = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Long.class);
        logger.info("the count query {}",counter);
        return counter;
    }
    public static  Collection<Integer> getRevIdsFromId(AuditSearchDTO auditSearchDTO, String search)
    {
        logger.info("searching for id {}",auditSearchDTO.getId());
        String dummyRequest = "";
        String entityClassName = auditSearchDTO.getEntityClassName();
        if(entityClassName.contains("TransRequest")||entityClassName.contains("Transfer")){
            dummyRequest = entityClassName;
            entityClassName = "TransRequest";
        }
        List<Map<String ,Object>> mapList=null;
        Collection<Integer> revIds = new ArrayList<>();
        String entityName = getOracleEntity(entityClassName);
        String auditEntity = entityName + "_AUD";
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "";
        if(entityName.contains("Trans_Request")){
            sql = "select a.REV from "+ auditEntity +" a WHERE a.id ="+auditSearchDTO.getId()+" or a.DTYPE = '"+dummyRequest+"'";
        }else {
            sql = "select a.REV from "+ auditEntity +" a WHERE a.id ="+auditSearchDTO.getId();

        }
        logger.info("the id query {}",sql);
        SqlParameterSource namedParameters = new MapSqlParameterSource();
        mapList= namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        if(!mapList.isEmpty()) {
            for (Map map : mapList) {
                revIds.add(Integer.parseInt(map.get("REV").toString()));
            }
//            logger.info("the revision list for that id is {}",revIds);
        }else {
            revIds =  null;
        }
        return revIds;
    }

}
