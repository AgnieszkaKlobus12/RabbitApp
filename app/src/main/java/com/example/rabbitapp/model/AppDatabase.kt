package com.example.rabbitapp.model

import android.content.Context
import com.example.rabbitapp.R
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.runBlocking


abstract class AppDatabase {

    companion object {
        private var INSTANCE: SupabaseClient? = null

        @Synchronized
        fun getInstance(ctx: Context): SupabaseClient {
            if (INSTANCE == null)
                INSTANCE = createSupabaseClient(
                    ctx.resources.getString(R.string.supabase_url),
                    ctx.resources.getString(R.string.supabase_key)
                ) {
                    defaultSerializer = KotlinXSerializer()
                    install(Auth)
                    install(Postgrest)
                }

            runBlocking {
                INSTANCE!!.auth.signInWith(Email) {
                    email = "example@email.com"
                    password = "example-password"
                }
            }
            return INSTANCE!!
        }

    }
}