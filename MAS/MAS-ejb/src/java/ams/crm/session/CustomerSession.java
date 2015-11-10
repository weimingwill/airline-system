/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ais.entity.Rule;
import ams.crm.entity.Customer;
import ams.crm.entity.RegCust;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Tongtong
 */
@Stateless
public class CustomerSession implements CustomerSessionLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Customer> getAllCustomers() {
        Query query = em.createQuery("SELECT c FROM Customer c");
        List<Customer> customers = new ArrayList<>();
        try {
            customers = (List<Customer>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return customers;
    }

    @Override
    public List<Customer> searchCustomer(String passportNo, String email, String firstName, String lastName, String membershipId) {
        List<Customer> customers = new ArrayList<Customer>();
        RegCust regCust = new RegCust();
        List<RegCust> regCusts = new ArrayList<>();
        if (passportNo != null) {
            Query query = em.createQuery("SELECT c FROM Customer c WHERE c.passportNo = :passportNo OR (c.firstName = :firstName AND c.lastName= :lastName)");
            query.setParameter("passportNo", passportNo);
            query.setParameter("firstName", firstName);
            query.setParameter("lastName", lastName);
            try {
                customers = query.getResultList();
            } catch (NoResultException ex) {
            }
        } else {
            Query query2 = em.createQuery("SELECT c , r FROM Customer c, RegCust r WHERE c.id = r.id AND (r.email=:email OR r.membershipId=:membershipId)");
            try {
                customers = query2.getResultList();

            } catch (NoResultException ex) {
            }

        }
        return customers;
    }

    @Override
    public List<Customer> searchCustomerByFirstName(String firstName) {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.firstName = :firstName");
        List<Customer> customers = new ArrayList<>();
        try {
            customers = (List<Customer>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return customers;
    }

    @Override
    public List<Customer> searchCustomerByLastName(String lastName) {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.lastName = :lastName");
        List<Customer> customers = new ArrayList<>();
        try {
            customers = (List<Customer>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return customers;
    }

    @Override
    public List<Customer> searchCusotmerByPassportNo(String passportNo) {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.passportNo = :passportNo");
        List<Customer> customers = new ArrayList<>();
        try {
            customers = (List<Customer>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return customers;
    }

}
