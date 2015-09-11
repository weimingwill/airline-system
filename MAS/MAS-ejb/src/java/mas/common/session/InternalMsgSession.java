/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.entity.SystemMsg;

/**
 *
 * @author winga_000
 */
@Stateless
public class InternalMsgSession implements InternalMsgSessionLocal {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public List<SystemMsg> getAllInternalMessages() {
        Query query = em.createQuery("SELECT m FROM SystemMsg m");
        return query.getResultList();
    }
}
