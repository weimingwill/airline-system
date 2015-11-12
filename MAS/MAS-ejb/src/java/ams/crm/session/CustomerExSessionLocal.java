/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.crm.entity.Membership;
import ams.crm.entity.RegCust;
import ams.crm.entity.helper.Phone;
import ams.crm.util.exception.ExistSuchRegCustException;
import ams.crm.util.exception.InvalidPasswordException;
import ams.crm.util.exception.NoSuchMembershipException;
import ams.crm.util.exception.NoSuchRegCustException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface CustomerExSessionLocal {
 //   public void createRegCust(String title, String firstname,String lastname, String passportNo, String nationality, String gender,Date dob, String email, String addr1, String addr2, String city, String state, String country, String zipCode, Phone mobilephone, Phone telephone, String pwd,String securQuest,String securAns, Boolean newsLetterPref, Boolean promoPref, String membershipClass, Double accMiles,Double custValue, Integer numOfFlights, String memberShipId) throws ExistSuchRegCustException;
//    public void verifyRegCustExistence(String passportNo) throws ExistSuchRegCustException;
    public List<RegCust> getAllRegCusts();
    public void updateMiles(String email, Double accMiles) throws NoSuchRegCustException;
    public void createRegCust(RegCust regCust)throws ExistSuchRegCustException;
    public Membership getMembershipByName(String membershipClassName) throws NoSuchMembershipException;
    public RegCust getRegCustByEmail (String email) throws NoSuchRegCustException;
    public void doLogin(String email, String inputPassword) throws NoSuchRegCustException, InvalidPasswordException;
    public void updateProfile(Long customerId,String passportNo, Date passportIssueDate, Date passportExpDate, String nationality, String email, String addr1, String addr2, String city, String state, String country, String zipCode,Phone phone, String securQuest, String securAns,Boolean newsLetterPref, Boolean promPref) throws ExistSuchRegCustException,NoSuchRegCustException;
//    public void verifyEmailExistence(String email) throws ExistSuchRegCustException;
}
