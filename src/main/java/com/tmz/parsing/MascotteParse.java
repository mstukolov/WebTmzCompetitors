package com.tmz.parsing;

import com.tmz.db.model.InventTable;
import com.tmz.db.model.PricesCompetitors;
import com.tmz.db.model.Reference;
import com.tmz.db.service.InventTableService;
import com.tmz.db.service.PricesCompetitorsService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by stukolov_m on 10.06.2015.
 */
public class MascotteParse extends AbstractParse{


    public static Map<String,String> cookiesMap = new HashMap();

    public MascotteParse(InventTableService inventTableService, PricesCompetitorsService priceService) {
        this.inventTableService = inventTableService;
        this.priceService = priceService;
    }

    public void run(List<Reference> urls) throws IOException {
        i = 0;
        //Удаление элементов с последней сессии загрузки
        prices.clear();
        items.clear();
        System.out.println("Start parse MASCOTTE...");
        String main = "https://shop.mascotte.ru/";


        Connection.Response res = Jsoup.connect(main)
                .method(Connection.Method.POST)
                .execute();

        cookiesMap.put("PHPSESSID", res.cookie("PHPSESSID"));
        cookiesMap.put("BPC", "b5fb3eb9a5aaa1f3da01b6f689eace13");

        for(Reference url : urls){

            if (url.getReference().contains("/dlya-muzhchin")) {category = "man";}
            if  (url.getReference().contains("/dlya-zhenshchin")){category = "woman";}

            Document document = Jsoup.connect(url.getReference()).cookies(cookiesMap).timeout(100 * 10000000).get();
            Element activePage = document.select("ul.pagination > li.active > a[href]").first();

            //parsePage(url);
            if(activePage != null){parsePage("https://shop.mascotte.ru" + activePage.attr("href"));}
            else{parsePage(url.getReference());}
        }
        writeDB(items, prices);
    }
    public static String goNextPage(String url) throws IOException {
        Document document = Jsoup.connect(url).cookies(cookiesMap).timeout(100 * 10000000).get();
        Element activePage = document.select("ul.pagination > li.active > a[href]").first();
        Elements pages = document.select("ul.pagination > li > a[href]");

        String next = null;

        for(Element pgs : pages) {
            if(Integer.valueOf(pgs.attr("data-page"))
                    > Integer.valueOf(activePage.attr("data-page"))) {
                next = "https://shop.mascotte.ru" + pgs.attr("href");
                break;
            }
        }
        return next;
    }
    private void parsePage(String url) throws IOException {
        System.out.println("Active page is: " + url);

        Document activePage = Jsoup.connect(url).cookies(cookiesMap).timeout(100 * 10000000).get();
        Elements links = activePage.select("div > a[href].thumbnail");

        for (Element lnk : links) {
            try {
                printPrices(lnk.attr("abs:href"), category);
            }catch(java.net.SocketTimeoutException ex){
                System.out.println("Read Timeout Exception");
                timeoutErrors++;
            }
        }
        goNextPage(url);
        if(goNextPage(url) != null){parsePage(goNextPage(url));}
    }
    private void printPrices(String scu, String category) throws IOException {

        Document docSCU = Jsoup.connect(scu).cookies(cookiesMap).get();
        String item = "", price = "", priceFirst = "0", kindshoes = "";


        item = trimArtikul(docSCU.select("ol > li.active").text());
        kindshoes =  docSCU.select("a[href]").get(33).text();
        price = docSCU.select("div.main-price").text().replaceAll("\\D","");

        try {
            Elements priceFirstElements = docSCU.select("div.old-price");
            if(!priceFirstElements.isEmpty()){
                priceFirst = priceFirstElements.text().replaceAll("\\D", "");
            }else{priceFirst = price;}
        }catch(NullPointerException ex){priceFirst = price;}
        //-----------------------------------------------------

        Element table = docSCU.select("table").get(0); //select the first table.
        Elements  pElems = table.select("tr");

        try{

            parseElements("Mascotte", item, kindshoes, Integer.valueOf(price), Integer.valueOf(priceFirst), category, pElems);
            i++;
            System.out.println("SCU #: " + item + " , " + Integer.valueOf(price.split(" ")[0])
                    + " , " + Integer.valueOf(priceFirst.split(" ")[0]) + " , "+ i);
        }catch (NumberFormatException ex){System.out.println("NumberFormatException for SCU: " + item);}
    }

}
