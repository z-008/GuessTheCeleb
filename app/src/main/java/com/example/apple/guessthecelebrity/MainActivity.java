package com.example.apple.guessthecelebrity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {


    public class DownlodTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";  //Content of the website the html content
            URL url;      //Website to access
            HttpURLConnection urlConnection = null;  //it is something like a browser to run the url

            try
            {
                url = new URL(urls[0]);  //first url
                urlConnection = (HttpURLConnection)url.openConnection();   //url entered in the browser
                InputStream in =urlConnection.getInputStream();    //data from url is taken one by one
                InputStreamReader reader= new InputStreamReader(in);   // reader to read data
                int data = reader.read();  //data stored here

                while(data!=-1)   //when data is downloaded completely data becomes = -1
                {
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }

            catch(Exception e)
            {
                e.printStackTrace();
                return "failed";
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownlodTask task = new DownlodTask();
        String result = null;


        try{
            result=task.execute("http://www.posh24.se/kandisar").get();  //execute function to call background thread or the doinbackground method

            //<div class="sidebarContainer">

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while(m.find())
            {
              System.out.println(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while(m.find())
            {
                System.out.println(m.group(1));
            }


        }

        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
        }

        //Log.i("Contents of Url: ",result);


    }
}
