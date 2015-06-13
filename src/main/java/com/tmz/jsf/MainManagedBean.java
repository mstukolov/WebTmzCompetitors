package com.tmz.jsf;


import com.tmz.db.model.PricesCompetitors;
import com.tmz.db.model.Reference;
import com.tmz.db.service.InventTableService;
import com.tmz.db.service.PricesCompetitorsService;
import com.tmz.db.service.ReferenceService;
import com.tmz.parsing.*;
import org.primefaces.event.CellEditEvent;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stukolov_m on 22.05.2015.
 */
@ManagedBean(name="mainManagedBean")
@SessionScoped
public class MainManagedBean implements Serializable {

    List<Reference> urlList = new ArrayList();
    List<Reference> selectedReferences = new ArrayList();
    Reference selected;
    private boolean chkEcco, chkCarloPazolini, chkMascotte, chkTj, chkEconika;

    @ManagedProperty(value="#{referenceService}")
    ReferenceService referenceService;

    @ManagedProperty(value="#{inventTableService}")
    InventTableService inventTableService;

    @ManagedProperty(value="#{pricesCompetitorsService}")
    PricesCompetitorsService priceService;

    String curReference;
    String logMessage;
    private int number;

    public MainManagedBean(){ }

    @PostConstruct
    public void init(){

        urlList = referenceService.findAll();
    }

    public void selectRow() {
        setCurReference(selected.getReference());
        addMessage(selected.getRecid().toString());
    }

    public void getActiveReferenceList(){
        for(Reference reference: selectedReferences){
            addMessage(reference.getReference());
        }
    }

    public void runDownloading() throws IOException {

        SimpleDateFormat df = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");

        StringBuffer sb = new StringBuffer("Запуск выгрузки....." + df.format(new Date()));
        setLogMessage(sb.append("\r\n").toString());

        if(chkTj == true){
                setLogMessage(sb.append("Началась выгрузка Tj: " + df.format(new Date())).append("\r\n").toString());
                priceService.deletePriceByDate("Tj", new Date());
                ChesterParse chesterParse = new ChesterParse(inventTableService, priceService);
                chesterParse.run(referenceService.findByCompetitor("Tj"));
                setLogMessage(sb.append("Завершена выгрузка Tj: " + df.format(new Date())).append("\r\n").toString());
        }
        if(chkEcco == true){
                setLogMessage(sb.append("Началась выгрузка Ecco: " + df.format(new Date())).append("\r\n").toString());
                EccoParse eccoParse = new EccoParse(inventTableService, priceService);
                eccoParse.run(referenceService.findByCompetitor("Ecco"));
                setLogMessage(sb.append("Завершена выгрузка Ecco: " + df.format(new Date())).append("\r\n").toString());
        }
        if(chkCarloPazolini == true){
                setLogMessage(sb.append("Началась выгрузка CarloPazolini: " + df.format(new Date())).append("\r\n").toString());
                CarloPazoliniParse carloPazoliniParse = new CarloPazoliniParse(inventTableService, priceService);
                carloPazoliniParse.run(referenceService.findByCompetitor("CarloPazolini"));
                setLogMessage(sb.append("Завершена выгрузка CarloPazolini: " + df.format(new Date())).append("\r\n").toString());
        }
        if(chkEconika == true){
                setLogMessage(sb.append("Началась выгрузка Econika: " + df.format(new Date())).append("\r\n").toString());
                EconikaParse econikaParse = new EconikaParse(inventTableService, priceService);
                econikaParse.run(referenceService.findByCompetitor("Econika"));
                setLogMessage(sb.append("Завершена выгрузка Econika: " + df.format(new Date())).append("\r\n").toString());
        }
        if(chkMascotte == true){
            setLogMessage(sb.append("Началась выгрузка Mascotte: " + df.format(new Date())).append("\r\n").toString());
            MascotteParse mascotteParse = new MascotteParse(inventTableService, priceService);
            mascotteParse.run(referenceService.findByCompetitor("Mascotte"));
            setLogMessage(sb.append("Завершена выгрузка Mascotte: " + df.format(new Date())).append("\r\n").toString());
        }
        setLogMessage(sb.append("Выгрузка завершена: " + df.format(new Date())).append("\r\n").toString());

    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        selected.setReference(newValue.toString());
        referenceService.update(selected);
        addMessage("Строка сохранена!");

    }
    public void addNewRow(String competitor){
        Reference reference = new Reference();
        reference.setReference("Укажите ссылку");
        reference.setCompetitor(competitor);
        referenceService.create(reference);
        urlList = referenceService.findAll();
        selected = reference;
    }
    public void deleteRow(Reference reference){
        referenceService.delete(reference);
        urlList = referenceService.findAll();
        selected = urlList.get(urlList.size() - 1);

    }
    public void editRow(Reference reference){
        referenceService.update(reference);
        addMessage("Строка сохранена!");
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<Reference> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<Reference> urlList) {
        this.urlList = urlList;
    }

    public Reference getSelected() {
        return selected;
    }

    public void setSelected(Reference selected) {
        this.selected = selected;
    }

    public ReferenceService getReferenceService() {
        return referenceService;
    }

    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
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

    public String getCurReference() {
        return curReference;
    }

    public void setCurReference(String curReference) {
        this.curReference = curReference;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
    public int getNumber() {
        return number;
    }

    public void increment() {
        number++;
    }

    public List<Reference> getSelectedReferences() {
        return selectedReferences;
    }

    public void setSelectedReferences(List<Reference> selectedReferences) {
        this.selectedReferences = selectedReferences;
    }

    public boolean isChkEcco() {
        return chkEcco;
    }

    public void setChkEcco(boolean chkEcco) {
        this.chkEcco = chkEcco;
    }

    public boolean isChkCarloPazolini() {
        return chkCarloPazolini;
    }

    public void setChkCarloPazolini(boolean chkCarloPazolini) {
        this.chkCarloPazolini = chkCarloPazolini;
    }

    public boolean isChkMascotte() {
        return chkMascotte;
    }

    public void setChkMascotte(boolean chkMascotte) {
        this.chkMascotte = chkMascotte;
    }

    public boolean isChkTj() {
        return chkTj;
    }

    public void setChkTj(boolean chkTj) {
        this.chkTj = chkTj;
    }

    public boolean isChkEconika() {
        return chkEconika;
    }

    public void setChkEconika(boolean chkEconika) {
        this.chkEconika = chkEconika;
    }
}
