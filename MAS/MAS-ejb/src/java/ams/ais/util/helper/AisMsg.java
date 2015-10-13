/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.helper;

import mas.common.util.helper.*;

/**
 *
 * @author Lewis
 */
public class AisMsg {

    public static final String EXIST_SUCH_CABIN_CLASS_TYPE_ERROR = "Cabin class type already exists";
    public static final String EXIST_SUCH_CABIN_CLASS_NAME_ERROR = "Cabin class name already exists";
    public static final String NO_SUCH_CABIN_CLASS_ERROR = "Cabin class doesn't exist";
    public static final String EXIST_SUCH_RULE_ERROR = "Rule already exists";
    public static final String NO_SUCH_RULE_ERROR = "Rule doesn't exists";
    public static final String EXIST_SUCH_TICKET_FAMILY_TYPE_ERROR = "Ticket family type already exists";
    public static final String EXIST_SUCH_TICKET_FAMILY_NAME_ERROR = "Ticket family name already exists";
    public static final String NO_SUCH_TICKET_FAMILY_ERROR = "Ticket family doesn't exist";
    public static final String EXIST_SUCH_BOOKING_CLASS_ERROR = "Booking class name already exists";
    public static final String NO_SUCH_BOOKING_CLASS_ERROR = "Booking class does not exist";
    public static final String EXIST_SUCH_TICKET_FAMILY_ERROR = "Ticket family already exists";
    public static final String NO_SUCH_CABINCLASS_TICKETFAMILY_ERROR = "Cabin Class and Ticket Family are not associated or does not exist";
    public static final String NEED_TICKET_FAMILY_ERROR = "Need to select at least one ticket family in each cabin class";
    public static final String NEED_BOOKING_CLASS_ERROR = "Need to select at least one booking class in each selected ticket family";
    public static final String WRONG_SUM_OF_TICKET_FAMILY_ERROR = "Sum of ticket family seat quantity should be equal to their cabin class seat quantity";
    public static final String WRONG_SUM_OF_BOOKING_CLASS_ERROR = "Sum of booking class seat quantity should be equal to their ticket family seat quantity";
    public static final String EXIST_SUCH_CHECK_POINT_ERROR = "Check point already exists";
    public static final String NO_SUCH_PHASE_DEMAND_ERROR = "Phase demand doesn't exist";
    public static final String DUPLICATE_PRICE_ERROR = "All prices entered should not be the same";
    public static final String NO_SUCH_TICKETFAMILY_RULE_ERROR = "TicketFamily or rule does not exist";
}
