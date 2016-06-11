package com.hotbitmapgg.geekcommunity.bean;

import java.io.Serializable;

public class NewsContentBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int isImage;

	private String content;
	
	

	public NewsContentBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public NewsContentBean(int isImage, String content)
	{
		super();
		this.isImage = isImage;
		this.content = content;
	}

	public int getIsImage()
	{
		return isImage;
	}

	public void setIsImage(int isImage)
	{
		this.isImage = isImage;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	@Override
	public String toString()
	{
		return "NewsContentBean [isImage=" + isImage + ", content=" + content + "]";
	}

}
