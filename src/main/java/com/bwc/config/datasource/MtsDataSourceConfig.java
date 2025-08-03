package com.bwc.config.datasource;

import javax.sql.DataSource;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

// @Configuration
// @EnableJpaRepositories(
//     basePackages = "com.bwc.messaging.sms.infrastructure.persistence.mts",
//     entityManagerFactoryRef = "mtsEntityManagerFactory",
//     transactionManagerRef = "mtsTransactionManager"
// )
public class MtsDataSourceConfig {

    @Bean(name = "mtsDataSource")
    @ConfigurationProperties(prefix = "spring.mts-datasource.datasource.hikari")
    public DataSource mtsDataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
    }

    @Bean(name = "mtsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mtsEntityManagerFactory(
            @Qualifier("mtsDataSource") DataSource dataSource) {
        
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.bwc.messaging.sms.infrastructure.persistence.mts");
        factory.setPersistenceUnitName("mts");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factory.setJpaVendorAdapter(vendorAdapter);
        
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        jpaProperties.put("hibernate.show_sql", false);
        factory.setJpaProperties(jpaProperties);
        
        return factory;
    }

    @Bean(name = "mtsTransactionManager")
    public PlatformTransactionManager mtsTransactionManager(
            @Qualifier("mtsEntityManagerFactory") LocalContainerEntityManagerFactoryBean mtsEntityManagerFactory) {
        return new JpaTransactionManager(mtsEntityManagerFactory.getObject());
    }
}
