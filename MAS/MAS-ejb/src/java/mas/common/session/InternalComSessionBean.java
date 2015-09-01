/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.session;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.common.entity.PlainTextMessage;

/**
 *
 * @author winga_000
 */
@Stateless
@LocalBean
public class InternalComSessionBean {

    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;

    public List<PlainTextMessage> getAllPlainTextMessage() {
        Query query = em.createQuery("SELECT m FROM PlainTextMessage m");
        return query.getResultList();
    }
}
