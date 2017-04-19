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
import javax.persistence.Lob;
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
@Table(name = "colors")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Colors.findAll", query = "SELECT c FROM Colors c")
    , @NamedQuery(name = "Colors.findById", query = "SELECT c FROM Colors c WHERE c.id = :id")
    , @NamedQuery(name = "Colors.findByName", query = "SELECT c FROM Colors c WHERE c.name = :name")})
public class Colors implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "imglink")
    private String imglink;
    @OneToMany(mappedBy = "colorId")
    private Collection<Details> detailsCollection;

    public Colors() {
    }

    public Colors(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImglink() {
        return imglink;
    }

    public void setImglink(String imglink) {
        this.imglink = imglink;
    }

    public Colors(String name, String imglink) {
        this.name = name;
        this.imglink = imglink;
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
        if (!(object instanceof Colors)) {
            return false;
        }
        Colors other = (Colors) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dal.Colors[ id=" + id + " ]";
    }
    
}
