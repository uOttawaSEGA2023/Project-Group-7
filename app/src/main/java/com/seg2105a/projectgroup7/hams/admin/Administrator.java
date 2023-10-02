package com.seg2105a.projectgroup7.hams.admin;

import com.seg2105a.projectgroup7.hams.user.User;
public class Administrator extends User {

    private static final Administrator instance = new Administrator();
    private Administrator() {
        super("Admin", "", new byte[]{-24, -75, -102, -39, 80, -12, 115, 64, 23, 90, 96, -36, 15, 103, -14, -123}, "Administrator", "000-000-000", "");
    }

    @Override
    protected void signUp() {
    }
    public static Administrator getInstance() {
        return instance;
    }

    @Override
    public void changeView() {

    }
}
