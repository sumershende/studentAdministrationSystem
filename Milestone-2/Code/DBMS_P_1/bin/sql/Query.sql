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