create table subscription_plans (
    id bigserial primary key ,
    user_id text not null ,
    type text default 'FREE',
    price_in_cents int default 0,
    created_at timestamp default now(),
    updated_at timestamp default now()
)