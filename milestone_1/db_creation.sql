 DROP TABLE Users CASCADE CONSTRAINTS;
 DROP TABLE Friendships CASCADE CONSTRAINTS;
 DROP TABLE Groups CASCADE CONSTRAINTS;
 DROP TABLE Messages CASCADE CONSTRAINTS;
 DROP TABLE Belongs_To CASCADE CONSTRAINTS;
 DROP TABLE Recipients CASCADE CONSTRAINTS;
 DROP TRIGGER check_group_limit;
 DROP TRIGGER UPDATE_MEM_COUNT;
 DROP TRIGGER delete_from_groups;
 
 
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
 	name varchar2(32),
 	memLimit INTEGER,
 	memCount INTEGER,
 	description varchar2(100) --I set this to the same number of characters a message is constrained to
  );
 
 CREATE TABLE Belongs_To
 (
 	member number,
 	groupID number,
 	CONSTRAINT Belongs_Comp_Key PRIMARY KEY (member, groupID),
 	CONSTRAINT Belongs_To_FK_Users FOREIGN KEY (member) REFERENCES Users(userID),
 	CONSTRAINT Belongs_To_FK_Groups FOREIGN KEY (groupID) REFERENCES Groups(groupID)
 );
 
 CREATE TABLE Messages
 (
 	msgID number PRIMARY KEY,
 	sender number,
 	subject varchar2(32),
 	content varchar2(100),
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

--Check Group Membership Limit 

--A trigger to enforce group membership is kept within limit
  CREATE OR REPLACE TRIGGER check_group_limit
	BEFORE INSERT
	ON Belongs_To
	REFERENCING NEW AS NEW_ROW
	FOR EACH ROW
	DECLARE
		v_limit INTEGER;
		v_count INTEGER;
	BEGIN
		SELECT memLimit INTO v_limit FROM Groups
		WHERE GROUPID = :NEW_ROW.groupID;
		SELECT memCount INTO v_count FROM Groups
		WHERE GROUPID = :NEW_ROW.groupID;
		IF v_count >= v_limit THEN ROLLBACK;
		END IF;
	END;
	/

	CREATE OR REPLACE TRIGGER UPDATE_MEM_COUNT
		AFTER INSERT
		ON Belongs_To
		REFERENCING NEW AS NEW_ROW
		FOR EACH ROW
		DECLARE
			v_count INTEGER;
		BEGIN
			SELECT MEMCOUNT INTO v_count FROM Groups
			WHERE GROUPID = :NEW_ROW.groupID;

			UPDATE GROUPS SET MEMCOUNT= v_count + 1
			WHERE GROUPID = :NEW_ROW.groupID;
		END;
		/

	commit;
  
 
--A trigger for deleting users from groups
	CREATE OR REPLACE TRIGGER delete_from_groups
	   BEFORE DELETE
	   ON Users
	   REFERENCING NEW AS EX_USER
	   FOR EACH ROW

	DECLARE
	   -- variable declarations
	   userToDelete INTEGER;

	BEGIN
	   -- trigger code
	   
	   userToDelete := :EX_USER.userID;
	   
	   --update group membership
	   UPDATE 
		(SELECT Groups.memCount AS mc
		FROM Groups INNER JOIN Belongs_To ON Groups.groupID = Belongs_To.groupID
		WHERE Belongs_To.member = userToDelete) up
		SET up.mc -= 1;
	   
	   
	   --Delete User from Belongs_To table
	   DELETE FROM Belongs_To
	   WHERE member = userToDelete;
	   
	   

	END;
	/