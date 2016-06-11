package com.hotbitmapgg.geekcommunity.bean;

import java.io.Serializable;

public class AuthCodeBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String result;

	private UserPhoneBean userPhoneInfo;

	private String desc;

	private String resultDesc;

	public AuthCodeBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public AuthCodeBean(String result, UserPhoneBean userPhoneInfo, String desc, String resultDesc)
	{
		super();
		this.result = result;
		this.userPhoneInfo = userPhoneInfo;
		this.desc = desc;
		this.resultDesc = resultDesc;
	}

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public UserPhoneBean getUserPhoneInfo()
	{
		return userPhoneInfo;
	}

	public void setUserPhoneInfo(UserPhoneBean userPhoneInfo)
	{
		this.userPhoneInfo = userPhoneInfo;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public String getResultDesc()
	{
		return resultDesc;
	}

	public void setResultDesc(String resultDesc)
	{
		this.resultDesc = resultDesc;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	@Override
	public String toString()
	{
		return "AuthCodeBean [result=" + result + ", userPhoneInfo=" + userPhoneInfo + ", desc=" + desc + ", resultDesc=" + resultDesc + "]";
	}

}
