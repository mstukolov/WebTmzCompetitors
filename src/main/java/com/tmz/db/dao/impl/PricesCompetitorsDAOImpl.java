package com.tmz.db.dao.impl;

import com.tmz.db.dao.PricesCompetitorsDAO;
import com.tmz.db.model.PricesCompetitors;
import com.tmz.db.model.Reference;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.hibernate.Criteria;
import org.hibernate.Query;
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

        fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

        Date today = new Date();
        Date yesterday = new Date(today.getTime() - (1000 * 60 * 60 * 24));

        String queryStr = "delete from PricesCompetitors where priceDate > :DATE and competitor = :COMP";
        Query query = getSessionFactory().getCurrentSession().createQuery(queryStr);
        query.setParameter("DATE", yesterday);
        query.setParameter("COMP", competitor);

        int res = query.executeUpdate();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

