package com.tmz.jsf;

import com.tmz.db.dao.ReferenceDAO;
import com.tmz.db.model.Reference;
import com.tmz.db.service.ReferenceService;
import com.tmz.parsing.EccoParse;
import org.primefaces.event.CellEditEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stukolov_m on 22.05.2015.
 */
@ManagedBean(name="mainManagedBean")
@SessionScoped
public class MainManagedBean implements Serializable {

    List<Reference> urlList = new ArrayList();
    Reference selected;
    @ManagedProperty(value="#{referenceService}")
    ReferenceService referenceService;

    String curReference;

    public MainManagedBean(){ }

    @PostConstruct
    public void init(){

        urlList = referenceService.findAll();
    }

    public void selectRow() {
        setCurReference(selected.getReference());
        addMessage(selected.getRecid().toString());
    }
    public void runDownloading(String summary) throws IOException {
        //for(String url : urlList){addMessage(url);} addMessage(summary);
        EccoParse eccoParse = new EccoParse();
        eccoParse.run();

    }

    public void saveReferences() throws IOException {

    }
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

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

    public String getCurReference() {
        return curReference;
    }

    public void setCurReference(String curReference) {
        this.curReference = curReference;
    }
}
