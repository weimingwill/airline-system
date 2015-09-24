/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.helper;

import ams.ais.entity.CabinClassTicketFamily;
import ams.ais.helper.CabinClassTicketFamilyId;
import ams.aps.entity.AircraftCabinClass;
import java.util.List;

/**
 *
 * @author winga_000
 */
public class Test {
    private AircraftCabinClass aircraftCabinClass;
    private CabinClassTicketFamily cabinClassTicketFamily;
    
    public void getMethod(){
        CabinClassTicketFamilyId cabinClassTicketFamilyId = new CabinClassTicketFamilyId();
        cabinClassTicketFamilyId.getAircraftCabinClassId().setCabinClassId(null);
        cabinClassTicketFamilyId.setTicketFamilyId(null);
        
        aircraftCabinClass.getCabinClass().getName();
        List<CabinClassTicketFamily> cabinClassTicektFamilyList = aircraftCabinClass.getCabinClassTicketFamilys();
        for (CabinClassTicketFamily cabinClassTicektFamily : cabinClassTicektFamilyList) {
            cabinClassTicektFamily.getTicketFamily().getTicketFamilyId();
        }
        cabinClassTicketFamily.setCabinClassTicketFamilyId(null);
    }
}
