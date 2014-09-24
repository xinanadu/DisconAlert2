package info.zhegui.disconalert;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;


public class MyActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView tvConnState;


    private GoogleApiClient mGoogleApiClient;


    ArrayList<String> results = new ArrayList<String>();


    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            tvConnState.append("\n" + (String) msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate(" + savedInstanceState + ")");
        setContentView(R.layout.activity_my);


        tvConnState = (TextView) findViewById(R.id.tv_conn_state);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        getNodes();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy()");

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void getNodes() {
        new Thread() {
            public void run() {

                NodeApi.GetConnectedNodesResult nodes =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for (Node node : nodes.getNodes()) {
                    results.add(node.getId());
                    log(".....node.getId():" + node.getId());
                }


            }
        }.start();
    }

    private void sendMessage(String text) {
        if (results.size() == 0) {
            return;
        }

        new Thread() {
            public void run() {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                        mGoogleApiClient, results.get(0), DATA_ITEM_RECEIVED_PATH, null).await();
                if (!result.getStatus().isSuccess()) {
                    log("ERROR: failed to send Message: " + result.getStatus());
                }else{
                    log("succeed in sending Message:");
                }


            }
        }.start();
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    private void log(String text) {
        Log.d("MyActivity", text);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log("onConnectionFailed(" + connectionResult + ")");
    }

    @Override
    public void onConnected(Bundle bundle) {
        log("onConnected(" + bundle + ")");


        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        log("onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        log("onMessageReceived(" + messageEvent + ")");
        Message msg = mHandler.obtainMessage();
        msg.obj = messageEvent.toString();
        msg.sendToTarget();
    }
}
