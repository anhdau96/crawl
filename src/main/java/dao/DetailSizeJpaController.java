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
            Details idDetail = detailSize.getIdDetail();
            if (idDetail != null) {
                idDetail = em.getReference(idDetail.getClass(), idDetail.getId());
                detailSize.setIdDetail(idDetail);
            }
            em.persist(detailSize);
            if (idSize != null) {
                idSize.getDetailSizeCollection().add(detailSize);
                idSize = em.merge(idSize);
            }
            if (idDetail != null) {
                idDetail.getDetailSizeCollection().add(detailSize);
                idDetail = em.merge(idDetail);
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
            Details idDetailOld = persistentDetailSize.getIdDetail();
            Details idDetailNew = detailSize.getIdDetail();
            if (idSizeNew != null) {
                idSizeNew = em.getReference(idSizeNew.getClass(), idSizeNew.getId());
                detailSize.setIdSize(idSizeNew);
            }
            if (idDetailNew != null) {
                idDetailNew = em.getReference(idDetailNew.getClass(), idDetailNew.getId());
                detailSize.setIdDetail(idDetailNew);
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
            if (idDetailOld != null && !idDetailOld.equals(idDetailNew)) {
                idDetailOld.getDetailSizeCollection().remove(detailSize);
                idDetailOld = em.merge(idDetailOld);
            }
            if (idDetailNew != null && !idDetailNew.equals(idDetailOld)) {
                idDetailNew.getDetailSizeCollection().add(detailSize);
                idDetailNew = em.merge(idDetailNew);
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
            Details idDetail = detailSize.getIdDetail();
            if (idDetail != null) {
                idDetail.getDetailSizeCollection().remove(detailSize);
                idDetail = em.merge(idDetail);
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
