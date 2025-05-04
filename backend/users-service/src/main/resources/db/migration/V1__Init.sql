create table users (
    id bigint primary key,
    username text not null unique ,
    email text not null unique ,
    given_name text not null ,
    family_name text not null ,
    bio text default '',
    avatar_url text
);

create sequence users_id_sequence increment 1 start 1;

create table user_social_links (
    user_id bigint not null ,
    social_media_type text not null ,
    link text not null
);
