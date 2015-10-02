/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.helper;

import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import java.util.List;

/**
 *
 * @author winga_000
 */
public class CabinClassTicketFamilyHelper {
    private CabinClass cabinClass;
    private List<TicketFamily> ticketFamilys;
    private boolean haveTicketFamily;

    public CabinClassTicketFamilyHelper() {
    }

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }

    public List<TicketFamily> getTicketFamilys() {
        return ticketFamilys;
    }

    public void setTicketFamilys(List<TicketFamily> ticketFamilys) {
        this.ticketFamilys = ticketFamilys;
    }

    public boolean isHaveTicketFamily() {
        return haveTicketFamily;
    }

    public void setHaveTicketFamily(boolean haveTicketFamily) {
        this.haveTicketFamily = haveTicketFamily;
    }
    
    
}
