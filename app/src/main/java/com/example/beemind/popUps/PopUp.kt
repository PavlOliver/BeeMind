package com.example.beemind.popUps

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.example.beemind.databinding.PopupLayoutBinding

/**
 * Class is used to manage PopUp Window
 * this window is used to add new Hive into database
 */
class PopUp() {
    private lateinit var title: String
    private lateinit var address: String
    private lateinit var popupWindow: PopupWindow
    constructor(title: String,address: String) : this() {
        this.title = title
        this.address = address
    }
    fun create(context: Context, onButtonClick: (title: String, address: String) -> Unit) {
        val popupBinding = PopupLayoutBinding.inflate(LayoutInflater.from(context))
        val titleEdit = popupBinding.titleEdit
        val addressEdit = popupBinding.addressEdit
        if(this::title.isInitialized){
            titleEdit.setText(this.title)
            addressEdit.setText(this.address)
        }
        this.popupWindow = PopupWindow(
            popupBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isFocusable = true
        popupWindow.isTouchable = true
        popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        popupBinding.popupButton.setOnClickListener {
            val title = titleEdit.text.toString()
            val address = addressEdit.text.toString()
            onButtonClick(title, address)
            this.popupWindow.dismiss()
        }
        popupWindow.showAtLocation(popupBinding.root, Gravity.CENTER, 0, 0)
    }
}