/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dal.Colors;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dal.Details;
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
public class ColorsJpaController implements Serializable {

    public ColorsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Colors colors) {
        if (colors.getDetailsCollection() == null) {
            colors.setDetailsCollection(new ArrayList<Details>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Details> attachedDetailsCollection = new ArrayList<Details>();
            for (Details detailsCollectionDetailsToAttach : colors.getDetailsCollection()) {
                detailsCollectionDetailsToAttach = em.getReference(detailsCollectionDetailsToAttach.getClass(), detailsCollectionDetailsToAttach.getId());
                attachedDetailsCollection.add(detailsCollectionDetailsToAttach);
            }
            colors.setDetailsCollection(attachedDetailsCollection);
            em.persist(colors);
            for (Details detailsCollectionDetails : colors.getDetailsCollection()) {
                Colors oldColorIdOfDetailsCollectionDetails = detailsCollectionDetails.getColorId();
                detailsCollectionDetails.setColorId(colors);
                detailsCollectionDetails = em.merge(detailsCollectionDetails);
                if (oldColorIdOfDetailsCollectionDetails != null) {
                    oldColorIdOfDetailsCollectionDetails.getDetailsCollection().remove(detailsCollectionDetails);
                    oldColorIdOfDetailsCollectionDetails = em.merge(oldColorIdOfDetailsCollectionDetails);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Colors colors) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Colors persistentColors = em.find(Colors.class, colors.getId());
            Collection<Details> detailsCollectionOld = persistentColors.getDetailsCollection();
            Collection<Details> detailsCollectionNew = colors.getDetailsCollection();
            Collection<Details> attachedDetailsCollectionNew = new ArrayList<Details>();
            for (Details detailsCollectionNewDetailsToAttach : detailsCollectionNew) {
                detailsCollectionNewDetailsToAttach = em.getReference(detailsCollectionNewDetailsToAttach.getClass(), detailsCollectionNewDetailsToAttach.getId());
                attachedDetailsCollectionNew.add(detailsCollectionNewDetailsToAttach);
            }
            detailsCollectionNew = attachedDetailsCollectionNew;
            colors.setDetailsCollection(detailsCollectionNew);
            colors = em.merge(colors);
            for (Details detailsCollectionOldDetails : detailsCollectionOld) {
                if (!detailsCollectionNew.contains(detailsCollectionOldDetails)) {
                    detailsCollectionOldDetails.setColorId(null);
                    detailsCollectionOldDetails = em.merge(detailsCollectionOldDetails);
                }
            }
            for (Details detailsCollectionNewDetails : detailsCollectionNew) {
                if (!detailsCollectionOld.contains(detailsCollectionNewDetails)) {
                    Colors oldColorIdOfDetailsCollectionNewDetails = detailsCollectionNewDetails.getColorId();
                    detailsCollectionNewDetails.setColorId(colors);
                    detailsCollectionNewDetails = em.merge(detailsCollectionNewDetails);
                    if (oldColorIdOfDetailsCollectionNewDetails != null && !oldColorIdOfDetailsCollectionNewDetails.equals(colors)) {
                        oldColorIdOfDetailsCollectionNewDetails.getDetailsCollection().remove(detailsCollectionNewDetails);
                        oldColorIdOfDetailsCollectionNewDetails = em.merge(oldColorIdOfDetailsCollectionNewDetails);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = colors.getId();
                if (findColors(id) == null) {
                    throw new NonexistentEntityException("The colors with id " + id + " no longer exists.");
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
            Colors colors;
            try {
                colors = em.getReference(Colors.class, id);
                colors.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The colors with id " + id + " no longer exists.", enfe);
            }
            Collection<Details> detailsCollection = colors.getDetailsCollection();
            for (Details detailsCollectionDetails : detailsCollection) {
                detailsCollectionDetails.setColorId(null);
                detailsCollectionDetails = em.merge(detailsCollectionDetails);
            }
            em.remove(colors);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Colors> findColorsEntities() {
        return findColorsEntities(true, -1, -1);
    }

    public List<Colors> findColorsEntities(int maxResults, int firstResult) {
        return findColorsEntities(false, maxResults, firstResult);
    }

    private List<Colors> findColorsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Colors.class));
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

    public Colors findColors(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Colors.class, id);
        } finally {
            em.close();
        }
    }

    public int getColorsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Colors> rt = cq.from(Colors.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Colors findByName(String name) {
        EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("Colors.findByName");
        query.setParameter("name", name);
        List<Colors> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return resultList.get(0);
        }
    }

    public Colors firstOrCreate(String name, String link) {
        Colors findByName = findByName(name);
        if (findByName == null) {
            Colors c = new Colors(name, link);
            create(c);
            return c;
        } else {
            return findByName;
        }
    }
}
