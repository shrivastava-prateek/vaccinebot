package com.debugchaos.vaccinebot;

import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.ALPHA_NUMERIC_REGEX;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.EMPTY_STRING;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.HELP_COMMAND;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.HELP_MESSAGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.INVALID_COMMAND_MESSAGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.MIN_18_AGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.MIN_45_AGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.REGISTER_COMMAND;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.REGISTRATION_DETAILS_COMMAND;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.SINGLE_SPACE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.START_COMMAND;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.UNREGISTER_COMMAND;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.debugchaos.vaccinebot.exception.InvalidRegisterRequestException;
import com.debugchaos.vaccinebot.service.CustomCommandService;
import com.debugchaos.vaccinebot.vo.PollingRequest;

@Component
public class VaccineBot extends TelegramLongPollingBot {

	@Autowired
	private CustomCommandService commandService;

	private static final Logger logger = LoggerFactory.getLogger(VaccineBot.class);

	@Value("${bot.username}")
	private String botUserName;
	@Value("${bot.token}")
	private String botToken;

	@Override
	public void onUpdateReceived(Update update) {

		logger.debug(update.getMessage().getFrom() + " " + update.getMessage().getFrom().getFirstName() + " "
				+ update.getMessage().getFrom().getLastName() + " " + update.getMessage().getFrom().getUserName() + " "
				+ update.getMessage().getFrom().getId() + " " + update.getMessage().getFrom().toString());

		String receivedMessage = update.getMessage().getText();
		String userFirstName = update.getMessage().getFrom().getFirstName() != null
				? update.getMessage().getFrom().getFirstName().trim()
				: "";
		String userLastName = update.getMessage().getFrom().getLastName() != null
				? update.getMessage().getFrom().getLastName().trim()
				: "";
		String userFullName = userFirstName + userLastName;
		Long userId = update.getMessage().getFrom().getId();
		Long chatId = update.getMessage().getChatId();

		logger.debug(receivedMessage + " " + userFirstName + " " + userLastName + " " + userFullName + " " + userId
				+ " " + chatId);

		if (receivedMessage == null || receivedMessage.isBlank()) {

			sendMessage(chatId, INVALID_COMMAND_MESSAGE);
			return;

		} else {
			receivedMessage = receivedMessage.trim().toUpperCase().replaceAll(ALPHA_NUMERIC_REGEX, EMPTY_STRING);
			logger.debug("Stripped message: " + receivedMessage);
		}

		if (receivedMessage.startsWith(REGISTER_COMMAND)) {

			String[] params = receivedMessage.split(SINGLE_SPACE);

			try {

				commandService.validateRegisterRequest(params);

			} catch (InvalidRegisterRequestException exception) {

				sendMessage(chatId, exception.getMessage());
				return;
			}

			logger.debug("Validated pin and age");

			int pincode = Integer.parseInt(params[1].trim());
			int age = Integer.parseInt(params[2].trim()) >= MIN_45_AGE ? MIN_45_AGE : MIN_18_AGE;

			PollingRequest pollingRequest = new PollingRequest(userId, userFullName, pincode, age, chatId, null);
			commandService.registerPollingRequest(pollingRequest);

		} else if (receivedMessage.equalsIgnoreCase(UNREGISTER_COMMAND)) {

			PollingRequest pollingRequest = new PollingRequest(userId, null, 0, 0, chatId, null);
			commandService.unregisterPollingRequest(pollingRequest);

		} else if (receivedMessage.equalsIgnoreCase(HELP_COMMAND)) {

			sendMessage(chatId, HELP_MESSAGE);

		} else if (receivedMessage.equalsIgnoreCase(START_COMMAND)) {

			String helpMessage = "Hi " + userFullName + ", To get started use /help command.";
			sendMessage(chatId, helpMessage);

		} else if (receivedMessage.equalsIgnoreCase(REGISTRATION_DETAILS_COMMAND)) {

			PollingRequest pollingRequest = new PollingRequest(userId, null, 0, 0, chatId, null);
			commandService.getRegistrationDetails(pollingRequest);

		} else {

			sendMessage(chatId, INVALID_COMMAND_MESSAGE);

		}

	}

	@Override
	public String getBotUsername() {
		return botUserName;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

	public void sendMessage(Long chatId, String message) {

		SendMessage sendMessage = new SendMessage();
		sendMessage.setParseMode(ParseMode.MARKDOWN);
		sendMessage.setText(message);
		sendMessage.setChatId(chatId + "");
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

}
