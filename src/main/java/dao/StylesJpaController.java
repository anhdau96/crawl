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
import dal.Styles;
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
public class StylesJpaController implements Serializable {

    public StylesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Styles styles) {
        if (styles.getDetailsCollection() == null) {
            styles.setDetailsCollection(new ArrayList<Details>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Details> attachedDetailsCollection = new ArrayList<Details>();
            for (Details detailsCollectionDetailsToAttach : styles.getDetailsCollection()) {
                detailsCollectionDetailsToAttach = em.getReference(detailsCollectionDetailsToAttach.getClass(), detailsCollectionDetailsToAttach.getId());
                attachedDetailsCollection.add(detailsCollectionDetailsToAttach);
            }
            styles.setDetailsCollection(attachedDetailsCollection);
            em.persist(styles);
            for (Details detailsCollectionDetails : styles.getDetailsCollection()) {
                Styles oldStyleIdOfDetailsCollectionDetails = detailsCollectionDetails.getStyleId();
                detailsCollectionDetails.setStyleId(styles);
                detailsCollectionDetails = em.merge(detailsCollectionDetails);
                if (oldStyleIdOfDetailsCollectionDetails != null) {
                    oldStyleIdOfDetailsCollectionDetails.getDetailsCollection().remove(detailsCollectionDetails);
                    oldStyleIdOfDetailsCollectionDetails = em.merge(oldStyleIdOfDetailsCollectionDetails);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Styles styles) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Styles persistentStyles = em.find(Styles.class, styles.getId());
            Collection<Details> detailsCollectionOld = persistentStyles.getDetailsCollection();
            Collection<Details> detailsCollectionNew = styles.getDetailsCollection();
            Collection<Details> attachedDetailsCollectionNew = new ArrayList<Details>();
            for (Details detailsCollectionNewDetailsToAttach : detailsCollectionNew) {
                detailsCollectionNewDetailsToAttach = em.getReference(detailsCollectionNewDetailsToAttach.getClass(), detailsCollectionNewDetailsToAttach.getId());
                attachedDetailsCollectionNew.add(detailsCollectionNewDetailsToAttach);
            }
            detailsCollectionNew = attachedDetailsCollectionNew;
            styles.setDetailsCollection(detailsCollectionNew);
            styles = em.merge(styles);
            for (Details detailsCollectionOldDetails : detailsCollectionOld) {
                if (!detailsCollectionNew.contains(detailsCollectionOldDetails)) {
                    detailsCollectionOldDetails.setStyleId(null);
                    detailsCollectionOldDetails = em.merge(detailsCollectionOldDetails);
                }
            }
            for (Details detailsCollectionNewDetails : detailsCollectionNew) {
                if (!detailsCollectionOld.contains(detailsCollectionNewDetails)) {
                    Styles oldStyleIdOfDetailsCollectionNewDetails = detailsCollectionNewDetails.getStyleId();
                    detailsCollectionNewDetails.setStyleId(styles);
                    detailsCollectionNewDetails = em.merge(detailsCollectionNewDetails);
                    if (oldStyleIdOfDetailsCollectionNewDetails != null && !oldStyleIdOfDetailsCollectionNewDetails.equals(styles)) {
                        oldStyleIdOfDetailsCollectionNewDetails.getDetailsCollection().remove(detailsCollectionNewDetails);
                        oldStyleIdOfDetailsCollectionNewDetails = em.merge(oldStyleIdOfDetailsCollectionNewDetails);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = styles.getId();
                if (findStyles(id) == null) {
                    throw new NonexistentEntityException("The styles with id " + id + " no longer exists.");
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
            Styles styles;
            try {
                styles = em.getReference(Styles.class, id);
                styles.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The styles with id " + id + " no longer exists.", enfe);
            }
            Collection<Details> detailsCollection = styles.getDetailsCollection();
            for (Details detailsCollectionDetails : detailsCollection) {
                detailsCollectionDetails.setStyleId(null);
                detailsCollectionDetails = em.merge(detailsCollectionDetails);
            }
            em.remove(styles);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Styles> findStylesEntities() {
        return findStylesEntities(true, -1, -1);
    }

    public List<Styles> findStylesEntities(int maxResults, int firstResult) {
        return findStylesEntities(false, maxResults, firstResult);
    }

    private List<Styles> findStylesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Styles.class));
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

    public Styles findStyles(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Styles.class, id);
        } finally {
            em.close();
        }
    }

    public int getStylesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Styles> rt = cq.from(Styles.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Styles findByName(String name) {
        EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("Styles.findByStyleName");
        query.setParameter("styleName", name);
        List<Styles> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return resultList.get(0);
        }
    }

    public Styles firstOrCreate(String name) {
        Styles findByName = findByName(name);
        if (findByName == null) {
            Styles s = new Styles(name);
            create(s);
            return s;
        } else {
            return findByName;
        }
    }

}
