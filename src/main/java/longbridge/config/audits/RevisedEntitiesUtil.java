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
import java.util.List;
import java.util.Map;

/**
 * Created by chiomarose on 10/07/2017.
 */
public class RevisedEntitiesUtil {

    static Logger logger = LoggerFactory.getLogger(CustomJdbcUtil.class);

    private static final String PACKAGE_NAME = "longbridge.models.";

@Transactional
    public static  List<Map<String ,Object>> revisedEntityDetails(String entityName,String revisionNumber)
   {
       List<Map<String ,Object>> map=null;

       try
       {
           String auditEntity = entityName + "_AUD";
           logger.info("this is the auditEntity{}",auditEntity);
           Integer rev= Integer.parseInt(revisionNumber);
           logger.info("this is the revision number"+rev);
           Class<?> clazz = Class.forName(PACKAGE_NAME + entityName);
           ApplicationContext context = SpringContext.getApplicationContext();
           DataSource dataSource = context.getBean(DataSource.class);
           logger.info("this is datasource {}",dataSource);
           NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
           String sql = "select * from " + auditEntity + " a where a.rev=:revisionid";
           logger.info("this is the sql{}",sql);
           SqlParameterSource namedParameters = new MapSqlParameterSource("revisionid", rev);
           logger.info("this is name parameter{}",namedParameters);
           map= namedParameterJdbcTemplate.queryForList(sql, namedParameters);
           logger.info("this is the result @@@@@ "+map);
       }

       catch (ClassNotFoundException e)
       {
           e.printStackTrace();
       }

       return map;
    }
}
