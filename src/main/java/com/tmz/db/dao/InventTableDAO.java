package com.tmz.db.dao;

import com.tmz.db.model.InventTable;

/**
 * Created by stukolov_m on 09.06.2015.
 */
public interface InventTableDAO {
    void persistScu(InventTable inventTable);

    InventTable findScuByCompetitor(String id);

    InventTable findScu(InventTable inventTable);

    void updateScu(InventTable inventTable);

    void deleteScu(InventTable inventTable);
}
