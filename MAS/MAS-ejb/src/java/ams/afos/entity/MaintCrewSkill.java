/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import ams.afos.entity.helper.MaintCrewSkillId;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author Lewis
 */
@Entity
@Table(name = "MAINTCREWSKILL")
public class MaintCrewSkill implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private MaintCrewSkillId maintCrewSkillId;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "MAINTCREWID", referencedColumnName = "SYSTEMUSERID")
    private MaintCrew maintCrew;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "MAINTCHECKTYPEID", referencedColumnName = "MAINTCHECKTYPEID")
    private MaintCheckType maintCheckType;
    
    @Column(name = "ISCAPABLE")
    private Boolean isCapable;

    /**
     * @return the maintCrewSkillId
     */
    public MaintCrewSkillId getMaintCrewSkillId() {
        return maintCrewSkillId;
    }

    /**
     * @param maintCrewSkillId the maintCrewSkillId to set
     */
    public void setMaintCrewSkillId(MaintCrewSkillId maintCrewSkillId) {
        this.maintCrewSkillId = maintCrewSkillId;
    }

    /**
     * @return the maintCrew
     */
    public MaintCrew getMaintCrew() {
        return maintCrew;
    }

    /**
     * @param maintCrew the maintCrew to set
     */
    public void setMaintCrew(MaintCrew maintCrew) {
        this.maintCrew = maintCrew;
    }

    /**
     * @return the maintCheckType
     */
    public MaintCheckType getMaintCheckType() {
        return maintCheckType;
    }

    /**
     * @param maintCheckType the maintCheckType to set
     */
    public void setMaintCheckType(MaintCheckType maintCheckType) {
        this.maintCheckType = maintCheckType;
    }

    /**
     * @return the isCapable
     */
    public Boolean getIsCapable() {
        return isCapable;
    }

    /**
     * @param isCapable the isCapable to set
     */
    public void setIsCapable(Boolean isCapable) {
        this.isCapable = isCapable;
    }
    

}
