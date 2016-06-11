package com.hotbitmapgg.geekcommunity.bean;

import java.io.Serializable;

public class UserPhoneBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String actCode;

	private String key;

	private String phone;

	private long valid;

	public UserPhoneBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public UserPhoneBean(String actCode, String key, String phone, long valid)
	{
		super();
		this.actCode = actCode;
		this.key = key;
		this.phone = phone;
		this.valid = valid;
	}

	public String getActCode()
	{
		return actCode;
	}

	public void setActCode(String actCode)
	{
		this.actCode = actCode;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public long getValid()
	{
		return valid;
	}

	public void setValid(long valid)
	{
		this.valid = valid;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	@Override
	public String toString()
	{
		return "UserPhoneBean [actCode=" + actCode + ", key=" + key + ", phone=" + phone + ", valid=" + valid + "]";
	}

}
