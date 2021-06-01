package com.debugchaos.vaccinebot;

import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.BEAN_DATASOURCE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.BEAN_ENTITYMANAGER_FACTORY;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.BEAN_TRANSACTION;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.BEAN_VACCINEBOT;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.ENTITY_PACKAGE_SCAN;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.HIBERNATE_FORMAT_SQL;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.HIBERNATE_GENERATE_STATISTICS;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.HIBERNATE_HBM2DDL_AUTO;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.HIBERNATE_SHOW_SQL;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.HIBERNATE_USE_SQL_COMMENTS;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.JPA_REPO_PACKAGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.PERSISTENCE_UNIT_NAME;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.PROFILE_ORACLEDB;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.PROFILE_POSTGRESDB;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.QUEUE_FACTORY;

import java.sql.SQLException;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;

@SpringBootApplication
@EnableJms
@EnableJpaRepositories(basePackages = JPA_REPO_PACKAGE)
@EnableTransactionManagement
@EnableScheduling
public class VaccinebotApplication {

	@Autowired
	private Environment env;

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Value("${db.url}")
	private String dbURL;
	@Value("${db.user}")
	private String dbUser;
	@Value("${db.password}")
	private String dbPassword;
	@Value("${db.rowprefetch}")
	private String dbRowPrefetch;
	@Value("${db.driverclass}")
	private String dbDriverClassName;

	public static void main(String[] args) {

		for (String arg : args) {
			logger.info(arg);
		}

		ConfigurableApplicationContext applicationContext = SpringApplication.run(VaccinebotApplication.class, args);
		VaccineBot vaccineBot = (VaccineBot) applicationContext.getBean(BEAN_VACCINEBOT);
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(vaccineBot);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Bean(QUEUE_FACTORY)
	public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		return factory;
	}

	@Bean // Serialize message content to json using TextMessage
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Profile(PROFILE_ORACLEDB)
	@Bean(BEAN_DATASOURCE)
	public DataSource getOracleDataSource() {
		Properties info = new Properties();
		info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, dbUser);
		info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, dbPassword);
		info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, dbRowPrefetch);
		OracleDataSource ods = null;
		try {
			ods = new OracleDataSource();
			ods.setURL(dbURL);
			ods.setConnectionProperties(info);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ods;

	}

	@Profile(PROFILE_POSTGRESDB)
	@Bean(BEAN_DATASOURCE)
	public DataSource getDataSource() {
		DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(dbDriverClassName);
		dataSourceBuilder.url(dbURL);
		dataSourceBuilder.username(dbUser);
		dataSourceBuilder.password(dbPassword);
		return dataSourceBuilder.build();

	}

	@Bean(BEAN_ENTITYMANAGER_FACTORY)
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier(BEAN_DATASOURCE) DataSource dataSource) throws NamingException {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(getDataSource());
		em.setPackagesToScan(ENTITY_PACKAGE_SCAN);
		em.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaProperties(additionalProperties());
		return em;
	}

	@Bean(BEAN_TRANSACTION)
	public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory)
			throws NamingException {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
		return transactionManager;
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty(HIBERNATE_HBM2DDL_AUTO, env.getProperty(HIBERNATE_HBM2DDL_AUTO));
		properties.setProperty(HIBERNATE_GENERATE_STATISTICS, env.getProperty(HIBERNATE_GENERATE_STATISTICS));
		properties.setProperty(HIBERNATE_USE_SQL_COMMENTS, env.getProperty(HIBERNATE_USE_SQL_COMMENTS));
		properties.setProperty(HIBERNATE_FORMAT_SQL, env.getProperty(HIBERNATE_FORMAT_SQL));
		properties.setProperty(HIBERNATE_SHOW_SQL, env.getProperty(HIBERNATE_SHOW_SQL));
		return properties;
	}

}
