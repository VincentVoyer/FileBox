package fr.licpro.filebox.service;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import fr.licpro.filebox.dto.response.FilesDto;
import fr.licpro.filebox.dto.response.TokenDto;



/**
 * Interface for REST API
 */
public interface IRestClient {
	
	
//	PUT      http://91.121.95.210:443/rest/customer/token?login=<LOGIN>&password=<PASSWORD> 
	//Récupération du token
	@PUT("/customer/token")
	TokenDto getToken(@Query("login") String login, @Query("password") String password);
	
// 	GET      http://91.121.95.210:443/rest/file/<TOKEN>?date=<TIMESTAMP> 
	//Récupération des documents à la racine
	@GET("/file/{token}")
	FilesDto getRoot(@Path("token") String token,@Query("date") String date);
	
//	GET      http://91.121.95.210:443/rest/file/<TOKEN>/<HASH_ID>?date=<TIMESTAMP> 
	//Récupération du contenu d'un document (soit une autre arborescence, soit un fichier)
	@GET("/file/{token}/{hashId}")
	Response getContent(@Path("token") String token,@Path("hashId") String hashId,@Query("date") String date);

}
