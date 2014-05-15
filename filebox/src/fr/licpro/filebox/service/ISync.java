package fr.licpro.filebox.service;


import java.io.Serializable;

import android.content.Context;


/**
 * Interface for sync object
 */
public interface ISync extends Serializable{
	
	/**
	 * Method used to execute sync 
	 * @param pContext application context
	 * @param pRestClient rest client
	 * @return
	 */
	boolean execute(final Context pContext, final IRestClient pRestClient);

}
