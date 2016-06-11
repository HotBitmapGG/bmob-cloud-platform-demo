package com.hotbitmapgg.geekcommunity.bean;

import java.io.Serializable;

public class BaseResponse implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String result;

    private String desc;

    /**
     * @return the result
     */
    public String getResult()
    {

        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result)
    {

        this.result = result;
    }

    /**
     * @return the desc
     */
    public String getDesc()
    {

        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc)
    {

        this.desc = desc;
    }
}
