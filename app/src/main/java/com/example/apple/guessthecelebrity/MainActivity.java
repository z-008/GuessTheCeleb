package com.example.apple.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ArrayList <String> celebURls = new ArrayList<String>();
    ArrayList <String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    ImageView imageView;
    int locationOfCorrectAnswer;
    String[] answers = new String[4];
    Button button0;
    Button button1;
    Button button2;
    Button button3;


    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap mybitmap = BitmapFactory.decodeStream(inputStream);
                return mybitmap;



            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
            return null;
        }
    }

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
        imageView = (ImageView)findViewById(R.id.imageView);
        button0 = (Button)findViewById(R.id.button);
        button1 = (Button)findViewById(R.id.button2);
        button2 = (Button)findViewById(R.id.button3);
        button3 = (Button)findViewById(R.id.button4);

        DownlodTask task = new DownlodTask();
        String result = null;


        try{
            result=task.execute("http://www.posh24.se/kandisar").get();  //execute function to call background thread or the doinbackground method

            //<div class="sidebarContainer">

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while(m.find())
            {
                celebURls.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while(m.find())
            {
                celebNames.add(m.group(1));
            }

            Random random = new Random();
            chosenCeleb = random.nextInt(celebURls.size());
            ImageDownloader imageTask = new ImageDownloader();
            Bitmap celebImage;
            celebImage = imageTask.execute(celebURls.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);
            locationOfCorrectAnswer = random.nextInt(4);

            int incorrectAnswersLocation;

            for(int i=0;i<4;i++)
            {

                if(i==locationOfCorrectAnswer)
                {
                    answers[i] = celebNames.get(chosenCeleb);
                }
                else
                {
                    incorrectAnswersLocation = random.nextInt(celebURls.size());

                    while(incorrectAnswersLocation==chosenCeleb) {
                        incorrectAnswersLocation = random.nextInt(celebURls.size());
                    }

                    answers[i] = celebNames.get(incorrectAnswersLocation);

                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);



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
