package com.example.rabbitapp.ui.settings.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentSettingsBinding
import com.example.rabbitapp.ui.synchronization.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logOutButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(R.string.google_server_client_id))
                .requestEmail()
                .build()
            val mGoogleSignInClient =
                GoogleSignIn.getClient(requireActivity().applicationContext, gso)
            val account = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext)
            Log.i("google account", account.toString())
            if (account != null)
                mGoogleSignInClient.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            requireActivity().startActivity(intent)
        }
    }

}