package com.example.android.cinematik.pojos;

/**
 * Created by Sabina on 4/7/2018.
 */

public class CastMember {

    private String castActorName;
    private String castCharName;
    private String castProfile;

    public CastMember(String castActorName, String castCharName, String castProfile) {
        this.castActorName = castActorName;
        this.castCharName = castCharName;
        this.castProfile = castProfile;
    }

    public String getCastActorName() { return castActorName; }

    public String getCastCharName() { return castCharName; }

    public String getCastProfile() { return castProfile; }
}
