package com.ddbs.cloudnote.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityCollector {
	private static List<Activity> activities = new ArrayList<Activity>();
	public static void addAct(Activity activity){
		activities.add(activity);
	}
	public static void removeAct(Activity activity){
		activities.remove(activity);
	}
	public static void finishAll(){
		for(Activity activity :activities){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
	}
}
