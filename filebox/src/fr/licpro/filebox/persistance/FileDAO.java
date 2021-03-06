/**
 * 
 */
package fr.licpro.filebox.persistance;

import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import fr.licpro.filebox.model.File;

/**
 * @author Vincent
 * @date 13 juin 2014
 */
public class FileDAO {

	public static List<File> getAllData(final Context pContext, final Class<File> pClazz) 
	{ 
		List<File> ret = null;
		SQLiteOpenHelper db = DataBaseHelper.getInstance(pContext); 
		ConnectionSource connectionSource = new AndroidConnectionSource(db); 
		try {
			Dao<File, Integer> dao = (Dao<File, Integer>) DaoManager.createDao(connectionSource, pClazz);
			if (dao != null) {
				ret = dao.queryForAll();
			}
		} 
		catch (SQLException e) { } 
		catch (java.sql.SQLException e) {e.printStackTrace();} 
		finally {connectionSource.closeQuietly(); }
		
		return ret;
	}
	
	public static List<File> getEqDatas(final Context pContext, final Class<File> pClazz, final String pField, final Object pValue, final String pOrder, final long pLimit) 
	{
		List<File> ret = null;
		SQLiteOpenHelper db = DataBaseHelper.getInstance(pContext); 
		ConnectionSource connectionSource = new AndroidConnectionSource(db); try {

			Dao<File, Integer> dao = (Dao<File, Integer>) DaoManager.createDao(connectionSource,pClazz);
			if (dao != null) {
				QueryBuilder<File, Integer> queryBuilder = dao.queryBuilder(); 
				if (pField != null)
					try {
						queryBuilder.where().eq(pField, pValue);
					} catch (java.sql.SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				if (pLimit != 0)
					queryBuilder.limit(pLimit); if (pOrder != null)
						queryBuilder.orderBy(pOrder, false); ret = queryBuilder.query();
			}
		} 
		catch (SQLException e) {} 
		catch (java.sql.SQLException e) {e.printStackTrace();} 
		finally { connectionSource.closeQuietly();} 
		
		return ret;
	}

	public static void storeSingleData(final Context pContext, final File pData) 
	{
		Class<?> type = pData.getClass();
		SQLiteOpenHelper db = DataBaseHelper.getInstance(pContext); ConnectionSource connectionSource = new AndroidConnectionSource(db); Dao<File, Integer> dao = null;
		try {
			dao = (Dao<File, Integer>) DaoManager.createDao(connectionSource, type); 
			if (dao != null) {
				dao.createOrUpdate(pData); 
			}
		} 
		catch (SQLException e) { } 
		catch (java.sql.SQLException e) {e.printStackTrace();} 
		finally {connectionSource.closeQuietly(); }
	}
}
