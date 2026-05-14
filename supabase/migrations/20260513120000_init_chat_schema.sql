-- Private chat rooms: schema, triggers, indexes, RLS, realtime authorization.
-- Apply via the Supabase SQL editor or `supabase db push`.

create extension if not exists moddatetime schema extensions;

-- ============================================================ tables

create table public.profiles (
  id           uuid primary key references auth.users(id) on delete cascade,
  email        text not null,
  display_name text not null,
  avatar_url   text,
  created_at   timestamptz not null default now(),
  updated_at   timestamptz not null default now()
);

create table public.rooms (
  id         uuid primary key default gen_random_uuid(),
  name       text not null check (char_length(name) between 1 and 120),
  created_by uuid references public.profiles(id) on delete set null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table public.room_members (
  room_id   uuid not null references public.rooms(id)    on delete cascade,
  user_id   uuid not null references public.profiles(id) on delete cascade,
  role      text not null default 'member' check (role in ('owner','admin','member')),
  joined_at timestamptz not null default now(),
  primary key (room_id, user_id)
);

create table public.messages (
  id         uuid primary key default gen_random_uuid(),
  room_id    uuid not null references public.rooms(id)    on delete cascade,
  sender_id  uuid          references public.profiles(id) on delete set null,
  content    text not null,                              -- opaque; ciphertext-ready for future E2EE
  created_at timestamptz not null default now(),
  edited_at  timestamptz,
  deleted_at timestamptz
);

-- ============================================================ updated_at

create trigger t_profiles_updated before update on public.profiles
  for each row execute procedure extensions.moddatetime(updated_at);
create trigger t_rooms_updated before update on public.rooms
  for each row execute procedure extensions.moddatetime(updated_at);

-- ============================================================ bootstrap triggers

create or replace function public.handle_new_user()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
  insert into public.profiles (id, email, display_name)
  values (new.id, new.email,
          coalesce(nullif(new.raw_user_meta_data->>'display_name',''), split_part(new.email,'@',1)));
  return new;
end $$;

create trigger on_auth_user_created after insert on auth.users
  for each row execute function public.handle_new_user();

-- Room creator becomes the first member (owner). Runs as definer to bypass RLS during bootstrap.
create or replace function public.handle_new_room()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
  insert into public.room_members (room_id, user_id, role)
  values (new.id, new.created_by, 'owner');
  return new;
end $$;

create trigger on_room_created after insert on public.rooms
  for each row execute function public.handle_new_room();

-- ============================================================ indexes

create unique index ux_profiles_email       on public.profiles (lower(email));
create index        ix_rooms_created_by      on public.rooms (created_by);
create index        ix_room_members_user     on public.room_members (user_id);   -- PK already covers (room_id, …)
create index        ix_messages_room_created on public.messages (room_id, created_at desc, id desc);
create index        ix_messages_sender       on public.messages (sender_id);

-- ============================================================ authorization helpers
-- SECURITY DEFINER => bypass RLS => no recursion when used inside room_members policies.

create or replace function public.is_room_member(p_room_id uuid)
returns boolean language sql security definer set search_path = '' stable as $$
  select exists(select 1 from public.room_members where room_id = p_room_id and user_id = auth.uid());
$$;

create or replace function public.is_room_admin(p_room_id uuid)
returns boolean language sql security definer set search_path = '' stable as $$
  select exists(select 1 from public.room_members
                where room_id = p_room_id and user_id = auth.uid() and role in ('owner','admin'));
$$;

create or replace function public.shares_room_with(p_user_id uuid)
returns boolean language sql security definer set search_path = '' stable as $$
  select exists(select 1 from public.room_members a join public.room_members b using (room_id)
                where a.user_id = auth.uid() and b.user_id = p_user_id);
$$;

-- Exact-email lookup for the "add member" flow; returns minimal info, no enumeration.
create or replace function public.find_user_by_email(p_email text)
returns table(id uuid, display_name text, avatar_url text)
language sql security definer set search_path = '' stable as $$
  select p.id, p.display_name, p.avatar_url
  from public.profiles p
  where lower(p.email) = lower(p_email)
  limit 1;
$$;

revoke execute on function
  public.is_room_member(uuid), public.is_room_admin(uuid),
  public.shares_room_with(uuid), public.find_user_by_email(text) from public;
grant execute on function
  public.is_room_member(uuid), public.is_room_admin(uuid),
  public.shares_room_with(uuid), public.find_user_by_email(text) to authenticated;

-- ============================================================ RLS

alter table public.profiles     enable row level security;
alter table public.rooms        enable row level security;
alter table public.room_members enable row level security;
alter table public.messages     enable row level security;

-- profiles: self + people you share a room with
create policy profiles_select on public.profiles for select to authenticated
  using (id = auth.uid() or public.shares_room_with(id));
create policy profiles_insert on public.profiles for insert to authenticated
  with check (id = auth.uid());
create policy profiles_update on public.profiles for update to authenticated
  using (id = auth.uid()) with check (id = auth.uid());

-- rooms: members read; anyone creates (as themselves); admins update/delete
create policy rooms_select on public.rooms for select to authenticated
  using (public.is_room_member(id));
create policy rooms_insert on public.rooms for insert to authenticated
  with check (created_by = auth.uid());
create policy rooms_update on public.rooms for update to authenticated
  using (public.is_room_admin(id)) with check (public.is_room_admin(id));
create policy rooms_delete on public.rooms for delete to authenticated
  using (public.is_room_admin(id));

-- room_members: members see the roster; admins add/change; admins remove or you leave
create policy rm_select on public.room_members for select to authenticated
  using (public.is_room_member(room_id));
create policy rm_insert on public.room_members for insert to authenticated
  with check (public.is_room_admin(room_id));
create policy rm_update on public.room_members for update to authenticated
  using (public.is_room_admin(room_id)) with check (public.is_room_admin(room_id));
create policy rm_delete on public.room_members for delete to authenticated
  using (public.is_room_admin(room_id) or user_id = auth.uid());

-- messages: members read; members send as themselves; authors edit own; authors/admins delete
create policy msg_select on public.messages for select to authenticated
  using (public.is_room_member(room_id));
create policy msg_insert on public.messages for insert to authenticated
  with check (sender_id = auth.uid() and public.is_room_member(room_id));
create policy msg_update on public.messages for update to authenticated
  using (sender_id = auth.uid()) with check (sender_id = auth.uid());
create policy msg_delete on public.messages for delete to authenticated
  using (sender_id = auth.uid() or public.is_room_admin(room_id));
-- Note: RLS can't restrict UPDATE to specific columns. To lock room_id/sender_id/created_at on edit,
-- add a BEFORE UPDATE guard trigger or use column-level grants.

-- ============================================================ realtime authorization

-- Postgres Changes stream; privacy enforced by msg_select / rm_select RLS above.
alter publication supabase_realtime add table public.messages, public.room_members;

-- Private channels (Realtime Authorization). Topic convention: 'room:<room_id>'.
-- realtime.messages already has RLS enabled by Supabase.
create policy rt_room_read on realtime.messages for select to authenticated
  using (public.is_room_member( (split_part(realtime.topic(), ':', 2))::uuid ));
create policy rt_room_write on realtime.messages for insert to authenticated   -- presence / broadcast on the topic
  with check (public.is_room_member( (split_part(realtime.topic(), ':', 2))::uuid ));