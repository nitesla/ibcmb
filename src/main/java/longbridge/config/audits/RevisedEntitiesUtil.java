package longbridge.config.audits;

import longbridge.config.SpringContext;
import org.apache.poi.ss.formula.functions.T;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.MidiDevice;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chiomarose on 10/07/2017.
 */
public class RevisedEntitiesUtil {

    static Logger logger = LoggerFactory.getLogger(CustomJdbcUtil.class);

    private static final String PACKAGE_NAME = "longbridge.models.";

    @Transactional
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
    @Transactional
    public static  Map<String, JSONObject> getEntityPastDetails(String entityName,String[] revId)
    {
        List<Integer> refIds = new ArrayList<>();
        for (String rev:revId) {
            refIds.add(Integer.parseInt(rev));
        }
        logger.info("the rev {}",refIds);
        Map<String,JSONObject> mergedDetails =  new HashMap<>();
        JSONObject jsonObject = null;
        entityName = getOracleEntity(entityName);
        String auditEntity = entityName + "_AUD";
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
//           logger.info("this is datasource {}",dataSource);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select * from "+ auditEntity +" a where a.REV in (:revIdList)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("revIdList", refIds);
        List<Map<String ,Object>> entityDetails = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        logger.info("the details before {}",entityDetails);
        if(!entityDetails.isEmpty()) {
            logger.info("the map key set {}",entityDetails.get(0).keySet());
            jsonObject =  new JSONObject(entityDetails.get(0));
            logger.info("json details {}",jsonObject);
            mergedDetails.put("pastDetails",jsonObject);
            mergedDetails.put("currentDetails", new JSONObject(entityDetails.get(1)));

        }

        return mergedDetails;
    }
    public static Map removeIrrelevantDetails(List<Map<String ,Object>> pastDetails,List<Map<String ,Object>> pastDetaisls){
        return null;
    }
    @Transactional
    public static  List<Map<String ,Object>> getCurrentDetails(NamedParameterJdbcTemplate namedParameterJdbcTemplate,String entity,Map refDetails)
    {
        List<Map<String ,Object>> mapList=null;
        String sql = "select * from " + entity + " a where a.id=:id";
        SqlParameterSource namedParameters =  new MapSqlParameterSource("id",refDetails.get("id"));
        mapList = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        logger.info("result details @@@@@ " + mapList);


        return mapList;
    }

}
