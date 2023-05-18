package com.example.beemind

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.beemind.databaseData.HiveData
import com.example.beemind.popUps.PopUp

/**
 * Class is used to manage Hive's of HiveFragment
 *
 * @property hiveList ArrayList of Hive's in apiary
 * @property navController to navigate to next Fragment with bundle
 */
class RecyclerAdapter(
    private val hiveList: ArrayList<HiveData>, private val navController: NavController
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_block_hive, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        val newList = hiveList[position]
        holder.hiveTitle.text = newList.getTitle()

    }

    override fun getItemCount(): Int {
        return this.hiveList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imageBtn: ImageButton
        var hiveTitle: TextView
        var deleteBtn: ImageButton
        var editBtn: ImageButton

        init {
            this.imageBtn = itemView.findViewById(R.id.nextBtn)
            this.hiveTitle = itemView.findViewById(R.id.text)
            this.deleteBtn = itemView.findViewById(R.id.deleteBtn)
            this.editBtn = itemView.findViewById(R.id.editBtn)

            this.imageBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("1", this.hiveTitle.text.toString())
                navController.navigate(R.id.action_hiveFragment_to_inHive, bundle)
            }
            this.deleteBtn.setOnClickListener {
                HiveData.delete(hiveList[this.layoutPosition].getTitle(), itemView.context)
                hiveList.removeAt(this.layoutPosition)
                notifyItemRemoved(this.layoutPosition)
            }
            this.editBtn.setOnClickListener {
                val titleNew = hiveList[layoutPosition].getTitle()
                val address = HiveData.getAddress(titleNew, itemView.context)
                Log.d(TAG, "address - > $address")
                val popup = PopUp(titleNew, address!!)
                popup.create(itemView.context) { title, address ->
                    if (HiveData.updateTitle(titleNew, title, address, itemView.context)) {
                        hiveList[this.layoutPosition] = HiveData(title)
                        notifyItemChanged(this.layoutPosition)
                    }
                }

            }

        }
    }
}