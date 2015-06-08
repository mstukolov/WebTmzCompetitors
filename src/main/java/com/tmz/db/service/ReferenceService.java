package com.tmz.db.service;

import com.tmz.db.model.Reference;

import java.util.List;

/**
 * Created by stukolov_m on 29.05.2015.
 */
public interface ReferenceService {
    void create(Reference reference);

    void update(Reference reference);

    void delete(Reference reference);

    List<Reference> findAll();
}
