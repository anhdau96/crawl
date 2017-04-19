/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dal.Visited;
import dao.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author SaiBack
 */
public class VisitedJpaController implements Serializable {

    public VisitedJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Visited visited) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(visited);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Visited visited) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            visited = em.merge(visited);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = visited.getId();
                if (findVisited(id) == null) {
                    throw new NonexistentEntityException("The visited with id " + id + " no longer exists.");
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
            Visited visited;
            try {
                visited = em.getReference(Visited.class, id);
                visited.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The visited with id " + id + " no longer exists.", enfe);
            }
            em.remove(visited);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Visited> findVisitedEntities() {
        return findVisitedEntities(true, -1, -1);
    }

    public List<Visited> findVisitedEntities(int maxResults, int firstResult) {
        return findVisitedEntities(false, maxResults, firstResult);
    }

    private List<Visited> findVisitedEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Visited.class));
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

    public Visited findVisited(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Visited.class, id);
        } finally {
            em.close();
        }
    }

    public int getVisitedCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Visited> rt = cq.from(Visited.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Visited findLink(String link) {
        EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("Visited.findByLink");
        query.setParameter("link", link);
        List<Visited> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return resultList.get(0);
        }
    }
}
