package com.example.beemind.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentResultOwner
import androidx.navigation.fragment.findNavController
import com.example.beemind.R
import com.example.beemind.databaseData.Database
import com.example.beemind.databinding.FragmentHomeBinding
import com.example.beemind.popUps.InfoPopup
import com.google.android.material.tabs.TabLayout.TabGravity

/**
 * Class is used to manage HomeFragment
 */
class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    /**
     * creates appearance of HomeFragment
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
        this.binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val logoBtn = this.binding.beeLogo
        val cursor =
            Database(requireContext()).readableDatabase.rawQuery("SELECT * FROM OWNER", null)
        if (cursor.moveToFirst()) {
            val ownerIndex = cursor.getColumnIndex("owner")
            val regNumIndex = cursor.getColumnIndex("reg_num")
            val emailIndex = cursor.getColumnIndex("email")
            val phoneIndex = cursor.getColumnIndex("phone")
            val stfaIndex = cursor.getColumnIndex("svfa")
            val latIndex = cursor.getColumnIndex("lat")
            val lonIndex = cursor.getColumnIndex("lon")

            this.binding.owner.text = cursor.getString(ownerIndex)
            this.binding.registerNumber.text = cursor.getString(regNumIndex)
            this.binding.emial.text = cursor.getString(emailIndex)
            this.binding.phone.text = cursor.getString(phoneIndex)
            this.binding.svps.text = cursor.getString(stfaIndex)
            this.binding.lat.text = cursor.getString(latIndex)
            this.binding.lon.text = cursor.getString(lonIndex)
            if (binding.lat.text.toString().length > 6) {
                this.binding.lat.text = this.binding.lat.text.substring(0, 6)
            }
            if (binding.lon.text.toString().length > 6) {
                this.binding.lon.text = this.binding.lon.text.substring(0, 6)
            }
        }
        cursor.close()
        logoBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_hiveFragment)
        }
        this.binding.ownerInfo.setOnClickListener {
            InfoPopup().create(
                this.binding.owner.text.toString(),
                this.binding.registerNumber.text.toString(),
                this.binding.emial.text.toString(),
                this.binding.phone.text.toString(),
                this.binding.svps.text.toString(),
                this.binding.lat.text.toString(),
                this.binding.lon.text.toString(),
                requireContext()
            ) { owner, registerNumber, email, phone, svfa, lat, lon ->
                rename(owner, registerNumber, email, phone, svfa, lat, lon)
            }
        }
        return this.binding.root
    }

    /**
     * this method is used to rename information about owner of apiary
     * also inserts information about owner to databse
     *
     * @param owner full name of the owner
     * @param regNum registration number of beekeeper
     * @param email email address of the owner
     * @param phone phone number to contact owner
     * @param svfa The state Veterinary and Food Administration
     * @param lat latitude of apiary
     * @param lon longitude of apiary
     */
    private fun rename(
        owner: String,
        regNum: String,
        email: String,
        phone: String,
        svfa: String,
        lat: String,
        lon: String
    ) {
        this.binding.owner.text = owner
        this.binding.registerNumber.text = regNum
        this.binding.emial.text = email
        this.binding.phone.text = phone
        this.binding.svps.text = svfa
        Log.d(TAG, "length ${binding.lat.text.toString().length}")
        if (lat.length > 6) {
            this.binding.lat.text = lat.substring(0, 6)
        } else {
            this.binding.lat.text = lat
        }
        if (lon.length > 6) {
            this.binding.lon.text = lon.substring(0, 6)
        } else {
            this.binding.lon.text = lon
        }
        val database = Database(requireContext()).writableDatabase
        database.execSQL("DELETE FROM OWNER")
        database.execSQL(
            "INSERT INTO OWNER (owner,reg_num,email,phone,svfa,lat,lon)" +
                    " VALUES('$owner','$regNum','$email','$phone','$svfa','$lat','$lon')"
        )

    }
}