package datahandlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.weatherreport.AsyncResponse;
import com.example.weatherreport.MainActivity;


public class CityForecast extends AsyncTask<Void,Integer ,String> 
 {
	private String[] CityArray;
	private Context context = null;
	private MainActivity mainActivity ;
	private static final int HTTP_STATUS_OK = 200;
	private AsyncResponse asyncResponse = null;
    private static byte[] buff = new byte[1024];
    private String result = null;
    private Double longitude=null;
    private Double latitude=null;
   

    ProgressDialog pDialog ;
    public CityForecast(MainActivity mainActivity, AsyncResponse asyncResponse,String[] cityArray)
	{
		this.CityArray= cityArray;
		this.mainActivity= mainActivity; 
		this.context = mainActivity.getApplicationContext();
		this.asyncResponse= asyncResponse; 
	}
    public CityForecast(MainActivity mainActivity, AsyncResponse asyncResponse,double d,double e)
	{
		this.longitude =d;
		this.latitude = e;
		this.mainActivity= mainActivity; 
		this.context = mainActivity.getApplicationContext();
		this.asyncResponse= asyncResponse; 
	}
    
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        pDialog = new ProgressDialog(mainActivity);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

    }	
	
	
	protected String doInBackground(Void... args)
	{
		HttpParams httpParameters = new BasicHttpParams();
		if(longitude== null)
		{
			String totalResult = "{ \"citynames\":[";
			int hitCounter =0;
			for (int cnt = 0; cnt < CityArray.length; cnt++)
			{
				String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q="+CityArray[cnt]+"&mode=json&units=metric&cnt=1";
			    try 
		        {
		        	httpParameters.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(10000));
		        	httpParameters.setParameter(CoreConnectionPNames.SO_TIMEOUT, new Integer(10000));
		        	HttpClient client = new DefaultHttpClient(httpParameters);
		        	
		            HttpGet request = new HttpGet(url);
		       
		            HttpResponse response = client.execute(request);
		        	
		            StatusLine status = response.getStatusLine();
		
		            if (status.getStatusCode() != HTTP_STATUS_OK) 
		            {
		            	return " 1 ";
		            }
		            else
		            {}
		            HttpEntity entity = response.getEntity();
		            InputStream ist = entity.getContent();
		            ByteArrayOutputStream content = new ByteArrayOutputStream();
		
		            int readCount = 0;
		            while ((readCount = ist.read(buff)) != -1) 
		            {
		                content.write(buff, 0, readCount);
		            }
		            result = new String (content.toByteArray());
		            totalResult += result +",";
		            if (result.length() == 0) 
		            {
		            	return " 2 ";
		            }
		            else
		            {}
		            hitCounter = 0;
		        }
			    catch (IOException e)
		        {
		        	cnt--;
		        	hitCounter++;
		        	if(hitCounter >= 3)
		        	{
			            return " 3 ";
		        	}
		        }
			}
		
			totalResult = totalResult.replaceAll(",$", "]}");
			return totalResult;
		}
		else
		{
			Boolean flag = false;
			int hitCounter =0;
			String totalResult = "{ \"citynames\":[";
			do
			{
				String url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="+latitude+"&lon="+longitude+"&mode=json&units=metric&cnt=14";
			    try 
		        {
		        	httpParameters = new BasicHttpParams();
		        	httpParameters.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(3000));
		        	httpParameters.setParameter(CoreConnectionPNames.SO_TIMEOUT, new Integer(3000));
		        	HttpClient client = new DefaultHttpClient(httpParameters);
		        	
		            HttpGet request = new HttpGet(url);
		       
		            HttpResponse response = client.execute(request);
		        	
		            StatusLine status = response.getStatusLine();
	
		            if (status.getStatusCode() != HTTP_STATUS_OK) 
		            {
			            return " 1 ";
		            }
	
		            HttpEntity entity = response.getEntity();
		            InputStream ist = entity.getContent();
		            ByteArrayOutputStream content = new ByteArrayOutputStream();
	
		            int readCount = 0;
		            while ((readCount = ist.read(buff)) != -1) 
		            {
		                content.write(buff, 0, readCount);
		            }
		            result = new String (content.toByteArray());
		            
		            
		            if (result.length() == 0) 
		            {
			        	return " 2 ";
		            }
		            else
		            {
		            	totalResult += result +"]}";
			            
		            }
		            flag = true ;
		        }
		        catch (IOException e)
		        {
		        	hitCounter++;
		        	if(hitCounter >= 3)
		        	{
		        		return " 3 ";
		        	}
		        }

			}while(flag == false);
			return totalResult;
		}
	}

	 @Override
     protected void onPostExecute(String result)
	 {
         super.onPostExecute(result);
         if (pDialog.isShowing())
             pDialog.dismiss();
         asyncResponse.processFinish(result);
	 }
}


