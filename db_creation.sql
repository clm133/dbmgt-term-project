DROP TABLE Users CASCADE CONSTRAINTS;
DROP TABLE Friendships CASCADE CONSTRAINTS;
DROP TABLE Groups CASCADE CONSTRAINTS;
DROP TABLE Messages CASCADE CONSTRAINTS;
DROP TABLE Belongs_To CASCADE CONSTRAINTS;
DROP TABLE RECIPIENTS CASCADE CONSTRAINTS;


CREATE TABLE Users
(	userID	number PRIMARY KEY,
	fname	varchar2(32),
	mname	varchar2(32),
	lname	varchar2(32),
	email	varchar2(64), --some people might have long names->long emails
	DOB 	date,
	loggedIn	timestamp
);

CREATE TABLE Friendships
(	friend1	number,
	friend2 number,
	status	number(1), --this is the attribute for pending/approved represented as 0/1 respectively
	established date,
	CONSTRAINT friend1_FK_Users FOREIGN KEY (friend1) REFERENCES Users(userID),
	CONSTRAINT friend2_FK_Users FOREIGN KEY (friend2) REFERENCES Users(userID),
	CONSTRAINT Friendships_status_check CHECK (status BETWEEN 0 AND 1),
	CONSTRAINT FRIENDSHIPS_COMP_KEY PRIMARY KEY (friend1, friend2)
);

CREATE TABLE Groups
(
	groupID number PRIMARY KEY,
	name varchar(32),
	memLimit number,
	description varchar(100) --I set this to the same number of characters a message is constrained to
);

CREATE TABLE Belongs_To
(
	member number,
	groupId number,
	CONSTRAINT Belongs_To_FK_Users FOREIGN KEY (member) REFERENCES Users(userID),
	CONSTRAINT Belongs_To_FK_Groups FOREIGN KEY (groupID) REFERENCES Groups(groupID)
);

CREATE TABLE Messages
(
	msgID number PRIMARY KEY,
	sender number,
	subject varchar(32),
	content varchar(100),
	dateSent date,
	CONSTRAINT Messaages_Sender_FK_Users FOREIGN KEY (sender) REFERENCES Users(userID)
);

CREATE TABLE Recipients
(
	msgID number,
	recipient number,
	CONSTRAINT Recipients_FK_Messages FOREIGN KEY (msgID) REFERENCES Messages(msgID),
	CONSTRAINT Recipients_FK_Users FOREIGN KEY (recipient) REFERENCES Users(userID)
);

/* Lets set up some constraints! Go data integrity! */

/* Check Group Membership Limit */

	/*A function to check if group limit is reached*/
CREATE OR REPLACE FUNCTION group_limit_reached(gID NUMBER)
	RETURN NUMBER
	IS 
		total_membership NUMBER;
		group_limit NUMBER;
		is_reached NUMBER;
	BEGIN
		SELECT COUNT(*)
		INTO total_membership
		FROM Belongs_To
		WHERE groupID = gID;
		
		SELECT memLimit
		INTO total_membership
		FROM Groups
		WHERE groupID = gID;
		
		IF total_membership = groupLimit
			:is_reached := 1; --1 indicates group limit reached
		ELSE
			:is_reached := 0; --indicates group limit is not reached
		END IF;
		
		RETURN(is_reached);
	END;
/
	/*Actual Constraint*/
ALTER TABLE Belongs_To ADD CONSTRAINT belongs_to_limit_reached CHECK(group_limit_reached(groupID) <> 1);