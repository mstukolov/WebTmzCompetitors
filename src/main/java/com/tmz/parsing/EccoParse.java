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

public class EccoParse {

    public static Integer i = 0, timeoutErrors= 0;
    public static String category = "";

    public static List<InventTable> items = new ArrayList<InventTable>();
    public static List<PricesCompetitors> prices = new ArrayList<PricesCompetitors>();

    InventTableService inventTableService;
    PricesCompetitorsService priceService;

    public EccoParse(InventTableService inventTableService, PricesCompetitorsService priceService) {
        this.inventTableService = inventTableService;
        this.priceService = priceService;
    }

    public void run(List<Reference> urls) throws IOException {
        i = 0;
        System.out.println("Start parse Ecco...");

        for(Reference url : urls){

            if       (url.getReference().contains("/men/")) {category = "мужская";}
            else if  (url.getReference().contains("/women/")){category = "женская";}
            else if  (url.getReference().contains("/kids/")){category = "детская";}

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
    private static void printPrices(String scu, String category) throws IOException {
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

            parseElements(item, kindshoes, Integer.valueOf(price), Integer.valueOf(priceFirst), category, pElems);

            i++;
            System.out.println("SCU #: " + item + " , " + Integer.valueOf(price.split(" ")[0])
                    + " , " + Integer.valueOf(priceFirst.split(" ")[0]) + " , "+"," + kindshoes + ","+ i);

        }catch (java.net.SocketException ex){System.out.println("java.net.SocketException: Connection reset");}
    }
    public static void parseElements(String scu, String kindshoes,
                                     Integer price, Integer priceFirst,
                                     String category, Elements pElems) throws UnsupportedEncodingException {
        String upperMaterial = new String("Верх".getBytes("UTF8"));
        String soleMaterial = new String("Подошва".getBytes("UTF8"));
        String liningMaterial = new String("Подкладка".getBytes("UTF8"));
        String countryElement = new String("Страна производства".getBytes("UTF8"));

        String upper= "", lining = "", sole = "", country = "";


        for(Element element: pElems){
            if(element.text().indexOf(upperMaterial) != -1){upper = trimElement(element.select("span.show_1").text());}
            else if(element.text().indexOf(soleMaterial) != -1){sole =  trimElement(element.select("span.show_1").text());}
            else if(element.text().indexOf(liningMaterial) != -1){lining =  trimElement(element.select("span.show_1").text());}
            else if(element.text().indexOf(countryElement) != -1){country = trimElement(element.select("span.show_1").text());}

        }
        PricesCompetitors nPrice =
                new PricesCompetitors("Ecco",  //�����
                        scu,              //�������
                        new Date(),       //���� ����
                        price,            //����
                        priceFirst        //������ ����
                );

        InventTable inventTable =
                new InventTable(scu.replaceAll(" ", ""),
                        "Ecco",
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
        System.out.println("�������� ������ � ���� ������:  " + df.format(new Date()));
        System.out.println("���-�� ����������� ���: " + prices.size());

//        InventTableService inventTableService = (InventTableService) context.getBean("inventTableService");
//        PricesCompetitorsService priceService = (PricesCompetitorsService) context.getBean("pricesCompetitorsService");

        //�������� ������ ��������
        for(InventTable inventTable : items) {
            if(inventTableService.findScu(inventTable) == null){inventTableService.persistScu(inventTable);}
        }
        //������ ����
        for(PricesCompetitors price : prices) {
            priceService.persistPrices(price);
        }
        System.out.println("����������� ������ � ���� ������: " + df.format(new Date()));
        System.out.println("���-�� �� ����������� SCU: " + timeoutErrors);

    }
    public static String  trimElement(String s){

        return (s.substring(s.lastIndexOf(":") + 1)).replaceAll(" ", "");
    }

    public InventTableService getInventTableService() {
        return inventTableService;
    }

    public void setInventTableService(InventTableService inventTableService) {
        this.inventTableService = inventTableService;
    }

    public PricesCompetitorsService getPriceService() {
        return priceService;
    }

    public void setPriceService(PricesCompetitorsService priceService) {
        this.priceService = priceService;
    }
}
