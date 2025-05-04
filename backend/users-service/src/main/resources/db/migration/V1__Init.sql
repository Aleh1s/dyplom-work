create table users (
    id text primary key,
    username text not null unique ,
    email text not null unique ,
    given_name text not null ,
    family_name text not null ,
    bio text default '',
    avatar_url text
);

create table user_social_links (
    user_id text not null ,
    social_media_type text not null ,
    link text not null
);
