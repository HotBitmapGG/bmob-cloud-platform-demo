package com.hotbitmapgg.geekcommunity.bean;

import cn.bmob.v3.BmobObject;

public class AlumnusContentBean extends BmobObject
{
	private User user;

	private String commentContent;

	public AlumnusContentBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public AlumnusContentBean(String tableName)
	{
		super(tableName);
		// TODO Auto-generated constructor stub
	}

	public AlumnusContentBean(User user, String commentContent)
	{
		super();
		this.user = user;
		this.commentContent = commentContent;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getCommentContent()
	{
		return commentContent;
	}

	public void setCommentContent(String commentContent)
	{
		this.commentContent = commentContent;
	}

	@Override
	public String toString()
	{
		return "AlumnusContentBean [user=" + user + ", commentContent=" + commentContent + "]";
	}

}
