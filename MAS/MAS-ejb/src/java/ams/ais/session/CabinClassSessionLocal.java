/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;


import ams.ais.entity.CabinClass;
import ams.ais.util.exception.ExistSuchCabinClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassTypeException;
import ams.ais.util.exception.NoSuchCabinClassException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface CabinClassSessionLocal {
     
   public List<CabinClass> getAllCabinClass();
   public void createCabinClass(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException;
   public void verifyCabinClassExistence(String type, String name) throws ExistSuchCabinClassNameException, ExistSuchCabinClassTypeException;
   public void deleteCabinClass(String name) throws NoSuchCabinClassException;
   public CabinClass getCabinClassByName(String name);
   public void updateCabinClass(String oldname, String type, String name) throws NoSuchCabinClassException, ExistSuchCabinClassNameException,ExistSuchCabinClassTypeException;
   public List<String> getAllOtherCabinClassByName (String name);
   public List<String> getAllOtherCabinClassByType (String type);
   public List<CabinClass> getAllOtherCabinClass(String name);
  
}
