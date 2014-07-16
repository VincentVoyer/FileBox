package fr.licpro.filebox.persistance;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import fr.licpro.filebox.model.File;
import fr.licpro.filebox.model.FileType;


/**
 * @author Vincent
 * @date 13 juin 2014
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
	/** Unique instance of DatabaseHelper */
	private static DataBaseHelper instance = null;
	/**
	 * Get the instance of DatabaseHelper.
	 * @return DatabaseHelper.
	 */
	public static DataBaseHelper getInstance(final Context pContext)
	{
		if(instance == null)
			instance = new DataBaseHelper(pContext);
		return instance;
	}

	/**
	 * Constructor
	 */
	private DataBaseHelper(final Context pContext) {
		super(pContext, "fileboxDb", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase fileboxDB, ConnectionSource pConnectionSource) {
		try {
			TableUtils.createTable(pConnectionSource, FileType.class);
			TableUtils.createTable(pConnectionSource, File.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase fileboxDB, ConnectionSource pConnectionSource, final int dbOldVersion, final int dbNewVersion) {
		try {
			TableUtils.dropTable(pConnectionSource, File.class, true);
			TableUtils.dropTable(pConnectionSource, FileType.class, true);
			onCreate(fileboxDB, pConnectionSource); // Create tables again
		} 
		catch (SQLException sqle) 
		{ }
		
	}

}
