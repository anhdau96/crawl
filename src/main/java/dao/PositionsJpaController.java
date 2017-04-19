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
import dal.Positions;
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
public class PositionsJpaController implements Serializable {

    public PositionsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Positions positions) {
        if (positions.getDetailsCollection() == null) {
            positions.setDetailsCollection(new ArrayList<Details>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Details> attachedDetailsCollection = new ArrayList<Details>();
            for (Details detailsCollectionDetailsToAttach : positions.getDetailsCollection()) {
                detailsCollectionDetailsToAttach = em.getReference(detailsCollectionDetailsToAttach.getClass(), detailsCollectionDetailsToAttach.getId());
                attachedDetailsCollection.add(detailsCollectionDetailsToAttach);
            }
            positions.setDetailsCollection(attachedDetailsCollection);
            em.persist(positions);
            for (Details detailsCollectionDetails : positions.getDetailsCollection()) {
                Positions oldPositionIdOfDetailsCollectionDetails = detailsCollectionDetails.getPositionId();
                detailsCollectionDetails.setPositionId(positions);
                detailsCollectionDetails = em.merge(detailsCollectionDetails);
                if (oldPositionIdOfDetailsCollectionDetails != null) {
                    oldPositionIdOfDetailsCollectionDetails.getDetailsCollection().remove(detailsCollectionDetails);
                    oldPositionIdOfDetailsCollectionDetails = em.merge(oldPositionIdOfDetailsCollectionDetails);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Positions positions) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Positions persistentPositions = em.find(Positions.class, positions.getId());
            Collection<Details> detailsCollectionOld = persistentPositions.getDetailsCollection();
            Collection<Details> detailsCollectionNew = positions.getDetailsCollection();
            Collection<Details> attachedDetailsCollectionNew = new ArrayList<Details>();
            for (Details detailsCollectionNewDetailsToAttach : detailsCollectionNew) {
                detailsCollectionNewDetailsToAttach = em.getReference(detailsCollectionNewDetailsToAttach.getClass(), detailsCollectionNewDetailsToAttach.getId());
                attachedDetailsCollectionNew.add(detailsCollectionNewDetailsToAttach);
            }
            detailsCollectionNew = attachedDetailsCollectionNew;
            positions.setDetailsCollection(detailsCollectionNew);
            positions = em.merge(positions);
            for (Details detailsCollectionOldDetails : detailsCollectionOld) {
                if (!detailsCollectionNew.contains(detailsCollectionOldDetails)) {
                    detailsCollectionOldDetails.setPositionId(null);
                    detailsCollectionOldDetails = em.merge(detailsCollectionOldDetails);
                }
            }
            for (Details detailsCollectionNewDetails : detailsCollectionNew) {
                if (!detailsCollectionOld.contains(detailsCollectionNewDetails)) {
                    Positions oldPositionIdOfDetailsCollectionNewDetails = detailsCollectionNewDetails.getPositionId();
                    detailsCollectionNewDetails.setPositionId(positions);
                    detailsCollectionNewDetails = em.merge(detailsCollectionNewDetails);
                    if (oldPositionIdOfDetailsCollectionNewDetails != null && !oldPositionIdOfDetailsCollectionNewDetails.equals(positions)) {
                        oldPositionIdOfDetailsCollectionNewDetails.getDetailsCollection().remove(detailsCollectionNewDetails);
                        oldPositionIdOfDetailsCollectionNewDetails = em.merge(oldPositionIdOfDetailsCollectionNewDetails);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = positions.getId();
                if (findPositions(id) == null) {
                    throw new NonexistentEntityException("The positions with id " + id + " no longer exists.");
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
            Positions positions;
            try {
                positions = em.getReference(Positions.class, id);
                positions.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The positions with id " + id + " no longer exists.", enfe);
            }
            Collection<Details> detailsCollection = positions.getDetailsCollection();
            for (Details detailsCollectionDetails : detailsCollection) {
                detailsCollectionDetails.setPositionId(null);
                detailsCollectionDetails = em.merge(detailsCollectionDetails);
            }
            em.remove(positions);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Positions> findPositionsEntities() {
        return findPositionsEntities(true, -1, -1);
    }

    public List<Positions> findPositionsEntities(int maxResults, int firstResult) {
        return findPositionsEntities(false, maxResults, firstResult);
    }

    private List<Positions> findPositionsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Positions.class));
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

    public Positions findPositions(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Positions.class, id);
        } finally {
            em.close();
        }
    }

    public int getPositionsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Positions> rt = cq.from(Positions.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public Positions findByName(String name) {
        EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("Positions.findByPosition");
        query.setParameter("position", name);
        List<Positions> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return resultList.get(0);
        }
    }

    public Positions firstOrCreate(String name) {
        Positions findByName = findByName(name);
        if (findByName == null) {
            Positions p = new Positions(name);
            create(p);
            return p;
        } else {
            return findByName;
        }
    }
}
