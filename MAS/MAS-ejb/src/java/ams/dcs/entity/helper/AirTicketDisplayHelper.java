/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.entity.helper;

/**
 *
 * @author ChuningLiu
 */
//display each leg
public class AirTicketDisplayHelper {
    private String flightNo;
    private String oriITAT;
    private String destITAT;
    private String oriName;
    private String destName;
    private String cabinClass;
    private String seat;
    private String status;
    
    public AirTicketDisplayHelper(){
        
    }

    /**
     * @return the flightNo
     */
    public String getFlightNo() {
        return flightNo;
    }

    /**
     * @param flightNo the flightNo to set
     */
    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    /**
     * @return the oriITAT
     */
    public String getOriITAT() {
        return oriITAT;
    }

    /**
     * @param oriITAT the oriITAT to set
     */
    public void setOriITAT(String oriITAT) {
        this.oriITAT = oriITAT;
    }

    /**
     * @return the destITAT
     */
    public String getDestITAT() {
        return destITAT;
    }

    /**
     * @param destITAT the destITAT to set
     */
    public void setDestITAT(String destITAT) {
        this.destITAT = destITAT;
    }

    /**
     * @return the cabinClass
     */
    public String getCabinClass() {
        return cabinClass;
    }

    /**
     * @param cabinClass the cabinClass to set
     */
    public void setCabinClass(String cabinClass) {
        this.cabinClass = cabinClass;
    }

    /**
     * @return the seat
     */
    public String getSeat() {
        return seat;
    }

    /**
     * @param seat the seat to set
     */
    public void setSeat(String seat) {
        this.seat = seat;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the oriName
     */
    public String getOriName() {
        return oriName;
    }

    /**
     * @param oriName the oriName to set
     */
    public void setOriName(String oriName) {
        this.oriName = oriName;
    }

    /**
     * @return the destName
     */
    public String getDestName() {
        return destName;
    }

    /**
     * @param destName the destName to set
     */
    public void setDestName(String destName) {
        this.destName = destName;
    }
    
}
