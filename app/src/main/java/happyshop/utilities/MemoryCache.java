package happyshop.utilities;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class MemoryCache
{
    private static MemoryCache memoryCache;

    private static LruCache<String, Bitmap> lruCache;

    private MemoryCache(){};

    public static MemoryCache getInstance()
    {
        if(memoryCache == null)
        {
            memoryCache = new MemoryCache();

            init();
        }

        return memoryCache;
    }

    private static void init()
    {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();

        final int cacheSize = maxMemory / 6;

        Log.i("maxHeapMemory: ", "" + maxMemory / 1024 / 1024 + " MB");
        Log.i("CacheSize: ",""+cacheSize / 1024/ 1024 + " MB");

        lruCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value)
            {
                // The cache size will be measured in kilobytes rather than
                // number of items.

                return value.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToCache(String key, Bitmap value)
    {
        if(getBitmapFromCache(key) == null)
        {
            lruCache.put(key, value);
        }
    }

    public Bitmap getBitmapFromCache(String key)
    {
        return lruCache.get(key);
    }

    public void tearDown()
    {
        lruCache.evictAll();
    }

}
