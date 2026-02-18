package com.dtt.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.SubscriberStatus;

@Repository
public interface SubscriberStatusRepository extends JpaRepository<SubscriberStatus, String>{

	SubscriberStatus findBysubscriberUid(String subscriberUniqueId);

}
