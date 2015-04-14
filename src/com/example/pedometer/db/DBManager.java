package com.example.pedometer.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pedometer.model.PedometerData;
import com.example.pedometer.model.User;
import com.example.pedometer.model.UserPedometer;

/**
 * 
 * @author lemon
 * 封装数据库类，对数据进行管理
 */
public class DBManager {
	
	private DBHelper helper;  
    private SQLiteDatabase db;  
    private static DBManager dbm; 
      
    private DBManager(Context context) {  
        helper = new DBHelper(context);  
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里  
        db = helper.getWritableDatabase();  
    }  
    
    public synchronized static DBManager getInstance(Context context){
    	if(dbm == null){
    		return new DBManager(context);
    	}
    	return dbm;
    }
      
    /** 
     * 
     * @param pedometerData 
     */  
    public void addUser(User user) {  
       if(user != null){
    	   ContentValues values = new ContentValues();
			//values.put("objectId", user.getObjectId());
			values.put("name", user.getName());
			values.put("passwd", user.getPasswd());
			values.put("email", user.getEmail());
		
			db.insert("user", null, values);
       }
    }  
      
    
    public boolean userIsExist(String name,String passwd){
    	Cursor c;
    	if(passwd == null)
    		c = db.query("user", new String[]{"name"}, "name=?", new String[]{name}, null, null, null);
    	else
    		c = db.query("user", new String[]{"name","passwd"}, "name=? and passwd=?", new String[]{name,passwd}, null, null, null);
    	if (c.moveToNext()){
    		c.close();
    		return true;
    	}
    	c.close();
    	return false;
    	
    }
    
  
    public void deleteUser(User user) {  
        if(user != null){  
        	//db.delete("user", "objectId=?", new String[]{ user.getObjectId()});
        }
    }  
    
    public void addUserPedometer(UserPedometer up){
    	if(up != null){
    		ContentValues values = new ContentValues();
    		values.put("uid", up.getUID());
    		values.put("paces", up.getPaces());
    		values.put("kilometers", up.getKilometers());
    		values.put("calories",up.getCalories());
    		values.put("walkDate", up.getDate());
    		db.insert("userpedometer", null, values);
    		
    	}
    }
    
    public void addPedometerData(PedometerData pData){
    	if(pData != null){
    		ContentValues values = new ContentValues();
    		values.put("paces", pData.getPaces());
    		values.put("kilometers", pData.getKilometers());
    		values.put("calories",pData.getCalories());
    		values.put("walkDate", pData.getWalkDate());
    		db.insert("pedometerData", null, values);
    	}
    }
    
    public void updateUser(User user) {
		if (user != null) {
			ContentValues values = new ContentValues();
			//values.put("objectId", user.getObjectId());
			values.put("name", user.getName());
			values.put("sex", user.getSex());
			values.put("picture", user.getPic());
			values.put("weight", user.getWeight());
			values.put("sensitivity", user.getSensitivity());
			values.put("step_length", user.getStep_length());
			values.put("groupId", user.getGroupId());
			//db.update("user", values, "objectId = ?",
			//		new String[] { user.getObjectId() });
			
		}
	}
    
    public List<UserPedometer> queryUserPedometer(){
    	ArrayList<UserPedometer> ups = new ArrayList<UserPedometer>();
    	
    	Cursor c = queryTheCursor("userpedometer");
    	while(c.moveToNext()){
    		UserPedometer up = new UserPedometer();
    		up.setUID(c.getInt(c.getColumnIndex("uid")));
    		up.setPaces(c.getInt(c.getColumnIndex("paces")));
    		up.setKilometers(c.getFloat(c.getColumnIndex("kilometers")));
    		up.setCalories(c.getFloat(c.getColumnIndex("calories")));
    		up.setDate(c.getString(c.getColumnIndex("walkDate")));
    		ups.add(up);
    	}
    	return ups;
    }
      
    public List<PedometerData> queryPedometerData(){
    	ArrayList<PedometerData> pedometerDatas = new ArrayList<PedometerData>();
    	
    	Cursor c = queryTheCursor("pedometerData");
    	while(c.moveToNext()){
    		PedometerData p = new PedometerData();
    		p.setPID(c.getInt(c.getColumnIndex("pid")));
    		p.setPaces(c.getInt(c.getColumnIndex("paces")));
    		p.setKilometers(c.getFloat(c.getColumnIndex("kilometers")));
    		p.setCalories(c.getFloat(c.getColumnIndex("calories")));
    		p.setWalkDate(c.getString(c.getColumnIndex("walkDate")));
    		pedometerDatas.add(p);
    	}
    	c.close();
    	return pedometerDatas;
    }
    
  
    /** 
     * query all pedometer data, return list 
     * @return List<Person> 
     */  
	
    public List<User> queryUser() {  
        ArrayList<User> users = new ArrayList<User>();  
        Cursor c = queryTheCursor("user");  
        while (c.moveToNext()) {  
            User u = new User();
            u.setUID(c.getInt(c.getColumnIndex("uid")));
            u.setName(c.getString(c.getColumnIndex("name")));
            u.setPasswd(c.getString(c.getColumnIndex("passwd")));
            u.setSex(c.getString(c.getColumnIndex("sex")));
            u.setSensitivity(c.getInt(c.getColumnIndex("sensitivity")));
            u.setEmail(c.getString(c.getColumnIndex("email")));
            u.setStep_length(c.getInt(c.getColumnIndex("step_length")));
            u.setGroupId(c.getInt(c.getColumnIndex("groupId")));
            u.setPic(c.getBlob(c.getColumnIndex("pic")));
            u.setWeight(c.getInt(c.getColumnIndex("weight")));
            users.add(u);
        }  
        c.close();  
        return users;  
    }  
    /** 
     * query all data, return cursor 
     * @return  Cursor 
     */  
    public Cursor queryTheCursor(String table) {  
        Cursor c = db.rawQuery("SELECT * FROM " + table, null);  
        return c;  
    }  
      
    /** 
     * close database 
     */  
    public void closeDB() {  
        db.close();  
    }  
    public boolean isDBActive(){
    	return db.isOpen();
    }
}
