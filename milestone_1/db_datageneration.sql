DECLARE 
	--User Vars
	fname varchar2(32);
	mname varchar2(32);
	lname varchar2(32);
	email varchar2(64);
	DOB date;
	loggedIn timestamp;
	
	--Friend Vars
	v_friend1 INTEGER;
	v_friend2 INTEGER;
	temp INTEGER;
	not_enough_rels BOOLEAN := TRUE;
	num_rels INTEGER := 0;
	
	--Group Vars
	gname varchar2(32);
	ml number;
	gdesc varchar2(100);
	
	--Belongs_To VARSAMP
	belongsID number;
	belongsML INTEGER;
	belongsExists BOOLEAN;
  
  --messages/recipients variables
  TYPE LIST_OF_IDS_TYPE IS TABLE OF USERS.USERID%TYPE;
  list_of_ids LIST_OF_IDS_TYPE := LIST_OF_IDS_TYPE();
	num_recipients INTEGER := 0;
	v_sender USERS.USERID%TYPE;
  v_recipient USERS.USERID%TYPE;
BEGIN

  -- DELETE PREVIOUS ROWS
  DELETE FROM RECIPIENTS;
  DELETE FROM MESSAGES;
  DELETE FROM Belongs_To;
  DELETE FROM FRIENDSHIPS;
  DELETE FROM Groups;
  DELETE FROM USERS;

  -- GENERATE RANDOM USERS
	FOR i in 1..100 LOOP
		fname := 'first' || i;
		mname := 'middle' || i;
		lname := 'last' || i;
		email := fname || lname || '@pitt.edu';
		SELECT TO_DATE(TRUNC(DBMS_RANDOM.VALUE(TO_CHAR(DATE '2015-01-01','J'),TO_CHAR(DATE '2016-6-30','J'))),'J') 
                   INTO loggedIn FROM DUAL;
		SELECT TO_DATE(TRUNC(DBMS_RANDOM.VALUE(TO_CHAR(DATE '1980-01-01','J'),TO_CHAR(DATE '2000-3-31','J'))),'J') 
                   INTO DOB FROM DUAL;
		INSERT INTO USERS VALUES(i, fname, mname, lname, email, SYSDATE, SYSDATE);
	END LOOP;
  commit;
  
  -- GENREATE RANDOM RELATIONSHIPS
  
  WHILE not_enough_rels LOOP
    SELECT FLOOR(DBMS_RANDOM.VALUE(1, 100)) INTO v_friend1 FROM DUAL;
    SELECT FLOOR(DBMS_RANDOM.VALUE(1, 100)) INTO v_friend2 FROM DUAL;

    CASE WHEN v_friend1 = v_friend2 AND v_friend1 < 100 THEN
      v_friend1 := v_friend1 + 1;
    WHEN v_friend1 = v_friend2 AND v_friend2 = 100 THEN
      v_friend2 := v_friend2 - 1;
    ELSE
      v_friend1 := v_friend1;
    END CASE;
    
    IF v_friend1 > v_friend2 THEN
      temp := v_friend2;
      v_friend2 := v_friend1;
      v_friend1 := temp;
    END IF;
    
    INSERT INTO FRIENDSHIPS COLUMNS (FRIEND1, FRIEND2, STATUS, ESTABLISHED)
    SELECT v_friend1 , v_friend2 , 1 , SYSDATE FROM DUAL
    WHERE NOT EXISTS ( 
      SELECT * FROM FRIENDSHIPS 
      WHERE FRIEND1 = v_friend1 AND FRIEND2 = v_friend2 
    );

    SELECT COUNT(*) INTO num_rels FROM FRIENDSHIPS;
    IF num_rels >= 200 THEN 
      not_enough_rels := FALSE;
    END IF;
  END LOOP;
  
  commit;
  
  -- GENREATE RANDOM GROUPS
  FOR i in 1..10 LOOP
	gdesc := 'This is a group. its number is ';
	gname := 'group' || i;
	gdesc := gdesc || i || '. group number ' || i || ' is the best!';
	SELECT FLOOR(DBMS_RANDOM.VALUE(1, 100)) INTO ml FROM DUAL; -- I kept the random number generator at 1-100 because I really like that there's a chance there will be a 1 person group and that makes me laugh
	INSERT INTO Groups VALUES(i, gname, ml, 0, gdesc);
  END LOOP;
  commit;
  
  -- GENERATE RANDOM BELONGS_TO
  not_enough_rels := TRUE;
  num_rels := 0;
  
  FOR i in 1..10 LOOP --for each group id (1-10)
	SELECT memLimit INTO belongsML FROM Groups WHERE groupID = i;
	WHILE not_enough_rels LOOP
		belongsExists := TRUE;
		WHILE belongsExists LOOP
			SELECT FLOOR(DBMS_RANDOM.VALUE(1, 100)) INTO belongsID FROM DUAL; --generate random userID
			SELECT COUNT(*) INTO temp FROM Belongs_To WHERE member = belongsID AND groupID = i; -- check if there exists a relation like this already generated
			belongsExists := temp = 1; --if a row in the belongs_to table already doesn't already exist, this will be a 0, creating a false and breaking the loop
		END LOOP;
		INSERT INTO Belongs_To VALUES(belongsID, i);
		commit;
		SELECT COUNT(*) INTO num_rels FROM Belongs_To WHERE groupID = i;
		IF num_rels = belongsML OR num_rels >= 15 THEN --if we've met group capactiy or got at least 15 members in a group, exit loop
		  not_enough_rels := FALSE;
		END IF;
	END LOOP;
  END LOOP;
  
  
  --GENERATE 300 MESSAGES WITH RECIPIENTS
  FOR i IN 1..300 LOOP
    SELECT FLOOR(DBMS_RANDOM.VALUE(1,100)) INTO v_sender FROM DUAL;
    SELECT FLOOR(DBMS_RANDOM.VALUE(1,5)) INTO num_recipients FROM DUAL;
    list_of_ids.EXTEND;
    FOR j IN 1..num_recipients LOOP
      BEGIN
      SELECT FLOOR(DBMS_RANDOM.VALUE(1, 100)) INTO v_recipient FROM DUAL;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_recipient := v_sender;
      END;
      IF v_recipient = v_sender THEN CONTINUE;
      END IF;
      list_of_ids.EXTEND;
      list_of_ids(list_of_ids.LAST) := v_recipient;
    END LOOP;
    
    INSERT INTO MESSAGES VALUES(i, v_sender, 'subject' || i, 'content' || i, SYSDATE);
    
    FOR j IN 1..list_of_ids.COUNT LOOP
      INSERT INTO RECIPIENTS SELECT i, list_of_ids(j) 
      FROM DUAL WHERE list_of_ids(j) IS NOT NULL;
    END LOOP;
    
    list_of_ids := LIST_OF_IDS_TYPE();
  END LOOP;
  
  END;
/

