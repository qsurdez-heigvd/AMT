
    set client_min_messages = WARNING;

    alter table if exists app_user_role 
       drop constraint if exists fk_user_role__user;

    alter table if exists app_user_token 
       drop constraint if exists fk_token__user;

    alter table if exists review 
       drop constraint if exists fk_review__author;

    alter table if exists review 
       drop constraint if exists fk_review__wine;

    alter table if exists review_comment 
       drop constraint if exists fk_comment__author;

    alter table if exists review_comment 
       drop constraint if exists fk_comment__review;

    alter table if exists wine_variety 
       drop constraint if exists fk_wine_variety__wine;

    drop table if exists app_user cascade;

    drop table if exists app_user_role cascade;

    drop table if exists app_user_token cascade;

    drop table if exists review cascade;

    drop table if exists review_comment cascade;

    drop table if exists wine cascade;

    drop table if exists wine_variety cascade;
