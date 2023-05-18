package com.example.beemind.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beemind.databaseData.Database
import com.example.beemind.databaseData.HiveData
import com.example.beemind.popUps.DatePick
import com.example.beemind.popUps.PopUp
import com.example.beemind.RecyclerAdapter
import com.example.beemind.databinding.FragmentHiveBinding

/**
 * Class managing HiveFragment which is used to display, add or edit Hives
 */
class HiveFragment : Fragment() {
    private lateinit var hives: ArrayList<HiveData>
    private lateinit var rec: RecyclerView
    private lateinit var adapter: RecyclerAdapter

    private lateinit var popUp: PopUp

    /**
     * adds hive to arrayList which is used by Adapter
     *
     * @param title
     */
    private fun addHive(title: String) {
        hives.add(HiveData(title))
        adapter.notifyItemInserted(hives.size - 1)
    }

    /**
     * creates appearance and listeners of elements in Hivefragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hives = ArrayList()
        val binding = FragmentHiveBinding.inflate(inflater)
        rec = binding.dynamicLayout
        rec.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecyclerAdapter(hives, findNavController())
        rec.adapter = adapter
        val addBtn = binding.addBtn
        val planBtn = binding.planAddBtn

        addBtn.setOnClickListener {
            popUp = PopUp()
            this.showPopup()
        }
        planBtn.setOnClickListener {
            this.showPlanner()
        }
        val cursor = Database(requireContext()).readableDatabase.rawQuery("SELECT * FROM HIVES", null)

        while (cursor.moveToNext()) {
            val titleIndex = cursor.getColumnIndex("title")
            if (titleIndex >= 0) {
                val title = cursor.getString(titleIndex)
                this.addHive(title)
            }
        }
        cursor.close()
        return binding.root
    }

    /**
     * method is used to call and show PopUp window
     * PopUp window contains dialog where title and mac address of Hive is entered
     */
    private fun showPopup() {
        popUp.create(requireContext()) { title, address ->
            this.addHive(title)
            HiveData.insert(title, address, requireContext())
        }
    }

    /**
     * method calls and shows a TaskPlanner
     * more about in DatePick class
     */
    private fun showPlanner(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    123
                )
            }
        }
        DatePick().create(requireContext())
    }
}
