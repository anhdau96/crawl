/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SaiBack
 */
@Entity
@Table(name = "detail_size")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DetailSize.findAll", query = "SELECT d FROM DetailSize d")
    , @NamedQuery(name = "DetailSize.findById", query = "SELECT d FROM DetailSize d WHERE d.id = :id")})
public class DetailSize implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "idSize", referencedColumnName = "id")
    @ManyToOne
    private Sizes idSize;
    @JoinColumn(name = "idDetal", referencedColumnName = "id")
    @ManyToOne
    private Details idDetal;

    public DetailSize() {
    }

    public DetailSize(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sizes getIdSize() {
        return idSize;
    }

    public void setIdSize(Sizes idSize) {
        this.idSize = idSize;
    }

    public Details getIdDetal() {
        return idDetal;
    }

    public void setIdDetal(Details idDetal) {
        this.idDetal = idDetal;
    }

    public DetailSize(Sizes idSize, Details idDetal) {
        this.idSize = idSize;
        this.idDetal = idDetal;
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
        if (!(object instanceof DetailSize)) {
            return false;
        }
        DetailSize other = (DetailSize) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dal.DetailSize[ id=" + id + " ]";
    }
    
}
