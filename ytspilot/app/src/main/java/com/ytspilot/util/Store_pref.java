package com.ytspilot.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Store_pref {

    Gson gson = new Gson();


    SharedPreferences pref_master;
    SharedPreferences.Editor editor_pref_master;
    Context c;

    public Store_pref(Context con) {
        c = con;
        pref_master = con.getSharedPreferences("pref_master_store_pref", 0);
    }

    public void open_editor() {
        // TODO Auto-generated method stub
        editor_pref_master = pref_master.edit();
    }

//    public void setPharmacy(Pharmacy mPharmacy) {
//        // TODO Auto-generated method stub
//        open_editor();
//        String ss = gson.toJson(mPharmacy);
//        editor_pref_master.putString("usepharmacy", ss);
//        editor_pref_master.commit();
//    }
//
//    public Pharmacy getPharmacy() {
//        // TODO Auto-generated method stub
//        Pharmacy new_User = gson.fromJson(pref_master.getString("usepharmacy", ""),
//                Pharmacy.class);
//        return new_User;
//
//    }


    public void setGcmEnable(Boolean isenable) {
        // TODO Auto-generated method stub
        open_editor();
        editor_pref_master.putBoolean("isenable", isenable);
        editor_pref_master.commit();
    }

    public Boolean getGcmEnable() {
        // TODO Auto-generated method stub
        return pref_master.getBoolean("isenable", true);
    }

    public void setTrip(Boolean isenable) {
        // TODO Auto-generated method stub
        open_editor();
        editor_pref_master.putBoolean("isenable", isenable);
        editor_pref_master.commit();
    }

    public Boolean getisTrip() {
        // TODO Auto-generated method stub
        return pref_master.getBoolean("isenable", false);
    }

    public void setAutoid(int autoid) {
        // TODO Auto-generated method stub
        open_editor();
        editor_pref_master.putInt("autoid", autoid);
        editor_pref_master.commit();
    }

    public int getAutoid() {
        // TODO Auto-generated method stub
        return pref_master.getInt("autoid", 0);
    }

    public void setStatus(int statusid) {
        // TODO Auto-generated method stub
        open_editor();
        editor_pref_master.putInt("statusid", statusid);
        editor_pref_master.commit();
    }

    public int getStatus() {
        // TODO Auto-generated method stub
        return pref_master.getInt("statusid", 0);
    }


    public void removeAll() {

        open_editor();
        editor_pref_master.clear();
        editor_pref_master.commit();

    }


}
