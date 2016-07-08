/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.session;

import ams.afos.entity.Checklist;
import ams.afos.entity.MaintCheckType;
import ams.afos.entity.MaintCrew;
import ams.afos.entity.MaintSchedule;
import ams.afos.util.helper.MaintScheduleClashException;
import ams.aps.entity.Aircraft;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Lewis
 */
@Local
public interface MaintMgmtSessionLocal {
    public List<MaintSchedule> getAllMaintTasks();
    public void addMaintTask(MaintSchedule newMaintSchedule) throws MaintScheduleClashException;
    public void updateMaintTask(MaintSchedule maintSchedule);
    public MaintCrew getCrewById(Long id);
    public List<MaintCheckType> getMaintCheckTypes();
    public List<MaintCrew> getMaintCrews();
    public MaintCheckType getCheckTypeById(Long id);
    public void createMaintChecklist(Checklist checklist);
    public Checklist getAircraftMaintChecklistByType(Aircraft aircraft, String type);
}
