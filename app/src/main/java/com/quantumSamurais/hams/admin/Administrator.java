package com.quantumSamurais.hams.admin;

import static com.quantumSamurais.hams.utils.ArrayUtils.convertByteArrayToList;

import android.content.Context;
import android.content.Intent;

import com.google.common.primitives.Bytes;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantumSamurais.hams.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Administrator extends User {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final byte[] password = new byte[]{24, 119, 82, 18, 72, 61, -68, -20, 6, 24, -96, -33, 41, 103, -43, -84};
    private static final byte[] salt = new byte[]{-21, -20, 33, 117, 26, -112, -119, 124, -47, -58, 73, -106, 10, -5, -82, 18};

    public Administrator() {
        super(
                "Admin",
                "",
                password,
                salt,
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
