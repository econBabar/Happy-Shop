package happyshop.asynctask;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
    public final static int TIME_OUT = 30 * 1000;

    public final static String METHOD = "GET";

    protected final static String ENCODING = "UTF-8";

    public String createQuery(HashMap<String, String> params) throws UnsupportedEncodingException {
        boolean isFirst = true;

        StringBuilder builder = new StringBuilder();

        for(Map.Entry<String, String> entry : params.entrySet())
        {
            if(isFirst)
            {
                builder.append("?");

                isFirst = false;
            }
            else
            {
                builder.append("&");
            }

            builder.append(entry.getKey());
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), ENCODING));
        }

        return builder.toString();
    }

    public String getJson(URL url) throws IOException
    {
        String result = null;

        HttpURLConnection connection = openConnectionAndConnect(url);

        InputStream inputStream = connection.getInputStream();

        result = readStream(inputStream);

        return result;
    }

    public HttpURLConnection openConnectionAndConnect(URL url) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setConnectTimeout(TIME_OUT);
        connection.setReadTimeout(TIME_OUT);
        connection.setRequestMethod(METHOD);
        connection.setDoInput(true);
        connection.connect();

        return connection;
    }

    public String readStream(InputStream stream) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new InputStreamReader(stream, ENCODING));

            String line;

            while((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        }
        finally
        {
            if(reader != null)
            {
                reader.close();
            }
        }
    }
}
