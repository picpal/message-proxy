package com.bwc.config.datasource;

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

// @Configuration
// @MapperScan(value = "com.bwc.message.v1.gate.dao", sqlSessionFactoryRef = "MaSqlSessionFactory")
public class MaDataSourceConfig {
	private final String MA_DATA_SOURCE = "MaDataSource";

	// @Bean(MA_DATA_SOURCE)
	// @ConfigurationProperties(prefix = "spring.ma-datasource.datasource.hikari")
	public DataSource MaDataSource() {
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build();
	}

	// @Bean
	public SqlSessionFactory MaSqlSessionFactory(@Qualifier(MA_DATA_SOURCE) DataSource dataSource) throws Exception {

		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);

		Resource[] res = new PathMatchingResourcePatternResolver().getResources(
			"classpath:sqlmap/message/ma/**/*_sql.xml");
		bean.setMapperLocations(res);

		Resource myBatisConfig = new PathMatchingResourcePatternResolver().getResource(
			"classpath:sqlmap/sql-mapper-config.xml");
		bean.setConfigLocation(myBatisConfig);

		return bean.getObject();
	}

	// @Bean
	public DataSourceTransactionManager MaTransactionManager(@Qualifier(MA_DATA_SOURCE) DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

}
