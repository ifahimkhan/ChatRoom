-- Trigger-only functions must not be directly callable via REST by anyone
revoke execute on function public.handle_new_user() from public, anon, authenticated;
revoke execute on function public.handle_new_room() from public, anon, authenticated;

-- Drop auto-created permissive Supabase policy that bypasses our rooms_insert check
drop policy if exists "Enable insert for authenticated users only" on public.rooms;