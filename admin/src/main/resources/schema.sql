create table if not exists admin
(
    id bigint(19) not null primary key ,
    username varchar(45) ,
    password varchar(200) ,
    nickname varchar(45) ,
    role varchar(45) ,
    create_time datetime not null default current_timestamp ,
    update_time datetime not null default current_timestamp on update current_timestamp
);



