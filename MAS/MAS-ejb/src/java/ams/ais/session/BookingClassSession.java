/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassTypeException;
import ams.ais.util.helper.AisMsg;
import java.util.List;
import javax.ejb.EJB;
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
public class BookingClassSession implements BookingClassSessionLocal {
    
    @EJB
    private BookingClassSessionLocal bookingClassSessionLocal;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createBookingClass(String name) throws ExistSuchBookingClassNameException {
        verifyBookingClassName(name);
        BookingClass bookingClass = new BookingClass();
        bookingClass.create(name);
        entityManager.persist(bookingClass);
    }

    @Override
    public void verifyBookingClassName(String name) throws ExistSuchBookingClassNameException {
        List<BookingClass> bookingClasses = getAllBookingClasses();
        if (bookingClasses != null) {
            for (BookingClass bookingClass : bookingClasses) {
                if (name.equals(bookingClass.getName())) {
                    throw new ExistSuchBookingClassNameException(AisMsg.EXIST_SUCH_BOOKING_CLASS_ERROR);
                }
            }
        }
    }

    @Override
    public List<BookingClass> getAllBookingClasses() {
        Query query = entityManager.createQuery("SELECT c FROM BookingClass c");
        return query.getResultList();
    }


}
