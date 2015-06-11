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
public class ChesterParse {

    InventTableService inventTableService;
    PricesCompetitorsService priceService;

    public static Integer i = 0, timeoutErrors= 0;
    public static String category = "";

    public static List<InventTable> items = new ArrayList<InventTable>();
    public static List<PricesCompetitors> prices = new ArrayList<PricesCompetitors>();
    public static List<String> errUpload = new ArrayList<String>();
    public static String mainPage = "http://www.tjonline.ru";

    public ChesterParse(InventTableService inventTableService, PricesCompetitorsService priceService) {
        this.inventTableService = inventTableService;
        this.priceService = priceService;
    }

    public void run(List<Reference> urls) throws IOException {
        i = 0;

        for(Reference url : urls){

            if            (url.getReference().contains("/man/")) {category = "мужская";}
            else if       (url.getReference().contains("/woman/")) {category = "женская";}
            parsePage(url.getReference());

        }
        writeDB(items, prices);
        printErrors();
    }
    private static void parsePage(String _current) throws IOException {

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


    private static void printPrices(String scu, String category) throws IOException {

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

        parseElements(item, kindshoes, Integer.valueOf(price.split(" ")[0]),
                Integer.valueOf(priceFirst.split(" ")[0]), category, pElems);

        i++;
        System.out.println("SCU #: " + item + " , " + Integer.valueOf(price.split(" ")[0])
                + " , " + Integer.valueOf(priceFirst.split(" ")[0]) + " , "+ i);
    }
    public static void parseElements(String scu, String kindshoes,
                                     Integer price,Integer priceFirst,
                                     String category, Elements pElems) throws UnsupportedEncodingException {
        String upperMaterial = new String("Материал верха".getBytes("UTF8"));
        String soleMaterial = new String("Материал подошвы".getBytes("UTF8"));
        String liningMaterial = new String("Подкладка".getBytes("UTF8"));
        String countryElement = new String("Страна производства".getBytes("UTF8"));

        String upper= "", lining = "", sole = "", country = "Китай";

        for(Element element: pElems){
            if(element.text().indexOf(upperMaterial) != -1){upper = trimElement(element.text());}
            else if(element.text().indexOf(soleMaterial) != -1){sole =  trimElement(element.text());}
            else if(element.text().indexOf(liningMaterial) != -1){lining =  trimElement(element.text());}

        }
        PricesCompetitors nPrice =
                new PricesCompetitors("Tj",  //контрагент
                        scu,              //номенклатура
                        new Date(),       //дата
                        price,            //цена
                        priceFirst        //первая цена
                );

        InventTable inventTable =
                new InventTable(scu,
                        "Tj",
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

    public void writeDB(List<InventTable> items, List<PricesCompetitors> prices){

        SimpleDateFormat df = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
        System.out.println("Налачась запись в базу данных:  " + df.format(new Date()));
        System.out.println("Кол-во загруженных цен: " + prices.size());

        for(InventTable inventTable : items) {
            if(inventTableService.findScu(inventTable) == null){inventTableService.persistScu(inventTable);}
        }

        for(PricesCompetitors price : prices) {
            priceService.persistPrices(price);
        }
        System.out.println("Закончилась запись в базу данных: " + df.format(new Date()));
        System.out.println("Кол-во не загруженных SCU: " + timeoutErrors);

    }
    public static void printErrors(){
        if(errUpload.size() > 0){ for(String err : errUpload){System.out.println(err);}}
        else{System.out.println("Ошибки не обнаружены");}
    }
    public static String  trimElement(String s){

        return s.substring(s.lastIndexOf(":") + 1);
    }
    public static List<Element> addNonClassifiyedElements() throws IOException {


        List<Element> nonclasslnk = new ArrayList<Element>();

        Element element = Jsoup.connect("http://www.carlopazolini.com/ru/collection/women/shoes/pumps/fl-zel5-3").get().body();
        nonclasslnk.add(element);

        return nonclasslnk;

    }
}
