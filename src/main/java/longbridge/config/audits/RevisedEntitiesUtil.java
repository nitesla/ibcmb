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

        try
        {
            String auditEntity = entityName + "_AUD";
            Class<?> clazz = Class.forName(PACKAGE_NAME + entityName);
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
        }

        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return revIds;
    }
    @Transactional
    public static  Map<String, JSONObject> getEntityPastDetails(String entityName,Integer rev)
    {
        Map<String,JSONObject> mergedDetails =  new HashMap<>();
        JSONObject jsonObject = null;
        List<Integer> revId = new ArrayList<>();
        String auditEntity = entityName + "_AUD";
        ApplicationContext context = SpringContext.getApplicationContext();
        DataSource dataSource = context.getBean(DataSource.class);
//           logger.info("this is datasource {}",dataSource);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select * from "+ auditEntity +" a where a.REV = :revId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("revId", rev);
        List<Map<String ,Object>> pastDetails = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        logger.info("the details before {}",pastDetails);
        if(!pastDetails.isEmpty()) {
            logger.info("the map key set {}",pastDetails.get(0).keySet());
            if(pastDetails.get(0).containsKey("REV")) {
                pastDetails.get(0).remove("REV");
            }
            if(pastDetails.get(0).containsKey("REVTYPE")) {
                pastDetails.get(0).remove("REVTYPE");
            }
            jsonObject =  new JSONObject(pastDetails.get(0));
            logger.info("json details {}",jsonObject);
            mergedDetails.put("pastDetails",jsonObject);
            List<Map<String ,Object>> currentDetails = getCurrentDetails(namedParameterJdbcTemplate, entityName, pastDetails.get(0));
            if(!currentDetails.isEmpty()) {
                jsonObject = new JSONObject(currentDetails.get(0));
                mergedDetails.put("currentDetails", jsonObject);
                logger.info("The current details is {}", currentDetails);

            }


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
