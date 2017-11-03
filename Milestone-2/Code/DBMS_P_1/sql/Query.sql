select * from Users
SELECT c_id, c_name FROM courses C inner join Pusers U on C.prof_id=U.userid 
WHERE U.userid = 'kogan' 

OriginalSql = SELECT c_id, c_name FROM courses AS C inner join users U on C.prof_id=U.userid 
WHERE U.userid = ?;, Error Msg = ORA-00933: SQL command not properly ended

SELECT prof_id FROM Professor WHERE userid = 'kogan'
	

select * from courses c inner join users u on c.prof_id=

SELECT prof_id FROM Students WHERE userid = ?

Select * from students where userid=?

SELECT prof_id FROM Professor WHERE userid='kogan'

select * from HASTA
select * from Students where st_id=10006
select C.c_id, C.c_name,T.st_id from Courses C, HASTA T where C.c_id = T.c_id and T.st_id in (select st_id from HASTA)