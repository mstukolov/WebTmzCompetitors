package com.tmz.db.dao.impl;

import com.tmz.db.dao.PricesCompetitorsDAO;
import com.tmz.db.model.PricesCompetitors;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by stukolov_m on 09.06.2015.
 */
@Transactional
@Repository("PricesCompetitorsDAO")
public class PricesCompetitorsDAOImpl implements PricesCompetitorsDAO {

    @Autowired
    private SessionFactory sessionFactory;

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

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

