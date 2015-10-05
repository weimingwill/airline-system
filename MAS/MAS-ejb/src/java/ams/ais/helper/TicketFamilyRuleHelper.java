/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.helper;

/**
 *
 * @author Bowen
 */
public class TicketFamilyRuleHelper {

    private Long ruleId;
    private Long ticketFamilyId;
    private String name;
    private double ruleValue;

    public TicketFamilyRuleHelper(Long ruleId, String name, double ruleValue) {
        this.name = name;
        this.ruleId = ruleId;
        this.ruleValue = ruleValue;
    }

    public TicketFamilyRuleHelper(Long ticketFamilyId, Long ruleId, String name, double ruleValue) {
        this.name = name;
        this.ticketFamilyId = ticketFamilyId;
        this.ruleId = ruleId;
        this.ruleValue = ruleValue;
    }

    public Long getTicketFamilyId() {
        return ticketFamilyId;
    }

    public void setTicketFamilyId(Long ticketFamilyId) {
        this.ticketFamilyId = ticketFamilyId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(double ruleValue) {
        this.ruleValue = ruleValue;
    }

}
