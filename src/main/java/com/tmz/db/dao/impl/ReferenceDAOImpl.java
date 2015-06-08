package com.tmz.db.dao.impl;

import com.tmz.db.dao.ReferenceDAO;
import com.tmz.db.model.Reference;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
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
}
