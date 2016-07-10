DECLARE 
	fname varchar2(32);
  mname varchar2(32);
  lname varchar2(32);
	email varchar2(64);
	DOB date;
	loggedIn timestamp;
  v_friend1 INTEGER;
  v_friend2 INTEGER;
  temp INTEGER;
  not_enough_rels BOOLEAN := TRUE;
  num_rels INTEGER := 0;
BEGIN

  -- DELETE PREVIOUS ROWS
  DELETE FROM USERS;
  DELETE FROM FRIENDSHIPS;

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
  END;
/

