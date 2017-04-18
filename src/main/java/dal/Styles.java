/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author SaiBack
 */
@Entity
@Table(name = "styles")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Styles.findAll", query = "SELECT s FROM Styles s")
    , @NamedQuery(name = "Styles.findById", query = "SELECT s FROM Styles s WHERE s.id = :id")
    , @NamedQuery(name = "Styles.findByStyleName", query = "SELECT s FROM Styles s WHERE s.styleName = :styleName")})
public class Styles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "styleName")
    private String styleName;
    @OneToMany(mappedBy = "styleId")
    private Collection<Details> detailsCollection;

    public Styles() {
    }

    public Styles(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public Styles(String styleName) {
        this.styleName = styleName;
    }
    
    
    @XmlTransient
    public Collection<Details> getDetailsCollection() {
        return detailsCollection;
    }

    public void setDetailsCollection(Collection<Details> detailsCollection) {
        this.detailsCollection = detailsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Styles)) {
            return false;
        }
        Styles other = (Styles) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dal.Styles[ id=" + id + " ]";
    }
    
}
