/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.session;

import ams.ais.entity.CabinClass;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.aps.entity.AircraftType;
import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchAircraftException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author winga_000
 */
@Stateless
public class AircraftSession implements AircraftSessionLocal {

    @PersistenceContext
    private EntityManager entityManager;

}
