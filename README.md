# README #

## Project Information ##

* Project: Merlion Airline System
* Version: 1.0
* Release Date: 14 Oct, 2015
* Description:
This repository contains the source code of merlion airline systems developed by Team ES02 from Kent Ridge Technology. The first system release covers common infrastructure, Airline Planning System and Airline Inventory system. 



## Setup Guidelines ##

The following Setup guildlines assume that users have installed the latest version of:
- Netbeans IDE with GlassFish Server 4.1 (https://netbeans.org/downloads/)
- MySQL Server (http://dev.mysql.com/downloads/mysql/)
- MySQL Connector (http://dev.mysql.com/downloads/connector/j/)
- MySQL Workbench (http://dev.mysql.com/downloads/workbench/)
- Java JDK 8 (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- Java EE 7 (http://www.oracle.com/technetwork/java/javaee/downloads/index.html)


1. Setup MYSQL Connection
   a. Open project in Netbeans IDE
   b. In Netbeans IDE, go to the "Service" tab
   c. Right Click "Database" and choose "New Connection"
   d. Choose "MySQL (Connector/J driver)" for "Driver" and locate and add the downloaded MySQL Connector jar file
   e. Click "Next"
   f. Name "Database" as "mas", fill in the correct hostname and port for your MySQL Connection
   g. Fill in the correct username and password for your MySQL Server Connection
   h. Click "Finish"
(Note: a new database connection like "jdbc:mysql://localhost:8889/mas?zeroDateTimeBehavior=convertToNull [root on Default schema]" should appear under "Database")

2. Setup JDBC Resource and JDBC Connection Pool
   a. Go to the enterprise bean project under the "Projects" tab of Netbeans IDE
   b. Right click and choose "New" -> "Other" -> "GlassFish" -> "JDBC Resource"
   c. Click "Next"
   d. Choose the newly added connection pool under "Create New JDBC Connection Pool"
   e. Change JNDI name to "jdbc/mas"
   f. Click "Next"
   g. Do not change anything for "Additional Properties" and click "Next"
   h. Name "JDBC Connection Pool Name" as "masConnectionPool"
   i. Choose the newly added "mas" MySQL Connection under "Extract from Existing Connection"
   j. Click "Next"
   k. Do not change anything for "Add Conncetion Pool Properties" and click "Next"
   l. Click "Finish"
(Note: a file with name "sun-resources.xml" or "glassfish-resources.xml" should appear under folder "Server Resources")

3. Setup JDBC Persistence Unit
   a. Go to the enterprise bean project under the "Projects" tab of Netbeans IDE
   b. Right click and choose "New" -> "Other" -> "Persistence" -> "Persistence Unit"
   c. Click "Next"
   d. Choose the "jdbc/mas" under "Data Source" and Click Finish
(Note: a file with name "persistence.xml" should appear under foler "Configuration Files")

4. Setup JMS Resource
   a. In Netbeans IDE, go to the service tab
   b. Find GlassFish Server 4.1 Under Servers and Start GlassFish Server
   c. Right Click GlassFish Server and Choose "View Domain Admin Console"
   d. In the left panel of admin console, Choose JMS Resources
   e. Choose Connection Factories and add a new Connection Factory with the following properties:
      - JNDI Name: jms/topicInternalComConnectionFactory
      - Resource Type: javax.jms.TopicConnectionFactory
	  - Description: Topic Internal Com Connection Factory
   f. Choose Destination Resources and add a new Destination Resource with the following properties:
      - JNDI Name: jms/topicInternalCom
	  - Physical Destination Name: PhysicalTopic
	  - Resource Type: javax.jms.Topic
	  - Description: Topic Internal Com
   g. Close the admin console

5. Clean and Build Project
   - Right click MAS project and choose "Clean and Build"

6. Deploy the Project
   - Right click MAS project and choose "Deploy"

7. Insert Default/Test Data
   - Open foler "data"
   - Run the "Sample_Test_Data.sql" SQLScript using SQL Workbench

8. Run the project
   - Open browser and go to link: http://[hostname]:[port]/MAS-war
   
Initial User:
username: admin
password: Admin1@

## Contacts ##

If you have any questions regarding to our system please contact our system admin. Their contacts are listed below:
* Zhuang Weiming: weiming@u.nus.edu
* Liu Xin: a0119495@u.nus.edu
* Liu Chuning: a0119510@u.nus.edu
* Chen Tongtong: chentongtong@u.nus.edu
* Wei Bowen: weibowen@u.nus.edu