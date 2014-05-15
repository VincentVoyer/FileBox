package fr.licpro.filebox.service;

import android.app.IntentService;
import android.content.Intent;


/**
 * Service for sync data
 */
public class SyncService extends IntentService {
	/**
	 * SyncService Tag for Log 
	 */
	private static final String TAG = SyncService.class.getSimpleName();
	
	/**
	 * Data in the intent
	 */
	public static final String SYNC_CLASS_INTENT = "fr.licpro.filebox.syncData";

	/**
	 * Client rest
	 */
	protected IRestClient mRestClient;

	/**
	 * Service constructor
	 */
	public SyncService() {
		super(SyncService.class.getSimpleName());
		
		// TODO create RestClient @see https://github.com/square/retrofit/blob/master/retrofit-samples/github-client/src/main/java/com/example/retrofit/GitHubClient.java
		//....
	
	}

	@Override
	protected void onHandleIntent(final Intent pIntent) {

		ISync sync = (ISync) pIntent.getSerializableExtra(SYNC_CLASS_INTENT);
		sync.execute(getApplicationContext(), mRestClient);
	}
}
