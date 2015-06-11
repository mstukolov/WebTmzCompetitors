package com.tmz.db.service.impl;

import com.tmz.db.dao.InventTableDAO;
import com.tmz.db.model.InventTable;
import com.tmz.db.service.InventTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by stukolov_m on 09.06.2015.
 */
@Service("inventTableService")
public class InventTableServiceImpl implements InventTableService {

    @Autowired
    InventTableDAO inventTableDAO;

    @Override
    @Transactional
    public void persistScu(InventTable _args) {
        inventTableDAO.persistScu(_args);
    }

    @Override
    public InventTable findScuByCompetitor(String _args) {
        return inventTableDAO.findScuByCompetitor(_args);
    }

    @Override
    public InventTable findScu(InventTable _args) {
        return inventTableDAO.findScu(_args);
    }

    @Override
    public void updateScu(InventTable _args) {
        inventTableDAO.updateScu(_args);
    }

    @Override
    public void deleteScu(InventTable _args) {
        inventTableDAO.deleteScu(_args);
    }
}


