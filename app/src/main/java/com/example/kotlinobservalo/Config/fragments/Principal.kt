package com.example.kotlinobservalo.Config.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.kotlinobservalo.R



class Principal : Fragment() {
    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.settings_activity, container, false)

        //btnNext = v.findViewById(R.id.btnNext)

        return v
    }

    override fun onStart() {
        super.onStart()

        //btnNext.setOnClickListener{}

        val action = PrincipalDirections.actionPrincipalToTeclado()
        v.findNavController().navigate(action)
    }

}