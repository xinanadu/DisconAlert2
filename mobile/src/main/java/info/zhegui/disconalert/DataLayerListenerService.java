package info.zhegui.disconalert;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerSample";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

    @Override
    public void onCreate() {
        super.onCreate();
        log("onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy()");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        log("onDataChanged( " + dataEvents+" )");
        final List events = FreezableUtils
                .freezeIterable(dataEvents);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            log("Failed to connect to GoogleApiClient.");
            return;
        }

//        // Loop through the events and send a message
//        // to the node that created the data item.
//        for (DataEvent event : events) {
//            Uri uri = event.getDataItem().getUri();
//
//            // Get the node id from the host value of the URI
//            String nodeId = uri.getHost();
//            // Set the data of the message to be the bytes of the URI.
//            byte[] payload = uri.toString().getBytes();
//
//            // Send the RPC
//            Wearable.MessageApi.sendMessage(googleApiClient, nodeId,
//                    DATA_ITEM_RECEIVED_PATH, payload);
//        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        log("onMessageReceived("+messageEvent+")");

        playSound();
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        log("onPeerConnected(" + peer + ")");
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);

        log("onPeerDisconnected(" + peer + ")");

        playSound();
    }

    private void playSound() {
        Intent intent=new Intent(this, MyService.class);
        intent.setAction("play_alert_sound");
        startService(intent);

        Intent intent2=new Intent(this, FrownActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent2);
    }

    private void log(String text) {
        Log.d("DataLayerListenerService", "Mobile  ...." + text);
    }


}