package fr.licpro.filebox;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.DateFormat;
import java.util.List;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.licpro.filebox.model.File;
import fr.licpro.filebox.persistance.FileDAO;
import fr.licpro.filebox.service.SyncService;
import fr.licpro.filebox.service.sync.ContentSync;
import fr.licpro.filebox.utils.FileCache;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A fragment representing a single File detail screen. This fragment is either
 * contained in a {@link FileListActivity} in two-pane mode (on tablets) or a
 * {@link FileDetailActivity} on handsets.
 */
public class FileDetailFragment extends Fragment implements OnClickListener {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	private class DataReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(SyncService.CONTENT_RECEIVE_DONE.equals(intent.getAction()))
			{
				View rootView = LayoutInflater.from(context).inflate(R.layout.fragment_file_detail,null);
				FileDetailFragment.this.addDoc(rootView);
			}
			else if(SyncService.CONTENT_RECEIVE_FAIL.equals(intent.getAction()))
			{
				String msg = intent.getStringExtra("errorMessage") != null ? intent.getStringExtra("errorMessage") : "erreur de lecture.";
				Log.w(SyncService.CONTENT_RECEIVE_FAIL, msg);
				msg = "erreur de lecture.";
				afficheMessage(context, msg);
			}
		}

		private void afficheMessage(Context context, String msgUser){
			Log.w("FileDetail", msgUser);
			Crouton.makeText(getActivity(), msgUser,Style.ALERT).show();
			//Toast.makeText(context, msgUser, Toast.LENGTH_SHORT).show();
		}


	}
	/*-----------------------------------------------------------------------------------*/
	/**
	 * mDataReceiver.
	 */
	private DataReceiver mDataReceiver;
	/**
	 * mItem.
	 */
	private File mItem;


	private ImageView img;
	private WebView web;
	private TextView text;
	private Button open;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FileDetailFragment() {
	}

	@Override
	public void onStart() {
		super.onStart();
		// register service
		mDataReceiver = new DataReceiver();
		getActivity().registerReceiver(mDataReceiver, new IntentFilter(SyncService.CONTENT_RECEIVE_DONE));
		getActivity().registerReceiver(mDataReceiver, new IntentFilter(SyncService.CONTENT_RECEIVE_FAIL));

	}

	@Override
	public void onStop() {
		if(mDataReceiver != null)
		{
			getActivity().unregisterReceiver(mDataReceiver);
		}
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the file content specified by the fragment.
			//mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
			if(getArguments().getString(ARG_ITEM_ID) != null)
			{
				List<File> files = FileDAO.getEqDatas(getActivity(), File.class, "mHashId", getArguments().getString(ARG_ITEM_ID), null, 0);
				if(files != null){
					mItem = files.get(0);
				}
			}
			else
			{
				Log.w("getArgument", "l'agument est null");
			}


		}
	}

	@Override
	public void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_file_detail,container, false);
		//View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_file_detail, null);
		// Show the file content.

		img = (ImageView)rootView.findViewById(R.id.imageViewDetail);
		web = (WebView)rootView.findViewById(R.id.webViewDetail);
		text = (TextView)rootView.findViewById(R.id.textViewDetail);
		open = (Button)rootView.findViewById(R.id.open);

		open.setOnClickListener(this);

		TextView fileName = (TextView) rootView.findViewById(R.id.fileNameList);
		TextView lastModif =  (TextView) rootView.findViewById(R.id.fileDate);

		if(mItem != null){
			if(fileName != null && lastModif != null){
				DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
						DateFormat.SHORT,
						DateFormat.SHORT);
				fileName.setText(mItem.getName().substring(0, mItem.getName().indexOf(".")).toUpperCase());
				lastModif.setText(shortDateFormat.format(mItem.getLastModification()));

				getFileContent();
			}
			else
			{
				Crouton.makeText(getActivity(), "Erreur: une erreur est survenue lors du chargement de la vue.",Style.ALERT).show();
			}
		}
		else
		{
			Crouton.makeText(getActivity(), "Le fichier n'a pas été trouver",Style.INFO).show();
		}


		return rootView;
	}

	/**
	 * Récupère le contenu du fichier.
	 */
	private void getFileContent() {
		Intent monIntent = new Intent(getActivity(),SyncService.class);
		monIntent.putExtra(SyncService.SYNC_CLASS_INTENT, new ContentSync(mItem.getHashId()));
		getActivity().startService(monIntent);
	}

	public void addDoc(View rootView){
		FileCache cache = new FileCache(getActivity());
		try{
			java.io.File f = cache.getFile(mItem.getHashId());

			switch (mItem.getIdType()) {
			case JPEG :
			case PNG :
				Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
				if(bitmap != null){
					img.setImageBitmap(bitmap);
					img.setVisibility(View.VISIBLE);
					img.invalidate();
				}
				else{
					Log.e("addDoc Image", "bitmap is null.");
				}
				hideOpenButton();
				break;
			case HTML:
				web.loadUrl("file://"+f.getAbsolutePath());
				web.setVisibility(View.VISIBLE);
				web.invalidate();
				hideOpenButton();
				break;
			case TEXT:

				BufferedReader reader = new BufferedReader(new FileReader(f));
				StringBuffer buffer = new StringBuffer();
				String aLine = null;
				while ( (aLine =reader.readLine()) != null) {
					buffer.append(aLine).append("\n");
				}

				text.setText(buffer.toString());
				reader.close();
				buffer = null;
				reader = null;
				text.setVisibility(View.VISIBLE);
				text.invalidate();
				hideOpenButton();
				break;
			default:
				break;
			}
			f = null;
			System.gc();
		}
		catch(Exception e){
			Log.e("Mise en page du doc", e.getMessage());
			Crouton.makeText(getActivity(), "Erreur: lecture du fichier momentanement impossible.",Style.INFO).show();
		}

	}

	/**
	 * @param rootView
	 */
	private void hideOpenButton() {

		open.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		FileCache cache = new FileCache(getActivity());
		java.io.File file = cache.getFile(mItem.getHashId());
		//String path = file.getAbsolutePath();
		Uri path = Uri.parse("file://"+file.getAbsolutePath()); 
		String ext = mItem.getName().substring(mItem.getName().indexOf(".")+1).toLowerCase();
		StringBuilder format = new StringBuilder();

		switch (mItem.getIdType()) {
		case MP3:
			format.append("audio/");
			break;
		case PDF:
		case  VCARD:
			format.append("application/");
			break;
		default:
			break;
		}
		format.append(ext);

		Log.w("open path", path.toString());
		Log.w("open format", format.toString());
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType( path,  format.toString());
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			getActivity().startActivity(intent);
		}
		catch(ActivityNotFoundException e)
		{
			Crouton.makeText(getActivity(), "Aucune application n'a été trouvée pour lire ce fichier.",Style.INFO).show();
		}
		
	}

}
