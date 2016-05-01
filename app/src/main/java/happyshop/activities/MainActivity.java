package happyshop.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import happyshop.adapters.GridViewBaseAdapter;
import happyshop.models.Product;
import happyshop.asynctask.ProductListTask;
import happyshop.utilities.MemoryCache;
import happyshop.utilities.SqliteUtils;
import sephora.test.happyshop.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    private TextView tvCart;

    private GridView gridView;

    private ProgressBar progressBar, loadingMoreProgress;

    private List<Product> products = new ArrayList<>();

    private GridViewBaseAdapter adapter;

    private ProductListTask productListTask = new ProductListTask(null, null, null);

    private String category = "";

    private int page = 1;

    private int previousLast;

    private SqliteUtils sqliteUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        init();

        initAndLoadProducts(progressBar);
    }

    private void init()
    {
        Button btChooseCat = (Button) findViewById(R.id.choose_cat);
        btChooseCat.setOnClickListener(this);

        tvCart = (TextView) findViewById(R.id.cart);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        loadingMoreProgress = (ProgressBar) findViewById(R.id.loading_more_progress);

        adapter = new GridViewBaseAdapter(this, R.layout.grid_product, products);

        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setOnScrollListener(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        sqliteUtils = new SqliteUtils(this);
    }

    private void initAndLoadProducts(ProgressBar progressBar)
    {
        if(productListTask.getStatus() == AsyncTask.Status.RUNNING)
        {
            System.out.println("Task already running Cancelling!");
            productListTask.cancel(true);
        }

        productListTask = new ProductListTask(this, adapter, progressBar);
        productListTask.execute(category, String.valueOf(page));
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Choose Category");
        menu.add(0, 1, 0, "Makeup");
        menu.add(0, 2, 0, "Skincare");
        menu.add(0, 3, 0, "Tools");
        menu.add(0, 4, 0, "Men");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        category = item.getTitle().toString();

        System.out.println(category);

        productListTask.cancel(true);

        adapter.clear();

        page = 1;

        previousLast = 0;

       // loadingMoreProgress.setVisibility(View.GONE);

        initAndLoadProducts(progressBar);

        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.choose_cat:

                registerForContextMenu(view);

                openContextMenu(view);

                unregisterForContextMenu(view);

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvCart.setText("Cart: " + sqliteUtils.getCartCount());
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {

        int lastVisiblePosition = absListView.getLastVisiblePosition();

        if(lastVisiblePosition > 0
                && lastVisiblePosition >= (totalItemCount - 1))
        {

            if(previousLast != lastVisiblePosition)
            {
                System.out.println("Bottom is reached");

                previousLast = lastVisiblePosition;

                page = page + 1;

                initAndLoadProducts(loadingMoreProgress);
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Product product = (Product) adapterView.getItemAtPosition(i);

        startActivity(new Intent(this, ProductDetailActivity.class).putExtra("id", product.getId()));
    }
}