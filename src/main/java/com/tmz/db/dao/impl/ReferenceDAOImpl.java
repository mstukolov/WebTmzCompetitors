package com.tmz.db.dao.impl;

import com.tmz.db.dao.ReferenceDAO;
import com.tmz.db.model.Reference;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by stukolov_m on 29.05.2015.
 */

@Transactional
@Repository("referenceDAO")
public class ReferenceDAOImpl implements ReferenceDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private FullTextSession fullTextSession;

    public void create(Reference reference) {
        sessionFactory.getCurrentSession().persist(reference);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().refresh(reference);
    }

    public void update(Reference reference) {
        sessionFactory.getCurrentSession().update(reference);
    }

    public void delete(Reference reference) {
        sessionFactory.getCurrentSession().delete(reference);
    }

    public List<Reference> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from Reference").list();
    }

    public List<Reference> findByCompetitor(String competitor) {
        fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        try {
            fullTextSession.createIndexer(Reference.class).startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Reference.class).get();
        BooleanQuery booleanQuery = new BooleanQuery();


        if (competitor != null) {
            BooleanJunction<BooleanJunction> competitorBJ = queryBuilder.bool();
            competitorBJ.should(queryBuilder.phrase().onField("competitor").sentence(competitor).createQuery());
            booleanQuery.add(competitorBJ.createQuery(), BooleanClause.Occur.MUST);
        }

        List<Reference> result = fullTextSession.createFullTextQuery(booleanQuery, Reference.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        return result;
    }
}
