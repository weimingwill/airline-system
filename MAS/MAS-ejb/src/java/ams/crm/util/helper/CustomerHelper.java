/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.util.helper;

import ams.ars.entity.AddOn;
import ams.crm.entity.Customer;
import ams.crm.entity.RegCust;
import ams.dcs.entity.Luggage;

/**
 *
 * @author weiming
 */
public class CustomerHelper {
    private Customer customer;
    private RegCust regCust;
    private AddOn meal;
    private boolean insurance;
    private Luggage luggage;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public AddOn getMeal() {
        return meal;
    }

    public void setMeal(AddOn meal) {
        this.meal = meal;
    }

    public boolean isInsurance() {
        return insurance;
    }

    public void setInsurance(boolean insurance) {
        this.insurance = insurance;
    }

    public Luggage getLuggage() {
        return luggage;
    }

    public void setLuggage(Luggage luggage) {
        this.luggage = luggage;
    }

    public RegCust getRegCust() {
        return regCust;
    }

    public void setRegCust(RegCust regCust) {
        this.regCust = regCust;
    }
}
