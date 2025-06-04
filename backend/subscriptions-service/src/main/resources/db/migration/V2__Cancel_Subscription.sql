alter table subscriptions add column is_cancelled boolean default false;
update subscriptions set is_cancelled = false where True;