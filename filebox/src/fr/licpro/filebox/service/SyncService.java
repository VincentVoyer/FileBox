package fr.licpro.filebox.service;

import fr.licpro.filebox.service.json.JacksonConverter;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * Service for sync data
 */
public class SyncService extends IntentService {
	/*--------------------------------- Constantes ---------------------------------*/
	/**
	 * VALIDATION_DONE.
	 */
	public static final String VALIDATION_DONE = "fr.iut.licpro.sync.SYNCDONE";

	/**
	 * VALIDATION_FAIL.
	 */
	public static final String VALIDATION_FAIL = "fr.iut.licpro.sync.SYNCFAIL";

	/**
	 * ROOT_RECEIVE_DONE.
	 */
	public static final String ROOT_RECEIVE_DONE = "fr.iut.licpro.sync.ROOTRECEIVEDONE";

	/**
	 * ROOT_RECEIVE_FAIL.
	 */
	public static final String ROOT_RECEIVE_FAIL = "fr.iut.licpro.sync.ROOTRECEIVEFAIL";

	/**
	 * CONTENT_RECEIVE_DONE.
	 */
	public static final String CONTENT_RECEIVE_DONE = "fr.iut.licpro.sync.CONTENTRECEIVEDONE";

	/**
	 * CONTENT_RECEIVE_FAIL.
	 */
	public static final String CONTENT_RECEIVE_FAIL = "fr.iut.licpro.sync.CONTENTRECEIVEFAIL";

	/**
	 * LOCK.
	 */
	private static final Object LOCK = new Object();


	public static class ConnectivityReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(SyncService.isNetworkAvailable(context))
			{
				synchronized (LOCK) {
					mConnected = true;
					LOCK.notifyAll();
				}
			}

		}

	}

	/*--------------------------------- Attributes ---------------------------------*/
	/**
	 * mConnected.
	 */
	private static boolean mConnected = false;

	/**
	 * API Url for filebox service.
	 */
	private final static String API_URL = "http://91.121.95.210:443/rest";

	/**
	 * SyncService Tag for Log 
	 */
	@SuppressWarnings("unused")
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
		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint(API_URL)
		.setLogLevel(LogLevel.FULL)
		.setConverter(new JacksonConverter())
		.build();

		mRestClient = restAdapter.create(IRestClient.class);

	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return info != null && info.isConnectedOrConnecting() ;
	}

	@Override
	protected void onHandleIntent(final Intent pIntent) {
		if(SyncService.isNetworkAvailable(getApplicationContext())){
			ISync sync = (ISync) pIntent.getSerializableExtra(SYNC_CLASS_INTENT);
			sync.execute(getApplicationContext(), mRestClient);	
		}
		else
		{
			mConnected = false;
			ConnectivityReceiver receiver = new ConnectivityReceiver();
			registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
			synchronized (LOCK) {
				try
				{
					while(!mConnected)
					{
						LOCK.wait(5000);
					}
				}
				catch(InterruptedException exception)
				{
					Log.e(SyncService.class.getName(), exception.getMessage());
				}
			}
		}

	}
}
