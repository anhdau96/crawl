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
import dal.Colors;
import dal.Items;
import dal.Positions;
import dal.Styles;
import dal.DetailSize;
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
public class DetailsJpaController implements Serializable {

    public DetailsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Details details) {
        if (details.getDetailSizeCollection() == null) {
            details.setDetailSizeCollection(new ArrayList<DetailSize>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Colors colorId = details.getColorId();
            if (colorId != null) {
                colorId = em.getReference(colorId.getClass(), colorId.getId());
                details.setColorId(colorId);
            }
            Items itemId = details.getItemId();
            if (itemId != null) {
                itemId = em.getReference(itemId.getClass(), itemId.getId());
                details.setItemId(itemId);
            }
            Positions positionId = details.getPositionId();
            if (positionId != null) {
                positionId = em.getReference(positionId.getClass(), positionId.getId());
                details.setPositionId(positionId);
            }
            Styles styleId = details.getStyleId();
            if (styleId != null) {
                styleId = em.getReference(styleId.getClass(), styleId.getId());
                details.setStyleId(styleId);
            }
            Collection<DetailSize> attachedDetailSizeCollection = new ArrayList<DetailSize>();
            for (DetailSize detailSizeCollectionDetailSizeToAttach : details.getDetailSizeCollection()) {
                detailSizeCollectionDetailSizeToAttach = em.getReference(detailSizeCollectionDetailSizeToAttach.getClass(), detailSizeCollectionDetailSizeToAttach.getId());
                attachedDetailSizeCollection.add(detailSizeCollectionDetailSizeToAttach);
            }
            details.setDetailSizeCollection(attachedDetailSizeCollection);
            em.persist(details);
            if (colorId != null) {
                colorId.getDetailsCollection().add(details);
                colorId = em.merge(colorId);
            }
            if (itemId != null) {
                itemId.getDetailsCollection().add(details);
                itemId = em.merge(itemId);
            }
            if (positionId != null) {
                positionId.getDetailsCollection().add(details);
                positionId = em.merge(positionId);
            }
            if (styleId != null) {
                styleId.getDetailsCollection().add(details);
                styleId = em.merge(styleId);
            }
            for (DetailSize detailSizeCollectionDetailSize : details.getDetailSizeCollection()) {
                Details oldIdDetalOfDetailSizeCollectionDetailSize = detailSizeCollectionDetailSize.getIdDetal();
                detailSizeCollectionDetailSize.setIdDetal(details);
                detailSizeCollectionDetailSize = em.merge(detailSizeCollectionDetailSize);
                if (oldIdDetalOfDetailSizeCollectionDetailSize != null) {
                    oldIdDetalOfDetailSizeCollectionDetailSize.getDetailSizeCollection().remove(detailSizeCollectionDetailSize);
                    oldIdDetalOfDetailSizeCollectionDetailSize = em.merge(oldIdDetalOfDetailSizeCollectionDetailSize);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Details details) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Details persistentDetails = em.find(Details.class, details.getId());
            Colors colorIdOld = persistentDetails.getColorId();
            Colors colorIdNew = details.getColorId();
            Items itemIdOld = persistentDetails.getItemId();
            Items itemIdNew = details.getItemId();
            Positions positionIdOld = persistentDetails.getPositionId();
            Positions positionIdNew = details.getPositionId();
            Styles styleIdOld = persistentDetails.getStyleId();
            Styles styleIdNew = details.getStyleId();
            Collection<DetailSize> detailSizeCollectionOld = persistentDetails.getDetailSizeCollection();
            Collection<DetailSize> detailSizeCollectionNew = details.getDetailSizeCollection();
            if (colorIdNew != null) {
                colorIdNew = em.getReference(colorIdNew.getClass(), colorIdNew.getId());
                details.setColorId(colorIdNew);
            }
            if (itemIdNew != null) {
                itemIdNew = em.getReference(itemIdNew.getClass(), itemIdNew.getId());
                details.setItemId(itemIdNew);
            }
            if (positionIdNew != null) {
                positionIdNew = em.getReference(positionIdNew.getClass(), positionIdNew.getId());
                details.setPositionId(positionIdNew);
            }
            if (styleIdNew != null) {
                styleIdNew = em.getReference(styleIdNew.getClass(), styleIdNew.getId());
                details.setStyleId(styleIdNew);
            }
            Collection<DetailSize> attachedDetailSizeCollectionNew = new ArrayList<DetailSize>();
            for (DetailSize detailSizeCollectionNewDetailSizeToAttach : detailSizeCollectionNew) {
                detailSizeCollectionNewDetailSizeToAttach = em.getReference(detailSizeCollectionNewDetailSizeToAttach.getClass(), detailSizeCollectionNewDetailSizeToAttach.getId());
                attachedDetailSizeCollectionNew.add(detailSizeCollectionNewDetailSizeToAttach);
            }
            detailSizeCollectionNew = attachedDetailSizeCollectionNew;
            details.setDetailSizeCollection(detailSizeCollectionNew);
            details = em.merge(details);
            if (colorIdOld != null && !colorIdOld.equals(colorIdNew)) {
                colorIdOld.getDetailsCollection().remove(details);
                colorIdOld = em.merge(colorIdOld);
            }
            if (colorIdNew != null && !colorIdNew.equals(colorIdOld)) {
                colorIdNew.getDetailsCollection().add(details);
                colorIdNew = em.merge(colorIdNew);
            }
            if (itemIdOld != null && !itemIdOld.equals(itemIdNew)) {
                itemIdOld.getDetailsCollection().remove(details);
                itemIdOld = em.merge(itemIdOld);
            }
            if (itemIdNew != null && !itemIdNew.equals(itemIdOld)) {
                itemIdNew.getDetailsCollection().add(details);
                itemIdNew = em.merge(itemIdNew);
            }
            if (positionIdOld != null && !positionIdOld.equals(positionIdNew)) {
                positionIdOld.getDetailsCollection().remove(details);
                positionIdOld = em.merge(positionIdOld);
            }
            if (positionIdNew != null && !positionIdNew.equals(positionIdOld)) {
                positionIdNew.getDetailsCollection().add(details);
                positionIdNew = em.merge(positionIdNew);
            }
            if (styleIdOld != null && !styleIdOld.equals(styleIdNew)) {
                styleIdOld.getDetailsCollection().remove(details);
                styleIdOld = em.merge(styleIdOld);
            }
            if (styleIdNew != null && !styleIdNew.equals(styleIdOld)) {
                styleIdNew.getDetailsCollection().add(details);
                styleIdNew = em.merge(styleIdNew);
            }
            for (DetailSize detailSizeCollectionOldDetailSize : detailSizeCollectionOld) {
                if (!detailSizeCollectionNew.contains(detailSizeCollectionOldDetailSize)) {
                    detailSizeCollectionOldDetailSize.setIdDetal(null);
                    detailSizeCollectionOldDetailSize = em.merge(detailSizeCollectionOldDetailSize);
                }
            }
            for (DetailSize detailSizeCollectionNewDetailSize : detailSizeCollectionNew) {
                if (!detailSizeCollectionOld.contains(detailSizeCollectionNewDetailSize)) {
                    Details oldIdDetalOfDetailSizeCollectionNewDetailSize = detailSizeCollectionNewDetailSize.getIdDetal();
                    detailSizeCollectionNewDetailSize.setIdDetal(details);
                    detailSizeCollectionNewDetailSize = em.merge(detailSizeCollectionNewDetailSize);
                    if (oldIdDetalOfDetailSizeCollectionNewDetailSize != null && !oldIdDetalOfDetailSizeCollectionNewDetailSize.equals(details)) {
                        oldIdDetalOfDetailSizeCollectionNewDetailSize.getDetailSizeCollection().remove(detailSizeCollectionNewDetailSize);
                        oldIdDetalOfDetailSizeCollectionNewDetailSize = em.merge(oldIdDetalOfDetailSizeCollectionNewDetailSize);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = details.getId();
                if (findDetails(id) == null) {
                    throw new NonexistentEntityException("The details with id " + id + " no longer exists.");
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
            Details details;
            try {
                details = em.getReference(Details.class, id);
                details.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The details with id " + id + " no longer exists.", enfe);
            }
            Colors colorId = details.getColorId();
            if (colorId != null) {
                colorId.getDetailsCollection().remove(details);
                colorId = em.merge(colorId);
            }
            Items itemId = details.getItemId();
            if (itemId != null) {
                itemId.getDetailsCollection().remove(details);
                itemId = em.merge(itemId);
            }
            Positions positionId = details.getPositionId();
            if (positionId != null) {
                positionId.getDetailsCollection().remove(details);
                positionId = em.merge(positionId);
            }
            Styles styleId = details.getStyleId();
            if (styleId != null) {
                styleId.getDetailsCollection().remove(details);
                styleId = em.merge(styleId);
            }
            Collection<DetailSize> detailSizeCollection = details.getDetailSizeCollection();
            for (DetailSize detailSizeCollectionDetailSize : detailSizeCollection) {
                detailSizeCollectionDetailSize.setIdDetal(null);
                detailSizeCollectionDetailSize = em.merge(detailSizeCollectionDetailSize);
            }
            em.remove(details);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Details> findDetailsEntities() {
        return findDetailsEntities(true, -1, -1);
    }

    public List<Details> findDetailsEntities(int maxResults, int firstResult) {
        return findDetailsEntities(false, maxResults, firstResult);
    }

    private List<Details> findDetailsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Details.class));
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

    public Details findDetails(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Details.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetailsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Details> rt = cq.from(Details.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
