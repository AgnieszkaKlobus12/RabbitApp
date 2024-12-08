package com.example.rabbitapp.ui.synchronization

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rabbitapp.MainActivity
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.ActivityLoginBinding
import com.example.rabbitapp.utils.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_login, LoginFragment())
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        handleSignInResult(task)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            loginViewModel.loginWithGoogle(account, ::loginCallback)
        } catch (e: ApiException) {
            Log.w("LOGIN", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun loginCallback(success: Boolean) {
        if (success) {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        }
    }

}