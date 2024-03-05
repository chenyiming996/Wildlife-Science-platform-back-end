create table if not exists user
(
    id bigint(19) not null primary key ,
    openid varchar(145) ,
    nick_name varchar(200) ,
    avatar_url varchar(400) ,
    role varchar(45) ,
    integral int ,
    games_num int ,
    create_time datetime not null default current_timestamp ,
    update_time datetime not null default current_timestamp on update current_timestamp
);




