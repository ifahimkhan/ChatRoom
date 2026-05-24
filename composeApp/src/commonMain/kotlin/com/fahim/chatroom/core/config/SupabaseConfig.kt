package com.fahim.chatroom.core.config

/**
 * Client-safe Supabase configuration. [anonKey] must be the legacy anon JWT (HS256),
 * NOT the sb_publishable_* key — PostgREST and Kong require the JWT for role assignment.
 *
 * Values are injected at build time via BuildKonfig, sourced from `local.properties`
 * (gitignored) or `SUPABASE_URL` / `SUPABASE_ANON_KEY` env vars in CI.
 */
data class SupabaseConfig(
    val url: String,
    val anonKey: String,
) {
    companion object {
        val Default: SupabaseConfig = SupabaseConfig(
            url = BuildKonfig.SUPABASE_URL,
            anonKey = BuildKonfig.SUPABASE_ANON_KEY,
        )
    }
}