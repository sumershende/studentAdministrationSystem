drop table param_inc_questions;
drop table Has_Solved;
drop table Assign_Attempt;
drop table Param_Answers;
drop table Param_Questions;
drop table Questions_In_Ex;
drop table Fixed_Questions;
drop table Fixed_Inc_Answers;
drop table Questions;
drop table Topics;
drop table Enrolled_In;
drop table HasTA;
drop table Courses;
drop table Professor;
drop table Exercises;
drop table Students;
drop table Master_Topics;
drop table Users ;
drop table Roles;

create table Roles(role varchar2(50), roleid int primary key);
create table Users(userid varchar2(30) primary key, password varchar(100), name varchar2(100),roleid int, FOREIGN KEY(roleid) REFERENCES Roles(roleid));
create table Master_Topics(tp_id int primary key , tp_name varchar2(100));
create table Professor(prof_id int primary key, userid varchar2(30), FOREIGN KEY (userid) REFERENCES Users(userid));
create table Courses(c_id varchar2(10) primary key, c_name varchar2(30) NOT NULL, c_start_date date, c_end_date date, prof_id int, levelGrad NUMBER(1,0), max_students int, FOREIGN KEY (prof_id) REFERENCES Professor (prof_id));
create table Topics(c_id varchar2(10), tp_id int, FOREIGN KEY (c_id) REFERENCES Courses (c_id) ON DELETE CASCADE, FOREIGN KEY (tp_id) REFERENCES Master_Topics (tp_id) ON DELETE CASCADE, UNIQUE(c_id, tp_id));
create table Students(st_id int primary key, userid varchar2(30), isGrad NUMBER(1,0) NOT NULL, FOREIGN KEY (userid) REFERENCES Users(userid));
create table Enrolled_In(c_id varchar2(10), st_id int, FOREIGN KEY (c_id) REFERENCES Courses (c_id)  ON DELETE CASCADE, FOREIGN KEY (st_id) REFERENCES Students(st_id)  ON DELETE CASCADE, UNIQUE(c_id, st_id));
create table HasTA(c_id varchar2(10), st_id int, FOREIGN KEY (c_id) REFERENCES Courses (c_id) ON DELETE CASCADE, FOREIGN KEY (st_id) REFERENCES Students(st_id) ON DELETE CASCADE, UNIQUE(c_id, st_id));
create table Questions(tp_id int, q_id int primary key, q_text varchar2(200) NOT NULL, q_hint varchar2(100), q_del_soln varchar(200), difficulty int, FOREIGN KEY (tp_id) REFERENCES Master_Topics (tp_id) ON DELETE CASCADE);
create table Fixed_Questions(q_id int,q_ans varchar(100)NOT NULL, FOREIGN KEY (q_id) REFERENCES Questions(q_id) ON DELETE CASCADE);
create table Fixed_Inc_Answers(q_id int, q_inc_answers varchar(100),FOREIGN KEY (q_id) REFERENCES Questions(q_id) ON DELETE CASCADE);
create table Param_Questions(q_id int, q_par_num int NOT NULL, q_comb_num int NOT NULL, q_param_value varchar2(50), FOREIGN KEY (q_id) REFERENCES Questions(q_id) ON DELETE CASCADE, UNIQUE(q_id, q_par_num, q_comb_num, q_param_value));
create table Param_Answers(q_id int, q_comb_num int, q_ans varchar2(100),  FOREIGN KEY (q_id) REFERENCES Questions(q_id) ON DELETE CASCADE);
create table Exercises(ex_id int primary key, ex_name varchar2(500)NOT NULL,ex_mode varchar2(20)NOT NULL, ex_start_date date NOT NULL, ex_end_date date NOT NULL, num_questions int NOT NULL, num_retries int NOT NULL, policy varchar2(20) NOT NULL, tp_id int, pt_correct int, pt_incorrect int, FOREIGN KEY (tp_id) REFERENCES Master_Topics(tp_id) ON DELETE CASCADE);
create table Questions_In_Ex(ex_id int,q_id int, FOREIGN KEY (q_id) REFERENCES Questions(q_id),FOREIGN KEY (ex_id) REFERENCES Exercises(ex_id));
create table Assign_Attempt(attempt_num int NOT NULL, ex_id int, q_id int, st_id int, is_correct NUMBER(1,0), q_comb_num int, FOREIGN KEY (ex_id) REFERENCES Exercises(ex_id), FOREIGN KEY (q_id) REFERENCES Questions(q_id) ON DELETE CASCADE, FOREIGN KEY (st_id) REFERENCES Students(st_id) ON DELETE CASCADE, UNIQUE(attempt_num, ex_id, q_id, st_id));
create table Has_Solved(st_id int, ex_id int, with_score NUMBER(7,2), submit_time date NOT NULL, FOREIGN KEY (st_id) REFERENCES Students(st_id) ON DELETE CASCADE, FOREIGN KEY (ex_id) REFERENCES Exercises(ex_id) ON DELETE CASCADE);
create table param_inc_questions(q_id int, q_comb_num int, q_inc_ans varchar2(200), FOREIGN KEY (q_id) REFERENCES Questions(q_id) ON DELETE CASCADE);

--------####### Constraints ######------------------------
ALTER TABLE Courses
DROP CONSTRAINT CheckEndLaterThanStart;

ALTER TABLE Courses  
ADD CONSTRAINT CheckEndLaterThanStart
CHECK (c_end_date >= c_start_date);

ALTER TABLE Exercises
DROP CONSTRAINT CheckEndDate;

ALTER TABLE Exercises  
ADD CONSTRAINT CheckEndDate
CHECK (ex_end_date >= ex_start_date);

--DROP FUNCTION CheckTAasGradStudent;
--CREATE FUNCTION CheckTAasGradStudent(student_id IN NUMBER)
--return NUMBER DETERMINISTIC
--Is ret NUMBER;
--begin
--    select count(*) 
--    into ret
--    from Students where st_id=student_id and isGrad=1;
--    return (ret);
--end CheckTAasGradStudent;
--
--ALTER TABLE HASTA
--DROP CONSTRAINT GradStudentAsTA;
--
--ALTER TABLE HASTA
--ADD (taAsGrad NUMBER GENERATED ALWAYS AS (CheckTAasGradStudent(st_id)) VIRTUAL);
--
--ALTER table HASTA 
--ADD CONSTRAINT GradStudentAsTA CHECK(taAsGrad=1);
--
--DROP function TAnotStudent;
--CREATE FUNCTION TAnotStudent(st_id IN NUMBER, c_id IN VARCHAR2)
--return NUMBER DETERMINISTIC
--Is ret NUMBER;
--begin
--	select count(*)
--	into ret
--	from Enrolled_In
--	where c_id=c_id and st_id=st_id;
--	return (ret);
--end TAnotStudent;
--
--ALTER TABLE HASTA
--ADD (taStudent NUMBER GENERATED ALWAYS AS (TAnotStudent(c_id, st_id)) VIRTUAL);
--
--ALTER TABLE HASTA
--DROP CONSTRAINT TAStudent;
--
--ALTER table HASTA 
--ADD CONSTRAINT TAStudent
--CHECK (taStudent=0);
--
--ALTER TABLE Enrolled_In
--ADD (taStudent NUMBER GENERATED ALWAYS AS (TAnotStudent(c_id, st_id)) VIRTUAL);
--
--ALTER TABLE Enrolled_In
--DROP CONSTRAINT TAnotStudent;
--
--ALTER table Enrolled_In 
--ADD CONSTRAINT TAnotStudent
--CHECK (taStudent=0);

DROP TRIGGER ta_not_student;
CREATE OR REPLACE TRIGGER ta_not_student
BEFORE INSERT OR UPDATE
   ON HASTA
   FOR EACH ROW
DECLARE
   ret NUMBER;
BEGIN
	select count(*) into ret
	from Enrolled_In
	where c_id = :new.c_id and st_id = :new.st_id;	
    IF ret > 0 THEN
    	raise_application_error(-20010,'ERROR: Enrolled in class. So, cannot become a TA');
  	END IF;      
END;

DROP TRIGGER ta_not_student_reverse;
CREATE OR REPLACE TRIGGER ta_not_student_reverse
BEFORE INSERT OR UPDATE
   ON Enrolled_In
   FOR EACH ROW
DECLARE
   ret NUMBER;
BEGIN
	select count(*) into ret
	from HASTA
	where c_id = :new.c_id and st_id = :new.st_id;	
    IF ret > 0 THEN
    	raise_application_error(-20010,'ERROR: Student cannot be enrolled in class as Student is a TA');
  	END IF;      
END;

DROP TRIGGER ta_grad_student;
CREATE OR REPLACE TRIGGER ta_grad_student
BEFORE INSERT OR UPDATE
   ON HASTA
   FOR EACH ROW
DECLARE
   ret NUMBER;
BEGIN
	select count(*) into ret
	from Students
	where isgrad=0 and st_id = :new.st_id;	
    IF ret > 0 THEN
    	raise_application_error(-20000,'ERROR: Not a grad student. So, cannot become a TA');
  	END IF;      
END;

DROP TRIGGER MaxStudentInClass;
CREATE OR REPLACE TRIGGER MaxStudentInClass
BEFORE INSERT OR UPDATE
   ON Enrolled_In
   FOR EACH ROW
DECLARE
   StudentsTotal NUMBER;
   maxStudent NUMBER;
BEGIN
	select count(st_id) into StudentsTotal from Enrolled_In e , courses c
	where e.c_id=c.c_id
	group by e.c_id having e.c_id = :new.c_id;
	
	select max_students into maxStudent from courses where c_id= :new.c_id;
	IF StudentsTotal >= maxStudent THEN
    	raise_application_error(-20010,'ERROR: Class Max Size limit reached');
  	END IF;      
END;
