package com.tmz.db.dao.impl;

import com.tmz.db.dao.PricesCompetitorsDAO;
import com.tmz.db.model.PricesCompetitors;
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
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by stukolov_m on 09.06.2015.
 */
@Transactional
@Repository("PricesCompetitorsDAO")
public class PricesCompetitorsDAOImpl implements PricesCompetitorsDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private FullTextSession fullTextSession;

    @Override
    public void persistPrices(PricesCompetitors pricesCompetitors) {
        sessionFactory.getCurrentSession().persist(pricesCompetitors);
    }

    @Override
    public PricesCompetitors findPricesByCompetitor(String id) {
        return (PricesCompetitors) sessionFactory.getCurrentSession().get(PricesCompetitors.class, id);
    }

    @Override
    public PricesCompetitors findPrice(Integer id) {
        return (PricesCompetitors) sessionFactory.getCurrentSession().get(PricesCompetitors.class, id);
    }

    @Override
    public void updatePrices(PricesCompetitors pricesCompetitors) {
        sessionFactory.getCurrentSession().update(pricesCompetitors);
    }

    @Override
    public void deletePrices(PricesCompetitors pricesCompetitors) {
        sessionFactory.getCurrentSession().delete(pricesCompetitors);
    }

    @Override
    public void deletePriceByDate(String competitor, Date date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyyy-mm-dd");
        fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

        try {
            fullTextSession.createIndexer(PricesCompetitors.class).startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(PricesCompetitors.class).get();
        BooleanQuery booleanQuery = new BooleanQuery();


        if (competitor != null) {
            BooleanJunction<BooleanJunction> competitorBJ = queryBuilder.bool();
            competitorBJ.should(queryBuilder.phrase().onField("competitor").sentence(competitor).createQuery());
            booleanQuery.add(competitorBJ.createQuery(), BooleanClause.Occur.MUST);
        }
        if (date != null) {

            Date today = new Date();
            Date yesterday = new Date(today.getTime() - (1000 * 60 * 60 * 24));

            BooleanJunction<BooleanJunction> dateBJ = queryBuilder.bool();
            dateBJ.should(queryBuilder.range().onField("priceDate").above(yesterday).createQuery());
            booleanQuery.add(dateBJ.createQuery(), BooleanClause.Occur.MUST);
        }

        List<PricesCompetitors> result = fullTextSession.createFullTextQuery(booleanQuery, PricesCompetitors.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        for(PricesCompetitors pricesCompetitors : result){
            sessionFactory.getCurrentSession().delete(pricesCompetitors);
        }
        sessionFactory.getCurrentSession().flush();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

