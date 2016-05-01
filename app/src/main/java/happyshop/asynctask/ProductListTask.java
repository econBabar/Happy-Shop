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

import happyshop.adapters.GridViewBaseAdapter;
import happyshop.models.Product;

public class ProductListTask extends BaseAsyncTask<String, Void, String>
{
    private Context context;

    private GridViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private String api = "http://sephora-mobile-takehome-apple.herokuapp.com/api/v1/products.json";

    public ProductListTask(Context context, GridViewBaseAdapter adapter,
                           ProgressBar progressBar)
    {
        this.context = context;

        this.adapter = adapter;

        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        if(progressBar != null) {
            this.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if(progressBar != null) {
            this.progressBar.setVisibility(View.GONE);
        }

        System.out.println("OnCancelled");
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getProducts(params[0], params[1]);
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

        if(progressBar != null){
            progressBar.setVisibility(View.GONE);
        }

        if(result != null)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray productsArray = jsonObject.getJSONArray("products");

                List<Product> products = new ArrayList<>();

                for(int i = 0; i < productsArray.length(); i++)
                {
                    JSONObject productObj = productsArray.getJSONObject(i);

                    Product product = new Product();
                    product.setId(productObj.getInt("id"));
                    product.setName(productObj.getString("name"));
                    product.setCategory(productObj.getString("category"));
                    product.setPrice(productObj.getInt("price"));
                    product.setImgUrl(productObj.getString("img_url"));
                    product.setUnderSale(productObj.getBoolean("under_sale"));

                    products.add(product);
                }

                adapter.add(products);
            }
            catch (JSONException e)
            {
                e.printStackTrace();

                Toast.makeText(context, "Server is down at the moment. Please try again later",
                               Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private String getProducts(String category, String page) throws IOException
    {
        String result;

        HashMap<String, String> params = new HashMap<>();

        if(!category.isEmpty())
        {
            params.put("category", category);
        }

        params.put("page", page);

        String query  = createQuery(params);

        api = api + query;

        URL url = new URL(api);

        System.out.println("Products List URL: " + url.toString());

        result = getJson(url);

        System.out.println("result: " + result);

        return result;
    }
}