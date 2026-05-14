package com.fahim.chatroom.core.supabase

import com.fahim.chatroom.core.config.SupabaseConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

fun buildSupabaseClient(
    config: SupabaseConfig,
    sessionManager: SessionManager,
): SupabaseClient =
    createSupabaseClient(supabaseUrl = config.url, supabaseKey = config.anonKey) {
        install(Auth) {
            this.sessionManager = sessionManager
        }
        install(Postgrest)
        install(Realtime)
    }
