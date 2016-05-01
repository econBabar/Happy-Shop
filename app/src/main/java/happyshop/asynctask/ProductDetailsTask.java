package happyshop.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import happyshop.activities.ProductDetailActivity;
import happyshop.adapters.GridViewBaseAdapter;
import happyshop.models.Product;

public class ProductDetailsTask extends BaseAsyncTask<String, Void, String>
{
    private Context context;

    private ProgressBar progressBar;

    private String api = "http://sephora-mobile-takehome-apple.herokuapp.com/api/v1/products/";

    public ProductDetailsTask(Context context, ProgressBar progressBar)
    {
        this.context = context;

        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getProductDetail(params[0]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        progressBar.setVisibility(View.GONE);

        if(result != null)
        {

            ((ProductDetailActivity) context).showDetails(result);
        }
        else
        {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private String getProductDetail(String id) throws IOException
    {
        String result;

        api = api + id + ".json";

        URL url = new URL(api);

        System.out.println("Product Detail URL: " + url.toString());

        result = getJson(url);

        System.out.println("result: " + result);

        return result;
    }

}