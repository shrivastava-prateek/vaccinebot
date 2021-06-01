package com.debugchaos.vaccinebot.task;

import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.COWIN_INITIAL_DELAY;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.COWIN_POLL_RATE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.debugchaos.vaccinebot.service.MessageReceiverService;

@Component
public class ScheduledTask {

	@Autowired
	MessageReceiverService messageService;

	// @Scheduled(cron = "0 0 0 * * ?", zone="Asia/Calcutta")
	// @Scheduled(cron = "@daily")
	// @Scheduled(cron = "* * * * * ?", zone="Asia/Calcutta")
	@Scheduled(cron = "${cron.cleanup}", zone = "${cron.zone}")
	public void cleanupOldSlots() {
		messageService.removeOldSlotsFromPollingRequests();
	}

	@Async
	@Scheduled(fixedRate = COWIN_POLL_RATE, initialDelay = COWIN_INITIAL_DELAY)
	public void pollCowinService() {
		messageService.pollCowinForEachPincode();
	}

}
