/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.util.helper;

/**
 *
 * @author Lewis
 */
public class AircraftCabinClassHelper {
    private Long id;
    private String type;
    private String name;
    private int seatQty;

    public AircraftCabinClassHelper(Long id, String type, String name, int seatQty) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.seatQty = seatQty;
    }

    public AircraftCabinClassHelper() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the seatQty
     */
    public int getSeatQty() {
        return seatQty;
    }

    /**
     * @param seatQty the seatQty to set
     */
    public void setSeatQty(int seatQty) {
        this.seatQty = seatQty;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
}
