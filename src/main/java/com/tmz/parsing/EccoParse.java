package com.tmz.parsing;

import com.tmz.db.model.InventTable;
import com.tmz.db.model.PricesCompetitors;
import com.tmz.db.model.Reference;
import com.tmz.db.service.InventTableService;
import com.tmz.db.service.PricesCompetitorsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stukolov_m on 27.05.2015.
 */

public class EccoParse extends AbstractParse{

    public EccoParse(InventTableService inventTableService, PricesCompetitorsService priceService) {
        this.inventTableService = inventTableService;
        this.priceService = priceService;
    }

    public void run(List<Reference> urls) throws IOException {
        i = 0;
        //Удаление элементов с последней сессии загрузки
        prices.clear();
        items.clear();
        System.out.println("Start parse Ecco...");

        for(Reference url : urls){

            if       (url.getReference().contains("/men/")) {category = "men";}
            else if  (url.getReference().contains("/women/")){category = "women";}
            else if  (url.getReference().contains("/kids/")){category = "kids";}

            System.out.println("Start parse URL = " + url.getReference());

            Document document = Jsoup.connect(url.getReference()).timeout(100 * 10000000).get();


            Elements links = document.select("ul.models > li > a[href]");
            for (Element lnk : links) {
                try {
                    printPrices(lnk.attr("abs:href"), category);
                }catch(java.net.SocketTimeoutException ex){
                    System.out.println("Read Timeout Exception");
                    timeoutErrors++;
                    //break;
                }
            }
        }
        writeDB(items, prices);
    }
    private void printPrices(String scu, String category) throws IOException {
        try {
            Document docSCU = Jsoup.connect(scu).get();
            String item = "", price = "", priceFirst = "", kindshoes = "";

            kindshoes = docSCU.select("#model_container > h1").first().text().split("\\s")[0];
            item = trimElement(docSCU.select("div.block > p.art").first().text());
            price = docSCU.select("dd.new").first().text().replaceAll("([^\\d])+", "");

            //STUM 16.01.2015 Добавлена первая цена-------
            try {
                priceFirst = docSCU.select("dd.old").first().text().replaceAll("([^\\d])+", "");
            }catch(NullPointerException ex){priceFirst = price;}
            //-----------------------------------------------------

            Elements pElems = docSCU.select("div.main > dl");

            parseElements("Ecco", item, kindshoes, Integer.valueOf(price), Integer.valueOf(priceFirst), category, pElems);

            i++;
            System.out.println("SCU #: " + item + " , " + Integer.valueOf(price.split(" ")[0])
                    + " , " + Integer.valueOf(priceFirst.split(" ")[0]) + " , "+"," + kindshoes + ","+ i);

        }catch (java.net.SocketException ex){System.out.println("java.net.SocketException: Connection reset");}
    }

}
