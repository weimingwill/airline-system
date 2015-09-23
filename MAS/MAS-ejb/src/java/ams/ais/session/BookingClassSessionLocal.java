/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NoSuchBookingClassException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Tongtong
 */
@Local
public interface BookingClassSessionLocal {
    public void createBookingClass(String name) throws ExistSuchBookingClassNameException;
    public void verifyBookingClassName(String name) throws ExistSuchBookingClassNameException;
    public List<BookingClass> getAllBookingClasses();
    public void deleteBookingClass(String name) throws NoSuchBookingClassException;
    public BookingClass search(String name) throws NoSuchBookingClassException;
}
