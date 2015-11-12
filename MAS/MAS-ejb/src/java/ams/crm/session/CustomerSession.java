/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.Rule;
import ams.ais.entity.SeatAllocationHistory;
import ams.crm.entity.Customer;
import ams.crm.entity.CustomerList;
import ams.crm.entity.Feedback;
import ams.crm.entity.RegCust;
import ams.crm.entity.SelectedCust;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mas.util.helper.CopierHelper;

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
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.DType NOT LIKE :dtype");
        query.setParameter("dtype", "SelectedCust");

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

//    @Override
//    public List<Customer> searchCustomerByFirstName(String firstName) {
//        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.firstName = :firstName");
//        List<Customer> customers = new ArrayList<>();
//        try {
//            customers = (List<Customer>) query.getResultList();
//        } catch (NoResultException ex) {
//        }
//        return customers;
//    }
//
//    @Override
//    public List<Customer> searchCustomerByLastName(String lastName) {
//        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.lastName = :lastName");
//        List<Customer> customers = new ArrayList<>();
//        try {
//            customers = (List<Customer>) query.getResultList();
//        } catch (NoResultException ex) {
//        }
//        return customers;
//    }
//
//    @Override
//    public List<Customer> searchCusotmerByPassportNo(String passportNo) {
//        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.passportNo = :passportNo");
//        List<Customer> customers = new ArrayList<>();
//        try {
//            customers = (List<Customer>) query.getResultList();
//        } catch (NoResultException ex) {
//        }
//        return customers;
//    }
    @Override
    public RegCust findRegCustByCustomer(Customer customer) {
        Query query = em.createQuery("SELECT r FROM RegCust r WHERE r.id = :ID");
        query.setParameter("ID", customer.getId());

        RegCust regCust = new RegCust();
        try {
            regCust = (RegCust) query.getSingleResult();
        } catch (NoResultException ex) {
        }
        return regCust;
    }

    @Override
    public Feedback createFeedback(RegCust regCust, String subject, String content, String channel) {
        Feedback feedback = new Feedback();
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);
        feedback.setCreatedTime(date);
        feedback.setChannel(channel);
        feedback.setSubject(subject);
        feedback.setContent(content);
        feedback.setStatus("Created");
        RegCust r = em.find(RegCust.class, regCust.getId());
        feedback.setRegCust(r);
        List<Feedback> feedbacks = r.getFeedbacks();
        feedbacks.add(feedback);
        r.setFeedbacks(feedbacks);
        em.persist(r);
        em.persist(feedback);
        return feedback;
    }

    @Override
    public List<Feedback> getRegCustFeedback(RegCust regCust) {
        List<Feedback> feedbacks = new ArrayList<>();
        RegCust r = new RegCust();
        r = em.find(RegCust.class, regCust.getId());
        return r.getFeedbacks();
    }

    @Override
    public List<Customer> getCustomerByPassportNo(String passportNo) {
        System.out.println("Sesson bean: passport number is:" + passportNo);
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.passportNo = :passportNo");
        query.setParameter("passportNo", passportNo);

        List<Customer> customers = new ArrayList<>();
        try {
            customers = (List<Customer>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return customers;
    }

    @Override
    public List<Customer> getCustomerByName(String firstName, String lastName) {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.firstName = :firstName AND c.lastName = :lastName");
        query.setParameter("firstName", firstName);

        query.setParameter("lastName", lastName);

        List<Customer> customers = new ArrayList<>();
        try {
            customers = (List<Customer>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return customers;
    }

    @Override
    public List<Customer> getCustomerByMembershipId(String membershipId) {
        Query query = em.createQuery("SELECT r FROM RegCust r WHERE r.membershipId = :membershipId");
        query.setParameter("membershipId", membershipId);

        RegCust regCust = new RegCust();
        try {
            regCust = (RegCust) query.getSingleResult();
        } catch (NoResultException ex) {
        }

        Query query2 = em.createQuery("SELECT c FROM Customer c WHERE c.id = regCust.id");
        List<Customer> customers = new ArrayList<>();
        try {
            customers = (List<Customer>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return customers;
    }

    @Override
    public List<String> getAllNationalities() {
        Query query = em.createQuery("SELECT c FROM Customer c");
        List<Customer> cs = new ArrayList<>();

        try {
            cs = (List<Customer>) query.getResultList();
        } catch (NoResultException ex) {
        }
        List<String> nationalities = new ArrayList<>();
        for (Customer customer : cs) {
            String nationality = new String();
            nationality = customer.getNationality();
            if (!nationalities.contains(nationality)) {
                nationalities.add(nationality);
            }
        }
        return nationalities;

    }

    @Override
    public List<RegCust> filterCustomer(String gender, String nationality, int minAge, int maxAge) {
        Query query = em.createQuery("SELECT r FROM RegCust r");
        List<RegCust> regCusts = new ArrayList<>();

        try {
            regCusts = (List<RegCust>) query.getResultList();
        } catch (NoResultException ex) {
        }
        List<RegCust> copy = new ArrayList<>(regCusts);

        for (RegCust r : regCusts) {
            int age = getAge(r.getDob());
            if (gender != null && !r.getGender().equals(gender)) {
                copy.remove(r);
            } else if (nationality != null && !r.getNationality().equals(nationality)) {
                copy.remove(r);
            } else if (age < minAge || age > maxAge) {
                copy.remove(r);
            }
        }

        return copy;
    }

    public static int getAge(Date dateOfBirth) {

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year   
        if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3)
                || (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
                && (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    @Override
    public CustomerList createCustomerList(List<RegCust> customers, String name) {
        System.out.println("create customer list 1");
        CustomerList customerList = new CustomerList();
        customerList.setName(name);
        System.out.println("create customer list 2: name: " + customerList.getName());

        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(yourmilliseconds);
        customerList.setCreatedTime(date);
        System.out.println("create customer list 3: date: " + customerList.getCreatedTime());

        List<SelectedCust> selectedCusts = new ArrayList<>();
        for (RegCust r : customers) {
            SelectedCust selectedCust = new SelectedCust();
            selectedCust.setAccMiles(r.getAccMiles());
            selectedCust.setActivated(r.getActivated());
            selectedCust.setAddr1(r.getAddr1());
            selectedCust.setAddr2(r.getAddr2());
            selectedCust.setAirTickets(r.getAirTickets());
            selectedCust.setCity(r.getCity());
            selectedCust.setCountry(r.getCountry());
            selectedCust.setCustValue(r.getCustValue());
            selectedCust.setDType(r.getDType());
            selectedCust.setDob(r.getDob());
            selectedCust.setEmail(r.getEmail());
            selectedCust.setFeedbacks(r.getFeedbacks());
            selectedCust.setFirstName(r.getFirstName());
            selectedCust.setLastName(r.getLastName());
            selectedCust.setGender(r.getGender());
            selectedCust.setMembership(r.getMembership());
            selectedCust.setMembershipClass(r.getMembershipClass());
            selectedCust.setMembershipId(r.getMembershipId());
            selectedCust.setMileTranses(r.getMileTranses());
            selectedCust.setNationality(r.getNationality());
            selectedCust.setNewsLetterPref(r.getNewsLetterPref());
            selectedCust.setNumOfFlights(r.getNumOfFlights());
            selectedCust.setPassportExpDate(r.getPassportExpDate());
            selectedCust.setPassportIssueDate(r.getPassportIssueDate());
            selectedCust.setPassportNo(r.getPassportNo());
            selectedCust.setPhone(r.getPhone());
            selectedCust.setPromoPref(r.getPromoPref());
            selectedCust.setProvince(r.getProvince());
            selectedCust.setPwd(r.getPwd());
            selectedCust.setSecurAns(r.getSecurAns());
            selectedCust.setSecurQuest(r.getSecurQuest());
            selectedCust.setTitle(r.getTitle());
            selectedCust.setZipCode(r.getZipCode());
            em.persist(selectedCust);
            selectedCusts.add(selectedCust);
        }

        customerList.setSelectedCusts(selectedCusts);
        customerList.setDescription(name);
        System.out.println("create customer list 4: description: " + customerList.getDescription());

        em.persist(customerList);

        return customerList;
    }

    @Override
    public List<RegCust> getAllRegCusts() {
        Query query = em.createQuery("SELECT r FROM RegCust r WHERE r.DType NOT LIKE :dtype");
        query.setParameter("dtype", "SelectedCust");

        List<RegCust> regCusts = new ArrayList<>();
        try {
            regCusts = (List<RegCust>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return regCusts;

    }

    @Override
    public List<CustomerList> getAllCustomerLists() {
        Query query = em.createQuery("SELECT c FROM CustomerList c");
        List<CustomerList> customerLists = new ArrayList<>();
        try {
            customerLists = (List<CustomerList>) query.getResultList();
        } catch (NoResultException ex) {
        }
        return customerLists;
    }
}
