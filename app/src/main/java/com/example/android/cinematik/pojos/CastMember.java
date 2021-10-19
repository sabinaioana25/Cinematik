package com.example.android.cinematik.pojos;

@SuppressWarnings("CanBeFinal")
public class CastMember {

    private final String castProfile;
    private final String castActorName;
    private final String castCharName;

    public CastMember(String castProfile, String castActorName, String castCharName) {
        this.castProfile = castProfile;
        this.castActorName = castActorName;
        this.castCharName = castCharName;
    }

    public String getCastProfile() { return castProfile; }

    public String getCastActorName() { return castActorName; }

    public String getCastCharName() { return castCharName; }
}
