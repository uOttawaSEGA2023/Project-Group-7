package com.quantumSamurais.hams.admin.listeners;

import android.content.Intent;
import android.view.View;

public interface RequestsActivityListener {
    void onAcceptClick(int position);
    void onRejectClick(int position);
    void onShowMoreClick(int position, Intent showMore);
}
