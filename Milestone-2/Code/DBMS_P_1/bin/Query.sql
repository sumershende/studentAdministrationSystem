SELECT q_text,  FROM QUESTIONS_IN_EX qe, QUESTIONS q 
where qe.q_id=q.q_id and ex_id='' and qe.q_id=''


SELECT  q_text, q_hint, q_del_soln, difficulty, m.tp_name
FROM QUESTIONS q, TOPICS t, MASTER_TOPICS m
WHERE q.tp_id=t.tp_id
and t.tp_id=m.tp_id
and t.c_id='CSC540'

select * from questions

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
select * from enrolled_in
select * from Topics where c_id = 'CSC440'

SELECT MT.tp_name, MT.tp_id FROM Master_Topics MT INNER JOIN Topics T ON MT.tp_id = T.tp_id WHERE T.c_id = 'CSC440'

where st_id=10006
select C.c_id, C.c_name,T.st_id from Courses C, HASTA T where C.c_id = T.c_id and T.st_id in (select st_id from HASTA)