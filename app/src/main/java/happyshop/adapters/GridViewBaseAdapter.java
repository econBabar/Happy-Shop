package happyshop.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Executor;

import happyshop.asynctask.BitmapDownloadTask;
import happyshop.models.Product;
import happyshop.utilities.Converter;
import happyshop.utilities.MemoryCache;
import sephora.test.happyshop.R;

public class GridViewBaseAdapter extends BaseAdapter
{
    private Context context;

    private int layoutResId;

    public List<Product> products;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private int reqWidth, reqHeight;

    public GridViewBaseAdapter(Context context, int layoutResId, List<Product> products)
    {
        this.context = context;

        this.layoutResId = layoutResId;

        this.products = products;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        reqWidth = (int) Converter.convertDpToPixels(140);

        reqHeight = reqWidth;

        System.out.println("GridView thumbnail reqWidth:" + reqWidth + ", reqHeight: " + reqHeight);
    }

    public void add(List<Product> products)
    {
        this.products.addAll(products);

        notifyDataSetChanged();
    }

    public void clear()
    {
        this.products.clear();

        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return products.size();
    }

    @Override
    public Product getItem(int position)
    {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View grid = convertView;

        if (grid == null) {
            grid = inflater.inflate(layoutResId, parent, false);

            holder = new Holder();

            holder.ivThumbnail = (ImageView) grid.findViewById(R.id.thumbnail);
            holder.tvName = (TextView) grid.findViewById(R.id.name);
            holder.tvPrice = (TextView) grid.findViewById(R.id.price);
            holder.tvOnSale = (TextView) grid.findViewById(R.id.on_sale);
            holder.progressBar = (ProgressBar) grid.findViewById(R.id.progress_bar);

            grid.setTag(holder);

        } else {
            holder = (Holder) grid.getTag();
        }

        final Product product = getItem(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("S$" + product.getPrice());
        holder.tvOnSale.setVisibility(product.isUnderSale() ? View.VISIBLE : View.INVISIBLE);

        String imageUrl = product.getImgUrl();

        Bitmap bitmap = memoryCache.getBitmapFromCache(imageUrl);

        if(bitmap != null)
        {
            holder.ivThumbnail.setImageBitmap(bitmap);
            holder.progressBar.setVisibility(View.GONE);
        }
        else
        {
            holder.ivThumbnail.setImageBitmap(null);
            holder.progressBar.setVisibility(View.VISIBLE);

            BitmapDownloadTask downloadTask = new BitmapDownloadTask(this, holder.progressBar);
            downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageUrl,
                    String.valueOf(reqWidth), String.valueOf(reqHeight));
        }

        return grid;
    }

    private static class Holder
    {
        ImageView ivThumbnail;

        TextView tvName, tvPrice, tvOnSale;

        ProgressBar progressBar;
    }
}