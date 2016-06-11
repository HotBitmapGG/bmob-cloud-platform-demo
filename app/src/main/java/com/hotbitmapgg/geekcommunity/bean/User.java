package com.hotbitmapgg.geekcommunity.bean;

import java.io.Serializable;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

public class User extends BmobChatUser implements Serializable
{

	private static final long serialVersionUID = 1L;

	private BmobRelation blogs;

	private String sortLetters;

	private Boolean sex;

	private Blog blog;

	private BmobGeoPoint location;

	private Integer hight;

	private String signature;

	private BmobFile avatars;

	private BmobRelation favorite;

	private String gerder;

	public Blog getBlog()
	{
		return blog;
	}

	public void setBlog(Blog blog)
	{
		this.blog = blog;
	}

	public Integer getHight()
	{
		return hight;
	}

	public void setHight(Integer hight)
	{
		this.hight = hight;
	}

	public BmobRelation getBlogs()
	{
		return blogs;
	}

	public void setBlogs(BmobRelation blogs)
	{
		this.blogs = blogs;
	}

	public BmobGeoPoint getLocation()
	{
		return location;
	}

	public void setLocation(BmobGeoPoint location)
	{
		this.location = location;
	}

	public Boolean getSex()
	{
		return sex;
	}

	public void setSex(Boolean sex)
	{
		this.sex = sex;
	}

	public String getSortLetters()
	{
		return sortLetters;
	}

	public void setSortLetters(String sortLetters)
	{
		this.sortLetters = sortLetters;
	}

	public String getSignature()
	{
		return signature;
	}

	public void setSignature(String signature)
	{
		this.signature = signature;
	}

	public BmobRelation getFavorite()
	{
		return favorite;
	}

	public void setFavorite(BmobRelation favorite)
	{
		this.favorite = favorite;
	}

	public String getGerder()
	{
		return gerder;
	}

	public void setGerder(String gerder)
	{
		this.gerder = gerder;
	}

	public BmobFile getAvatars()
	{
		return avatars;
	}

	public void setAvatars(BmobFile avatars)
	{
		this.avatars = avatars;
	}

	@Override
	public String toString()
	{
		return "User [blogs=" + blogs + ", sortLetters=" + sortLetters + ", sex=" + sex + ", blog=" + blog + ", location=" + location + ", hight=" + hight + ", signature=" + signature + ", avatars=" + avatars + ", favorite=" + favorite + ", gerder=" + gerder + "]";
	}
	
	

}
