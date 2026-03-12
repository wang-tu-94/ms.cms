package com.myproject.ms.cms.repository;

import com.myproject.ms.cms.model.MobilePage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobilePageRepository extends MongoRepository<MobilePage, ObjectId>, MobilePageRepositoryCustom {
}
