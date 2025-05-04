create table subscription_plans
(
    id             bigserial primary key,
    user_id        text not null,
    type           text      default 'FREE',
    price_in_cents int       default 0,
    created_at     timestamp default now(),
    updated_at     timestamp default now()
);

create table subscriptions
(
    id                   bigserial primary key,
    subscriber_id        text   not null,
    subscribed_on_id     text   not null,
    subscription_plan_id bigint not null references subscription_plans (id),
    created_at           timestamp default now(),
    expired_at           timestamp default now() + interval '1 month'
);