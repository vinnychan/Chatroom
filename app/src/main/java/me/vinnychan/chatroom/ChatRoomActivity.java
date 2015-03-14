package me.vinnychan.chatroom;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincentchan on 15-03-10.
 */
public class ChatRoomActivity extends Activity {
    List<String> dummyData;
    MessageAdapter messagesAdapter;
    ListView messagesList;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        username = getIntent().getStringExtra("USERNAME");
        dummyData = new ArrayList<>();

        messagesList = (ListView) findViewById(R.id.messages_list);

        messagesAdapter = new MessageAdapter(this, dummyData);
        messagesList.setAdapter(messagesAdapter);
        setupSocketIO();
    }

    private void setupSocketIO() {
        final SocketIO socketIO = new SocketIO();
        socketIO.connect();

        socketIO.onMessage(this, new SocketIO.MessageListener() {
            @Override
            public void onMessage(String username, String message) {
                dummyData.add(username + ": " + message);
                messagesAdapter.notifyDataSetChanged();

                messagesList.setSelection(dummyData.size() - 1);

                Log.i("ChatRoomActivity", username + ": " + message);
            }
        });

        Button button = (Button) findViewById(R.id.send_message);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText messageText = (EditText) findViewById(R.id.message_text);
                String message = messageText.getText().toString();
                messageText.setText("");
                socketIO.attemptSend(username, message);
            }

        });
    }

    class MessageAdapter extends ArrayAdapter<String> {

        List<String> messagesData;

        public MessageAdapter(Context context, List<String> data) {
            super(context, 0);
            messagesData = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.row_messages, null);

            String messageToShow = messagesData.get(position);
            TextView messageToShowTextView = (TextView) view.findViewById(R.id.message);
            messageToShowTextView.setText(messageToShow);

            return view;
        }

        @Override
        public int getCount() {
            return messagesData.size();
        }
    }

}
