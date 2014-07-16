package fr.licpro.filebox;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import fr.licpro.filebox.adapter.FileAdapter;
import fr.licpro.filebox.model.File;
import fr.licpro.filebox.persistance.FileDAO;
import fr.licpro.filebox.service.SyncService;
import fr.licpro.filebox.service.sync.ContentSync;
import fr.licpro.filebox.service.sync.RootSync;


/**
 * A list fragment representing a list of Files. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link FileDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class FileListFragment extends ListFragment {
	
	private class ContentReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(SyncService.ROOT_RECEIVE_DONE.equals(intent.getAction()))
			{
				List<File> files = getFiles();
				if(files != null){
					FileListFragment.this.relaod(files);
				}
				
			}
			else if(SyncService.CONTENT_RECEIVE_DONE.equals(intent.getAction()))
			{
				String parentHashID = intent.getStringExtra("parentHashID");
				List<File> files = FileDAO.getEqDatas(context, File.class, "mIdParent", parentHashID, null, 0);
				if(files != null){
					FileListFragment.this.relaod(files);
				}
			}
			else if(SyncService.ROOT_RECEIVE_FAIL.equals(intent.getAction()) || SyncService.CONTENT_RECEIVE_FAIL.equals(intent.getAction()))
			{
				String msg = intent.getStringExtra("errorMessage") != null ? intent.getStringExtra("errorMessage") : "pas de fichier";
				afficheMessage(context, msg);
			}
		}
		
		public List<File> getFiles()
		{
			return getFiles("");
		}
		
		public List<File> getFiles(String idParent)
		{
			List<File> res = null;
			
			res = FileDAO.getEqDatas(getActivity(), File.class, "mIdParent", idParent, null, 0);

			if(res != null ){
				res = sortFiles(res);
			}			
			return res;
		}
		
		private List<File> sortFiles(List<File> files){
			// TODO: sort file by mIsFolder.
			List<File> folders = new ArrayList<File>();
			List<File> others = new ArrayList<File>();
			List<File> res = new ArrayList<File>();
			
			for(File file : files){
				if(file.isFolder())
				{
					folders.add(file);
				}
				else
				{
					others.add(file);
				}
			}
			
			res.addAll(folders);
			res.addAll(others);
			
			return res;
		}
		
		private void afficheMessage(Context context, String msgUser){
			Crouton.makeText(getActivity(), msgUser,Style.ALERT).show();
			//Toast.makeText(context, msgUser, Toast.LENGTH_SHORT).show();
		}
		
		
	}
	/*-----------------------------------------------------------------------------------*/
	/**
	 * mContentReceiver.
	 */
	private ContentReceiver mContentReceiver;
	

	/**
	 * navigation history.
	 */
	private static Stack<String> mNavigateHistory = new Stack<String>();
	
	private static String currentHashId = "";
	
	/*-----------------------------------------------------------------------------------*/
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	@SuppressWarnings("unused")
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FileListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: make list adapter.
		List<File> mFiles = new ArrayList<File>();
		
		setListAdapter(new FileAdapter(getActivity(), mFiles));
		

	}
	
	@Override
	public void onStart() {
		super.onStart();
		// register service
		if(mContentReceiver == null){
			mContentReceiver = new ContentReceiver();
		}
		getActivity().registerReceiver(mContentReceiver, new IntentFilter(SyncService.CONTENT_RECEIVE_DONE));
		getActivity().registerReceiver(mContentReceiver, new IntentFilter(SyncService.CONTENT_RECEIVE_FAIL));
		getActivity().registerReceiver(mContentReceiver, new IntentFilter(SyncService.ROOT_RECEIVE_DONE));
		getActivity().registerReceiver(mContentReceiver, new IntentFilter(SyncService.ROOT_RECEIVE_FAIL));
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
		if(currentHashId == ""){
			getContent();
		}
		else{
			if(mContentReceiver == null){
				mContentReceiver = new ContentReceiver();
			}
			relaod(mContentReceiver.getFiles(currentHashId));
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

//	@Override
//	public void onResume() {
//		if(mNavigateHistory.isEmpty() || mNavigateHistory.size() == 0){
//			getContent();
//		}
//		else{
//			relaod(mContentReceiver.getFiles(currentHashId));
//		}
//	}
	
	@Override
	public void onStop() {
		if(mContentReceiver != null)
		{
			getActivity().unregisterReceiver(mContentReceiver);
		}
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	};
	
	
	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		//mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
		// TODO: Logique de navigation.
		
		FileAdapter adapter = (FileAdapter)getListAdapter();
		File file = (File)adapter.getItem(position);
		if(file.isFolder()){
			openFolder(file);
		}
		else{
			openDetail(file);
		}
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	public void openFolder(File file){
		if(currentHashId != ""){
			addNavigateParent(currentHashId);
		}
		
		currentHashId = file.getHashId();
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getContent(file.getHashId());
	}
	
	public void openDetail(File file)
	{
//		Intent detail = new Intent(getActivity(),FileDetailActivity.class);
//		StringBuilder sb = new StringBuilder();
//		detail.putExtra("ARG_ITEM_ID",file.getHashId());
//		getActivity().startActivity(detail);
		
		mCallbacks.onItemSelected(file.getHashId());
	}
	
	public void upFolder(){
		String parent = getNavigateParent();
		Log.w("upFolder",parent==null?"":parent);
		if(parent != null){
			getContent(parent);
			currentHashId = parent;
		}
		else
		{
			currentHashId = "";
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
			getContent();
		}
		
	}
	
	private void getContent(){
		Intent monIntent = new Intent(getActivity(),SyncService.class);
		monIntent.putExtra(SyncService.SYNC_CLASS_INTENT, new RootSync());
		getActivity().startService(monIntent);
		
	}
	
	private void getContent(String hashid){
		if(hashid == ""){
			getContent();
		}
		else{
			Intent monIntent = new Intent(getActivity(),SyncService.class);
			monIntent.putExtra(SyncService.SYNC_CLASS_INTENT, new ContentSync(hashid));
			getActivity().startService(monIntent);
		}
				
	}
	
	/**
	 * Return the old parent hashid.
	 * @return the old parent hashid.
	 */
	public String getNavigateParent(){
		if(mNavigateHistory.size() > 0){
			String parent = mNavigateHistory.pop();
			if(mNavigateHistory.size() == 0){
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
			return parent;
		}
		
		return null;
	}
	
	/**
	 * Add new parent hashid.
	 * @param newParent new parent hashid.
	 */
	public void addNavigateParent(String newParent){
		mNavigateHistory.push(newParent);
	}
	
	/**
	 * Refresh the file list.
	 * @param files new file list.
	 */
	public void relaod(List<File> files){
		((FileAdapter)getListAdapter()).load(files);
	}
}
