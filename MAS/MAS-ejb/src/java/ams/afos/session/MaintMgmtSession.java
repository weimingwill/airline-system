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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Lewis
 */
@Stateless
public class MaintMgmtSession implements MaintMgmtSessionLocal {

    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;

    @Override
    public List<MaintSchedule> getAllMaintTasks() {
        Query q = em.createQuery("SELECT m FROM MaintSchedule m WHERE m.deleted = FALSE ORDER BY m.startTime");
        return (List<MaintSchedule>) q.getResultList();
    }

    @Override
    public void addMaintTask(MaintSchedule newMaintSchedule) throws MaintScheduleClashException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:ss:mm");
        if (clashMaintSchedule(newMaintSchedule)) {
            System.out.println("Clash with existing maintenance task");
            throw new MaintScheduleClashException("New maintenance task from " + sdf.format(newMaintSchedule.getStartTime()) + " to " + sdf.format(newMaintSchedule.getEndTime()) + " for aircraft " + newMaintSchedule.getAircraft().getTailNo() + " clashes with another maintenance task");
        } else if (clashFlightSchedule(newMaintSchedule)) {
            System.out.println("Aircraft not idle");
            throw new MaintScheduleClashException("Aircraft is not idle, cannot add maintenance schedule");
        } else {
            System.out.println("Not Clash Add New Maintenance task");
            newMaintSchedule.setAircraft(em.find(Aircraft.class, newMaintSchedule.getAircraft().getAircraftId()));
            newMaintSchedule.setCheckType(em.find(MaintCheckType.class, newMaintSchedule.getCheckType().getMaintCheckTypeId()));
            List<MaintCrew> crews = new ArrayList();
            for (MaintCrew crew : newMaintSchedule.getMaintCrews()) {
                crews.add(em.find(MaintCrew.class, crew.getSystemUserId()));
            }
            newMaintSchedule.setMaintCrews(crews);
            em.persist(newMaintSchedule);
        }
    }

    private boolean clashMaintSchedule(MaintSchedule maintSchedule) {
        Query q = em.createQuery("SELECT m FROM MaintSchedule m WHERE m.deleted = FALSE AND (:inStartTime BETWEEN m.startTime AND m.endTime) OR (:inEndTime BETWEEN m.startTime AND m.endTime) OR (:inStartTime <= m.startTime AND :inEndTime >= m.endTime)");
        q.setParameter("inStartTime", maintSchedule.getStartTime());
        q.setParameter("inEndTime", maintSchedule.getEndTime());
        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean clashFlightSchedule(MaintSchedule maintSchedule) {
        Query q = em.createQuery("SELECT f FROM FlightSchedule f WHERE f.deleted = FALSE AND (:inStartTime BETWEEN f.departDate AND f.arrivalDate) OR (:inEndTime BETWEEN f.departDate AND f.arrivalDate) OR (:inStartTime <= f.departDate AND :inEndTime >= f.arrivalDate)");
        q.setParameter("inStartTime", maintSchedule.getStartTime());
        q.setParameter("inEndTime", maintSchedule.getEndTime());
        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void updateMaintTask(MaintSchedule maintSchedule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public MaintCrew getCrewById(Long id) {
        return em.find(MaintCrew.class, id);
    }

    @Override
    public List<MaintCheckType> getMaintCheckTypes() {
        Query q = em.createQuery("SELECT ct FROM MaintCheckType ct");
        return (List<MaintCheckType>) q.getResultList();
    }

    @Override
    public List<MaintCrew> getMaintCrews() {
        // TODO: Check Maint Skills?
        Query q = em.createQuery("SELECT mc FROM MaintCrew mc WHERE mc.deleted = FALSE");
        return (List<MaintCrew>) q.getResultList();
    }

    @Override
    public MaintCheckType getCheckTypeById(Long id) {
        return em.find(MaintCheckType.class, id);
    }

    @Override
    public void createMaintChecklist(Checklist checklist) {
        List<Checklist> fsChecklists;
        boolean foundExistingChecklist = false;
        Checklist newChecklist;
        if (checklist.getIsTemplate()) {
            em.persist(checklist);
            em.flush();
        }
        newChecklist = new Checklist();
        newChecklist.setChecklistItems(checklist.getChecklistItems());
        newChecklist.setDeleted(false);
        newChecklist.setIsTemplate(false);
        newChecklist.setType(checklist.getType());
        Aircraft thisAircraft = checklist.getAircraft();

        if (thisAircraft.getChecklists() == null) {
            // if aircraft does not have any checklist, create a new checklist and add it to flight schedule
            em.persist(newChecklist);
            fsChecklists = new ArrayList();
            fsChecklists.add(newChecklist);
            thisAircraft.setChecklists(fsChecklists);
            newChecklist.setAircraft(em.find(Aircraft.class, thisAircraft.getAircraftId()));
            em.merge(thisAircraft);
            em.merge(newChecklist);
        } else {
            // if aircraft has some checklist, check if there exists same type checklist
            fsChecklists = thisAircraft.getChecklists();
            for (Checklist cl : fsChecklists) {
                if (cl.getType().equals(checklist.getType())) {
                    // if found same type checklist
                    cl.setChecklistItems(checklist.getChecklistItems());
                    foundExistingChecklist = true;
                    break;
                }
            }
            if (!foundExistingChecklist) {
                // if not found then add the checklist to aircraft checklists
                em.persist(newChecklist);
                newChecklist.setAircraft(em.find(Aircraft.class, thisAircraft.getAircraftId()));
                thisAircraft.getChecklists().add(newChecklist);
                em.merge(newChecklist);
            }
            em.merge(thisAircraft);
        }
    }

    @Override
    public Checklist getAircraftMaintChecklistByType(Aircraft aircraft, String type) {
        Query q = em.createQuery("SELECT c FROM Aircraft a, IN(a.checklists) c WHERE a.aircraftId = :aircraftId AND c.deleted = FALSE AND c.type = :type");
        q.setParameter("aircraftId", aircraft.getAircraftId());
        q.setParameter("type", type);
        List<Checklist> output = q.getResultList();
        return (output == null || output.isEmpty()) ? null : output.get(0);
    }
}
