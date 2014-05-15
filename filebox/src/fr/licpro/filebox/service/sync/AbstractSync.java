package fr.licpro.filebox.service.sync;


import retrofit.RetrofitError;
import android.content.Context;
import fr.licpro.filebox.service.IRestClient;
import fr.licpro.filebox.service.ISync;

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

}
