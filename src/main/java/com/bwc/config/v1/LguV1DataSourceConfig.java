package com.bwc.config.v1;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@MapperScan(value = "com.bwc.message.v1.sender.dao.lgu.v1", sqlSessionFactoryRef = "LguV1SqlSessionFactory")
public class LguV1DataSourceConfig {
	private final String LGU_V1_DATA_SOURCE = "LguV1DataSource";

	@Bean(LGU_V1_DATA_SOURCE)
	@ConfigurationProperties(prefix = "spring.lgu-v1-datasource.datasource.hikari")
	public DataSource MtsDataSource() {
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean
	public SqlSessionFactory LguV1SqlSessionFactory(@Qualifier(LGU_V1_DATA_SOURCE) DataSource dataSource) throws
		Exception {

		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);

		Resource[] res = new PathMatchingResourcePatternResolver().getResources(
			"classpath:sqlmap/message/lgu/v1/**/*_sql.xml");
		bean.setMapperLocations(res);

		Resource myBatisConfig = new PathMatchingResourcePatternResolver().getResource(
			"classpath:sqlmap/sql-mapper-config.xml");
		bean.setConfigLocation(myBatisConfig);

		return bean.getObject();
	}

	@Bean
	public DataSourceTransactionManager LguV1TransactionManager(@Qualifier(LGU_V1_DATA_SOURCE) DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
