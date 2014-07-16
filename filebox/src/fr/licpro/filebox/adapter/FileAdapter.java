package fr.licpro.filebox.adapter;

import java.util.ArrayList;
import java.util.List;

import fr.licpro.filebox.R;
import fr.licpro.filebox.model.File;
import fr.licpro.filebox.model.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Vincent
 * @date 5 juin 2014
 */
public class FileAdapter extends BaseAdapter {

	private List<File> mFiles;
	private Context mContext;
	
	public FileAdapter(Context context,List<File>files){
		setContext(context);
		setFiles(files);
	}
	
	private void setContext(Context context){
		if(context == null){
			throw new NullPointerException("le context doit être renseigner.");
		}
		mContext = context;
	}
	
	private void setFiles(List<File> files){
		mFiles = files;
	}
	
	/**
	 * Méthode utilisée pour chaque élément affiché de la liste.
	 */
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder = null;
		  // Si la vue n'est pas recyclée
		  if(convertView == null) {
		    // On récupère le layout
		    convertView  = LayoutInflater.from(mContext).inflate(R.layout.list_template_file, null);
		    			
		    holder = new ViewHolder();
		    // On place les widgets de notre layout dans le holder
		    holder.fileNameList = (TextView) convertView.findViewById(R.id.fileNameList);
		    holder.isFolder = (ImageView) convertView.findViewById(R.id.isFolder);
		    			
		    // puis on insère le holder en tant que tag dans le layout
		    convertView.setTag(holder);
		  } else {
		    // Si on recycle la vue, on récupère son holder en tag
		    holder = (ViewHolder)convertView.getTag();
		  }
		    
		  // Dans tous les cas, on récupère le contact téléphonique concerné
		  File file = (File)getItem(position);
		  // Si cet élément existe vraiment…
		  if(file != null) {
		    // On place dans le holder les informations sur le contact
		    holder.fileNameList.setText(file.getName());
		    
		    if(file.isFolder()){
		    	
		    	holder.isFolder.setImageResource(R.drawable.dossier);
		    }
		    else
		    {
		    	holder.isFolder.setImageResource(getImgRessource(file));
		    }
		    //holder.isFolder.setVisibility(file.isFolder()?View.VISIBLE:View.INVISIBLE);
		  }
		  return convertView;
	}

	private int getImgRessource(File file){
		switch (file.getIdType()) {
		case HTML:
			return R.drawable.html;
		case JPEG:
			return R.drawable.jpg;
		case MP3:
			return R.drawable.mp3;
		case PDF:
			return R.drawable.pdf;
		case PNG:
			return R.drawable.png;
		case TEXT:
			return R.drawable.ico_default;
		case VCARD:
			return R.drawable.vcard;
		}
		return R.drawable.dossier;
	}
	@Override
	public int getCount() {
		return mFiles.size();
	}

	@Override
	public Object getItem(int position) {
		return mFiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mFiles.get(position).getId();
	}
	
	public void addFile(File file){
		if(mFiles == null){
			mFiles = new ArrayList<File>();
		}
		
		mFiles.add(file);
		
		notifyDataSetChanged();
	}
	
	public void load(List<File> files){
		mFiles = files;
		notifyDataSetChanged();
	}

}
