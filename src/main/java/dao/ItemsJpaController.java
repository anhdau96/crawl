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
import dal.Details;
import dal.Items;
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
public class ItemsJpaController implements Serializable {

    public ItemsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Items items) {
        if (items.getDetailsCollection() == null) {
            items.setDetailsCollection(new ArrayList<Details>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Details> attachedDetailsCollection = new ArrayList<Details>();
            for (Details detailsCollectionDetailsToAttach : items.getDetailsCollection()) {
                detailsCollectionDetailsToAttach = em.getReference(detailsCollectionDetailsToAttach.getClass(), detailsCollectionDetailsToAttach.getId());
                attachedDetailsCollection.add(detailsCollectionDetailsToAttach);
            }
            items.setDetailsCollection(attachedDetailsCollection);
            em.persist(items);
            for (Details detailsCollectionDetails : items.getDetailsCollection()) {
                Items oldItemIdOfDetailsCollectionDetails = detailsCollectionDetails.getItemId();
                detailsCollectionDetails.setItemId(items);
                detailsCollectionDetails = em.merge(detailsCollectionDetails);
                if (oldItemIdOfDetailsCollectionDetails != null) {
                    oldItemIdOfDetailsCollectionDetails.getDetailsCollection().remove(detailsCollectionDetails);
                    oldItemIdOfDetailsCollectionDetails = em.merge(oldItemIdOfDetailsCollectionDetails);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Items items) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Items persistentItems = em.find(Items.class, items.getId());
            Collection<Details> detailsCollectionOld = persistentItems.getDetailsCollection();
            Collection<Details> detailsCollectionNew = items.getDetailsCollection();
            Collection<Details> attachedDetailsCollectionNew = new ArrayList<Details>();
            for (Details detailsCollectionNewDetailsToAttach : detailsCollectionNew) {
                detailsCollectionNewDetailsToAttach = em.getReference(detailsCollectionNewDetailsToAttach.getClass(), detailsCollectionNewDetailsToAttach.getId());
                attachedDetailsCollectionNew.add(detailsCollectionNewDetailsToAttach);
            }
            detailsCollectionNew = attachedDetailsCollectionNew;
            items.setDetailsCollection(detailsCollectionNew);
            items = em.merge(items);
            for (Details detailsCollectionOldDetails : detailsCollectionOld) {
                if (!detailsCollectionNew.contains(detailsCollectionOldDetails)) {
                    detailsCollectionOldDetails.setItemId(null);
                    detailsCollectionOldDetails = em.merge(detailsCollectionOldDetails);
                }
            }
            for (Details detailsCollectionNewDetails : detailsCollectionNew) {
                if (!detailsCollectionOld.contains(detailsCollectionNewDetails)) {
                    Items oldItemIdOfDetailsCollectionNewDetails = detailsCollectionNewDetails.getItemId();
                    detailsCollectionNewDetails.setItemId(items);
                    detailsCollectionNewDetails = em.merge(detailsCollectionNewDetails);
                    if (oldItemIdOfDetailsCollectionNewDetails != null && !oldItemIdOfDetailsCollectionNewDetails.equals(items)) {
                        oldItemIdOfDetailsCollectionNewDetails.getDetailsCollection().remove(detailsCollectionNewDetails);
                        oldItemIdOfDetailsCollectionNewDetails = em.merge(oldItemIdOfDetailsCollectionNewDetails);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = items.getId();
                if (findItems(id) == null) {
                    throw new NonexistentEntityException("The items with id " + id + " no longer exists.");
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
            Items items;
            try {
                items = em.getReference(Items.class, id);
                items.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The items with id " + id + " no longer exists.", enfe);
            }
            Collection<Details> detailsCollection = items.getDetailsCollection();
            for (Details detailsCollectionDetails : detailsCollection) {
                detailsCollectionDetails.setItemId(null);
                detailsCollectionDetails = em.merge(detailsCollectionDetails);
            }
            em.remove(items);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Items> findItemsEntities() {
        return findItemsEntities(true, -1, -1);
    }

    public List<Items> findItemsEntities(int maxResults, int firstResult) {
        return findItemsEntities(false, maxResults, firstResult);
    }

    private List<Items> findItemsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Items.class));
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

    public Items findItems(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Items.class, id);
        } finally {
            em.close();
        }
    }

    public int getItemsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Items> rt = cq.from(Items.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
