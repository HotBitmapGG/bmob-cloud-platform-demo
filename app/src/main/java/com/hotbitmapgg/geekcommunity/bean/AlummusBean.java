package com.hotbitmapgg.geekcommunity.bean;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class AlummusBean extends BmobObject implements Serializable
{
	private static final long serialVersionUID = -6280656428527540320L;

	private User author;

	private String content;

	private BmobFile Contentfigureurl;

	private List<BmobFile> Contentfigureurls;

	private List<String> thumbnailUrls;

	private int love;

	private int hate;

	private int share;

	private int comment;

	private boolean isPass;

	private boolean myFav;

	private boolean myLove;

	private BmobRelation relation;

	private String formPhone;

	private int commentNum;

	private List<String> names;

	public List<String> getNames()
	{
		return names;
	}

	public void setNames(List<String> names)
	{
		this.names = names;
	}

	public int getCommentNum()
	{
		return commentNum;
	}

	public void setCommentNum(int commentNum)
	{
		this.commentNum = commentNum;
	}

	public String getFormPhone()
	{
		return formPhone;
	}

	public void setFormPhone(String formPhone)
	{
		this.formPhone = formPhone;
	}

	public BmobRelation getRelation()
	{
		return relation;
	}

	public void setRelation(BmobRelation relation)
	{
		this.relation = relation;
	}

	public User getAuthor()
	{
		return author;
	}

	public void setAuthor(User author)
	{
		this.author = author;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public BmobFile getContentfigureurl()
	{
		return Contentfigureurl;
	}

	public void setContentfigureurl(BmobFile contentfigureurl)
	{
		Contentfigureurl = contentfigureurl;
	}

	public int getLove()
	{
		return love;
	}

	public void setLove(int love)
	{
		this.love = love;
	}

	public int getHate()
	{
		return hate;
	}

	public void setHate(int hate)
	{
		this.hate = hate;
	}

	public int getShare()
	{
		return share;
	}

	public void setShare(int share)
	{
		this.share = share;
	}

	public int getComment()
	{
		return comment;
	}

	public void setComment(int comment)
	{
		this.comment = comment;
	}

	public boolean isPass()
	{
		return isPass;
	}

	public void setPass(boolean isPass)
	{
		this.isPass = isPass;
	}

	public boolean getMyFav()
	{
		return myFav;
	}

	public void setMyFav(boolean myFav)
	{
		this.myFav = myFav;
	}

	public boolean getMyLove()
	{
		return myLove;
	}

	public void setMyLove(boolean myLove)
	{
		this.myLove = myLove;
	}

	public List<BmobFile> getContentfigureurls()
	{
		return Contentfigureurls;
	}

	public void setContentfigureurls(List<BmobFile> contentfigureurls)
	{
		Contentfigureurls = contentfigureurls;
	}

	public List<String> getThumbnailUrls()
	{
		return thumbnailUrls;
	}

	public void setThumbnailUrls(List<String> thumbnailUrls)
	{
		this.thumbnailUrls = thumbnailUrls;
	}

	@Override
	public String toString()
	{
		return "AlummusBean [author=" + author + ", content=" + content + ", Contentfigureurl=" + Contentfigureurl + ", Contentfigureurls=" + Contentfigureurls + ", thumbnailUrls=" + thumbnailUrls + ", love=" + love + ", hate=" + hate + ", share=" + share + ", comment=" + comment + ", isPass=" + isPass + ", myFav=" + myFav + ", myLove=" + myLove + ", relation=" + relation + "]";
	}

}
