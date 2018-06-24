package com.example.android.cinematik.pojos;

public class CastMember {

    private String castProfile;
    private String castActorName;
    private String castCharName;

    public CastMember(String castProfile, String castActorName, String castCharName) {
        this.castProfile = castProfile;
        this.castActorName = castActorName;
        this.castCharName = castCharName;
    }

    public String getCastProfile() { return castProfile; }

    public String getCastActorName() { return castActorName; }

    public String getCastCharName() { return castCharName; }


}
