package br.com.luisfga.spring.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class PersistenceConfig {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceConfig.class);

    @Bean
    public DataSource hsqldbDataSource(){
        logger.debug("hsqldbDataSource");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:file:/appdata/spring-demo-db");
        dataSource.setUsername( "sa" );
        dataSource.setPassword( "" );
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        logger.debug("entityManagerFactoryBean");
        LocalContainerEntityManagerFactoryBean factory
                = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(hsqldbDataSource());
        factory.setPackagesToScan("br.com.luisfga.spring.business.entities");
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.schema-generation.database.action", "update");
//        properties.setProperty("javax.persistence.schema-generation.database.action", "drop-and-create");
        factory.setJpaProperties(properties);

        return factory;
    }

}
