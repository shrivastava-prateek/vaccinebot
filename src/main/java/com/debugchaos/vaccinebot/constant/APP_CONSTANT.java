package com.debugchaos.vaccinebot.constant;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public interface APP_CONSTANT {
	
	String REGISTER_COMMAND = "REGISTER";
	String UNREGISTER_COMMAND = "UNREGISTER";
	String HELP_COMMAND = "HELP";
	String START_COMMAND = "START";
	String REGISTRATION_DETAILS_COMMAND = "REGISTRATIONS";

	String HELP_MESSAGE = "To get notified for the available vaccination slots in your district, send the message as:\n" 
			+ "*register [pincode] [age]* \n"
			+ "e.g., *register 475661 28* \n" 
			+ "To unregister use */unregister* command. \n" 
			+ "To get all the registrations details \n use */registrations* command. \n"
			+ "*Please note:* \n"
			+ "You will get notified only when there is a slot available, also notification will stop only "
			+ "when you unregister.";
	String REGISTERED_MESSAGE = "Registered Successfully!";
	String UNREGISTERED_MESSAGE = "Unregistered Successfully!";
	
	String INVALID_COMMAND_MESSAGE = "Not a recognized command! Please check help using /help command";
	String INVALID_AGE_MESSAGE = "Invalid age!";
	String INVALID_PINCODE_MESSAGE = "Invalid pincode!";
	String INSUFFICIENT_PARAMETERS_MESSAGE = "Please send message in this format only: *register [pincode] [age]*";
	String DDOS_MESSAGE = "You have reached the maximum limit of registrations.\n"
			+ "Please unregister using /unregister command and then register again.";
	String ALREADY_REGISTERED_MESSAGE = "You are already registered for this query!";
	String NOT_REGISTERED_MESSAGE = "No Registrations available!";

	
	String QUEUE_FACTORY = "queueFactory";
	String REGISTRATION_QUEUE = "registerQueue";
	String UNREGISTERATION_QUEUE = "unregisterQueue";
	String REGISTERATIONDETAILS_QUEUE = "registrationDetailsQueue";
	
	int THREAD_POOL_EXECUTOR_SIZE = 50;
	
	String HIBERNATE_HBM2DDL_AUTO="hibernate.hbm2ddl.auto";
	String HIBERNATE_GENERATE_STATISTICS = "hibernate.generate_statistics";
	String HIBERNATE_USE_SQL_COMMENTS = "hibernate.use_sql_comments";
	String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
	String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	
	String PERSISTENCE_UNIT_NAME = "entityManagerFactory";
	
	String EMPTY_STRING="";
	String SINGLE_SPACE=" ";
	String ALPHA_NUMERIC_REGEX = "[^a-zA-Z0-9\\s]";
	String PINCODE_REGEX = "^[1-9]{1}[0-9]{2}[0-9]{3}$";
	Pattern PINCODE_PATTERN = Pattern.compile(PINCODE_REGEX);
	String AGE_REGEX = "^(1[8-9]|[2-9][0-9]|1[0-2][0-9])$";
	Pattern AGE_PATTERN = Pattern.compile(AGE_REGEX);

	int MIN_18_AGE = 18;
	int MIN_45_AGE = 45;
	
	DateTimeFormatter ddMMyyyyFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	String ZONEID_INDIA = "Asia/Kolkata";
	
	String BEAN_DATASOURCE="dataSource";
	String BEAN_TRANSACTION = "transaction";
	String BEAN_ENTITYMANAGER_FACTORY = "entityManagerFactory";
	String BEAN_VACCINEBOT = "vaccineBot";
	String BEAN_MESSAGERECEIVER_SERVICE = "messageReceiverService";
	String JPA_REPO_PACKAGE="com.debugchaos.vaccinebot.vo";
	String ENTITY_PACKAGE_SCAN="com.debugchaos.vaccinebot.vo";
	
	String PROFILE_POSTGRESDB="postgresdb";
	String PROFILE_ORACLEDB="oracledb";
	
}
