/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity;

import ams.ais.helper.CabinClassTicketFamilyId;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author winga_000
 */
@Entity
@IdClass(CabinClassTicketFamilyId.class)
@Table(name = "CABINCLASS_TICKETFAMILY")
public class CabinClassTicketFamily implements Serializable {
    @Id 
    private Long cabinClassId;
    
    @Id
    private Long ticketFamilyId;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "CABINCLASSID", referencedColumnName="CABINCLASSID")
    private CabinClass cabinClass;
    
    @OneToOne
    @PrimaryKeyJoinColumn(name = "TICKETFAMILYID", referencedColumnName="TICKETFAMILYID")
    private TicketFamily ticketFamily;

    @Column(name = "SEATQTY") 
    private int seatQty;

    
    
    public Long getCabinClassId() {
        return cabinClassId;
    }

    public void setCabinClassId(Long cabinClassId) {
        this.cabinClassId = cabinClassId;
    }

    public Long getTicketFamilyId() {
        return ticketFamilyId;
    }

    public void setTicketFamilyId(Long ticketFamilyId) {
        this.ticketFamilyId = ticketFamilyId;
    }
    
    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }

    public TicketFamily getTicketFamily() {
        return ticketFamily;
    }

    public void setTicketFamily(TicketFamily ticketFamily) {
        this.ticketFamily = ticketFamily;
    }

    public int getSeatQty() {
        return seatQty;
    }

    public void setSeatQty(int seatQty) {
        this.seatQty = seatQty;
    }
    
    
}