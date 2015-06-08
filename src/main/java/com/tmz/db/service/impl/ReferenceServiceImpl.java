package com.tmz.db.service.impl;

import com.tmz.db.dao.ReferenceDAO;
import com.tmz.db.model.Reference;
import com.tmz.db.service.ReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by stukolov_m on 29.05.2015.
 */
@Service("referenceService")
public class ReferenceServiceImpl implements ReferenceService {

    @Autowired
    ReferenceDAO referenceDAO;

    public void create(Reference reference) {
        referenceDAO.create(reference);
    }

    public void update(Reference reference) {
        referenceDAO.update(reference);
    }

    public void delete(Reference reference) {
        referenceDAO.delete(reference);
    }

    public List<Reference> findAll() {
        return referenceDAO.findAll();
    }
}
