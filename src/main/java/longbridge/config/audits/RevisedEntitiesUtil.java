package longbridge.config.audits;

import longbridge.config.SpringContext;
import org.apache.poi.ss.formula.functions.T;
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
           logger.info("this is the auditEntity{}",auditEntity);
//           Integer rev= Integer.parseInt(revisionNumber);
           logger.info("this is the revision number"+rev);
           Class<?> clazz = Class.forName(PACKAGE_NAME + entityName);
           ApplicationContext context = SpringContext.getApplicationContext();
           DataSource dataSource = context.getBean(DataSource.class);
           logger.info("this is datasource {}",dataSource);
           NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
           String sql = "select a.rev from "+ auditEntity +" a where a.id in (select ar.id from " + auditEntity + " ar where ar.rev = :revisionid)";
           logger.info("this is the sql{}",sql);
           SqlParameterSource namedParameters = new MapSqlParameterSource("revisionid", rev);
           mapList= namedParameterJdbcTemplate.queryForList(sql, namedParameters);
                          logger.info("this is the result @@@@@ " + mapList);
           if(!mapList.isEmpty()) {
               for (Map map : mapList) {
                   revId.add(Integer.parseInt(map.get("REV").toString()));
               }
           }


//           if(!mapList.isEmpty()) {
//               logger.info("this is the result @@@@@ " + mapList.get(0).get("id"));
//               mapList = getAllEntityDetails(namedParameterJdbcTemplate,auditEntity,mapList.get(0));
//
//           }
       }

       catch (ClassNotFoundException e)
       {
           e.printStackTrace();
       }

       return revId;
    }
    @Transactional
    public static  List<Map<String ,Object>> getAllEntityDetails(NamedParameterJdbcTemplate namedParameterJdbcTemplate,String auditEntity,Map refDetails)
   {
       List<Map<String ,Object>> mapList=null;
               String sql = "select * from " + auditEntity + " a where a.id=:id";
               SqlParameterSource namedParameters =  new MapSqlParameterSource("id",refDetails.get("id"));
               mapList = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
               logger.info("result details @@@@@ " + mapList);


       return mapList;
    }

}
