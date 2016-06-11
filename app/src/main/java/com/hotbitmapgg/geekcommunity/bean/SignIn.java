package com.hotbitmapgg.geekcommunity.bean;

import java.io.Serializable;
import java.util.Date;

public class SignIn implements Serializable
{

    public String signId;

    public Date signDate;

    public Integer integral;

    public String status;

    public SignIn(String signId, Date signDate, Integer integral, String status)
    {

        this.signId = signId;
        this.signDate = signDate;
        this.integral = integral;
        this.status = status;
    }

    public String getSignId()
    {

        return signId;
    }

    public void setSignId(String signId)
    {

        this.signId = signId;
    }

    public Date getSignDate()
    {

        return signDate;
    }

    public void setSignDate(Date signDate)
    {

        this.signDate = signDate;
    }

    public Integer getIntegral()
    {

        return integral;
    }

    public void setIntegral(Integer integral)
    {

        this.integral = integral;
    }

    public String getStatus()
    {

        return status;
    }

    public void setStatus(String status)
    {

        this.status = status;
    }
}
