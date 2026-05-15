package com.fahim.chatroom.core.config

/**
 * Client-safe Supabase configuration. [anonKey] must be the legacy anon JWT (HS256),
 * NOT the sb_publishable_* key — PostgREST and Kong require the JWT for role assignment.
 *
 * TODO: source these from a non-committed config (BuildKonfig / xcconfig / CI secret) before release.
 */
data class SupabaseConfig(
    val url: String,
    val anonKey: String,
) {
    companion object {
        val Default: SupabaseConfig = SupabaseConfig(
            url = "https://wzndlbtuacwjoqdsguqq.supabase.co",
            anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind6bmRsYnR1YWN3am9xZHNndXFxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg2MDI1OTYsImV4cCI6MjA5NDE3ODU5Nn0.nZ68GmkNUmgFMMo6_ThJgKcKBfu6-UFMNfJHAMuIDMo",
        )
    }
}