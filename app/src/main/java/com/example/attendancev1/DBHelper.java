package com.example.attendancev1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context,"attendance.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table StudentDetails(Fname text,Lname text,Password text,Status text,IndexNUmber text PRIMARY KEY,Programme text,Level text, Pic BLOB)");
        db.execSQL("Create table TutorDetails(Fname text,Lname text,Password text,Status text,StaffId text PRIMARY KEY,Pic BLOB)");
        db.execSQL("Create table ClassDetails(rowid integer PRIMARY KEY,Programme text,Course text ,Level text)");
        db.execSQL("Create table ProgressDetails(rowid integer PRIMARY KEY,Course text ,Progress integer,UNIQUE (Course) ON CONFLICT REPLACE)");
        db.execSQL("Create table LoggedIn(Log integer PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists StudentDetails");
        db.execSQL("drop table if exists TutorDetails");
        db.execSQL("drop table if exists ClassDetails");
        db.execSQL("drop table if exists ProgressDetails");
        db.execSQL("drop table if exists LoggedIn");
    }

    public boolean insertIntoLog (int Log){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Log",Log);
        long result = db.insert("LoggedIn",null,contentValues);
        return result != -1;
    }

    public boolean updateLog(int Log){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Log",Log);
            long result = db.update("LoggedIn", contentValues,null,null);
            return result != -1;
    }

    public Cursor viewLoggedIn(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from LoggedIn",null);
    }

    public boolean insertPicIntoStudent(String Pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Pic",Pic);
        long result = db.insert("StudentDetails", null, contentValues);
        return result != -1;
    }

    public boolean insertPicIntoTutor(String Pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Pic",Pic);
        long result = db.insert("TutorDetails", null, contentValues);
        return result != -1;
    }

    public boolean updatePicInStudent(String stat,String Pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Pic",Pic);
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from StudentDetails where Status = ?",new String[]{stat});
        if(cursor.getCount()>0) {
            long result = db.update("StudentDetails", contentValues, "Status=?", new String[]{stat});
            return result != -1;
        }else {
            return false;
        }
    }

    public boolean updatePicInTutor(String stat,String Pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Pic",Pic);
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from TutorDetails where Status = ?",new String[]{stat});
        if(cursor.getCount()>0) {
            long result = db.update("TutorDetails", contentValues, "Status=?", new String[]{stat});
            return result != -1;
        }else {
            return false;
        }
    }

    public boolean updatePasswordInStudent(String stat,String Password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Pic",Password);
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from StudentDetails where Status = ?",new String[]{stat});
        if(cursor.getCount()>0) {
            long result = db.update("StudentDetails", contentValues, "Status=?", new String[]{stat});
            return result != -1;
        }else {
            return false;
        }
    }

    public boolean updatePasswordInTutor(String stat,String Password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Pic",Password);
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from TutorDetails where Status = ?",new String[]{stat});
        if(cursor.getCount()>0) {
            long result = db.update("TutorDetails", contentValues, "Status=?", new String[]{stat});
            return result != -1;
        }else {
            return false;
        }
    }

    public boolean insertData1(String Fname,String Lname,String Password,String Status,String index,String Programme,String Level,String Pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Fname",Fname);
        contentValues.put("Lname",Lname);
        contentValues.put("Password",Password);
        contentValues.put("Status",Status);
        contentValues.put("IndexNumber",index);
        contentValues.put("Programme",Programme);
        contentValues.put("Level",Level);
        contentValues.put("Pic",Pic);
        long result = db.insert("StudentDetails",null,contentValues);
        return result != -1;
    }

    public boolean insertData2(String Fname,String Lname,String Password,String Status,String StaffId,String Pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Fname",Fname);
        contentValues.put("Lname",Lname);
        contentValues.put("Password",Password);
        contentValues.put("Status",Status);
        contentValues.put("StaffId",StaffId);
        contentValues.put("Pic",Pic);
        long result = db.insert("TutorDetails",null,contentValues);
        return result != -1;
    }

    public boolean insertDataIntoClass(String Programme,String Course,String Level){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Programme",Programme);
        contentValues.put("Course",Course);
        contentValues.put("Level",Level);
        long result = db.insert("ClassDetails",null,contentValues);
        return result != -1;
    }

    public boolean insertDataIntoProgress(String Course,Integer Progress){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Course",Course);
        contentValues.put("Progress",Progress);
        long result = db.insert("ProgressDetails",null,contentValues);
        return result != -1;
    }


    public boolean updateDataInProgress(String Course,Integer Progress){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Progress",Progress);
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from ProgressDetails where Course = ?",new String[]{Course});
        if(cursor.getCount()>0) {
            long result = db.update("ProgressDetails", contentValues, "Course=?", new String[]{Course});
            return result != -1;
        }else {
            return false;
        }
    }

    /*public boolean updateData(String index,String name,String course){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("course",course);
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from StudentDetails where indexNumber = ?",new String[]{index});
        if(cursor.getCount()>0) {
            long result = db.update("StudentDetails", contentValues, "indexNumber=?", new String[]{index});
            return result != -1;
        }else {
            return false;
        }
    }*/

    /*public boolean deleteData(String index){
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from StudentDetails where indexNumber = ?",new String[]{index});
        if(cursor.getCount()>0) {
            long result = db.delete("StudentDetails", "indexNumber=?", new String[]{index});
            return result != -1;
        }else {
            return false;
        }
    }*/

    public Cursor viewData1(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from StudentDetails",null);
    }

    public Cursor viewIndexInStudent(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select IndexNUmber from StudentDetails",null);
    }

    public Cursor viewIndexInTutor(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select StaffId from TutorDetails",null);
    }

    public Cursor viewProgrammeInStudent(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select Programme from StudentDetails",null);
    }

    public Cursor viewLevelInStudent(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select Level from StudentDetails",null);
    }

    public Cursor viewData2(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from TutorDetails",null);
    }

    public Cursor viewAllInClass(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from ClassDetails",null);
    }

    public Cursor viewProgrammeInClass(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select Programme from ClassDetails where rowid=?",new String[]{ID});
    }
    public Cursor viewCourseInClass(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select Course from ClassDetails where rowid=?",new String[]{ID});
    }
    public Cursor viewLevelInClass(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select Level from ClassDetails where rowid=?",new String[]{ID});
    }

    public Cursor viewAllInProgress(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from ProgressDetails",null);
    }

    public Cursor viewProgressInProgress(String Course){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select Progress from ProgressDetails where Course=?",new String[]{Course});
    }

    public Cursor viewCourseInProgress(String rowId){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select Course from ProgressDetails where rowId=?",new String[]{rowId});
    }

    public Cursor viewStatus(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select Status from TutorDetails",null);
    }

    @SuppressLint("Recycle")
    public int Login1(String Index, String Password){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = new String[]{Index,Password};
        try{
            int i;
            Cursor c;
            c = db.rawQuery("select * from StudentDetails where IndexNumber=? and Password=?",selectionArgs);
            c.moveToFirst();
            i = c.getCount();
            return i;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @SuppressLint("Recycle")
    public int Login2(String StaffId, String Password){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = new String[]{StaffId,Password};
        try{
            int i;
            Cursor c;
            c = db.rawQuery("select * from TutorDetails where StaffId=? and Password=?",selectionArgs);
            c.moveToFirst();
            i = c.getCount();
            return i;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

}

