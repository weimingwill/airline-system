	INSERT INTO `mas`.`FLIGHTSCHEDULE`(`ARRIVALDATE`,`ARRIVALGATE`,`ARRIVALTERMINAL`,
		`ARRIVALTIME`,`DELETED`,`DEPARTDATE`,`DEPARTGATE`,`DEPARTTERMINAL`,`DEPARTTIME`,`AIRCRAFT_AIRCRAFTID`) 
	VALUES('2016-07-18','5','2','20:00:00',0,'2016-07-18','20','1','10:00:00',1);
	INSERT INTO `mas`.`FLIGHTSCHEDULE`(`ARRIVALDATE`,`ARRIVALGATE`,`ARRIVALTERMINAL`,
		`ARRIVALTIME`,`DELETED`,`DEPARTDATE`,`DEPARTGATE`,`DEPARTTERMINAL`,`DEPARTTIME`,`AIRCRAFT_AIRCRAFTID`) 
	VALUES('2016-08-18','5','1','19:00:00',0,'2016-08-19','20','2','09:00:00',1);

	INSERT INTO `mas`.`FLIGHTSCHEDULE`(`ARRIVALDATE`,`ARRIVALGATE`,`ARRIVALTERMINAL`,
		`ARRIVALTIME`,`DELETED`,`DEPARTDATE`,`DEPARTGATE`,`DEPARTTERMINAL`,`DEPARTTIME`,`AIRCRAFT_AIRCRAFTID`) 
	VALUES('2016-07-17','10','3','19:00:00',0,'2016-07-17','20','2','09:00:00',2);

INSERT INTO `mas`.`FLIGHTSCHEDULE`(`ARRIVALDATE`,`ARRIVALGATE`,`ARRIVALTERMINAL`,
	`ARRIVALTIME`,`DELETED`,`DEPARTDATE`,`DEPARTGATE`,`DEPARTTERMINAL`,`DEPARTTIME`,`AIRCRAFT_AIRCRAFTID`) 
VALUES('2016-08-18','5','1','19:00:00',0,'2016-08-19','20','1','10:00:00',3);
INSERT INTO `mas`.`FLIGHTSCHEDULE`(`ARRIVALDATE`,`ARRIVALGATE`,`ARRIVALTERMINAL`,
	`ARRIVALTIME`,`DELETED`,`DEPARTDATE`,`DEPARTGATE`,`DEPARTTERMINAL`,`DEPARTTIME`,`AIRCRAFT_AIRCRAFTID`) 
VALUES('2016-08-20','5','1','18:00:00',0,'2016-08-21','19','4','11:00:00',4);
INSERT INTO `mas`.`FLIGHTSCHEDULE`(`ARRIVALDATE`,`ARRIVALGATE`,`ARRIVALTERMINAL`,
	`ARRIVALTIME`,`DELETED`,`DEPARTDATE`,`DEPARTGATE`,`DEPARTTERMINAL`,`DEPARTTIME`,`AIRCRAFT_AIRCRAFTID`) 
VALUES('2016-07-20','1','1','09:00:00',0,'2016-07-21','15','4','11:00:00',5);

INSERT INTO `mas`.`FLIGHTSCHEDULE`(`ARRIVALDATE`,`ARRIVALGATE`,`ARRIVALTERMINAL`,
	`ARRIVALTIME`,`DELETED`,`DEPARTDATE`,`DEPARTGATE`,`DEPARTTERMINAL`,`DEPARTTIME`,`AIRCRAFT_AIRCRAFTID`) 
VALUES('2016-07-22','5','2','18:00:00',0,'2016-07-23','10','3','10:00:00',10);
INSERT INTO `mas`.`FLIGHTSCHEDULE`(`ARRIVALDATE`,`ARRIVALGATE`,`ARRIVALTERMINAL`,
	`ARRIVALTIME`,`DELETED`,`DEPARTDATE`,`DEPARTGATE`,`DEPARTTERMINAL`,`DEPARTTIME`,`AIRCRAFT_AIRCRAFTID`) 
VALUES('2016-09-20','1','3','05:00:00',0,'2016-09-21','15','2','11:00:00',11);


INSERT INTO `mas`.`TICKETFAMILY`(`NAME`,`TYPE`,`CABINCLASS_CABINCLASSID`) VALUES ('Flex','MSF','1');
INSERT INTO `mas`.`TICKETFAMILY`(`NAME`,`TYPE`,`CABINCLASS_CABINCLASSID`) VALUES ('Standard','MSST','1');
INSERT INTO `mas`.`TICKETFAMILY`(`NAME`,`TYPE`,`CABINCLASS_CABINCLASSID`) VALUES ('Save','MSS','1');
INSERT INTO `mas`.`TICKETFAMILY`(`NAME`,`TYPE`,`CABINCLASS_CABINCLASSID`) VALUES ('Flex','BF','2');
INSERT INTO `mas`.`TICKETFAMILY`(`NAME`,`TYPE`,`CABINCLASS_CABINCLASSID`) VALUES ('Standard','BST','2');
INSERT INTO `mas`.`TICKETFAMILY`(`NAME`,`TYPE`,`CABINCLASS_CABINCLASSID`) VALUES ('Save','BS','2');
INSERT INTO `mas`.`TICKETFAMILY`(`NAME`,`TYPE`,`CABINCLASS_CABINCLASSID`) VALUES ('Standard','PEST','3');
INSERT INTO `mas`.`TICKETFAMILY`(`NAME`,`TYPE`,`CABINCLASS_CABINCLASSID`) VALUES ('Standard','EST','4');


INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('A','1');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('B','1');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('C','2');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('D','2');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('E','3');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('F','4');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('G','5');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('H','6');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('I','7');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('J','8');

INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('AA','110');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('BB','110');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('CC','111');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('DD','111');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('EE','112');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('FF','112');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('GG','113');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('HH','113');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('II','114');
INSERT INTO `mas`.`BOOKINGCLASS`(`NAME`,`TICKETFAMILY_TICKETFAMILYID`) VALUES ('JJ','115');


INSERT INTO `mas`.`FLIGHTSCHEDULE`(`ARRIVALDATE`,`ARRIVALGATE`,`ARRIVALTERMINAL`,
	`ARRIVALTIME`,`DELETED`,`DEPARTDATE`,`DEPARTGATE`,`DEPARTTERMINAL`,`DEPARTTIME`,`AIRCRAFT_AIRCRAFTID`) 
VALUES('2016-07-18','5','2','20:00:00',0,'2016-07-18','20','1','10:00:00',12);

INSERT INTO `mas`.`TICKETFAMILY`(`NAME`,`TYPE`,`CABINCLASS_CABINCLASSID`) VALUES ('TestWeiming1','TW1','1');
INSERT INTO `mas`.`TICKETFAMILY`(`NAME`,`TYPE`,`CABINCLASS_CABINCLASSID`) VALUES ('TestWeiming2','TW2','1');