package longbridge.models.audits;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import longbridge.config.SpringContext;

/**
 * Created by ayoade_farooq@yahoo.com on 4/19/2017.
 */
public class CustomJdbcUtil {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Transactional
	public static boolean auditEntity(String entityName) {
		ApplicationContext context = SpringContext.getApplicationContext();
		DataSource dataSource = context.getBean(DataSource.class);
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "select count(*) from audit_config ac where ac.table_name = :entity and ac.enabled='Y' ";
		SqlParameterSource namedParameters = new MapSqlParameterSource("entity", entityName);
		Integer cnt = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);

		return cnt >= 1;
	}


}
