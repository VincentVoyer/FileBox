/**
 * 
 */
package fr.licpro.filebox.service.sync;

import java.util.List;

import retrofit.RetrofitError;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import fr.licpro.filebox.dto.response.FilesDto;
import fr.licpro.filebox.model.File;
import fr.licpro.filebox.service.IRestClient;
import fr.licpro.filebox.service.SyncService;

/**
 * @author Vincent
 * @date 22 mai 2014
 */
public class RootSync extends AbstractSync<FilesDto> {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 4771265364924684562L;

	@Override
	protected FilesDto execute(IRestClient pRestClient) throws RetrofitError {

		String token = getToken();
		if(token != null){
			return pRestClient.getRoot(token, null);
		}
		
		return null;
	}

	private String getToken(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		return preferences.getString("token", null);
	}

	@Override
	protected void onSuccess() {
		try{
			// send files list with broadcast 
			if(mData != null){
				List<File> files = convert(mData);
				
				saveFiles(files);
				Intent intent = new Intent(SyncService.ROOT_RECEIVE_DONE);
				intent.setPackage("fr.licpro.filebox");
				mContext.sendBroadcast(intent);
			}
			else{
				onError(new NullPointerException("Convertion failed."));
			}
		}
		catch(Exception e){
			onError(e);
		}
	}

	@Override
	protected void onError(Exception e) {
		Intent intent = new Intent(SyncService.ROOT_RECEIVE_FAIL);
		intent.setPackage("fr.licpro.filebox");
		mContext.sendBroadcast(intent);
	}

	

//	/**
//	 * Get fileType instance from fileTypeEnum value.
//	 * @param fileTypeEnum fileTypeEnum value.
//	 * @return fileType instance.
//	 */
//	private FileType getFileType(FileTypeEnum fileTypeEnum){
//		FileTypeDAO fileTypeDao = new FileTypeDAO();
//		List<FileType> res = fileTypeDao.getAllData(mContext, FileType.class);
//		FileType fileType = FileType.getFileType(fileTypeEnum.name());
//		FileType newFileType = null;
//		
//		if(res != null){
//			for(FileType ft : res){
//				if(ft.getName().equals(fileTypeEnum.name())){
//					newFileType = ft;
//				}
//			}
//		}	
//		
//		if(newFileType == null){
//			fileTypeDao.storeSingleData(mContext, fileType);
//		}
//		
//		return fileType;
//	}

}
