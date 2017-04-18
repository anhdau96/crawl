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
@Table(name = "items")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Items.findAll", query = "SELECT i FROM Items i")
    , @NamedQuery(name = "Items.findById", query = "SELECT i FROM Items i WHERE i.id = :id")
    , @NamedQuery(name = "Items.findByCate", query = "SELECT i FROM Items i WHERE i.cate = :cate")
    , @NamedQuery(name = "Items.findBySubcate", query = "SELECT i FROM Items i WHERE i.subcate = :subcate")})
public class Items implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Lob
    @Column(name = "title")
    private String title;
    @Lob
    @Column(name = "link")
    private String link;
    @Column(name = "cate")
    private String cate;
    @Column(name = "subcate")
    private String subcate;
    @Lob
    @Column(name = "description")
    private String description;
    @Lob
    @Column(name = "keyword")
    private String keyword;
    @OneToMany(mappedBy = "itemId")
    private Collection<Details> detailsCollection;

    public Items() {
    }

    public Items(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getSubcate() {
        return subcate;
    }

    public void setSubcate(String subcate) {
        this.subcate = subcate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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
        if (!(object instanceof Items)) {
            return false;
        }
        Items other = (Items) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dal.Items[ id=" + id + " ]";
    }
    
}
