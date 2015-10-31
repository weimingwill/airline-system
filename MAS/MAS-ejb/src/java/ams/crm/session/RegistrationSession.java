/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;


import ams.crm.entity.Membership;
import ams.crm.entity.RegCust;
import ams.crm.util.exception.ExistSuchRegCustException;
import ams.crm.util.exception.NoSuchMembershipException;
import ams.crm.util.helper.CrmMsg;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Bowen
 */
@Stateless

public class RegistrationSession implements RegistrationLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager entityManager;
    
    
    
    @Override
    public void createRegCust(RegCust regCust) throws ExistSuchRegCustException{
        
        Random r = new Random( System.currentTimeMillis() );
        verifyRegCustExistence(regCust.getPassportNo());
        verifyEmailExistence(regCust.getEmail());
        regCust.setAccMiles(0.0);
        regCust.setCustValue(0.0);
        regCust.setNumOfFlights(0);
        try {
            regCust.setMembership(entityManager.find(Membership.class,getMembershipByName("Elite Bronze").getId()));
        } catch (NoSuchMembershipException ex) {
            Logger.getLogger(RegistrationSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        regCust.setMemberShipId("MA"+ 10000 + r.nextInt(20000));
        entityManager.persist(regCust);
    }
//    public void createRegCust(String title, String firstname,String lastname, String passportNo, String nationality, String gender,Date dob, String email, String addr1, String addr2, String city, String state, String country, String zipCode, Phone mobilephone, Phone telephone, String pwd,String securQuest,String securAns, Boolean newsLetterPref, Boolean promoPref, String membershipClass, Double accMiles,Double custValue, Integer numOfFlights, String memberShipId) throws ExistSuchRegCustException {
//        RegCust regCust = new RegCust();
//      
//        verifyRegCustExistence(passportNo);
//        regCust.setTitle(title);
//        regCust.setFirstName(firstname);
//        regCust.setLastName(lastname);
//        regCust.setNationality(nationality);
//        regCust.setGender(gender);
//        regCust.setDob(dob);
//        regCust.setEmail(email);
//        regCust.setAddr1(addr1);
//        regCust.setAddr2(addr2);
//        regCust.setCity(city);
//        regCust.setCountry(country);
//        regCust.setZipCode(zipCode);
//        regCust.setMobilephone(mobilephone);
//        regCust.setTelephone(telephone);
//        regCust.setPwd(pwd);
//        regCust.setSecurQuest(securQuest);
//        regCust.setSecurAns(securAns);
//        regCust.setNewsLetterPref(newsLetterPref);
//        regCust.setPromoPref(promoPref);
//        regCust.setMembershipClass(membershipClass);
//        regCust.setAccMiles(accMiles);
//        regCust.setCustValue(custValue);
//        regCust.setNumOfFlights(numOfFlights);
//        regCust.setMemberShipId(memberShipId);
//        entityManager.persist(regCust);
//    }

    @Override
    public void verifyRegCustExistence(String passportNo) throws ExistSuchRegCustException {
        List<RegCust> regCusts = getAllRegCusts();
        if (regCusts != null) {
            for (RegCust rc : regCusts) {
                if (passportNo.equals(rc.getPassportNo())) {
                    throw new ExistSuchRegCustException(CrmMsg.EXIST_SUCH_Reg_Cust_ERROR);
                }
            }
        }
    }

    @Override
    public List<RegCust> getAllRegCusts() {
        Query query = entityManager.createQuery("SELECT r FROM RegCust r");
        return query.getResultList();
    }
    
    @Override
    public Membership getMembershipByName(String membershipClassName) throws NoSuchMembershipException{
        Query query = entityManager.createQuery("SELECT c FROM Membership c WHERE c.name = :inMembershipName");
        query.setParameter("inMembershipName", membershipClassName);
        Membership selectMembership = null;
        try {
            selectMembership = (Membership) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NoSuchMembershipException(CrmMsg.NO_SUCH_MEMBERSHIP_NAME_ERROR);
        }catch(NonUniqueResultException e){
            
        }
        return selectMembership;
    }
    
    @Override
    public void verifyEmailExistence(String email) throws ExistSuchRegCustException {
        List<RegCust> regCusts = getAllRegCusts();
        if (regCusts != null) {
            for (RegCust rc : regCusts) {
                if (email.equals(rc.getEmail())) {
                    throw new ExistSuchRegCustException(CrmMsg.EXIST_SUCH_Reg_Cust_ERROR);
                }
            }
        }
    } 
}   
