package com.tmz.local;

import com.tmz.db.service.ReferenceService;
import com.tmz.parsing.AbstractParse;
import com.tmz.parsing.ChesterParse;
import com.tmz.parsing.EccoParse;

import java.io.IOException;

/**
 * Created by stukolov_m on 25.08.2015.
 */
public class BaseThread implements Runnable{
    Thread t;
    AbstractParse parse;
    ReferenceService referenceService;

    public BaseThread() {
        t = new Thread(this, "Base thread");
        t.start();
    }
    public BaseThread(AbstractParse abstractParse,  ReferenceService referenceService) {
        t = new Thread(this, "Base thread");
        this.parse = abstractParse;
        this.referenceService = referenceService;
        t.start();
    }

    @Override
    public void run() {

        if(parse instanceof EccoParse){
            System.out.println("Запущен класс Ecco");
            try {
                parse.run(referenceService.findByCompetitor("Ecco"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(parse instanceof ChesterParse){
            System.out.println("Запущен класс ChesterParse");
            try {
                parse.run(referenceService.findByCompetitor("Tj"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
