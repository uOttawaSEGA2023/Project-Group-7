package com.seg2105a.projectgroup7.hams.admin;

import com.google.firebase.firestore.Blob;
import com.seg2105a.projectgroup7.hams.user.User;
import com.seg2105a.projectgroup7.hams.user.UserType;

import java.util.HashMap;
import java.util.Map;

public class Administrator extends User {

    private static final Administrator instance = new Administrator();
    private boolean isRegistered = false;
    private Administrator() {
        super("Admin", "", "12345".toCharArray(), "Administrator", "000-000-000", "");
        this.signUp();
    }

    @Override
    protected void signUp() {
        if(isRegistered) return;
        Map<String,Object> user = new HashMap<>();
        user.put("username","Admin");
        user.put("password", Blob.fromBytes(this.getPassword()));
        isRegistered = true;
    }
    public static Administrator getInstance() {
        return instance;
    }

    @Override
    public void changeView() {

    }
}
