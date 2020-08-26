package com.example.myproject1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = "Broadcast tag";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context, "Geofences triggered", Toast.LENGTH_SHORT).show();

    }
    /*

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            Log.d(TAG,"onRecieve: error recieving geofence event" );
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

        for(Geofence geofence : geofenceList){
            Log.d(TAG, "onRecieve"+geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL",Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context,"GEOFENCE_TRANSITION_EXIT",Toast.LENGTH_SHORT).show();
                break;

        }
    }*/
}
