package com.debugchaos.vaccinebot.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.debugchaos.vaccinebot.dao.PollingRequestDao;
import com.debugchaos.vaccinebot.vo.PollingRequest;

@Service
@Transactional
public class PollingRequestService {

	@Autowired
	PollingRequestDao pollingRequestDao;

	public void saveRequest(PollingRequest pollingRequest) {
		pollingRequest.setCreatedDate(Instant.now());

		pollingRequestDao.saveRequest(pollingRequest);
	}

	public void deleteRequestByUserId(Long userId) {
		pollingRequestDao.deleteAllRequestByUserId(userId);
	}

	public List<PollingRequest> getAllPollingRequest() {
		return pollingRequestDao.findAll();
	}
}
