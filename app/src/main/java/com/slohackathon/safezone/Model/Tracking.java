package com.slohackathon.safezone.Model;

public class Tracking {
    String email,uid,lat,lon;
    public Tracking(String email, String uid, String lat, String lon)
    {
        this.email=email;
        this.lat=lat;
        this.uid=uid;
        this.lon=lon;

    }
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
