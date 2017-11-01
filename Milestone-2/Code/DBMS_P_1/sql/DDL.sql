
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

create table Master_Topics(tp_id int primary key , tp_name varchar2(100));
create table Professor(prof_id int primary key, prof_name varchar2(30) NOT NULL);
create table Courses(c_id varchar2(10) primary key, c_name varchar2(30)NOT NULL, c_start_date date, c_end_date date, prof_id int, levelGrad NUMBER(1,0),max_students int,FOREIGN KEY (prof_id) REFERENCES Professor (prof_id));
create table Topics(tp_id int, c_id varchar2(10), FOREIGN KEY (c_id) REFERENCES Courses (c_id), FOREIGN KEY (tp_id) REFERENCES Master_Topics (tp_id));
create table Students(st_id int primary key, st_name varchar2(30) NOT NULL, isGrad NUMBER(1,0) NOT NULL);
create table Enrolled_In(c_id varchar2(10), st_id int, FOREIGN KEY (c_id) REFERENCES Courses (c_id), FOREIGN KEY (st_id) REFERENCES Students(st_id) );
create table HasTA(c_id varchar2(10), st_id int, FOREIGN KEY (c_id) REFERENCES Courses (c_id), FOREIGN KEY (st_id) REFERENCES Students(st_id));
create table Questions(tp_id int,q_id int primary key, q_text varchar2(200) NOT NULL, q_hint varchar2(100), q_del_soln varchar(200), FOREIGN KEY (tp_id) REFERENCES Master_Topics (tp_id));
create table Fixed_Questions(q_id int,q_ans varchar(100)NOT NULL, FOREIGN KEY (q_id) REFERENCES Questions(q_id));
create table Fixed_Inc_Answers(q_id int, q_inc_answers varchar(100),FOREIGN KEY (q_id) REFERENCES Questions(q_id));
create table Param_Questions(q_id int, q_par_num int NOT NULL, q_comb_num int NOT NULL, q_param_value varchar2(50), FOREIGN KEY (q_id) REFERENCES Questions(q_id));
create table Param_Answers(q_id int, q_comb_num int, q_ans varchar2(100));
create table Exercises(ex_id int primary key, ex_name varchar2(30)NOT NULL,ex_mode varchar2(20)NOT NULL, ex_start_date date NOT NULL, ex_end_date date NOT NULL, num_questions int NOT NULL, num_retries int NOT NULL, policy varchar2(20) NOT NULL);
create table Questions_In_Ex(ex_id int,q_id int, FOREIGN KEY (q_id) REFERENCES Questions(q_id),FOREIGN KEY (ex_id) REFERENCES Exercises(ex_id));
create table Assign_Attempt(attempt_num int NOT NULL, ex_id int, q_id int, st_id int, is_correct NUMBER(1,0), q_comb_num int, FOREIGN KEY (ex_id) REFERENCES Exercises(ex_id), FOREIGN KEY (q_id) REFERENCES Questions(q_id), FOREIGN KEY (st_id) REFERENCES Students(st_id));
create table Has_Solved(st_id int, ex_id int, with_score int, submit_time date NOT NULL, FOREIGN KEY (st_id) REFERENCES Students(st_id), FOREIGN KEY (ex_id) REFERENCES Exercises(ex_id));
create table Roles(roleid int primary key, role varchar2(50));
create table Users(userid varchar2(30), password varchar(100),roleid int, firstname varchar2(100), lastname varchar2(100), CONSTRAINT login PRIMARY KEY (userid, password), FOREIGN KEY(roleid) REFERENCES Roles(roleid));

--------####### Constraints ######------------------------
ALTER TABLE Courses
DROP CONSTRAINT CheckEndLaterThanStart

ALTER TABLE Courses  
ADD CONSTRAINT CheckEndLaterThanStart
CHECK (c_end_date >= c_start_date)

DROP FUNCTION CheckTAasGradStudent;
CREATE FUNCTION CheckTAasGradStudent(student_id IN NUMBER)
return number DETERMINISTIC
Is ret number;
begin
    select count(*) 
    into ret
    from Students where st_id=student_id and isGrad=1;
    return (ret);
end CheckTAasGradStudent;

ALTER TABLE HASTA
DROP CONSTRAINT GradStudentAsTA;

ALTER TABLE HASTA
ADD (taAsGrad NUMBER GENERATED ALWAYS AS (CheckTAasGradStudent(st_id)) VIRTUAL);

ALTER table HASTA 
ADD CONSTRAINT GradStudentAsTA CHECK(taAsGrad=1);

DROP function TAnotStudent;
CREATE FUNCTION TAnotStudent(st_id IN NUMBER, c_id IN VARCHAR2)
return number DETERMINISTIC
Is ret number;
begin
	select count(*)
	into ret
	from Enrolled_In
	where c_id=c_id and st_id=st_id;
	return (ret);
end TAnotStudent;

ALTER TABLE HASTA
ADD (taStudent NUMBER GENERATED ALWAYS AS (TAnotStudent(st_id, c_id)) VIRTUAL);

ALTER TABLE HASTA
DROP CONSTRAINT TAStudent;

ALTER table HASTA 
ADD CONSTRAINT TAStudent
CHECK (taStudent=0);

ALTER TABLE Enrolled_In
ADD (taStudent NUMBER GENERATED ALWAYS AS (TAnotStudent(st_id, c_id)) VIRTUAL);

ALTER TABLE Enrolled_In
DROP CONSTRAINT TAnotStudent;

ALTER table Enrolled_In 
ADD CONSTRAINT TAnotStudent
CHECK (taStudent=0);

