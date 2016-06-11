package com.hotbitmapgg.geekcommunity.bean;

import java.io.Serializable;
import java.util.Map;

public class SelectBean implements Serializable
{

	private Map<Integer , PhotoBean> maps ;
	
	

	public SelectBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public SelectBean(Map<Integer, PhotoBean> maps)
	{
		super();
		this.maps = maps;
	}

	public Map<Integer, PhotoBean> getMaps()
	{
		return maps;
	}

	public void setMaps(Map<Integer, PhotoBean> maps)
	{
		this.maps = maps;
	}
	
	
}
