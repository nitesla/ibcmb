package longbridge.config.audits;

import longbridge.config.SpringContext;
import longbridge.utils.StringUtil;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by chiomarose on 10/07/2017.
 */
public class RevisedEntitiesUtil {

    static Logger logger = LoggerFactory.getLogger(RevisedEntitiesUtil.class);

    private static final String PACKAGE_NAME = "longbridge.models.";


    public static  List<Integer> revisedEntityDetails(String entityName,Integer revId)
    {
        List<Map<String ,Object>> mapList=null;
        List<Integer> revIds = new ArrayList<>();
        entityName = getOracleEntity(entityName);

        String auditEntity = entityName + "_AUD";
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select a.rev from "+ auditEntity +" a where a.id in (select ar.id from " + auditEntity + " ar where ar.rev = :revisionid)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("revisionid", revId);
        mapList= namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        if(!mapList.isEmpty()) {
            for (Map map : mapList) {
                revIds.add(Integer.parseInt(map.get("REV").toString()));
            }
        }

        return revIds;
    }

    private static String getOracleEntity(String enttyname){
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
        List<Integer> refIds = new ArrayList<>();
        for (String rev:revId) {
            refIds.add(Integer.parseInt(rev));
        }
        List<String> itemList =  new ArrayList<>();
        List<String> itemList2 =  new ArrayList<>();
        List<String> headers =  new ArrayList<>();
        Map<String,List<String>> mergedDetails =  new HashMap<>();
        JSONObject jsonObject = null;
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
            entityDetails = removeIrrelevantDetails(entityDetails);
            if(entityDetails.size()>1)
            {
                for (String item:entityDetails.get(0).keySet())
                {
                    if(entityDetails.get(0).get(item)!=null)
                    {
                        itemList.add(entityDetails.get(0).get(item).toString());
                    }

                    else
                    {
                        itemList.add("");

                    }
                }

                mergedDetails.put("pastDetails",itemList);
                for (String item:entityDetails.get(1).keySet())
                {
                    if(entityDetails.get(1).get(item)!=null)
                    {

                        itemList2.add(entityDetails.get(1).get(item).toString());
                    }

                    else{
                        itemList2.add("");
                    }

                }
                mergedDetails.put("currentDetails",itemList2);
                mergedDetails.put("keys",new ArrayList<>(entityDetails.get(0).keySet()));
            }
//           else
//               {
//               for (String item:entityDetails.get(0).keySet())
//               {
//                   itemList.add(entityDetails.get(0).get(item).toString());
//               }
//               mergedDetails.put("currentDetails",itemList);
//               mergedDetails.put("keys",new ArrayList<>(entityDetails.get(0).keySet()));
//              }

        }

        return mergedDetails;
    }
    public static Map<String, Object> getEntityDetailsById(String entityName, int rev)
    {
            Integer revId  = new Integer(rev);
            entityName = getOracleEntity(entityName);
            String auditEntity = entityName + "_AUD";
            ApplicationContext context = SpringContext.getApplicationContext();
            DataSource dataSource = context.getBean(DataSource.class);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            String sql = "select * from "+ auditEntity +" a where a.REV = :revId";
            SqlParameterSource namedParameters = new MapSqlParameterSource("revId", revId);
            List<Map<String ,Object>> entityDetails = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
            if(!entityDetails.isEmpty())
            {
                entityDetails = removeIrrelevantDetails(entityDetails);
    //            logger.info("this is the entity details {}",entityDetails);
            }

            return entityDetails.get(0);
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

}
