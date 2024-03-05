create table if not exists animal
(
    id bigint(19) not null primary key ,
    name varchar(45) ,
    description varchar(1000) ,
    type varchar(45) ,
    area varchar(145) ,
    tag varchar(45) ,
    url varchar(200) ,
    version int default 0 ,
    create_time datetime not null default current_timestamp ,
    update_time datetime not null default current_timestamp on update current_timestamp
);



