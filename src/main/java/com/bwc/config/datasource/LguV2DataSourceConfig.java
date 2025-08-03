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
//     basePackages = "com.bwc.messaging.sms.infrastructure.persistence.lgu.v2",
//     entityManagerFactoryRef = "lguV2EntityManagerFactory",
//     transactionManagerRef = "lguV2TransactionManager"
// )
public class LguV2DataSourceConfig {

    @Bean(name = "lguV2DataSource")
    @ConfigurationProperties(prefix = "spring.lgu-v2-datasource.datasource.hikari")
    public DataSource lguV2DataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
    }

    @Bean(name = "lguV2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean lguV2EntityManagerFactory(
            @Qualifier("lguV2DataSource") DataSource dataSource) {
        
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.bwc.messaging.sms.infrastructure.persistence.lgu.v2");
        factory.setPersistenceUnitName("lguV2");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factory.setJpaVendorAdapter(vendorAdapter);
        
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        jpaProperties.put("hibernate.show_sql", false);
        factory.setJpaProperties(jpaProperties);
        
        return factory;
    }

    @Bean(name = "lguV2TransactionManager")
    public PlatformTransactionManager lguV2TransactionManager(
            @Qualifier("lguV2EntityManagerFactory") LocalContainerEntityManagerFactoryBean lguV2EntityManagerFactory) {
        return new JpaTransactionManager(lguV2EntityManagerFactory.getObject());
    }
}
