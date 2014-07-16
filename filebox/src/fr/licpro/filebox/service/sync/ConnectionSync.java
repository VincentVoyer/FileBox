package fr.licpro.filebox.service.sync;

import android.content.Intent;
import retrofit.RetrofitError;
import fr.licpro.filebox.dto.response.TokenDto;
import fr.licpro.filebox.service.IRestClient;
import fr.licpro.filebox.service.SyncService;


/**
 * Method to Sync the startUp data
 */
public class ConnectionSync extends AbstractSync<TokenDto> {
	private static final String ERROR_MESSAGE_LOGIN = "";
	private static final String ERROR_MESSAGE_PWD = "";
	private String login;
	private String password;
	
	public ConnectionSync(String login, String password)
	throws NullPointerException
	{
		setLogin(login);
		setPassword(password);
	}
	
	private void setPassword(String password)
			throws NullPointerException{
		if(password == null)
		{
			throw new NullPointerException(ERROR_MESSAGE_PWD);
		}
		this.password = password;
		
	}

	private void setLogin(String login)
			throws NullPointerException{
		if(login == null)
		{
			throw new NullPointerException(ERROR_MESSAGE_LOGIN);
		}
		this.login = login;
	}

	@Override
	protected TokenDto execute(final IRestClient pRestClient) throws RetrofitError {
		return pRestClient.getToken(login, password);
	}

	@Override
	protected void onSuccess() {
		Intent intent = new Intent(SyncService.VALIDATION_DONE);
		intent.setPackage("fr.licpro.filebox");
		intent.putExtra("token",mData.getToken());
		mContext.sendBroadcast(intent);
	}

	@Override
	protected void onError(Exception e) {
		Intent intent = new Intent(SyncService.VALIDATION_FAIL);
		intent.setPackage("fr.licpro.filebox");
		mContext.sendBroadcast(intent);
	}
	
}
