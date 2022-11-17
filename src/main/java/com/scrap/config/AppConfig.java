/**
 * 
 * @author PRASANTH
 *
 */
package com.scrap.config;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

/**
 * @author PRASANTH
 *
 * 2:47:33 AM
 */
@Configuration
@PropertySource(value = { "classpath:application.properties" })
public class AppConfig  {

		@Autowired
		private Environment env;

		@Bean(name="db1")
		public DataSource dataSource() {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
			dataSource.setUrl(env.getProperty("jdbc.url"));
			dataSource.setUsername(env.getProperty("jdbc.username"));
			dataSource.setPassword(env.getProperty("jdbc.password"));
			dataSource.setMaxActive(env.getProperty("jdbc.maxActive", Integer.class));
			dataSource.setMaxIdle(env.getProperty("jdbc.maxIdle", Integer.class));
			dataSource.setMinIdle(env.getProperty("jdbc.minIdle", Integer.class));
			dataSource.setMaxOpenPreparedStatements(env.getProperty("jdbc.maxStatements", Integer.class));
			dataSource.setMaxWait(env.getProperty("jdbc.maxWait", Integer.class));
			dataSource.setTestOnBorrow(true);
			dataSource.setValidationQuery("select 1");
			return dataSource;
		}


		@Bean(name="jdbcTemplate")
		public JdbcTemplate jdbcTemplate(@Qualifier("db1") DataSource dataSource) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate();
			jdbcTemplate.setDataSource(dataSource);
			jdbcTemplate.setQueryTimeout(10);
			jdbcTemplate.setResultsMapCaseInsensitive(true);
			return jdbcTemplate;
		}
}
