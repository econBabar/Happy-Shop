package happyshop.activities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import happyshop.asynctask.BitmapDownloadTask;
import happyshop.asynctask.ProductDetailsTask;
import happyshop.models.Product;
import happyshop.utilities.Converter;
import happyshop.utilities.MemoryCache;
import happyshop.utilities.SqliteUtils;
import sephora.test.happyshop.R;

public class ProductDetailActivity extends AppCompatActivity
{
    private int productId;

    private ImageView ivProductImage;

    private TextView tvName, tvPrice, tvOnSale, tvDescription;

    private Button btAddToCart;

    private ProgressBar progressBar, imageProgressBar;

    private LinearLayout llContent;

    private Product product;

    private SqliteUtils sqliteUtils;

    private ProductDetailsTask productDetailsTask = new ProductDetailsTask(null, progressBar);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_detail);

        init();

        fetchProductDetails();
    }

    private void init()
    {
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        productId = getIntent().getExtras().getInt("id");

        sqliteUtils = new SqliteUtils(this);

        ivProductImage = (ImageView) findViewById(R.id.product_image);

        tvName = (TextView) findViewById(R.id.name);
        tvPrice = (TextView) findViewById(R.id.price);
        tvOnSale = (TextView) findViewById(R.id.on_sale);
        tvDescription = (TextView) findViewById(R.id.description);

        btAddToCart = (Button) findViewById(R.id.addToCart);
        if(sqliteUtils.isAlreadyInCart(productId))
        {
            System.out.println("Already in cart");

            btAddToCart.setText("Remove From Cart");
            btAddToCart.setTag("remove");
        }

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        imageProgressBar = (ProgressBar) findViewById(R.id.image_progress_bar);

        llContent = (LinearLayout) findViewById(R.id.content_layout);


    }

    private void fetchProductDetails()
    {
        productDetailsTask = new ProductDetailsTask(this, progressBar);
        productDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                String.valueOf(productId));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() ==  android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDetails(String json)
    {
        llContent.setVisibility(View.VISIBLE);

        try
        {
            JSONObject jsonObject = new JSONObject(json);

            JSONObject productObj = jsonObject.getJSONObject("product");

            product = new Product();

            product.setId(productObj.getInt("id"));
            product.setName(productObj.getString("name"));
            product.setCategory(productObj.getString("category"));
            product.setDescription(productObj.getString("description"));
            product.setPrice(productObj.getInt("price"));
            product.setImgUrl(productObj.getString("img_url"));
            product.setUnderSale(productObj.getBoolean("under_sale"));

            tvName.setText(product.getName());
            tvPrice.setText("S$" + product.getPrice());
            tvDescription.setText(product.getDescription());
            tvOnSale.setVisibility(product.isUnderSale() ? View.VISIBLE : View.GONE);

            MemoryCache memoryCache = MemoryCache.getInstance();

            Bitmap bitmap = memoryCache.getBitmapFromCache(product.getImgUrl());

            if(bitmap != null)
            {
                ivProductImage.setImageBitmap(bitmap);
            }
            else
            {
                int reqWidth = getResources().getDisplayMetrics().widthPixels;
                int reqHeight = (int) Converter.convertDpToPixels(280);

                BitmapDownloadTask downloadTask = new BitmapDownloadTask(ivProductImage, imageProgressBar);
                downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, product.getImgUrl(),
                        String.valueOf(reqWidth), String.valueOf(reqHeight));
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();

            Toast.makeText(this, "Server is down at the moment. Please try again later",
                    Toast.LENGTH_SHORT).show();
        }
    }



    public void addToCart(View v)
    {
        if(btAddToCart.getTag().toString().equals("add"))
        {
            sqliteUtils.addToCart(product);

            btAddToCart.setTag("remove");
            btAddToCart.setText("Remove From Cart");
        }
        else
        {
            sqliteUtils.removeFromCart(product.getId());

            btAddToCart.setTag("add");
            btAddToCart.setText("Add To Cart");
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(productDetailsTask.getStatus() == AsyncTask.Status.RUNNING) {
            productDetailsTask.cancel(true);
        }
    }
}