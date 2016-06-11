package com.hotbitmapgg.geekcommunity.pick;

import java.io.Serializable;

/**
 * Created by Dragos Raducanu (raducanu.dragos@gmail.com) on 3/23/14.
 */
public class ImageItem implements Serializable{
    /**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String mThumbPath;
	private String mPath;
    private String mTitle;
    private String mSubtitle;
    private boolean mSelected;

    public ImageItem() {

    }
    public ImageItem(String path, String title, String subtitle) {
        this.setPath(path);
        this.setTitle(title);
        this.setSubtitle(subtitle);
    }
    
    
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getmThumbPath() {
        return mThumbPath;
    }
    public void setmThumbPath(String mThumbPath) {
        this.mThumbPath = mThumbPath;
    }
    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }
    
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public void setSubtitle(String mSubtitle) {
        this.mSubtitle = mSubtitle;
    }

    public boolean isSelected() {
        return this.mSelected;
    }

    public void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mPath == null) ? 0 : mPath.hashCode());
		result = prime * result
				+ ((mSubtitle == null) ? 0 : mSubtitle.hashCode());
		result = prime * result + ((mTitle == null) ? 0 : mTitle.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageItem other = (ImageItem) obj;
		if (mPath == null) {
			if (other.mPath != null)
				return false;
		} else if (!mPath.equals(other.mPath))
			return false;
		if (mSubtitle == null) {
			if (other.mSubtitle != null)
				return false;
		} else if (!mSubtitle.equals(other.mSubtitle))
			return false;
		if (mTitle == null) {
			if (other.mTitle != null)
				return false;
		} else if (!mTitle.equals(other.mTitle))
			return false;
		return true;
	}
    
}
