-- device_tokens: stores FCM/APNs registration tokens per user/device for push notifications.

create table public.device_tokens (
  id            uuid primary key default gen_random_uuid(),
  user_id       uuid not null references public.profiles(id) on delete cascade,
  token         text not null unique,
  platform      text not null check (platform in ('android','ios','web')),
  last_seen_at  timestamptz not null default now(),
  created_at    timestamptz not null default now(),
  updated_at    timestamptz not null default now()
);

create index ix_device_tokens_user on public.device_tokens(user_id);

create trigger t_device_tokens_updated before update on public.device_tokens
  for each row execute procedure extensions.moddatetime(updated_at);

alter table public.device_tokens enable row level security;

create policy dt_select on public.device_tokens
  for select using (auth.uid() = user_id);

create policy dt_insert on public.device_tokens
  for insert with check (auth.uid() = user_id);

create policy dt_update on public.device_tokens
  for update using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy dt_delete on public.device_tokens
  for delete using (auth.uid() = user_id);
