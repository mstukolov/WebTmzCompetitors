package com.tmz.parsing;

import com.tmz.db.model.InventTable;
import com.tmz.db.model.PricesCompetitors;
import com.tmz.db.model.Reference;
import com.tmz.db.service.InventTableService;
import com.tmz.db.service.PricesCompetitorsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stukolov_m on 10.06.2015.
 */
public class EconikaParse extends AbstractParse{


    public EconikaParse(InventTableService inventTableService, PricesCompetitorsService priceService) {
        this.inventTableService = inventTableService;
        this.priceService = priceService;
    }

    public void run(List<Reference> urls) throws IOException {
        category = "woman";
        i = 0;
        //Удаление элементов с последней сессии загрузки
        prices.clear();
        items.clear();
        for(Reference url : urls){

            Document document = Jsoup.connect(url.getReference()).timeout(100 * 10000000).get();
            Elements links = document.select("div.catalog-items > ul > li > a[href]");


            for (Element lnk : links) {
                try {
                    printPrices(lnk.attr("abs:href"), category);
                }catch(java.net.SocketTimeoutException ex){
                    errUpload.add(lnk.attr("abs:href"));
                    System.out.println("Read Timeout Exception");
                    timeoutErrors++;
                    //break;
                }
            }
        }
        writeDB(items, prices);
        printErrors();
    }
    private void printPrices(String scu, String category) throws IOException {

        Document docSCU = Jsoup.connect(scu).get();
        String item = "", price = "", priceFirst = "", kindshoes = "";

        kindshoes =  docSCU.select("h1").text().split("\\s")[0];
        item = docSCU.select("div.item-attr > dl > dd").get(0).text();
        price = docSCU.select(".price-current").first().text().split(" ")[0].replaceAll("\\D", "");


        try {
            priceFirst = docSCU.select(".price-old").first().text().split(" ")[0].replaceAll("\\D", "");
        }catch(NullPointerException ex){priceFirst = price;}
        //-----------------------------------------------------

        Elements  pElems = docSCU.select("div.item-attr > dl > dd");

        parseElements("Econika", item, kindshoes, Integer.valueOf(price), Integer.valueOf(priceFirst), category, pElems);

        i++;
        System.out.println("SCU #: " + item + " , " + Integer.valueOf(price.split(" ")[0])
                + " , " + Integer.valueOf(priceFirst.split(" ")[0]) + " , "+ i);
    }


}
