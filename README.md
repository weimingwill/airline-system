# README #

## Project Information ##

* Project: Merlion Airline System
* Version: 1.0
* Release Date: 14 Oct, 2015
* Description:
This repository contains the source code of merlion airline systems developed by Team ES02 from Kent Ridge Technology. The first system release covers common infrastructure, Airline Planning System and Airline Inventory system. 



## Setup Guidelines ##
###The following Setup guildlines assume that users have installed the latest version of:###
* Netbeans IDE with GlassFish Server 4.1 (https://netbeans.org/downloads/)
* MySQL Server (http://dev.mysql.com/downloads/mysql/)
* MySQL Connector (http://dev.mysql.com/downloads/connector/j/)
* MySQL Workbench (http://dev.mysql.com/downloads/workbench/)
* Java JDK 8 (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Java EE 7 (http://www.oracle.com/technetwork/java/javaee/downloads/index.html)


#### Setup MYSQL Connection ####
1. Open project in Netbeans IDE
2. In Netbeans IDE, go to the "Service" tab
3. Right Click "Database" and choose "New Connection"
4. Choose "MySQL (Connector/J driver)" for "Driver" and locate and add the downloaded MySQL Connector jar file
5. Click "Next"
6. Name "Database" as "mas", fill in the correct hostname and port for your MySQL Connection
7.Fill in the correct username and password for your MySQL Server Connection
8.Click "Finish"
* (Note: a new database connection like "jdbc:mysql://localhost:8889/mas?zeroDateTimeBehavior=convertToNull [root on Default schema]" should appear under "Database")

#### Setup JDBC Resource and JDBC Connection Pool ####
1. Go to the enterprise bean project under the "Projects" tab of Netbeans IDE
2. Right click and choose "New" -> "Other" -> "GlassFish" -> "JDBC Resource"
3. Click "Next"
4. Choose the newly added connection pool under "Create New JDBC Connection Pool"
5. Change JNDI name to "jdbc/mas"
6. Click "Next"
7. Do not change anything for "Additional Properties" and click "Next"
8. Name "JDBC Connection Pool Name" as "masConnectionPool"
9. Choose the newly added "mas" MySQL Connection under "Extract from Existing Connection"
10. Click "Next"
11. Do not change anything for "Add Conncetion Pool Properties" and click "Next"
12. Click "Finish"
* Note: a file with name "sun-resources.xml" or "glassfish-resources.xml" should appear under folder "Server Resources")###

#### Setup JDBC Persistence Unit ####
1. Go to the enterprise bean project under the "Projects" tab of Netbeans IDE
2. Right click and choose "New" -> "Other" -> "Persistence" -> "Persistence Unit"
3. Click "Next"
4. Choose the "jdbc/mas" under "Data Source" and Click Finish
(Note: a file with name "persistence.xml" should appear under foler "Configuration Files")

#### Setup JMS Resource ####
1. In Netbeans IDE, go to the service tab
2. Find GlassFish Server 4.1 Under Servers and Start GlassFish Server
3. Right Click GlassFish Server and Choose "View Domain Admin Console"
4. In the left panel of admin console, Choose JMS Resources
5. Choose Connection Factories and add a new Connection Factory with the following properties:
      - JNDI Name: jms/topicInternalComConnectionFactory
      - Resource Type: javax.jms.TopicConnectionFactory
     - Description: Topic Internal Com Connection Factory
6. Choose Destination Resources and add a new Destination Resource with the following properties:
      - JNDI Name: jms/topicInternalCom
     - Physical Destination Name: PhysicalTopic
     - Resource Type: javax.jms.Topic
     - Description: Topic Internal Com
7. Close the admin console

#### Clean and Build Project ####
1. Right click MAS project and choose "Clean and Build"

#### Deploy the Project ####
1. Right click MAS project and choose "Deploy"

#### Insert Default/Test Data ####
1. Open foler "data"
2. Run the "Sample_Test_Data.sql" SQLScript using SQL Workbench

#### Run the project ####
1. Open browser and go to link: http://[hostname]:[port]/MAS-war
   
#### Initial User: ####

* username: admin
* password: Admin1@

## Contacts ##

If you have any questions regarding to our system please contact our system admin. Their contacts are listed below:

* Zhuang Weiming: weiming@u.nus.edu

* Liu Xin: a0119495@u.nus.edu

* Liu Chuning: a0119510@u.nus.edu

* Chen Tongtong: chentongtong@u.nus.edu

* Wei Bowen: weibowen@u.nus.edu