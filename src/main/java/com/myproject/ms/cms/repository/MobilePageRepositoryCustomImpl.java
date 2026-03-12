package com.myproject.ms.cms.repository;

import com.myproject.ms.cms.model.MobilePage;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
public class MobilePageRepositoryCustomImpl implements MobilePageRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    public MobilePageRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<MobilePage> searchByFilter(MobilePageFilter filter, Pageable pageable) {
        Query query = new Query();

        if (filter.name() != null) {
            query.addCriteria(Criteria.where("name").regex(filter.name(), "i"));
        }

        if (filter.state() != null) {
            query.addCriteria(Criteria.where("state").is(filter.state()));
        }

        if (filter.afterPublishedDate() != null) {
            query.addCriteria(Criteria.where("publishedDate").gte(filter.afterPublishedDate()));
        }

        if (filter.ids() != null) {
            List<ObjectId> objectIds = filter.ids().stream()
                    .filter(ObjectId::isValid)
                    .map(ObjectId::new)
                    .toList();
            if (!objectIds.isEmpty()) {
                query.addCriteria(Criteria.where("_id").in(objectIds));
            }
        }

        if (pageable.getSort().isUnsorted()) {
            query.with(Sort.by(Sort.Direction.DESC, "publishedDate"));
        } else {
            query.with(pageable.getSort());
        }

        // 2. Récupérer le "Count" total avant d'appliquer la pagination
        long total = mongoTemplate.count(query, MobilePage.class);

        // 3. Appliquer la pagination (Skip/Limit) et le Tri
        query.with(pageable);

        // 4. Exécuter la requête pour les données de la page
        List<MobilePage> content = mongoTemplate.find(query, MobilePage.class);

        // 5. Retourner l'objet Page complet
        return PageableExecutionUtils.getPage(content, pageable, () -> total);

    }
}
