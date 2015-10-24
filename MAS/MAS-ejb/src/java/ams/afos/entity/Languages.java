/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Table;

/**
 *
 * @author Lewis
 */
@Embeddable
@Table(name = "CHECKLIST_CHECKLISTITEMS")
public class Languages implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;

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
}
