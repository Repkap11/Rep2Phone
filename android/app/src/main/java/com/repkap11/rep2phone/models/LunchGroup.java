package com.repkap11.rep2phone.models;

/**
 * Created by paul on 8/2/17.
 */

public class LunchGroup {
    public String displayName;
    public Boolean hasWeirdBeer;

    public LunchGroup() {
    }

    public LunchGroup(String displayName, boolean hasWeirdBeer) {
        this();
        this.displayName = displayName;
        this.hasWeirdBeer = hasWeirdBeer;
    }
}
