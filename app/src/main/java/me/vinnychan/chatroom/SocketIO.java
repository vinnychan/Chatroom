package me.vinnychan.chatroom;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

public class SocketIO {
    public static final String SERVER = "http://104.236.100.179:3000";
    private Socket mSocket;
    private Emitter.Listener listener;

    public void connect() {
        try {
            mSocket = IO.socket(SERVER);
            mSocket.open();
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    public void attemptSend(String username, String message) {
        if (TextUtils.isEmpty(message) || TextUtils.isEmpty(username)) {
            return;
        }

        Log.i("SocketIO", "Attempting to send " + username + " " + message);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("message", message);
            jsonObject.put("username", username);
            mSocket.emit("new message", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onMessage(final Activity activity, final MessageListener onMessageListener) {
        this.listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Log.i("SocketIO", "RECEIVED MESSAGE");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String username;
                        String message;
                        try {
                            JSONObject data = (JSONObject) args[0];
                            //JSONObject data = new JSONObject((String) args[0]);
                            username = data.getString("username");
                            message = data.getString("message");
                        } catch (JSONException e) {
                            return;
                        }
                        onMessageListener.onMessage(username, message);

                    }
                });
            }
        };

        mSocket.on("new message", this.listener);
    }

    public interface MessageListener {
        public void onMessage(String username, String message);
    }

    public void destroy() {
        mSocket.disconnect();
        mSocket.off("new message", listener);
    }
}