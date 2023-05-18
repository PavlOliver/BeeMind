package com.example.beemind.databaseData

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.beemind.popUps.ErrorNotification

/**
 * Class represents a Hive which consists of it's title and mac address
 * and some basic operations to work with database
 *
 * @property title
 */
class HiveData(private val title: String) {

    /**
     * @return title of the Hive
     */
    fun getTitle(): String {
        return this.title
    }

    companion object {
        /**
         * @param title
         * @param context
         * @return mac address of Hive based on it's title
         */
        fun getAddress(title:String,context: Context): String? {
            val cursor = Database(context).readableDatabase.rawQuery(
                "SELECT address from HIVES where title='$title'",
                null
            )
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("address")
            if (index >= 0) {
                return cursor.getString(index).toString()
            }
            cursor.close()
            return null
        }

        /**
         * inserts data received as parameter to database
         *
         * @param title of the Hive
         * @param address of the Hive
         * @param context of the application
         * @return true if insert was successful
         */
        fun insert(title:String,address:String,context: Context): Boolean {
            val database = Database(context)
            return try {
                database.writableDatabase.execSQL("INSERT INTO HIVES(title,address) values ('$title','$address')")
                true
            } catch (e: SQLiteConstraintException) {
                ErrorNotification(e.toString(), context)
                false
            }
        }

        /**
         * updates title in the whole database
         * and every table which contains this title without integrity violation
         *
         * @param oldTitle hive's title which will be changed
         * @param newTitle title which replace oldTitle
         * @param address new address
         * @param context of the application
         * @return true if update was successful
         */
        fun updateTitle(oldTitle:String,newTitle:String,address:String,context: Context):Boolean{
            val database = Database(context)
            return try{
                database.writableDatabase.execSQL("UPDATE CURE set title = '$newTitle' WHERE title='$oldTitle'")
                database.writableDatabase.execSQL("UPDATE INSPECTION set title = '$newTitle' WHERE title='$oldTitle'")
                database.writableDatabase.execSQL("UPDATE MONITORING set title = '$newTitle' WHERE title='$oldTitle'")
                database.writableDatabase.execSQL("UPDATE HIVES set title = '$newTitle',address='$address' WHERE title='$oldTitle'")
                true
            } catch (e: SQLiteConstraintException) {
                ErrorNotification(e.toString(), context)
                false
            }
        }

        /**
         * operation will delete hive data from whole database
         * and won't violate it's integrity
         *
         * @param title of hive which is going to be deleted
         * @param context of the application
         * @return
         */
        fun delete(title: String, context: Context): Boolean {
            val database = Database(context)
            return try {
                database.writableDatabase.execSQL("DELETE FROM CURE WHERE title='$title'")
                database.writableDatabase.execSQL("DELETE FROM INSPECTION WHERE title='$title'")
                database.writableDatabase.execSQL("DELETE FROM MONITORING WHERE title='$title'")
                database.writableDatabase.execSQL("DELETE FROM HIVES WHERE title='$title'")
                true
            } catch (e: SQLiteConstraintException) {
                ErrorNotification(e.toString(), context)
                false
            }
        }
    }
}