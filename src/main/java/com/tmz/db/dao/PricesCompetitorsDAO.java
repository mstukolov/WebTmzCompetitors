package com.tmz.db.dao;

import com.tmz.db.model.PricesCompetitors;

/**
 * Created by stukolov_m on 09.06.2015.
 */
public interface PricesCompetitorsDAO {
    void persistPrices(PricesCompetitors pricesCompetitors);

    PricesCompetitors findPricesByCompetitor(String id);

    PricesCompetitors findPrice(Integer id);

    void updatePrices(PricesCompetitors pricesCompetitors);

    void deletePrices(PricesCompetitors pricesCompetitors);
}
