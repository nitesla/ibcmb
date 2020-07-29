package longbridge.config.audits;

import longbridge.config.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * Created by ayoade_farooq@yahoo.com on 4/19/2017.
 */
@Service
public class CustomJdbcUtil {

    static Logger logger = LoggerFactory.getLogger(CustomJdbcUtil.class);


    private DataSource dataSource;

    @Value("${jdbc.schema.prefix:}")
    private String schema;

    @Autowired
    public CustomJdbcUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Cacheable("audit-config")
    public boolean auditEntity(String entityName) {
        logger.debug("@@@@@@@@@@ Entity name:" + entityName);
        logger.debug("@@@@@@@@@@ Datasource name:" + dataSource);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select count(*) from audit_config ac where ac.table_name = :entity and ac.enabled='Y' ";
        SqlParameterSource namedParameters = new MapSqlParameterSource("entity", entityName);
        Integer cnt = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);

        return cnt >= 1;
    }


}
