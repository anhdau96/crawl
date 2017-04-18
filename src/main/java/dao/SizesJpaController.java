/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dal.DetailSize;
import dal.Sizes;
import dao.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author SaiBack
 */
public class SizesJpaController implements Serializable {

    public SizesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sizes sizes) {
        if (sizes.getDetailSizeCollection() == null) {
            sizes.setDetailSizeCollection(new ArrayList<DetailSize>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<DetailSize> attachedDetailSizeCollection = new ArrayList<DetailSize>();
            for (DetailSize detailSizeCollectionDetailSizeToAttach : sizes.getDetailSizeCollection()) {
                detailSizeCollectionDetailSizeToAttach = em.getReference(detailSizeCollectionDetailSizeToAttach.getClass(), detailSizeCollectionDetailSizeToAttach.getId());
                attachedDetailSizeCollection.add(detailSizeCollectionDetailSizeToAttach);
            }
            sizes.setDetailSizeCollection(attachedDetailSizeCollection);
            em.persist(sizes);
            for (DetailSize detailSizeCollectionDetailSize : sizes.getDetailSizeCollection()) {
                Sizes oldIdSizeOfDetailSizeCollectionDetailSize = detailSizeCollectionDetailSize.getIdSize();
                detailSizeCollectionDetailSize.setIdSize(sizes);
                detailSizeCollectionDetailSize = em.merge(detailSizeCollectionDetailSize);
                if (oldIdSizeOfDetailSizeCollectionDetailSize != null) {
                    oldIdSizeOfDetailSizeCollectionDetailSize.getDetailSizeCollection().remove(detailSizeCollectionDetailSize);
                    oldIdSizeOfDetailSizeCollectionDetailSize = em.merge(oldIdSizeOfDetailSizeCollectionDetailSize);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Sizes sizes) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sizes persistentSizes = em.find(Sizes.class, sizes.getId());
            Collection<DetailSize> detailSizeCollectionOld = persistentSizes.getDetailSizeCollection();
            Collection<DetailSize> detailSizeCollectionNew = sizes.getDetailSizeCollection();
            Collection<DetailSize> attachedDetailSizeCollectionNew = new ArrayList<DetailSize>();
            for (DetailSize detailSizeCollectionNewDetailSizeToAttach : detailSizeCollectionNew) {
                detailSizeCollectionNewDetailSizeToAttach = em.getReference(detailSizeCollectionNewDetailSizeToAttach.getClass(), detailSizeCollectionNewDetailSizeToAttach.getId());
                attachedDetailSizeCollectionNew.add(detailSizeCollectionNewDetailSizeToAttach);
            }
            detailSizeCollectionNew = attachedDetailSizeCollectionNew;
            sizes.setDetailSizeCollection(detailSizeCollectionNew);
            sizes = em.merge(sizes);
            for (DetailSize detailSizeCollectionOldDetailSize : detailSizeCollectionOld) {
                if (!detailSizeCollectionNew.contains(detailSizeCollectionOldDetailSize)) {
                    detailSizeCollectionOldDetailSize.setIdSize(null);
                    detailSizeCollectionOldDetailSize = em.merge(detailSizeCollectionOldDetailSize);
                }
            }
            for (DetailSize detailSizeCollectionNewDetailSize : detailSizeCollectionNew) {
                if (!detailSizeCollectionOld.contains(detailSizeCollectionNewDetailSize)) {
                    Sizes oldIdSizeOfDetailSizeCollectionNewDetailSize = detailSizeCollectionNewDetailSize.getIdSize();
                    detailSizeCollectionNewDetailSize.setIdSize(sizes);
                    detailSizeCollectionNewDetailSize = em.merge(detailSizeCollectionNewDetailSize);
                    if (oldIdSizeOfDetailSizeCollectionNewDetailSize != null && !oldIdSizeOfDetailSizeCollectionNewDetailSize.equals(sizes)) {
                        oldIdSizeOfDetailSizeCollectionNewDetailSize.getDetailSizeCollection().remove(detailSizeCollectionNewDetailSize);
                        oldIdSizeOfDetailSizeCollectionNewDetailSize = em.merge(oldIdSizeOfDetailSizeCollectionNewDetailSize);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sizes.getId();
                if (findSizes(id) == null) {
                    throw new NonexistentEntityException("The sizes with id " + id + " no longer exists.");
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
            Sizes sizes;
            try {
                sizes = em.getReference(Sizes.class, id);
                sizes.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sizes with id " + id + " no longer exists.", enfe);
            }
            Collection<DetailSize> detailSizeCollection = sizes.getDetailSizeCollection();
            for (DetailSize detailSizeCollectionDetailSize : detailSizeCollection) {
                detailSizeCollectionDetailSize.setIdSize(null);
                detailSizeCollectionDetailSize = em.merge(detailSizeCollectionDetailSize);
            }
            em.remove(sizes);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Sizes> findSizesEntities() {
        return findSizesEntities(true, -1, -1);
    }

    public List<Sizes> findSizesEntities(int maxResults, int firstResult) {
        return findSizesEntities(false, maxResults, firstResult);
    }

    private List<Sizes> findSizesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sizes.class));
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

    public Sizes findSizes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sizes.class, id);
        } finally {
            em.close();
        }
    }

    public int getSizesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sizes> rt = cq.from(Sizes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Sizes findByName(String name) {
        EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("Sizes.findBySizeName");
        query.setParameter("sizeName", name);
        List<Sizes> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return resultList.get(0);
        }
    }

    public Sizes firstOrCreate(String name) {
        Sizes findByName = findByName(name);
        if (findByName == null) {
            Sizes s = new Sizes(name);
            create(s);
            return s;
        } else {
            return findByName;
        }
    }
}
