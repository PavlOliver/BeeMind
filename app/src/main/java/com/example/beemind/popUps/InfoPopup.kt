package com.example.beemind.popUps

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.example.beemind.databinding.InfoPopupBinding

/**
 *
 * Class manage functionality and appearance of PopUp Window
 */
class InfoPopup {
    private lateinit var popupWindow: PopupWindow

    /**
     * Method creates an PopUp windows
     * which is used to change information about owner
     *
     * @param ownerOld full name of owner saved in database
     * @param regNumOld registration number of beekeeper saved in database
     * @param emailOld email of owner saved in database
     * @param phoneOld phone number saved in database
     * @param svfaOld State Veterinary and Food Administration saved in database
     * @param latOld latitude saved in database
     * @param lonOld longitude saved in database
     * @param context of the application
     * @param rename lambda function which rewrite the TextViews
     */
    fun create(
        ownerOld: String,
        regNumOld: String,
        emailOld: String,
        phoneOld: String,
        svfaOld: String,
        latOld: String,
        lonOld: String,
        context: Context,
        rename: (owner: String, regNum: String, email: String, phone: String, svfa: String, lat: String, lon: String) -> Unit
    ) {
        val popupBinding = InfoPopupBinding.inflate(LayoutInflater.from(context))
        if(!ownerOld.equals("Full name"))
            popupBinding.ownerE.setText(ownerOld)
        if(!regNumOld.equals("register number"))
            popupBinding.registerNumberE.setText(regNumOld)
        if(!emailOld.equals("email@mail.com"))
            popupBinding.emailE.setText(emailOld)
        if(!phoneOld.contains('X'))
            popupBinding.phoneE.setText(phoneOld)
        if(!svfaOld.contains("SVaFA"))
            popupBinding.svfaE.setText(svfaOld)
        if(!latOld.contains("lat"))
            popupBinding.latE.setText(latOld)
        if(!lonOld.contains("lon"))
            popupBinding.lonE.setText(lonOld)
        this.popupWindow = PopupWindow(
            popupBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isFocusable = true
        popupWindow.isTouchable = true
        popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        popupBinding.popupButton.setOnClickListener {
            rename(
                popupBinding.ownerE.text.toString(),
                popupBinding.registerNumberE.text.toString(),
                popupBinding.emailE.text.toString(),
                popupBinding.phoneE.text.toString(),
                popupBinding.svfaE.text.toString(),
                popupBinding.latE.text.toString(),
                popupBinding.lonE.text.toString()
            )
            this.popupWindow.dismiss()
        }
        popupWindow.showAtLocation(popupBinding.root, Gravity.CENTER, 0, 0)
    }
}