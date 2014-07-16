package fr.licpro.filebox.model;

import java.io.Serializable;
import java.util.Date;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.licpro.filebox.dto.enums.FileTypeEnum;

@DatabaseTable(tableName = "File")
public class File implements Serializable{
	
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private static int NB_ELEMENT  = 0;
	
	/**
	 * id.
	 */
	@DatabaseField(id = true)
	private int mId;
	/**
	 * name.
	 */
	@DatabaseField
	private String mName;
	/**
	 * isFolder.
	 */
	@DatabaseField
	private boolean mIsFolder;
	/**
	 * hashId.
	 */
	@DatabaseField
	private String mHashId;
	/**
	 * lastModification.
	 */
	@DatabaseField
	private Date mLastModification;
	/**
	 * idParent.
	 */
	@DatabaseField
	private String mIdParent = "";
	/**
	 * type.
	 */
	@DatabaseField
	private FileTypeEnum mFileType;
	
	public File() {
		NB_ELEMENT--;
		mId = NB_ELEMENT;
	}
	
	/*---------------------------------------------------------------------*/
	/*---------------------------Getters and Setters-----------------------*/
	/*---------------------------------------------------------------------*/
	/**
	 * Return id.
	 * @return the id.
	 */
	public int getId() {
		return mId;
	}
	/**
	 * Return name.
	 * @return the name.
	 */
	public String getName() {
		return mName;
	}
	/**
	 * Return isFolder.
	 * @return the isFolder.
	 */
	public boolean isFolder() {
		return mIsFolder;
	}
	/**
	 * Return hashId.
	 * @return the hashId.
	 */
	public String getHashId() {
		return mHashId;
	}
	/**
	 * Return lastModification.
	 * @return the lastModification.
	 */
	public Date getLastModification() {
		return mLastModification;
	}
	/**
	 * Return idParent.
	 * @return the idParent.
	 */
	public String getIdParent() {
		return mIdParent;
	}
	/**
	 * Return idType.
	 * @return the idType.
	 */
	public FileTypeEnum getIdType() {
		return mFileType;
	}
	/**
	 * Modify id.
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.mId = id;
	}
	/**
	 * Modify name.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.mName = name;
	}
	/**
	 * Modify isFolder.
	 * @param isFolder the isFolder to set
	 */
	public void setFolder(boolean isFolder) {
		this.mIsFolder = isFolder;
	}
	/**
	 * Modify hashId.
	 * @param hashId the hashId to set
	 */
	public void setHashId(String hashId) {
		this.mHashId = hashId;
	}
	/**
	 * Modify lastModification.
	 * @param lastModification the lastModification to set
	 */
	public void setLastModification(Date lastModification) {
		this.mLastModification = lastModification;
	}
	/**
	 * Modify idParent.
	 * @param idParent the idParent to set
	 */
	public void setIdParent(String idParent) {
		this.mIdParent = idParent;
	}
	/**
	 * Modify fileType.
	 * @param idType the idType to set
	 */
	public void setIdType(FileTypeEnum idType) {
		this.mFileType = idType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mHashId == null) ? 0 : mHashId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		File other = (File) obj;
		if (mHashId == null) {
			if (other.mHashId != null)
				return false;
		} else if (!mHashId.equals(other.mHashId))
			return false;
		return true;
	}
	
	
}
