package com.fahim.chatroom.core.config

/**
 * Client-safe Supabase configuration. [anonKey] is the publishable anon key — never the service_role key.
 *
 * TODO: source these from a non-committed config (BuildKonfig / xcconfig / CI secret) before release.
 * The placeholders below must be replaced with your project's values for the client to connect.
 */
data class SupabaseConfig(
    val url: String,
    val anonKey: String,
) {
    companion object {
        val Default: SupabaseConfig = SupabaseConfig(
            url = "https://wzndlbtuacwjoqdsguqq.supabase.co",
            anonKey = "sb_publishable_Qjs_lATrqXh2PJLpf3pVDA_zcQnEnH8",
        )
    }
}