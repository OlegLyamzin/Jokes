package ru.onidev.jokes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class JokesFragment extends Fragment {
    private HandlerWorker handlerWorker;
    private Handler mUIHandler = new Handler();
    static ArrayList<Joke> jokes = new ArrayList<>();
    private LinearLayout linearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_jokes, container, false);

        handlerWorker = new HandlerWorker("handlerWorker");
        handlerWorker.start();
        handlerWorker.prepareHandler();

        linearLayout = v.findViewById(R.id.linearLayoutJokes);
        makeList(getContext());

        v.findViewById(R.id.reloadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                jokes.clear();
                EditText editText = v.findViewById(R.id.editText);
                startRequest(v.getContext(), Integer.valueOf(editText.getText().toString()));
            }
        });

        return v;
    }

    public void startRequest(final Context context, final int count) {
        handlerWorker.postTask(new Runnable() {
            @Override
            public void run() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String url = "http://api.icndb.com/jokes/random/" + count + "?firstName=Chuck&amp;lastName=Norris&escape=javascript";
                            URL obj = new URL(url);
                            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                            connection.setRequestMethod("GET");

                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String inputLine;
                            StringBuilder response = new StringBuilder();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            JSONArray jArray = new JSONObject(response.toString()).getJSONArray("value");
                            if (jArray != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    jokes.add(Joke.parse(jArray.getJSONObject(i)));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        makeList(context);
                    }
                });
            }
        });
    }

    private void makeList(final Context context) {
        for (int i = 0; i < jokes.size(); i++) {
            final int finalI = i;
            handlerWorker.postTask(new Runnable() {
                @Override
                public void run() {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (jokes.get(finalI) != null) {
                                createNewElement(jokes.get(finalI), context);
                            }
                        }
                    });
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    public synchronized void createNewElement(Joke joke, Context context) {

        LinearLayout linearLayout = new LinearLayout(context);
        TextView onlineText = new TextView(context);
        TextView nameText = new TextView(context);
        TextView cityText = new TextView(context);

        LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsText.gravity = Gravity.CENTER_VERTICAL;

        LinearLayout.LayoutParams linerLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linerLayoutParams.topMargin = 4;
        linerLayoutParams.bottomMargin = 4;

        linearLayout.setLayoutParams(linerLayoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundResource(R.drawable.background);
        linearLayout.setPadding(8, 8, 8, 8);

        onlineText.setText("id: " + Integer.toString(joke.getId()));
        onlineText.setLayoutParams(layoutParamsText);

        nameText.setText(joke.getJokeText());
        nameText.setLayoutParams(layoutParamsText);
        nameText.setTextSize(18);

        StringBuilder stringBuilder = new StringBuilder();
        ArrayList categories = joke.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            stringBuilder.append(categories.get(i)).append(" ");
        }
        cityText.setText("categories: " + stringBuilder);
        cityText.setLayoutParams(layoutParamsText);
        cityText.setTextSize(16);

        linearLayout.addView(nameText);
        linearLayout.addView(cityText);
        linearLayout.addView(onlineText);

        this.linearLayout.addView(linearLayout);
    }
}
