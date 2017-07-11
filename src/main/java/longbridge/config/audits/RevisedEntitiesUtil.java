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
    public static  List<Integer> revisedEntityDetails(String entityName,Integer rev)
    {
        List<Map<String ,Object>> mapList=null;
        List<Integer> revId = new ArrayList<>();

        try
        {
            String auditEntity = entityName + "_AUD";
            Class<?> clazz = Class.forName(PACKAGE_NAME + entityName);
            ApplicationContext context = SpringContext.getApplicationContext();
            DataSource dataSource = context.getBean(DataSource.class);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            String sql = "select a.rev from "+ auditEntity +" a where a.id in (select ar.id from " + auditEntity + " ar where ar.rev = :revisionid)";
            SqlParameterSource namedParameters = new MapSqlParameterSource("revisionid", rev);
            mapList= namedParameterJdbcTemplate.queryForList(sql, namedParameters);
            if(!mapList.isEmpty()) {
                for (Map map : mapList) {
                    revId.add(Integer.parseInt(map.get("REV").toString()));

                }
            }
        }

        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return revId;
    }
    @Transactional
    public static  Map<String, JSONObject> getEntityPastDetails(String entityName,Integer rev)
    {
        List<Map<String ,Object>> entityDetails=null;
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
        entityDetails= namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        logger.info("the details before {}",entityDetails);
        if(!entityDetails.isEmpty()) {
            if(entityDetails.get(0).containsKey("REV")) {
                entityDetails.get(0).remove("REV");
            }
            if(entityDetails.get(0).containsKey("REVTYPE")) {
                entityDetails.get(0).remove("REVTYPE");
            }
            jsonObject =  new JSONObject(entityDetails.get(0));
            logger.info("json details {}",jsonObject);
            mergedDetails.put("pastDetails",jsonObject);
            logger.info("the map key set {}",jsonObject);
            entityDetails = getCurrentDetails(namedParameterJdbcTemplate, entityName, entityDetails.get(0));
            if(!entityDetails.isEmpty()) {
                jsonObject = new JSONObject(entityDetails.get(0));
                mergedDetails.put("currentDetails", jsonObject);
                logger.info("The current details is {}", entityDetails);
            }


        }

        return mergedDetails;
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
