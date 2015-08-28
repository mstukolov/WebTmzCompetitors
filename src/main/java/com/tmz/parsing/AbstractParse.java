package com.tmz.parsing;

import com.tmz.db.model.InventTable;
import com.tmz.db.model.PricesCompetitors;
import com.tmz.db.model.Reference;
import com.tmz.db.service.InventTableService;
import com.tmz.db.service.PricesCompetitorsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stukolov_m on 13.08.2015.
 */
public class AbstractParse {

    InventTableService inventTableService;
    PricesCompetitorsService priceService;

    public static List<InventTable> items = new ArrayList<InventTable>();
    public static List<PricesCompetitors> prices = new ArrayList<PricesCompetitors>();

    public static Integer i = 0, timeoutErrors= 0;
    public static String category = "";
    public static List<String> errUpload = new ArrayList<String>();

    public void parseElements(String competitor,
                              String scu,
                              String kindshoes,
                              Integer price,
                              Integer priceFirst,
                              String category,
                              Elements pElems) throws UnsupportedEncodingException {

        String upperMaterial = new String("Материал верха".getBytes("UTF8"));
        String soleMaterial = new String("Материал подошвы".getBytes("UTF8"));
        String liningMaterial = new String("Материал подкладки".getBytes("UTF8"));
        String countryElement = new String("Страна производства".getBytes("UTF8"));

        String upper= "", lining = "", sole = "", country = "";

        for(Element element: pElems){
            if(element.text().indexOf(upperMaterial) != -1){upper = trimElement(element.text());}
            else if(element.text().indexOf(soleMaterial) != -1){sole =  trimElement(element.text());}
            else if(element.text().indexOf(liningMaterial) != -1){lining =  trimElement(element.text());}
            else if(element.text().indexOf(countryElement) != -1){country = trimElement(element.text());}

        }

        fillArraysLists(competitor, scu, price, priceFirst, kindshoes, upper, lining, sole, country);

    }
    public void writeDB(List<InventTable> items, List<PricesCompetitors> prices){

        SimpleDateFormat df = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
        System.out.println("Налачась запись в базу данных:  " + df.format(new Date()));
        System.out.println("Кол-во загруженных цен:  " + prices.size());

        for(InventTable inventTable : items) {
            if(inventTableService.findScu(inventTable) == null){inventTableService.persistScu(inventTable);}
        }
        for(PricesCompetitors price : prices) {
            priceService.persistPrices(price);
        }
        System.out.println("Закончилась запись в базу данных:  " + df.format(new Date()));
        System.out.println("Кол-во не загруженных SCU: " + timeoutErrors);

    }

    public void fillArraysLists(String competitor,
                                String scu,
                                Integer price,
                                Integer priceFirst,
                                String kindshoes,
                                String upper,
                                String lining,
                                String sole,
                                String country) throws UnsupportedEncodingException {

        PricesCompetitors nPrice =
                new PricesCompetitors(competitor,
                        scu,
                        new Date(),
                        price,
                        priceFirst
                );

        InventTable inventTable =
                new InventTable(scu,
                        competitor,
                        new String(category.getBytes(),"utf-8"),
                        kindshoes,
                        new String(upper.getBytes(), "utf8"),
                        lining,
                        "",
                        "",
                        sole,
                        country,
                        new Date());
        items.add(inventTable);
        prices.add(nPrice);
    }

    public static void printErrors(){
        if(errUpload.size() > 0){ for(String err : errUpload){System.out.println(err);}}
        else{System.out.println("Ошибки не обнаружены");}
    }
    public static String  trimElement(String s){

        return s.substring(s.lastIndexOf(":") + 1);
    }
    public static String  trimArtikul(String s) {
        return s.substring(s.lastIndexOf("�������") + 8);
    }

    public static List<Element> addNonClassifiyedElements() throws IOException {
        List<Element> nonclasslnk = new ArrayList<Element>();
        Element element = Jsoup.connect("http://www.carlopazolini.com/ru/collection/women/shoes/pumps/fl-zel5-3").get().body();
        nonclasslnk.add(element);
        return nonclasslnk;
    }
    public void run(List<Reference> urls) throws IOException {
        System.out.println("Your inside abstract class");
    }
}
