package com.debugchaos.vaccinebot.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.debugchaos.vaccinebot.vo.PollingRequest;

@Repository
public class PollingRequestDao {

	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	public List<PollingRequest> findAll() {
		return manager.createQuery("from " + PollingRequest.class.getName()).getResultList();
	}

	public void saveRequest(PollingRequest pollingRequest) {
		manager.persist(pollingRequest);
	}

	public void deleteAllRequestByUserName(String userName) {
		manager.createQuery("delete from PollingRequest pr where pr.userName=:userName")
				.setParameter("userName", userName).executeUpdate();
	}

}
