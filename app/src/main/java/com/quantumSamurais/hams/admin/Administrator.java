package com.quantumSamurais.hams.admin;

import android.content.Context;
import android.content.Intent;

import com.quantumSamurais.hams.admin.activities.ViewRequestsActivity;
import com.quantumSamurais.hams.database.Request;
import com.quantumSamurais.hams.database.callbacks.RequestsResponseListener;
import com.quantumSamurais.hams.login.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;
import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.utils.ArrayUtils;

public class Administrator extends User {

    private static final byte[] PASSWORD = new byte[]{24, 119, 82, 18, 72, 61, -68, -20, 6, 24, -96, -33, 41, 103, -43, -84};
    private static final byte[] SALT = new byte[]{-21, -20, 33, 117, 26, -112, -119, 124, -47, -58, 73, -106, 10, -5, -82, 18};

    public Administrator() {
        super(
                "Admin",
                "",
                ArrayUtils.packBytes(PASSWORD),
                ArrayUtils.packBytes(SALT),
                "Administrator",
                "000-000-000",
                ""
        );
    }

    @Override
    public void changeView(Context currentContext) {
        Intent adminView = new Intent(currentContext, ViewRequestsActivity.class);
        adminView.putExtra("userType", UserType.ADMIN);
      // adminView.putExtra("user",this);
        currentContext.startActivity(adminView);
    }




}
