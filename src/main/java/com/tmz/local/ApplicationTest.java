package com.tmz.local;

import com.tmz.db.service.InventTableService;
import com.tmz.db.service.PricesCompetitorsService;
import com.tmz.db.service.ReferenceService;
import com.tmz.parsing.ChesterParse;
import com.tmz.parsing.EccoParse;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by stukolov_m on 25.08.2015.
 */
public class ApplicationTest {

    public static ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

    public static void main(String[] args) {

        InventTableService inventTableService = (InventTableService) context.getBean("inventTableService");
        PricesCompetitorsService priceService = (PricesCompetitorsService) context.getBean("pricesCompetitorsService");
        ReferenceService referenceService = (ReferenceService) context.getBean("referenceService");;

        ChesterParse chesterParse = new ChesterParse(inventTableService, priceService);
        EccoParse eccoParse = new EccoParse(inventTableService, priceService);

        new BaseThread(chesterParse, referenceService);
        new BaseThread(eccoParse, referenceService);

    }
}
