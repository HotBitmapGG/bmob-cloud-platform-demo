package com.hotbitmapgg.geekcommunity.bean;

import java.io.Serializable;
import java.util.List;

public class ImagePackage implements Serializable
{

	private static final long serialVersionUID = 1L;

	private List<String> urls;

	public List<String> getUrls()
	{
		return urls;
	}

	public void setUrls(List<String> urls)
	{
		this.urls = urls;
	}

}
