package fr.licpro.filebox.service.sync;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.mime.TypedInput;
import android.content.Context;
import android.content.Intent;
import fr.licpro.filebox.dto.commons.FileDto;
import fr.licpro.filebox.dto.response.FilesDto;
import fr.licpro.filebox.model.File;
import fr.licpro.filebox.persistance.FileDAO;
import fr.licpro.filebox.service.IRestClient;
import fr.licpro.filebox.service.ISync;
import fr.licpro.filebox.service.SyncService;
import fr.licpro.filebox.service.json.JacksonConverter;

/**
 * Abstract sync class
 */
public abstract class AbstractSync<T> implements ISync {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Class Tag for Logger
	 */
	private static final String TAG = AbstractSync.class.getCanonicalName();

	/**
	 * The data object
	 */
	protected T mData;
	
	/**
	 * Application Context
	 */
	protected transient Context mContext;

	/**
	 * Implement this method to perform your custom Task
	 *
	 * Success and Error handling can be performed by
	 * overriding either onSuccess() or the onError exception handler
	 */
	protected abstract T execute(final IRestClient pRestClient) throws RetrofitError;

	/**
	 * Method to start the current Sync
	 * @param pRestClient the rest client
	 * @return true if they are no error false otherwise
	 */
	@Override
	public boolean execute(final Context pContext, final IRestClient pRestClient) {
		boolean success = true;
		mContext = pContext;
		try {
			mData = execute(pRestClient);
			onSuccess();
		} catch (RetrofitError e) {
			// TODO extract exception
			onError(e);
		}
		return success;
	}
	
	/**
	 * Success method called when no error was found
	 */
	protected abstract void onSuccess();
	
	/**
	 *  Error method called when exception was throw
	 * @param e Exception
	 */
	protected abstract void onError(Exception e);
	
	@SuppressWarnings("unchecked")
	public static <T> T getBodyAs(Response mData,Type type){
		TypedInput body = mData.getBody();
		
		if(body == null)
			return null;
		try{
			return  (T) new JacksonConverter().fromBody(body, type);
		}
		catch(ConversionException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Save files list.
	 * @param files files list to save.
	 */
	protected void saveFiles(List<File> files) {
		List<File> dBase = FileDAO.getAllData(mContext, File.class);
		for(File file : files)
		{
			if(!dBase.contains(file)){
				FileDAO.storeSingleData(mContext, file);
			}
		}
	}
	
	/**
	 * Convert FilesDto into files list.
	 * @param filesDto source.
	 * @return files list.
	 */
	protected List<File> convert(FilesDto filesDto){
		List<File> files = new ArrayList<File>();

		for(FileDto file : filesDto.getListFile())
		{
			files.add(createFile(file));
		}

		return files;
	}

	/**
	 * Create a file instance from fileDto instance.
	 * @param file source.
	 * @return file instance.
	 */
	protected File createFile(FileDto file){
		File newFile = new File();

		newFile.setName(file.getName());
		newFile.setFolder(file.isIsFolder());
		newFile.setLastModification(file.getLastModification());
		newFile.setHashId(file.getHashId());
		if(!file.isIsFolder())
		{
			//it's a file
			newFile.setIdType(file.getFileType());
		}

		return newFile;
	}
}
