SELECT q_text,  FROM QUESTIONS_IN_EX qe, QUESTIONS q 
where qe.q_id=q.q_id and ex_id='' and qe.q_id=''

SELECT S.st_id, U.name, with_score, H.ex_id
					FROM Has_Solved H
					WHERE H.st_id = S.st_id and S.isGrad=1 and U.userid=S.userid and H.ex_id in 
								(select E.ex_id from Exercises E, Topics T
								where T.c_id = 'CSC540' and E.tp_id = T.tp_id )

SELECT  q_text, q_hint, q_del_soln, difficulty, m.tp_name
FROM QUESTIONS q, TOPICS t, MASTER_TOPICS m
WHERE q.tp_id=t.tp_id
and t.tp_id=m.tp_id
and t.c_id='CSC540'

SELECT ex_id, with_score
FROM HAS_SOLVED 
WHERE st_id=100007

select * from Enrolled_in


select * from questions
select * from TOPICS
select * from Users
SELECT c_id, c_name FROM courses C inner join Pusers U on C.prof_id=U.userid 
WHERE U.userid = 'kogan' 

OriginalSql = SELECT c_id, c_name FROM courses AS C inner join users U on C.prof_id=U.userid 
WHERE U.userid = ?;, Error Msg = ORA-00933: SQL command not properly ended

SELECT prof_id FROM Professor WHERE userid = 'kogan'
	
INSERT INTO Enrolled_In (c_id, st_id) VALUES ('CSC541', 10003);
select * from courses c inner join users u on c.prof_id=

insert into Enrolled_In (c_id, st_id) values ('CSC',10007)
SELECT prof_id FROM Students WHERE userid = ?

Select * from Enrolled_In where userid=?

SELECT prof_id FROM Professor WHERE userid='kogan'
Select * from courses
select * from HASTA
select * from Students 
select * from Courses
select * from Enrolled_In
select * from Topics where c_id = 'CSC440'

SELECT MT.tp_name, MT.tp_id FROM Master_Topics MT INNER JOIN Topics T ON MT.tp_id = T.tp_id WHERE T.c_id = 'CSC440'

where st_id=10006
select C.c_id, C.c_name,T.st_id from Courses C, HASTA T where C.c_id = T.c_id and T.st_id in (select st_id from HASTA)

SELECT DISTINCT E.st_id, U.name FROM Enrolled_In E, Students S, Users U
				WHERE E.st_id=S.st_id and S.userid=U.userid and c_id='CSC540'

insert into Hasta values('CSC541', 10001)
insert into Hasta values('CSC440', 10004)
delete from Hasta where st_id=10002
select DESCRIPTION, TRIGGER_BODY  from user_triggers
SHOW ERRORS TRIGGER TA_NOT_STUDENT

SELECT * FROM QUESTIONS_IN_EX qe, QUESTIONS q WHERE qe.q_id=q.q_id and ex_id=? and qe.q_id=?

SELECT *
FROM QUESTIONS q, MASTER_TOPICS t
WHERE q.tp_id=t.tp_id and q.tp_id = 4

select * from parameterized_questions

select CASE
           WHEN EXISTS (SELECT F.q_id FROM FIXED_QUESTIONS F WHERE F.q_id=Q.q_id ) THEN 0
           ELSE 1
        END AS question_type, q_id
FROM Questions Q where q_id=4
--- Professor and TA is able to view any course
--To edit an exercise, please enter its ID or press 0 to go back: 
SHOW ERRORS TRIGGER MaxStudentInClass
IF StudentsTotal > max THEN
    	raise_application_error(-20010,'ERROR: Class Max Size limit reached');
  	END IF;   
	select count(st_id) from Enrolled_In e inner join courses c on e.c_id=c.c_id
	group by e.c_id having e.c_id= 'CSC440';
	
	select * from courses where c_id='CSC440'
	Select * from Enrolled_in where c_id='CSC440'
	select * from students where st_id =10002
	select * from HASTA where c_id ='CSC541' and st_id=10002
	INSERT INTO Enrolled_In (C_ID, ST_ID) VALUES ('CSC440', 10005)
	delete from Enrolled_In where c_id='CSC440' and st_id=10001
	
	select * from questions where q_id=32
	
	insert into assign_attempt  values (1, 3, 32, 10007, null, null)
	truncate table assign_attempt
	select * from assign_attempt
	select * from has_solved wher
	select * from students
	
	select CASE WHEN EXISTS (SELECT F.q_id FROM FIXED_QUESTIONS F WHERE F.q_id=Q.q_id ) THEN 0
	ELSE 1 
						END AS question_type 
						FROM Questions Q where q_id=32
						
	select *
	from Exercises E, Topics T
	where T.c_id = 'CSC540' and E.tp_id = T.tp_id and 
	E.ex_id not in (select ex_id from Assign_Attempt where st_id = 10007)
	
	Select * from enrolled_In where st_id=10007
	
	SELECT H.with_score, H.submit_time, H.ex_id, J.pt_correct, J.pt_incorrect, J.num_questions
					FROM Has_Solved H ,(
					SELECT E.ex_id, pt_correct, pt_incorrect, num_questions 
					FROM Exercises E, Topics T 
					WHERE E.tp_id = T.tp_id and T.c_id = 'CSC540'
					)J WHERE H.st_id = 10007 AND H.ex_id = J.ex_id
	
			
					SELECT H.st_id, H.with_score, H.submit_time, J.ex_end_date, H.ex_id, J.pt_correct, J.pt_incorrect 
				FROM Has_Solved H inner join
				(SELECT E.ex_id, E.pt_correct, E.pt_incorrect, E.num_questions, E.ex_end_date 
				FROM Exercises E, Topics T
				WHERE E.tp_id = T.tp_id and T.c_id = 'CSC540'
				) J On H.ex_id = J.ex_id
				WHERE H.st_id = 10007  
	
				select * from Has_Solved where st_id=10007
				
				
				SELECT Q.q_text, Q.q_hint, A.is_correct, Q.q_del_soln, Q.q_id 
						FROM Questions Q, Questions_In_Ex QE, Assign_Attempt A 
						WHERE QE.ex_id=3 and Q.q_id = QE.q_id 
						and A.ex_id = 3 and A.st_id = 10007 and A.q_id = QE.q_ids
						
						
						select Q.q_text, Q.q_hint, is_correct, Q.q_del_soln, Q.q_id from Questions Q, Ouestions_In_Ex QE, Assign_Attempt A
								where QE.ex_id=3
								and Q.q_id = QE.q_id 
								and A.ex_id = 3 and A.st_id = 10007 and A.q_id = QE.q_id
								
								SELECT q_comb_num FROM (SELECT * FROM Param_Questions PQ WHERE PQ.q_id = 1
					ORDER BY dbms_random.value) where rownum <= 1
					
					SELECT q_inc_ans FROM (SELECT * FROM Param_Inc_Questions PI WHERE PI.q_id = 1
					and PI.q_comb_num = 1 ORDER BY dbms_random.value) where rownum <= 3