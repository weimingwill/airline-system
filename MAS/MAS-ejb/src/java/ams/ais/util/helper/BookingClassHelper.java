/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.helper;

import ams.ais.entity.BookingClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author winga_000
 */
public class BookingClassHelper {

    private BookingClass bookingClass;
    private int seatQty;
    private float price;
    private float basicPrice;
    private float priceCoefficient;
    private float demandMean;
    private float demandDev;

    public BookingClassHelper() {
    }

    public BookingClass getBookingClass() {
        return bookingClass;
    }

    public void setBookingClass(BookingClass bookingClass) {
        this.bookingClass = bookingClass;
    }

    public int getSeatQty() {
        return seatQty;
    }

    public void setSeatQty(int seatQty) {
        this.seatQty = seatQty;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPriceCoefficient() {
        return priceCoefficient;
    }

    public void setPriceCoefficient(float priceCoefficient) {
        this.priceCoefficient = priceCoefficient;
    }

    public float getDemandMean() {
        return demandMean;
    }

    public void setDemandMean(float demandMean) {
        this.demandMean = demandMean;
    }

    public float getDemandDev() {
        return demandDev;
    }

    public void setDemandDev(float demandDev) {
        this.demandDev = demandDev;
    }

    public float getBasicPrice() {
        return basicPrice;
    }

    public void setBasicPrice(float basicPrice) {
        this.basicPrice = basicPrice;
    }

    public static List<String> getBookingClsNames() {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        List<String> bookingClsNames = new ArrayList<>();
        Map<Integer, String> nameMap = new HashMap<>();
        for (int i = 0; i < alphabet.length; i++) {
            bookingClsNames.add(String.valueOf(alphabet[i]).toUpperCase());
            nameMap.put(i, String.valueOf(alphabet[i]));
        }
        return bookingClsNames;
    }
}
