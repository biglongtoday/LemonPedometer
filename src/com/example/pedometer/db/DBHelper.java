package com.example.pedometer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME="pedometer.db";
	private static final int DATABASE_VERSION=1;
	
	
	private static final String CREATE_USER = "create table user(" +
			"uid integer primary key autoincrement," +
			"name text," +
			"email text," +
			"passwd text not null," +
			"sex char(4) default '男'," +
			"sensitivity text default '一级'," +
			"step_length real default 50," +
			"weight real," +    
			"pic blob," +
			"groupId int default 1)";
	
	
	public static final String CREATE_PEDOMETERDATA = "create table pedometerData("
			+ "pid integer primary key autoincrement,"
			+ "paces integer,"
			+ "kilometers real,"
			+ "calories real," 
			+ "walkDate date)";
	
	public static final String CREATE_USERPEDOMETER = "create table userpedometer(" +
			"pid integer primary key autoincrement," +
			"uid integer," +
			"paces int," +
			"kilometers real," +
			"calories real," +
			"walkDate date," +
			"foreign key (uid) references user(uid) on delete cascade on update cascade)";
	
	public static final String CREATE_GROUP = "create table group1("
			+ "id integer primary key autoincrement,"
			+ "total_number integer,"
			+ "member_number integer)";
	
	public DBHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL(CREATE_USER);
		db.execSQL(CREATE_PEDOMETERDATA);
		db.execSQL(CREATE_USERPEDOMETER);
		db.execSQL(CREATE_GROUP);
	}

	//如果version值被改为2，系统发现数据库版本不同，调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
