select q_text, q_hint from Questions Q, Questions_In_Ex E where E.q_id=Q.q_id and E.ex_id=1;

select q_text, q_hint from Questions Q, Questions_In_Ex E where E.q_id=Q.q_id and E.ex_id=1