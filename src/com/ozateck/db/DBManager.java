package com.ozateck.db;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager{
	
	private static final String TAG = "myTag";
	
	private String dbName     = "okami.db";
	private int    dbVersion  = 1;
	private String tableName  = "mytable";
	private String primaryKey = "myind";
	private String columns[]  = {"score"};
	
	private DBHelper       dbHelper;
	private SQLiteDatabase db;
	
	public DBManager(Context context){
		//DB�I�u�W�F�N�g�擾
		dbHelper = new DBHelper(context, dbName, dbVersion, tableName);
	}
	
	//DB�̏�����(�����l����)
	public void resetAll(){
		db = dbHelper.getWritableDatabase();
		dbHelper.resetAll(db);
		db.close();
	}
	
	//DB�̏�����(�����l�ݒ�)
	public void initialize(){
		Log.d(TAG, "DBManager initialize");
		resetAll();
		db = dbHelper.getWritableDatabase();
		try{
			for(int r=0; r<5; r++){
				List<String> itemData = new ArrayList<String>();
				for(int i=0; i<columns.length; i++){
					itemData.add(""+100*r);
				}
				insert(itemData);
			}
		}catch(Exception e){
			Log.d(TAG, "initialize error." + e.toString());
		}
		db.close();
	}

	//�ǉ�
	public void insert(List<String> itemData){
		//ContentValues
		ContentValues values = new ContentValues();
		//values.put("myind", "");//�ǉ��̏ꍇ�Aprimary key �Ȃ̂ł����͔����Ă���
		
		for(int i=0; i<columns.length; i++){
			String str = itemData.get(i);
			values.put(columns[i], str);
		}
		
		//db
		db = dbHelper.getWritableDatabase();
		db.insert(tableName, "", values);
		db.close();
	}

	//�X�V
	public void update(int myind, List<String> itemData) throws Exception{
		//ContentValues
		ContentValues values = new ContentValues();
		values.put(primaryKey,  new Integer(myind));
		
		for(int i=0; i<itemData.size(); i++){
			String str = itemData.get(i);
			values.put(columns[i], str);
		}
		
		//db
		db = dbHelper.getWritableDatabase();
		db.update(tableName, values, "myind=" + myind, null);
		db.close();
	}
	
	//�폜
	public void delete(int myind) throws Exception{
		//db
		db = dbHelper.getWritableDatabase();
		db.delete(tableName, primaryKey + "=" + myind, null);
		db.close();
	}
	
	//�������̎擾
	public int getTotalCount(){
		db = dbHelper.getWritableDatabase();
		int count;
		Cursor cursor = db.query(tableName, columns, 
								 null, null, null, null, null);
		count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}
	
	//�f�[�^�̃��X�g���擾(score��)
	public List<List<String>> getList(int limit){
		db = dbHelper.getWritableDatabase();
		List<List<String>> itemList = new ArrayList<List<String>>();
		Cursor cursor = db.query(tableName, columns, 
				null, null, null, null, "score DESC", ""+limit);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			List<String> itemData = new ArrayList<String>();
			for(int i=0; i<columns.length; i++){
				String str = cursor.getString(i);
				itemData.add(str);
			}
			
			itemList.add(itemData);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return itemList;
	}
}