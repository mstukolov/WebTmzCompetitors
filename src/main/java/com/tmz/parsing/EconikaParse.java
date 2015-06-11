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
public class EconikaParse {

    InventTableService inventTableService;
    PricesCompetitorsService priceService;

    public static Integer i = 0, timeoutErrors= 0;
    public static String category = "";
    public static List<InventTable> items = new ArrayList<InventTable>();
    public static List<PricesCompetitors> prices = new ArrayList<PricesCompetitors>();
    public static List<String> errUpload = new ArrayList<String>();

    public EconikaParse(InventTableService inventTableService, PricesCompetitorsService priceService) {
        this.inventTableService = inventTableService;
        this.priceService = priceService;
    }

    public void run(List<Reference> urls) throws IOException {
        category = "женская";
        i = 0;

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
    private static void printPrices(String scu, String category) throws IOException {

        Document docSCU = Jsoup.connect(scu).get();
        String item = "", price = "", priceFirst = "", kindshoes = "";

        kindshoes =  docSCU.select("h1").text().split("\\s")[0];
        item = docSCU.select("div.item-attr > dl > dd").get(0).text();
        price = docSCU.select(".price-current").first().text().split(" ")[0].replaceAll("\\D", "");

        //STUM 16.01.2015 ���������� �����������(������) ����-------
        try {
            priceFirst = docSCU.select(".price-old").first().text().split(" ")[0].replaceAll("\\D", "");
        }catch(NullPointerException ex){priceFirst = price;}
        //-----------------------------------------------------

        Elements  pElems = docSCU.select("div.item-attr > dl > dd");

        parseElements(item, kindshoes, Integer.valueOf(price), Integer.valueOf(priceFirst), category, pElems);

        i++;
        System.out.println("SCU #: " + item + " , " + Integer.valueOf(price.split(" ")[0])
                + " , " + Integer.valueOf(priceFirst.split(" ")[0]) + " , "+ i);
    }
    public static void parseElements(String scu, String kindshoes,
                                     Integer price,
                                     Integer priceFirst,
                                     String category, Elements pElems) throws UnsupportedEncodingException {
        String upperMaterial = new String("Материал верха".getBytes("UTF8"));
        String soleMaterial = new String("Материал подошвы".getBytes("UTF8"));
        String liningMaterial = new String("Материал подкладки".getBytes("UTF8"));
        String countryElement = new String("Страна производства".getBytes("UTF8"));

        String upper= "", lining = "", sole = "", country = "";

        upper = trimElement(pElems.get(1).text());
        sole =  trimElement(pElems.get(3).text());
        lining =  trimElement(pElems.get(2).text());


        PricesCompetitors nPrice =
                new PricesCompetitors("Econika",  //�����
                        scu,              //�������
                        new Date(),       //���� ����
                        price,            //����
                        priceFirst        //������ ����
                );

        InventTable inventTable =
                new InventTable(scu,
                        "Econika",
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
    public static void printErrors(){
        if(errUpload.size() > 0){ for(String err : errUpload){System.out.println(err);}}
        else{System.out.println("������ �� ����������");}
    }
    public static String  trimElement(String s){

        return s.substring(s.lastIndexOf(":") + 1);
    }
}
