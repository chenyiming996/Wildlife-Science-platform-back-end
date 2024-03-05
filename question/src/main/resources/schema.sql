create table if not exists question
(
    id bigint(19) not null primary key ,
    description varchar(450) ,
    answer varchar(45) ,
    a varchar(245) ,
    b varchar(245) ,
    c varchar(245) ,
    d varchar(245) ,
    num int(10) ,
    right_num int(10) ,
    rate double ,
    type varchar(45) ,
    create_time datetime not null default current_timestamp ,
    update_time datetime not null default current_timestamp on update current_timestamp
);

create table if not exists practice
(
    id bigint(19) not null primary key ,
    user_id bigint(19),
    question_id bigint(19),
    type varchar(45),
    answer boolean,
    create_time datetime not null default current_timestamp,
    index (user_id),
    index (question_id)
);



