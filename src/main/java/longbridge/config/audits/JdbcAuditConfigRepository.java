package longbridge.config.audits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class JdbcAuditConfigRepository {

    private Logger logger = LoggerFactory.getLogger(JdbcAuditConfigRepository.class);
    private static String SCHEMA_PROPERTY = "jdbc.schema.prefix";
    private DataSource dataSource;

    @Value("${jdbc.schema.prefix:}")
    String schema;

    @Autowired
    public JdbcAuditConfigRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Cacheable("audit-config")
    public  boolean auditEntity(String entityName) {
        logger.info("Checking auditable {}",entityName);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        String sql = String.format("select count(*) from %saudit_config ac where ac.full_name = :entity and ac.enabled='Y' ", schema);
        SqlParameterSource namedParameters = new MapSqlParameterSource("entity", entityName);
        Integer cnt = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        return cnt >= 1;
    }


}
