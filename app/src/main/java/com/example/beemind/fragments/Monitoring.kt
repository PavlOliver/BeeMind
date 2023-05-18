package com.example.beemind.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.beemind.BluetoothCommunication
import com.example.beemind.databaseData.Database
import com.example.beemind.databaseData.HiveData
import com.example.beemind.R
import com.example.beemind.databinding.FragmentMonitoringBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * Class is used for managing MonitoringFragment
 */
class Monitoring : Fragment() {
    private lateinit var bluetooth: BluetoothCommunication

    /**
     * Creates a presence and basic functionality of elements in Fragment
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
        val hiveTitle = arguments?.getString("1")
        val bundle = Bundle()
        bundle.putString("1", hiveTitle)
        val binding = FragmentMonitoringBinding.inflate(inflater, container, false)

        binding.graph.setOnClickListener {
            findNavController().navigate(R.id.action_monitoring2_to_graphFragment, bundle)
        }
        binding.connectBtn.setOnClickListener {
            val address: String? = HiveData.getAddress(hiveTitle!!, requireContext())
            if (address != null) {
                bluetooth = BluetoothCommunication(address)
                bluetooth.start(requireActivity(),hiveTitle)
            }
        }
        binding.rqstBtn.setOnClickListener {
            if (::bluetooth.isInitialized)
                bluetooth.sendData("R")
        }
        binding.liveDataBtn.setOnClickListener {
            if (::bluetooth.isInitialized) {
                val list = bluetooth.sendLiveRqst()
                if(list.isNotEmpty()) {
                    binding.degree.text = list[0]
                    binding.humidity.text = list[1]
                    binding.weight.text = list[2]
                }
            }
        }
        return binding.root
    }

    /**
     * is used to close a bluetooth connection when Fragment is abandoned
     */
    override fun onDestroyView() {
        super.onDestroyView()
        if (::bluetooth.isInitialized)
            bluetooth.close()
    }
}