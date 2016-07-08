/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.helper;

import ams.ais.entity.CabinClassTicketFamily;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author winga_000
 */
public class TicketFamilyMapHelper {

    private TicketFamilyMapHelper() {
    }

    public static List<CabinClassTicketFamily> getKeysFromValue(Map<CabinClassTicketFamily, Boolean> hm, boolean value) {
        List<CabinClassTicketFamily> list = new ArrayList<>();
        for (CabinClassTicketFamily c : hm.keySet()) {
            if (hm.get(c).equals(value)) {
                list.add(c);
            }
        }
        return list;
    }
}
