package ru.onidev.jokes;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Joke {
    private int id;
    private String jokeText;
    private ArrayList<String> categories;

    private Joke(int id, String jokeText, ArrayList<String> categories) {
        this.id = id;
        this.jokeText = jokeText;
        this.categories = categories;
    }

    static Joke parse(JSONObject json){
        int id = 0;
        String joke = "nothing";
        ArrayList<String> categoriesList = new ArrayList<>();
        try {
            id =  json.getInt("id");
            joke = json.getString("joke");
            JSONArray jArray = json.getJSONArray("categories");
            if (jArray != null) {
                for (int i=0;i<jArray.length();i++){
                    categoriesList.add(jArray.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Joke(id,joke,categoriesList);
    }

    public int getId() {
        return id;
    }

    String getJokeText() {
        return jokeText;
    }

    ArrayList<String> getCategories() {
        return categories;
    }
}
