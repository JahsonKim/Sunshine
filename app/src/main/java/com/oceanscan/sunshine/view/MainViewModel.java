package com.oceanscan.sunshine.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.Nullable;

public class MainViewModel extends AndroidViewModel {
private String[ ] data ;
    public MainViewModel(@Nullable Application application){
        super(application);
    }

    public String[] getData() {
        return data;
    }
}
