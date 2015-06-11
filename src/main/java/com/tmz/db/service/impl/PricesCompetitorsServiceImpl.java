package com.tmz.db.service.impl;

import com.tmz.db.dao.PricesCompetitorsDAO;
import com.tmz.db.model.PricesCompetitors;
import com.tmz.db.service.PricesCompetitorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by stukolov_m on 09.06.2015.
 */
@Service("pricesCompetitorsService")
public class PricesCompetitorsServiceImpl implements PricesCompetitorsService {

    @Autowired
    PricesCompetitorsDAO pricesCompetitorsDAO;

    @Override
    @Transactional
    public void persistPrices(PricesCompetitors pricesCompetitors) {
        pricesCompetitorsDAO.persistPrices(pricesCompetitors);
    }

    @Override
    @Transactional
    public PricesCompetitors findPricesByCompetitor(String id) {
        return pricesCompetitorsDAO.findPricesByCompetitor(id);
    }

    @Override
    public PricesCompetitors findPrice(Integer id) {
        return pricesCompetitorsDAO.findPrice(id);
    }

    @Override
    @Transactional
    public void updatePrices(PricesCompetitors pricesCompetitors) {
        pricesCompetitorsDAO.updatePrices(pricesCompetitors);
    }

    @Override
    @Transactional
    public void deletePrices(PricesCompetitors pricesCompetitors) {
        pricesCompetitorsDAO.deletePrices(pricesCompetitors);
    }

    @Override
    public void deletePriceByDate(String competitor, Date date) {
        pricesCompetitorsDAO.deletePriceByDate(competitor, date);
    }
}
