/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.ais.entity.Rule;
import ams.ais.entity.TicketFamily;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface GeneralInformationSessionLocal {
   public List<Rule> getNameChangeRule(); 
   public List<TicketFamily> getNameChangeTicketFamily();
   public List<Rule> getCancellationRule();
   public List<Rule> getLuggage();
   public List<Rule> getFlightChangeRules();
   public List<Rule> getOtherServiceRules();
   public List<Rule> getNoShowFee();
}
