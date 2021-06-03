package com.debugchaos.vaccinebot.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.debugchaos.vaccinebot.service.MessageReceiverService;

@Component
public class ScheduledTask {

	@Autowired
	MessageReceiverService messageService;

	@Scheduled(cron = "${cron.cleanup}", zone = "${cron.zone}")
	public void cleanupOldSlots() {
		messageService.removeOldSlotsFromPollingRequests();
	}

	@Async
	@Scheduled(fixedRateString = "${cowin.poll.rate}", initialDelayString = "${cowin.poll.initialdelay}")
	public void pollCowinService() {
		messageService.pollCowinForEachPincode();
	}

}
