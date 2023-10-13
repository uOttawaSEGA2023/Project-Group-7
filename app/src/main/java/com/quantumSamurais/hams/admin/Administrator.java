package com.quantumSamurais.hams.admin;

import android.content.Context;
import android.content.Intent;

import com.quantumSamurais.hams.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;
public class Administrator extends User {

    public Administrator() {
        super(
                "Admin",
                "",
                new byte[]{-24, -75, -102, -39, 80, -12, 115, 64, 23, 90, 96, -36, 15, 103, -14, -123},
                "Administrator",
                "000-000-000",
                ""
        );
    }

    @Override
    public void changeView(Context currentContext) {
        Intent adminView = new Intent(currentContext,LoginInteractiveMessage.class);
        adminView.putExtra("userType", UserType.ADMIN);
      // adminView.putExtra("user",this);
        currentContext.startActivity(adminView);
    }
}
