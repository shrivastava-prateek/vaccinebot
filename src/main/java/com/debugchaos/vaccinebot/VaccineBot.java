package com.debugchaos.vaccinebot;

import com.debugchaos.vaccinebot.service.CustomCommandService;
import com.debugchaos.vaccinebot.vo.PollingRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class VaccineBot extends TelegramLongPollingBot {

	@Autowired
	private Environment env;

	@Autowired
	private CustomCommandService commandServive;

	private static final String RegisterCommand = "register";
	private static final String UnregisterCommand = "/unregister";
	private static final String HelpCommand = "/help";
	private static final String StartCommand = "/start";

	private static final Logger logger = LoggerFactory.getLogger(TelegramLongPollingBot.class);

	@Override
	public void onUpdateReceived(Update update) {

		String receivedMessage = update.getMessage().getText();
		String userName = update.getMessage().getFrom().getUserName();
		String name = update.getMessage().getFrom().getFirstName();
		userName = update.getMessage().getFrom().getId().toString();

		logger.info(update.getMessage().getFrom() + " " + update.getMessage().getFrom().getFirstName() + " "
				+ update.getMessage().getFrom().getLastName() + " " + update.getMessage().getFrom().getUserName() + " "
				+ update.getMessage().getFrom().getId() + " " + update.getMessage().getFrom().toString());

		logger.info(userName + " " + receivedMessage + " " + name);

		Long chatId = update.getMessage().getChatId();

		if (receivedMessage.startsWith(RegisterCommand)) {
			String[] params = receivedMessage.split(" ");
			PollingRequest pollingRequest = new PollingRequest(userName, params[1], params[2], chatId);
			commandServive.registerPollingRequest(pollingRequest);
			sendMessage(chatId, "Registered Successfully!");
		} else if (receivedMessage.equalsIgnoreCase(UnregisterCommand)) {
			PollingRequest pollingRequest = new PollingRequest(userName, null, null, chatId);
			commandServive.unregisterPollingRequest(pollingRequest);
			sendMessage(chatId, "Unregistered Successfully!");
		} else if (receivedMessage.equalsIgnoreCase(HelpCommand)) {
			String helpMessage = "To get notified for the available vaccinnation "
					+ "slots in your district, send the message as:\n"
					+ "*register [pincode] [age]* \n"
					+ "e.g., *register 475661 28* \n"
					+ "To unregister use */unregister* command. \n"
					+ "*Please note:* \n"
					+ "You will get notified only when there is a slot available, also notification will stop only "
					+ "when you unregister.";
			sendMessage(chatId, helpMessage);
		} else if (receivedMessage.equalsIgnoreCase(StartCommand)) {
			String helpMessage = "Hi " + name + ", To get started use /help command.";
			sendMessage(chatId, helpMessage);
		} else {
			sendMessage(chatId, "Not a recognized command! Please check help using /help command");
		}

	}

	@Override
	public String getBotUsername() {
		return env.getProperty("bot.username");
	}

	@Override
	public String getBotToken() {
		return env.getProperty("bot.token");
	}

	public void sendMessage(Long chatId, String message) {

		SendMessage sendMessage = new SendMessage();
		sendMessage.setParseMode(ParseMode.MARKDOWN);
		sendMessage.setText(message.toString());
		sendMessage.setChatId(chatId + "");
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

}
