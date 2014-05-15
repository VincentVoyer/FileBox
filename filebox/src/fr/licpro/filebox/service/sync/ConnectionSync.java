package fr.licpro.filebox.service.sync;

import retrofit.RetrofitError;
import fr.licpro.filebox.dto.response.TokenDto;
import fr.licpro.filebox.service.IRestClient;


/**
 * Method to Sync the startUp data
 */
public class ConnectionSync extends AbstractSync<TokenDto> {

	
	@Override
	protected TokenDto execute(final IRestClient pRestClient) throws RetrofitError {
		return null;
	}

	@Override
	protected void onSuccess() {
		
	}

	@Override
	protected void onError(Exception e) {
		
	}

}
