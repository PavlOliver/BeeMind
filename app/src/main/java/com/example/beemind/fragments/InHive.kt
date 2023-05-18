package com.example.beemind.fragments

import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.beemind.R
import com.example.beemind.databinding.FragmentInHiveBinding

/**
 * Class is used to manage InHiveFragment
 * class is just used as transition between three fragments
 *
 */
class InHive : Fragment() {

    /**
     * creates an appearance of InHiveFragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        val hiveTitle = arguments?.getString("1")
        Log.d(TAG,"inHivewe ${hiveTitle.toString()}")
        val binding = FragmentInHiveBinding.inflate(inflater, container, false)

        val bundle = Bundle()
        bundle.putString("1", hiveTitle)

        binding.cure.setOnClickListener {
            findNavController().navigate(R.id.action_inHive_to_cureFragment,bundle)
        }
        binding.inspection.setOnClickListener {
            findNavController().navigate(R.id.action_inHive_to_inspectionFragment,bundle)
        }
        binding.monitoring.setOnClickListener {
            findNavController().navigate(R.id.action_inHive_to_monitoring2,bundle)
        }
        return binding.root
    }
}