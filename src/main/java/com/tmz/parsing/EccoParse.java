package com.tmz.parsing;

import com.tmz.db.model.InventTable;
import com.tmz.db.model.PricesCompetitors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stukolov_m on 27.05.2015.
 */
public class EccoParse {
    //public static ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    public static Integer i = 0, timeoutErrors= 0;
    public static String category = "";

    public static List<InventTable> items = new ArrayList<InventTable>();
    public static List<PricesCompetitors> prices = new ArrayList<PricesCompetitors>();


    public void run() throws IOException {

        List<String> urls = new ArrayList<String>();

        System.out.println("Start parse Ecco...");

        String ballerinas = "http://www.ecco-shoes.ru/women/shoes/ballerinas/?newcollection=1&&c=cl#ch";
        String sandals = "http://www.ecco-shoes.ru/women/shoes/sandals/?newcollection=1&&c=cl#ch";
        String ankle = "http://www.ecco-shoes.ru/women/shoes/ankle/?newcollection=1&&c=cl#ch";
        String boots = "http://www.ecco-shoes.ru/women/shoes/boots/?newcollection=1&&c=cl#ch";
        String sneakers = "http://www.ecco-shoes.ru/women/shoes/sneakers/?newcollection=1&&c=cl#ch";
        String moccasins = "http://www.ecco-shoes.ru/women/shoes/moccasins/?newcollection=1&&c=cl#ch";
        String lowshoes = "http://www.ecco-shoes.ru/women/shoes/lowshoes/?newcollection=1&&c=cl#ch";
        String sandal = "http://www.ecco-shoes.ru/women/shoes/sandal/?newcollection=1&&c=cl#ch";
        String shoe = "http://www.ecco-shoes.ru/women/shoes/shoe/?newcollection=1&&c=cl#ch";


        String _boots = "http://www.ecco-shoes.ru/men/shoes/boots/?newcollection=1&&c=cl#ch";
        String _sneakers = "http://www.ecco-shoes.ru/men/shoes/sneakers/?newcollection=1&&c=cl#ch";
        String _moccasins = "http://www.ecco-shoes.ru/men/shoes/moccasins/?newcollection=1&&c=cl#ch";
        String _lowshoes = "http://www.ecco-shoes.ru/men/shoes/lowshoes/?newcollection=1&&c=cl#ch";
        String _sandal = "http://www.ecco-shoes.ru/men/shoes/sandal/?newcollection=1&&c=cl#ch";
        String _shoe = "http://www.ecco-shoes.ru/men/shoes/shoe/?newcollection=1&&c=cl#ch";

//        String bags = "http://www.ecco-shoes.ru/accessories/bags/?newcollection=1&&c=cl#ch";
//        String wallets = "http://www.ecco-shoes.ru/accessories/wallets/?newcollection=1&&c=cl#ch";
//        String accessories = "http://www.ecco-shoes.ru/accessories/belts/?newcollection=1&&c=cl#ch";
//        String womenAccessories = "http://www.ecco-shoes.ru/women/accessories/?newcollection=1&&c=cl#ch";
//        String menAccessories = "http://www.ecco-shoes.ru/men/accessories/?newcollection=1&&c=cl#ch";


        urls.add(ballerinas);
        urls.add(sandals);
        urls.add(ankle);
        urls.add(boots);
        urls.add(sneakers);
        urls.add(moccasins);
        urls.add(lowshoes);
        urls.add(sandal);
        urls.add(shoe);

        urls.add(_boots);
        urls.add(_sneakers);
        urls.add(_moccasins);
        urls.add(_lowshoes);
        urls.add(_sandal);
        urls.add(_shoe);

        for(String url : urls){

            if       (url.contains("/men/")) {category = "�������";}
            else if  (url.contains("/women/")){category = "�������";}
            else if  (url.contains("/kids/")){category = "�������";}

            System.out.println("Start parse URL = " + url);

            Document document = Jsoup.connect(url).timeout(100 * 10000000).get();


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
        //writeDB(items, prices);
    }
    private static void printPrices(String scu, String category) throws IOException {
        try {
            Document docSCU = Jsoup.connect(scu).get();
            String item = "", price = "", priceFirst = "", kindshoes = "";

            kindshoes = docSCU.select("#model_container > h1").first().text().split("\\s")[0];
            item = trimElement(docSCU.select("div.block > p.art").first().text());
            price = docSCU.select("dd.new").first().text().replaceAll("([^\\d])+", "");

            //STUM 16.01.2015 ���������� �����������(������) ����-------
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
        String upperMaterial = new String("����".getBytes("UTF8"));
        String soleMaterial = new String("�������".getBytes("UTF8"));
        String liningMaterial = new String("���������".getBytes("UTF8"));
        String countryElement = new String("������ ������������".getBytes("UTF8"));

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

//        SimpleDateFormat df = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
//        System.out.println("�������� ������ � ���� ������:  " + df.format(new Date()));
//        System.out.println("���-�� ����������� ���: " + prices.size());
//
//        InventTableService inventTableService = (InventTableService) context.getBean("inventTableService");
//        PricesCompetitorsService priceService = (PricesCompetitorsService) context.getBean("pricesCompetitorsService");
//
//        //�������� ������ ��������
//        for(InventTable inventTable : items) {
//            if(inventTableService.findScu(inventTable) == null){inventTableService.persistScu(inventTable);}
//        }
//        //������ ����
//        for(PricesCompetitors price : prices) {
//            priceService.persistPrices(price);
//        }
//        System.out.println("����������� ������ � ���� ������: " + df.format(new Date()));
//        System.out.println("���-�� �� ����������� SCU: " + timeoutErrors);

    }
    public static String  trimElement(String s){

        return (s.substring(s.lastIndexOf(":") + 1)).replaceAll(" ", "");
    }
}
