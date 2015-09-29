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
    private Long id;
    
    private String name;
    private double ruleValue;

    public TicketFamilyRuleHelper(Long id,  String name, double ruleValue) {
        this.id = id;
        this.name = name;
        this.ruleValue = ruleValue;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
