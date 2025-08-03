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
//     basePackages = "com.bwc.messaging.sms.infrastructure.persistence.lgu.v1",
//     entityManagerFactoryRef = "lguV1EntityManagerFactory",
//     transactionManagerRef = "lguV1TransactionManager"
// )
public class LguV1DataSourceConfig {

    @Bean(name = "lguV1DataSource")
    @ConfigurationProperties(prefix = "spring.lgu-v1-datasource.datasource.hikari")
    public DataSource lguV1DataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
    }

    @Bean(name = "lguV1EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean lguV1EntityManagerFactory(
            @Qualifier("lguV1DataSource") DataSource dataSource) {
        
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.bwc.messaging.sms.infrastructure.persistence.lgu.v1");
        factory.setPersistenceUnitName("lguV1");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factory.setJpaVendorAdapter(vendorAdapter);
        
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        jpaProperties.put("hibernate.show_sql", false);
        factory.setJpaProperties(jpaProperties);
        
        return factory;
    }

    @Bean(name = "lguV1TransactionManager")
    public PlatformTransactionManager lguV1TransactionManager(
            @Qualifier("lguV1EntityManagerFactory") LocalContainerEntityManagerFactoryBean lguV1EntityManagerFactory) {
        return new JpaTransactionManager(lguV1EntityManagerFactory.getObject());
    }
}
