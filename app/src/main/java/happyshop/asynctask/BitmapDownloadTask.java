package happyshop.asynctask;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import happyshop.utilities.BitmapProcessor;
import happyshop.utilities.MemoryCache;

public class BitmapDownloadTask extends BaseAsyncTask<String, Void, Bitmap>
{
    private BaseAdapter adapter;

    private ImageView imageView;

    private ProgressBar progressBar;

    public String imgUrl;

    private BitmapProcessor bitmapProcessor = new BitmapProcessor();

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private URL url;

    public BitmapDownloadTask(BaseAdapter adapter, ProgressBar progressBar)
    {
        this.adapter = adapter;

        this.progressBar = progressBar;
    }

    public BitmapDownloadTask(ImageView imageView, ProgressBar progressBar)
    {
        this.imageView = imageView;

        this.progressBar = progressBar;
    }


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        imgUrl = params[0];

        return downloadBitmap(imgUrl, params[1], params[2]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if(progressBar != null)
        {
            progressBar.setVisibility(View.GONE);
        }

        if(bitmap != null)
        {
            memoryCache.addBitmapToCache(imgUrl, bitmap);

            if(imageView != null)
            {
                imageView.setImageBitmap(bitmap);
            }

            if(adapter != null)
            {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public Bitmap downloadBitmap(final String imgUrl, String reqWidth, String reqHeight)
    {
        try
        {
            if(memoryCache.getBitmapFromCache(imgUrl) != null)
            {
                return memoryCache.getBitmapFromCache(imgUrl);
            }

            url = new URL(imgUrl);

            InputStream inputStream = url.openStream();

            int width = Integer.parseInt(reqWidth);

            int height = Integer.parseInt(reqHeight);

            Bitmap bitmap = bitmapProcessor.decodeSampledBitmapFromStream(inputStream,
                            url, width, height);

            return bitmap;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}