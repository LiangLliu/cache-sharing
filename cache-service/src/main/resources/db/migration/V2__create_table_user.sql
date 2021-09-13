create table if not exists `user`
(
    id                   bigint auto_increment primary key comment '主键',
    username             varchar(128)                        not null comment '用户名',
    password             varchar(128)                        not null comment '密码'
);

INSERT INTO demo.user (id, username, password) VALUES (1, '张三', '111111');
INSERT INTO demo.user (id, username, password) VALUES (2, '李四', '222222');
INSERT INTO demo.user (id, username, password) VALUES (3, '王五', '333333');
INSERT INTO demo.user (id, username, password) VALUES (4, '田六', '444444');