package com.tmz.db.model;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by stukolov_m on 29.05.2015.
 */
@Entity
@Indexed
@Table(name = "reference")
public class Reference implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "recid", nullable = false)
    private Integer recid;

    @Column(name = "reference", nullable = false)
    @Field(index = org.hibernate.search.annotations.Index.YES, store = Store.NO, analyze = Analyze.YES)
    private String reference;

    @Column(name = "competitor", nullable = false)
    @Field(index = org.hibernate.search.annotations.Index.YES, store = Store.NO, analyze = Analyze.YES)
    private String competitor;

    public Reference() {
    }

    public Reference(String reference, String competitor) {
        this.reference = reference;
        this.competitor = competitor;
    }

    public Integer getRecid() {
        return recid;
    }

    public void setRecid(Integer recid) {
        this.recid = recid;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCompetitor() {
        return competitor;
    }

    public void setCompetitor(String competitor) {
        this.competitor = competitor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reference reference1 = (Reference) o;

        if (recid != null ? !recid.equals(reference1.recid) : reference1.recid != null) return false;
        if (reference != null ? !reference.equals(reference1.reference) : reference1.reference != null) return false;
        return !(competitor != null ? !competitor.equals(reference1.competitor) : reference1.competitor != null);

    }

    @Override
    public int hashCode() {
        int result = recid != null ? recid.hashCode() : 0;
        result = 31 * result + (reference != null ? reference.hashCode() : 0);
        result = 31 * result + (competitor != null ? competitor.hashCode() : 0);
        return result;
    }
}
