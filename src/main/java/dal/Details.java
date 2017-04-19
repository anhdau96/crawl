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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "details")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Details.findAll", query = "SELECT d FROM Details d")
    , @NamedQuery(name = "Details.findById", query = "SELECT d FROM Details d WHERE d.id = :id")
    , @NamedQuery(name = "Details.findByPrice", query = "SELECT d FROM Details d WHERE d.price = :price")
    , @NamedQuery(name = "Details.findByImg", query = "SELECT d FROM Details d WHERE d.img = :img")
    , @NamedQuery(name = "Details.findBySku", query = "SELECT d FROM Details d WHERE d.sku = :sku")})
public class Details implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Lob
    @Column(name = "link")
    private String link;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private Double price;
    @Column(name = "img")
    private String img;
    @Column(name = "sku")
    private String sku;
    @OneToMany(mappedBy = "idDetail")
    private Collection<DetailSize> detailSizeCollection;
    @JoinColumn(name = "colorId", referencedColumnName = "id")
    @ManyToOne
    private Colors colorId;
    @JoinColumn(name = "itemId", referencedColumnName = "id")
    @ManyToOne
    private Items itemId;
    @JoinColumn(name = "positionId", referencedColumnName = "id")
    @ManyToOne
    private Positions positionId;
    @JoinColumn(name = "styleId", referencedColumnName = "id")
    @ManyToOne
    private Styles styleId;

    public Details() {
    }

    public Details(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @XmlTransient
    public Collection<DetailSize> getDetailSizeCollection() {
        return detailSizeCollection;
    }

    public void setDetailSizeCollection(Collection<DetailSize> detailSizeCollection) {
        this.detailSizeCollection = detailSizeCollection;
    }

    public Colors getColorId() {
        return colorId;
    }

    public void setColorId(Colors colorId) {
        this.colorId = colorId;
    }

    public Items getItemId() {
        return itemId;
    }

    public void setItemId(Items itemId) {
        this.itemId = itemId;
    }

    public Positions getPositionId() {
        return positionId;
    }

    public void setPositionId(Positions positionId) {
        this.positionId = positionId;
    }

    public Styles getStyleId() {
        return styleId;
    }

    public void setStyleId(Styles styleId) {
        this.styleId = styleId;
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
        if (!(object instanceof Details)) {
            return false;
        }
        Details other = (Details) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dal.Details[ id=" + id + " ]";
    }
    
}
