package com.example.beemind.databaseData

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.beemind.popUps.ErrorNotification

/**
 * Class contains basic operation with Tasks entered by user
 *
 */
class Tasks {
    companion object {
        /**
         * @param context
         * @return all task from database
         */
        fun get(context: Context): String {
            var retStr = ""
            val cursor = Database(context).readableDatabase.rawQuery(
                "SELECT task from TASK",
                null
            )
            while(cursor.moveToNext()) {
                val index = cursor.getColumnIndex("task")
                if (index >= 0) {
                    retStr = retStr + cursor.getString(index) + "\n"
                }
            }
            cursor.close()
            return retStr
        }

        /**
         * inserts a new task into database
         *
         * @param task body of task
         * @param context of the application
         * @return
         */
        fun insert(task:String, context: Context):Boolean {
            val database = Database(context)
            return try {
                database.writableDatabase.execSQL("INSERT INTO TASK(task) values ('$task')")
                true
            } catch (e: SQLiteConstraintException) {
                ErrorNotification(e.toString(), context)
                false
            }
        }

        /**
         * delete every single task from the table
         *
         * @param context of the application
         * @return
         */
        fun delete(context: Context):Boolean{
            val database = Database(context)
            return try{
                database.writableDatabase.execSQL("DELETE FROM TASK")
                true
            } catch (e: SQLiteConstraintException) {
                ErrorNotification(e.toString(), context)
                false
            }
        }
    }
}