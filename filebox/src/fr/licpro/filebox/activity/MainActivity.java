package fr.licpro.filebox.activity;

import java.util.Stack;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
//import android.widget.Toast;
import fr.licpro.filebox.FileListActivity;
import fr.licpro.filebox.R;
import fr.licpro.filebox.service.SyncService;
import fr.licpro.filebox.service.sync.ConnectionSync;
import fr.licpro.filebox.utils.FileboxConstant;

public class MainActivity extends Activity implements OnClickListener {

	private class SyncDoneReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(SyncService.VALIDATION_DONE.equals(intent.getAction())){
//				String msgUser = "Connexion établie.";
//				afficheMessage(context, msgUser);
				
				addToken(intent);
				openListFile(context);
			
			}
			else if(SyncService.VALIDATION_FAIL.equals(intent.getAction()))
			{
			
				String msgUser = "erreur dans la connexion.Mot de passe et/ou Identifiant erroné";
				afficheMessage(context, msgUser);
				
			}
		}
		
		private void addToken(Intent intent){
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			Editor editor = pref.edit();
			editor.putString("token", intent.getStringExtra("token"));
			editor.commit();
		}
		
		private void afficheMessage(Context context, String msgUser){
			Crouton.makeText(MainActivity.this, msgUser,Style.ALERT).show();
			//Toast.makeText(context, msgUser, Toast.LENGTH_SHORT).show();
		}
		
		private void openListFile(Context context){
			Intent intent = new Intent(context,FileListActivity.class);
			context.startActivity(intent);
		}
		
	}
	/*----------------------------------------------------------------------*/
	
	/**
	 * syncDoneReceiver.
	 */
	private SyncDoneReceiver syncDoneReceiver;
	/**
	 * login.
	 */
	private TextView login;
	/**
	 * password.
	 */
	private TextView password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialise textview element to receiv data
		login    = (TextView)findViewById(R.id.login);
		password = (TextView)findViewById(R.id.password);
		Button connect  = (Button)findViewById(R.id.connect);
		connect.setOnClickListener(this);
		
		
		syncDoneReceiver = new SyncDoneReceiver();
		registerReceiver(syncDoneReceiver, new IntentFilter(SyncService.VALIDATION_DONE));
		registerReceiver(syncDoneReceiver, new IntentFilter(SyncService.VALIDATION_FAIL));
	}
	
	@Override
	protected void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	};
	
	@Override
	protected void onStop() {
		if(syncDoneReceiver != null)
		{
			unregisterReceiver(syncDoneReceiver);
		}
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		String loginContent = login.getText().toString();
		String passwordContent = password.getText().toString();
		
		if(!loginContent.isEmpty()){
			if(!passwordContent.isEmpty()){
				connectionRequest(loginContent, passwordContent);
			}
			else
			{
				informerUtilisateur(FileboxConstant.MSG_ERROR_PWD);
			}
		}
		else
		{
			informerUtilisateur(FileboxConstant.MSG_ERROR_LOGIN);
		}
	}

	/**
	 * Realise the connection request.
	 * @param login user login.
	 * @param password user password.
	 */
	private void connectionRequest(String login, String password){
		Intent monIntent = new Intent(this,SyncService.class);
		monIntent.putExtra(SyncService.SYNC_CLASS_INTENT, new ConnectionSync(login, password));
		startService(monIntent);
	}
	
	/**
	 * Informe l'utilisateur d'un message.
	 * @param msgUser message à donner à l'utilisateur.
	 */
	private void informerUtilisateur(String msgUser) {
		Crouton.makeText(MainActivity.this, msgUser,Style.INFO).show();
		//Toast.makeText(this, msgUser, Toast.LENGTH_SHORT).show();
	}

}
