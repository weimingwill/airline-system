/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import mas.common.entity.SystemUser;

/**
 *
 * @author Lewis
 */
@Entity
public class MaintCrew extends SystemUser implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String maintCrewId;
    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private List<MaintCrewSkill> skills;

    /**
     * @return the skills
     */
    public List<MaintCrewSkill> getSkills() {
        return skills;
    }

    /**
     * @param skills the skills to set
     */
    public void setSkills(List<MaintCrewSkill> skills) {
        this.skills = skills;
    }

    /**
     * @return the maintCrewId
     */
    public String getMaintCrewId() {
        return maintCrewId;
    }

    /**
     * @param maintCrewId the maintCrewId to set
     */
    public void setMaintCrewId(String maintCrewId) {
        this.maintCrewId = maintCrewId;
    }
    
}
