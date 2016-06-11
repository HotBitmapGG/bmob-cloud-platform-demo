package com.hotbitmapgg.geekcommunity.bean;

import java.io.Serializable;

public class PhotoBean implements Serializable
{


    private String path;

    public PhotoBean()
    {

        super();
        // TODO Auto-generated constructor stub
    }

    public PhotoBean(String path)
    {

        super();
        this.path = path;
    }

    public String getPath()
    {

        return path;
    }

    public void setPath(String path)
    {

        this.path = path;
    }

    @Override
    public String toString()
    {

        return "PhotoBean [path=" + path + "]";
    }
}
