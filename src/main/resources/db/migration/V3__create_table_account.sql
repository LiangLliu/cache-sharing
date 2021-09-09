create table if not exists `account`
(
    id                   bigint auto_increment primary key comment '主键',
    username             varchar(128)                        not null comment '用户名',
    password             varchar(128)                        not null comment '密码',
    created_time         timestamp default CURRENT_TIMESTAMP not null,
    updated_time         timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
);