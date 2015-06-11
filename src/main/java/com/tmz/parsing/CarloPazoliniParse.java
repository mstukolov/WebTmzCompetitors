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
 * Created by stukolov_m on 09.06.2015.
 */
public class CarloPazoliniParse {

    InventTableService inventTableService;
    PricesCompetitorsService priceService;

    public static Integer i = 0, timeoutErrors= 0;
    public static String category = "";


    public static List<InventTable> items = new ArrayList<InventTable>();
    public static List<PricesCompetitors> prices = new ArrayList<PricesCompetitors>();

    public static List<String> errUpload = new ArrayList<String>();

    public CarloPazoliniParse(InventTableService inventTableService, PricesCompetitorsService priceService) {
        this.inventTableService = inventTableService;
        this.priceService = priceService;
    }

    public void run(List<Reference> urls) throws IOException {
        i = 0;

        for(Reference url : urls){

            if       (url.getReference().contains("/men/")) {category = "мужская";}
            else if  (url.getReference().contains("/women/")){category = "женская";}

            Document document = Jsoup.connect(url.getReference()).timeout(100 * 10000000).get();
            Elements links = document.select("a[href].content");

            //links.addAll(addNonClassifiyedElements());

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
    private static void printPrices(String scu, String category) throws IOException {

        Document docSCU = Jsoup.connect(scu).get();
        String item = "", price = "", priceFirst = "", kindshoes = "";

        kindshoes =  docSCU.select("h1").first().text();
        item = docSCU.select("h3").first().text();
        price = docSCU.select("p.price.size25").first().text();

        //STUM 16.01.2015 Добавление зачеркнутой(первой) цены
        try {
            priceFirst = docSCU.select("div.col-md-5 > p.price.size15").first().text();
        }catch(NullPointerException ex){priceFirst = price;}

        Elements  pElems = docSCU.select("div.collapse-area > p");

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
        String liningMaterial = new String("Материал подкладки".getBytes("UTF8"));
        String countryElement = new String("Страна производства".getBytes("UTF8"));

        String upper= "", lining = "", sole = "", country = "";

        for(Element element: pElems){
            if(element.text().indexOf(upperMaterial) != -1){upper = trimElement(element.text());}
            else if(element.text().indexOf(soleMaterial) != -1){sole =  trimElement(element.text());}
            else if(element.text().indexOf(liningMaterial) != -1){lining =  trimElement(element.text());}
            else if(element.text().indexOf(countryElement) != -1){country = trimElement(element.text());}

        }
        PricesCompetitors nPrice =
                new PricesCompetitors("CarloPazolini",  //Бренд
                        scu,              //Артикул
                        new Date(),       //Дата цены
                        price,            //цена
                        priceFirst        //Первая цена
                );

        InventTable inventTable =
                new InventTable(scu,
                        "CarloPazolini",
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

        //Создание нового артикула
        for(InventTable inventTable : items) {
            if(inventTableService.findScu(inventTable) == null){inventTableService.persistScu(inventTable);}
        }
        //Запись цены
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
