/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dal.DetailSize;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dal.Sizes;
import dal.Details;
import dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author SaiBack
 */
public class DetailSizeJpaController implements Serializable {

    public DetailSizeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DetailSize detailSize) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sizes idSize = detailSize.getIdSize();
            if (idSize != null) {
                idSize = em.getReference(idSize.getClass(), idSize.getId());
                detailSize.setIdSize(idSize);
            }
            Details idDetal = detailSize.getIdDetal();
            if (idDetal != null) {
                idDetal = em.getReference(idDetal.getClass(), idDetal.getId());
                detailSize.setIdDetal(idDetal);
            }
            em.persist(detailSize);
            if (idSize != null) {
                idSize.getDetailSizeCollection().add(detailSize);
                idSize = em.merge(idSize);
            }
            if (idDetal != null) {
                idDetal.getDetailSizeCollection().add(detailSize);
                idDetal = em.merge(idDetal);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DetailSize detailSize) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetailSize persistentDetailSize = em.find(DetailSize.class, detailSize.getId());
            Sizes idSizeOld = persistentDetailSize.getIdSize();
            Sizes idSizeNew = detailSize.getIdSize();
            Details idDetalOld = persistentDetailSize.getIdDetal();
            Details idDetalNew = detailSize.getIdDetal();
            if (idSizeNew != null) {
                idSizeNew = em.getReference(idSizeNew.getClass(), idSizeNew.getId());
                detailSize.setIdSize(idSizeNew);
            }
            if (idDetalNew != null) {
                idDetalNew = em.getReference(idDetalNew.getClass(), idDetalNew.getId());
                detailSize.setIdDetal(idDetalNew);
            }
            detailSize = em.merge(detailSize);
            if (idSizeOld != null && !idSizeOld.equals(idSizeNew)) {
                idSizeOld.getDetailSizeCollection().remove(detailSize);
                idSizeOld = em.merge(idSizeOld);
            }
            if (idSizeNew != null && !idSizeNew.equals(idSizeOld)) {
                idSizeNew.getDetailSizeCollection().add(detailSize);
                idSizeNew = em.merge(idSizeNew);
            }
            if (idDetalOld != null && !idDetalOld.equals(idDetalNew)) {
                idDetalOld.getDetailSizeCollection().remove(detailSize);
                idDetalOld = em.merge(idDetalOld);
            }
            if (idDetalNew != null && !idDetalNew.equals(idDetalOld)) {
                idDetalNew.getDetailSizeCollection().add(detailSize);
                idDetalNew = em.merge(idDetalNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detailSize.getId();
                if (findDetailSize(id) == null) {
                    throw new NonexistentEntityException("The detailSize with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetailSize detailSize;
            try {
                detailSize = em.getReference(DetailSize.class, id);
                detailSize.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detailSize with id " + id + " no longer exists.", enfe);
            }
            Sizes idSize = detailSize.getIdSize();
            if (idSize != null) {
                idSize.getDetailSizeCollection().remove(detailSize);
                idSize = em.merge(idSize);
            }
            Details idDetal = detailSize.getIdDetal();
            if (idDetal != null) {
                idDetal.getDetailSizeCollection().remove(detailSize);
                idDetal = em.merge(idDetal);
            }
            em.remove(detailSize);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DetailSize> findDetailSizeEntities() {
        return findDetailSizeEntities(true, -1, -1);
    }

    public List<DetailSize> findDetailSizeEntities(int maxResults, int firstResult) {
        return findDetailSizeEntities(false, maxResults, firstResult);
    }

    private List<DetailSize> findDetailSizeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetailSize.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public DetailSize findDetailSize(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetailSize.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetailSizeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetailSize> rt = cq.from(DetailSize.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
