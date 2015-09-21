/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface CabinClassSessionLocal {
    
   public List<CabinClass> getAllCabinClass();
   public void createCabinClass(String name, String type);
   public void deleteCabinClass(String name);
   public CabinClass getCabinClassByName(String name);
   
}
