package com.monolithical.firebasedemo.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.monolithical.firebasedemo.LoginActivity
import com.monolithical.firebasedemo.R
import com.monolithical.firebasedemo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        FirebaseAuth.getInstance().currentUser?.let {
            binding.tvUserGreeting.text = "Hello ${it.displayName}"
        }

        binding.btnSignOut.setOnClickListener {
            // 1: going to sign out off app Firebase disk cache
            FirebaseAuth.getInstance().signOut()
            // 2: sign out app out / disconnect using GoogleSingInClient
            // https://developers.google.com/identity/sign-in/android/disconnect
            // quick and dirty
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, this is in the values.xml file that
                // is generated from your google-services.json (aka webclientId)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
            // signout returns a task, attach oncomplete listener to process navigation to LoginActivity when done
            googleSignInClient.signOut().addOnCompleteListener {
                // 3: and got to login screen
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                // 4: finish activity
                requireActivity().finish()
            }

        }
    }
}