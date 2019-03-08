package com.acme.mvc.config;

import com.acme.mvc.misc.TablePrefixedSpringPhysicalNamingStrategy;
import com.acme.mvc.domain.Product;
import com.acme.mvc.repository.OrderRepository;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("com.acme.mvc.repository")
class PersistenceConfiguration {
  @Bean
  DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
//        .addScripts("schema.sql", "data.sql")
        .setType(EmbeddedDatabaseType.H2)
        .build();
  }

  @Bean
  LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();
    entityManagerFactoryBean.setPackagesToScan(Product.class.getPackage().getName());
    entityManagerFactoryBean.setDataSource(dataSource);
    entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

    Map<String, Object> jpaHibernateProperties = new HashMap<>();
    jpaHibernateProperties.put(AvailableSettings.SHOW_SQL, true);
    jpaHibernateProperties.put(AvailableSettings.FORMAT_SQL, true);
    jpaHibernateProperties.put(AvailableSettings.HBM2DDL_AUTO, "create-drop");
    jpaHibernateProperties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, TablePrefixedSpringPhysicalNamingStrategy.class);
    entityManagerFactoryBean.setJpaPropertyMap(jpaHibernateProperties);

    return entityManagerFactoryBean;
  }

  @Bean
  PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
