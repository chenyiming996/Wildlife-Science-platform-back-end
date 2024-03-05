create table if not exists helpline
(
    id bigint(19) not null primary key ,
    province varchar(45) ,
    email varchar(45) ,
    phone varchar(45) ,
    fax varchar(45) ,
    address varchar(150) ,
    code varchar(45) ,
    first_index varchar(45) ,
    version int default 0 ,
    create_time datetime not null default current_timestamp ,
    update_time datetime not null default current_timestamp on update current_timestamp
);

create table if not exists news
(
    id bigint(19) not null primary key ,
    tag varchar(45) ,
    url varchar(200) ,
    title varchar(100) ,
    content varchar(5000) ,
    origin varchar(150) ,
    version int default 0 ,
    create_time datetime not null default current_timestamp ,
    update_time datetime not null default current_timestamp on update current_timestamp
    );


