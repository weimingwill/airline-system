/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.TicketFamily;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface TicketFamilySessionLocal {
    public TicketFamily getTicketFamilyByName (String ticketFamilyName);
}
