package info.zhegui.disconalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class MyActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView mTextView;

    private GoogleApiClient mGoogleApiClient;


    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";


    ArrayList<String> results = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate(" + savedInstanceState + ")");
        setContentView(R.layout.activity_my);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextView.setText("test service");

                Button btnGetNodes = (Button) stub.findViewById(R.id.button);
                btnGetNodes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        getNodes();
                    }
                });

                Button btnSendMsg = (Button) stub.findViewById(R.id.button2);
                btnSendMsg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        sendMessage("ccc");
                    }
                });
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        log("onConnected(" + bundle + ")");
    }

    @Override
    public void onConnectionSuspended(int i) {
        log("onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log("onConnectionFailed(" + connectionResult + ")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy()");
    }

    private void log(String text) {
        Log.d("MyActivity", text);
    }
}
