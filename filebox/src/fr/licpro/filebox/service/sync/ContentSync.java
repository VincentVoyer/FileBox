/**
 * 
 */
package fr.licpro.filebox.service.sync;

import java.io.IOException;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import fr.licpro.filebox.dto.response.FilesDto;
import fr.licpro.filebox.model.File;
import fr.licpro.filebox.persistance.FileDAO;
import fr.licpro.filebox.service.IRestClient;
import fr.licpro.filebox.service.SyncService;
import fr.licpro.filebox.utils.FileCache;

/**
 * @author Vincent
 * @date 22 mai 2014
 */
public class ContentSync extends AbstractSync<Response> {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * mHashId.
	 */
	private String mHashId;

	public ContentSync(String hashId){
		mHashId = hashId;	
	}

	private String getToken(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		return preferences.getString("token", null);
	}
	
	@Override
	protected Response execute(IRestClient pRestClient)
			throws RetrofitError {
		String token = getToken();
		if(token != null){
			return pRestClient.getContent(token, mHashId, null);
		}
		
		return null;
	}

	@Override
	protected void onSuccess() {
		try{
			
			if(!saveInCacheIfFile()){
				FilesDto filesDto = getBodyAs(mData, FilesDto.class);
				if(filesDto != null){
					List<File>  files = convert(filesDto);
					for(File file : files){
						file.setIdParent(mHashId);
					}
					saveFiles(files);				
				}
			}		
			
			Intent intent = new Intent(SyncService.CONTENT_RECEIVE_DONE);
			intent.setPackage("fr.licpro.filebox");
			intent.putExtra("parentHashID", mHashId);
			mContext.sendBroadcast(intent);
		}
		catch(Exception e){
			onError(e);
		}
	}

	/**
	 * @throws IOException
	 */
	private boolean saveInCacheIfFile() throws IOException {
		List<File> filesRecup = FileDAO.getEqDatas(mContext, File.class, "mHashId", mHashId, null, 0);
		File saveFile = filesRecup.get(0);
		if(!saveFile.isFolder()){
			FileCache cache = new FileCache(mContext);
			cache.putFile(mHashId, mData.getBody().in());
			return true;
		}
		return false;
	}

	@Override
	protected void onError(Exception e) {
		Intent intent = new Intent(SyncService.CONTENT_RECEIVE_FAIL);
		intent.setPackage("fr.licpro.filebox");
		intent.putExtra("errorMessage", e.getMessage());
		mContext.sendBroadcast(intent);
	}

}
