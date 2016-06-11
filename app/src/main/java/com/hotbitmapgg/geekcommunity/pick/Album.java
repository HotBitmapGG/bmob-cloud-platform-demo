package com.hotbitmapgg.geekcommunity.pick;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dragos Raducanu (raducanu.dragos@gmail.com) on 3/23/14.
 */
public class Album implements Serializable {
    /**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 */
	private static final long serialVersionUID = 1L;
	private String mId;
    private String mName;
    private ArrayList<ImageItem> mImages;
    private boolean isCheck;


    public Album(){
        this.mImages = new ArrayList<ImageItem>();
    }
    public void addImage(ImageItem img) {
        mImages.add(img);
    }
    
    public void addImage(List<ImageItem> imgs){
    	mImages.addAll(imgs);
    }

    public int getCount() {
        return mImages.size();
    }

    public String getThumbPath() {
        if (mImages.size() < 0) {
            return null;
        }
        return mImages.get(0).getPath();
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getId() {
        return this.mId;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return this.mName;
    }

    public ImageItem getImageAt(int index) {
        return mImages.get(index);
    }

    public ArrayList<ImageItem> getImages(){
        return this.mImages;
    }
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
    
}
