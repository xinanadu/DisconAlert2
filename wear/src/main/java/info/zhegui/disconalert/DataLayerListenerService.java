package info.zhegui.disconalert;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerSample";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        if (Log.isLoggable(TAG, Log.DEBUG)) {
//            Log.d(TAG, "onDataChanged: " + dataEvents);
//        }
//        final List events = FreezableUtils
//                .freezeIterable(dataEvents);
//
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .build();
//
//        ConnectionResult connectionResult =
//                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);
//
//        if (!connectionResult.isSuccess()) {
//            Log.e(TAG, "Failed to connect to GoogleApiClient.");
//            return;
//        }
//
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
//    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        log("onPeerConnected("+peer+")");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        startActivityFrown();
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);

        startActivityFrown();

        log("onPeerDisconnected("+peer+")");
    }

    private void startActivityFrown(){
        Intent intent=new Intent(this, FrownActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void log(String text) {
        Log.d("DataLayerListenerService", "Mobile  ...."+ text);
}}