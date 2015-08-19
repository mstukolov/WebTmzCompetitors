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
public class ChesterParse extends AbstractParse{

    public static String mainPage = "http://www.tjonline.ru";

    public ChesterParse(InventTableService inventTableService, PricesCompetitorsService priceService) {
        this.inventTableService = inventTableService;
        this.priceService = priceService;
    }

    public void run(List<Reference> urls) throws IOException {
        i = 0;
        //Удаление элементов с последней сессии загрузки
        prices.clear();
        items.clear();

        for(Reference url : urls){

            if            (url.getReference().contains("/man/")) {category = "man";}
            else if       (url.getReference().contains("/woman/")) {category = "woman";}
            parsePage(url.getReference());

        }
        writeDB(items, prices);
        printErrors();
    }
    private  void parsePage(String _current) throws IOException {

        System.out.println(_current);
        Document document = Jsoup.connect(_current).timeout(100 * 10000000).get();

        Elements nextPage = document.select(".modern-page-next");

        Elements blocks = document.select("div.CatalogElement");
        for(Element block : blocks) {
            printPrices(mainPage + block.select("a").get(2).attr("href"), category);
        }

        if(! nextPage.isEmpty()){parsePage(mainPage + nextPage.get(0).attr("href"));}
        else{System.out.println("Это последняя страница");}


    }


    private void printPrices(String scu, String category) throws IOException {

        Document docSCU = Jsoup.connect(scu).get();
        String item = "", price = "", priceFirst = "", kindshoes = "";

        kindshoes =  docSCU.select("div.Articul").text();
        item = docSCU.select("span.h1-articul").first().text();
        price = docSCU.select("div.Price > strong").first().text().replaceAll("\\D", "");

        //STUM 16.01.2015 Добавление зачеркнутой(первой) цены
        try {
            priceFirst = docSCU.select("div.Price > .old").text().replaceAll("\\D", "");
        }catch(NullPointerException ex){priceFirst = "0";}
        if(priceFirst.equals("")){priceFirst = "0";}

        Elements  pElems = docSCU.select(".ElementParam").select("tr");

        parseElements("Tj", item, kindshoes, Integer.valueOf(price.split(" ")[0]),
                Integer.valueOf(priceFirst.split(" ")[0]), category, pElems);

        i++;
        System.out.println("SCU #: " + item + " , " + Integer.valueOf(price.split(" ")[0])
                + " , " + Integer.valueOf(priceFirst.split(" ")[0]) + " , "+ i);
    }

}
