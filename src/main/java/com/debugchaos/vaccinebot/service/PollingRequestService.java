package com.debugchaos.vaccinebot.service;

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

		pollingRequestDao.saveRequest(pollingRequest);
	}

	public void deleteRequestByUserName(String userName) {
		pollingRequestDao.deleteAllRequestByUserName(userName);
	}

	public List<PollingRequest> getAllPollingRequest() {
		return pollingRequestDao.findAll();
	}
}
