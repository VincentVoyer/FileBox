/**
 * 
 */
package fr.licpro.filebox.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.licpro.filebox.activity.MainActivity;
import fr.licpro.filebox.dto.enums.FileTypeEnum;
import fr.licpro.filebox.persistance.FileTypeDAO;

/**
 * @author Vincent
 * @date 5 juin 2014
 */
@DatabaseTable(tableName = "FileType")
public class FileType {
	
	private static int NB_ELEMENT = 0;

	/**
	 * id.
	 */
	@DatabaseField(id = true)
	private int mId;
	/**
	 * name.
	 */
	@DatabaseField
	private String mName;

	private static ConcurrentMap<String, FileType> filesTypes = null; 
	
	public static FileType getFileType(String name){
		if(filesTypes == null){
			filesTypes = new ConcurrentHashMap<String, FileType>();
			initFilesTypes();
		}
		
		return filesTypes.get(name);
	}
	
	private static void initFilesTypes(){
		
		FileTypeDAO fileTypeDao = new FileTypeDAO();
		
		for(FileTypeEnum file : FileTypeEnum.values()){
			FileType fileType = new FileType();
			fileType.setName(file.name());
			
			filesTypes.put(file.name(),fileType);
		}
	}
	
	public FileType(){
		NB_ELEMENT--;
		mId = NB_ELEMENT;
	}
	
	/*---------------------------------------------------------------------*/
	/*---------------------------Getters and Setters-----------------------*/
	/*---------------------------------------------------------------------*/
	/**
	 * Return id.
	 * @return the id.
	 */
	public int getId() {
		return mId;
	}
	/**
	 * Return name.
	 * @return the name.
	 */
	public String getName() {
		return mName;
	}
	/**
	 * Modify id.
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.mId = id;
	}
	/**
	 * Modify name.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.mName = name;
	}
	
	
}
