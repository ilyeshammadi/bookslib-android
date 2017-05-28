package ilyeshammadi.booklib.asyntasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ilyeshammadi.booklib.adapters.ListBookAdapter;
import ilyeshammadi.booklib.models.Book;
import ilyeshammadi.booklib.utils.Http;

import static ilyeshammadi.booklib.utils.Constants.SERVER_URL;
import static ilyeshammadi.booklib.utils.Constants.TAG;

/**
 * Created by ilyes on 5/26/17.
 */

public class GetListBooksTask extends AsyncTask<String, Void, ArrayList<Book>> {

    private ListBookAdapter adapter;
    private Context context;
    private ArrayList<Book> booksList = new ArrayList<>();
    private Boolean loading = true;

    public GetListBooksTask(Context context, ListBookAdapter adapter) {
        this.adapter = adapter;
        this.context = context;
    }

    public GetListBooksTask( Context context, ListBookAdapter adapter, boolean loading) {
        this.adapter = adapter;
        this.context = context;
        this.loading = loading;
    }

    @Override
    protected ArrayList<Book> doInBackground(String... params) {
        loading = true;

        String url;
        if (params.length > 0){
            url = params[0];
        }else {
            url = SERVER_URL + "/api/books/?format=json";
        }

        String data = Http.get(this.context, url);

        Log.i(TAG, "doInBackground: " + data);


        try {
            JSONObject topLevel = null;

            topLevel = new JSONObject(data);

            JSONArray results = topLevel.getJSONArray("results");

            // Save the next page link
            String nextLink = topLevel.getString("next");
            if (nextLink != null) {
                Http.setPref(context, "next", nextLink);
            }

            Book book = null;

            for (int i = 0; i < results.length(); i++) {
                JSONObject bookNode = (JSONObject) results.get(i);
                book = Book.fromJson(bookNode);
                booksList.add(book);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return booksList;
    }


    @Override
    protected void onPostExecute(ArrayList<Book> books) {
        this.loading = false;
        this.adapter.swap(books);
    }
}
