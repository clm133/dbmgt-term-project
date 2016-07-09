DECLARE 
	fname varchar2(32);
  mname varchar2(32);
  lname varchar2(32);
	email varchar2(64);
	DOB date;
	loggedIn timestamp;
BEGIN
	FOR i in 1..100 LOOP
		fname := 'first' || i;
    mname := 'middle' || i;
    lname := 'last' || i;
		email := fname || lname || '@pitt.edu';
		SELECT TO_DATE(TRUNC(DBMS_RANDOM.VALUE(TO_CHAR(DATE '2015-01-01','J'),TO_CHAR(DATE '2016-6-30','J'))),'J') 
                   INTO loggedIn FROM DUAL;
		SELECT TO_DATE(TRUNC(DBMS_RANDOM.VALUE(TO_CHAR(DATE '1980-01-01','J'),TO_CHAR(DATE '2000-3-31','J'))),'J') 
                   INTO DOB FROM DUAL;
		INSERT INTO USERS VALUES(i, fname, mname, lname, email, dob, loggedIn);
	END LOOP;
END;
/

