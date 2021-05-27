package com.debugchaos.vaccinebot;

import java.sql.SQLException;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.debugchaos.vaccinebot.service.MessageReceiverService;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;

@SpringBootApplication
@EnableJms
@EnableJpaRepositories(basePackages = "com.debugchaos.vaccinebot.vo")
@EnableTransactionManagement
public class VaccinebotApplication {

	@Autowired
	private Environment env;

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {

		for (String arg : args) {
			logger.info(arg);
		}

		ConfigurableApplicationContext applicationContext = SpringApplication.run(VaccinebotApplication.class, args);
		VaccineBot vaccineBot = (VaccineBot) applicationContext.getBean("vaccineBot");
		MessageReceiverService messageReceiver = (MessageReceiverService) applicationContext
				.getBean("messageReceiverService");

		try {
			messageReceiver.initializeRequestSet();
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(vaccineBot);
			messageReceiver.pollForLife();
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Bean("queueFactory")
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

	@Bean("oracledb")
	public DataSource getDataSource() {
		Properties info = new Properties();
		info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, env.getProperty("db.user"));
		info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, env.getProperty("db.password"));
		info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, env.getProperty("db.rowprefetch"));
		OracleDataSource ods = null;
		try {
			ods = new OracleDataSource();
			ods.setURL(env.getProperty("db.url"));
			ods.setConnectionProperties(info);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ods;

	}
	
//	@Bean("oracledb")
//	public DataSource getDataSource() {
//		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//		dataSourceBuilder.driverClassName("org.postgresql.Driver");
//		dataSourceBuilder.url("jdbc:postgresql://localhost:5432/postgres");
//		dataSourceBuilder.username("postgres");
//		dataSourceBuilder.password("postgres");
//		return dataSourceBuilder.build();
//
//	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(getDataSource());
		em.setPackagesToScan(env.getProperty("packages.toscan"));
		em.setPersistenceUnitName("entityManagerFactory");
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaProperties(additionalProperties());
		return em;
	}

	@Bean("transaction")
	public PlatformTransactionManager transactionManager() throws NamingException {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		properties.setProperty("hibernate.generate_statistics", env.getProperty("hibernate.generate_statistics"));
		properties.setProperty("hibernate.use_sql_comments", env.getProperty("hibernate.use_sql_comments"));
		properties.setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
		properties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		return properties;
	}

}
