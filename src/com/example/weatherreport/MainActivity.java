package com.example.weatherreport;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;
import datahandlers.CityForecast;
import datahandlers.GPSTracker;

public class MainActivity extends Activity implements AsyncResponse
{
	ExpandableListAdapter listAdapter;
	 ExpandableListView expListView;
	 List<String> listDataHeader;
	 HashMap<String, List<String>> listDataChild;
	 String [] CityArray;
	
	 
	 private static final String TAG_CITY_NAMES = "citynames";
	 private static final String TAG_CITY_NAME = "name";
	 private static final String TAG_CITY= "city";
	 
	 private static final String TAG_LIST = "list";
	    private static final String TAG_WEATHER= "weather";
	    private static final String TAG_COUNT = "cnt";
	    private static final String TAG_DATE = "dt";
	    private static final String TAG_COD = "cod";
	    
	    private static final String TAG_OBJECT_TEMPERATURE = "temp"; 
	    private static final String TAG_TEMPERATURE_MIN = "min";
	    private static final String TAG_TEMPERATURE_MAX= "max";
	    private static final String TAG_TEMPERATURE_DAY= "day";
	    private static final String TAG_WEATHER_MAIN = "main";
	    private static final String TAG_WEATHER_DESCRIPTION = "description";
	    private static final String TAG_HUMIDITY= "humidity";
	    private static final String TAG_SPEED = "speed";

	    JSONArray ARRAY_LIST = null;
	    JSONArray CITY_NAME_LIST = null;
	    JSONArray ARRAY_WEATHER= null;
	 
	    GPSTracker gps;
	    
	EditText editText = null;	
	public void showToast(String msg) 
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onSubmitClick(View view)
    {
    	InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
    	inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);
    	
    	editText = (EditText) findViewById(R.id.editText1);
    	String ss = editText.getText().toString();
    	if(!ss.equals(""))
    	{
	    	CityArray = ss.trim().split("\\s*,\\s*");
	    	CityForecast cityForecast = new CityForecast(MainActivity.this ,MainActivity.this, CityArray);
	    	cityForecast.execute();
    	}
    	else
    	{
    		showToast("No city Entered");	
    	}
    	
    }
    
    public void onGPSClick(View view)
    {
    	 gps = new GPSTracker(MainActivity.this);
         if(gps.canGetLocation())
         {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            showToast( "Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
            CityForecast cityForecast = new CityForecast(MainActivity.this ,MainActivity.this,longitude,latitude);
     		cityForecast.execute();

         }
         else
         {
             gps.showSettingsAlert();
         }
    }
	@Override
	public void processFinish(String result) 
	{
		if(result.trim().equalsIgnoreCase("1"))
		{
			showToast("HTTP_STATUS_NOT_OK \n please check ur connection"); 
		}
		else if(result.trim().equalsIgnoreCase("2"))
		{
			showToast("Please tr again"); 
		}
		else if(result.trim().equalsIgnoreCase("3"))
		{
			showToast("Bad Connection Network  !"); 
		}
		else
		{
			listDataHeader = new ArrayList<String>();
			 List<String> subMenuList = new ArrayList<String>();
	        listDataChild = new HashMap<String, List<String>>();
	 
	        int list_count =0;
			try
			{
				JSONObject respObj = new JSONObject(result);
				String description = null;
				String main = null;
				CITY_NAME_LIST = respObj.getJSONArray(TAG_CITY_NAMES);
				for (int i1 = 0; i1 < CITY_NAME_LIST.length(); i1++) 
				{
					JSONObject temp1_list = CITY_NAME_LIST.getJSONObject(i1);
					if(!temp1_list.getString(TAG_COD).equals("404"))
					{	
						JSONObject cityName = temp1_list.getJSONObject(TAG_CITY);
						String cityname1= cityName.getString(TAG_CITY_NAME);
						 
						
						ARRAY_LIST = temp1_list.getJSONArray(TAG_LIST);
						for (int i = 0; i < ARRAY_LIST.length(); i++) 
						{
							 String listobj= null;
							 JSONObject temp_list = ARRAY_LIST.getJSONObject(i);
							 String temp_date = temp_list.getString(TAG_DATE);
							 String date = temp_date.concat("555");
							 date = usingDateFormatter(Long.parseLong(date));
							 subMenuList = new ArrayList<String>();
							 JSONObject temprature1 = temp_list.getJSONObject(TAG_OBJECT_TEMPERATURE);
							 String day = temprature1.getString(TAG_TEMPERATURE_DAY);
							 String min = temprature1.getString(TAG_TEMPERATURE_MIN);
							 String max = temprature1.getString(TAG_TEMPERATURE_MAX);
						
							 ARRAY_WEATHER = temp_list.getJSONArray(TAG_WEATHER);
							 for (int j = 0; j < ARRAY_WEATHER.length(); j++) 
							 {
								 JSONObject temp_weather= ARRAY_WEATHER.getJSONObject(j);
								 description = temp_weather.getString(TAG_WEATHER_DESCRIPTION);
								 main = temp_weather.getString(TAG_WEATHER_MAIN);
							 }
							 String speed = temp_list.getString(TAG_SPEED);
							 String humidity = temp_list.getString(TAG_HUMIDITY);
							 subMenuList.add("Min. \t"+min+ (char) 0x00B0);
							 subMenuList.add("Max. \t"+max+ (char) 0x00B0);
							 subMenuList.add("Winds Speed \t"+speed+ " m/s");
							 if(!humidity.equals("0"))
								 subMenuList.add("Humidity \t"+humidity + "%");
							 
							 listobj = cityname1+" , "+date+", "+description+", "+day+", "+min+", "+max;
							 listDataHeader.add(listobj);
							 listDataChild.put(listDataHeader.get(list_count++), subMenuList);			
							 }
						listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
						expListView.setAdapter(listAdapter);
					}
					else
					{
						showToast(" No such City Found");
					}
				}	
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			} 
		}
	}
	private String usingDateFormatter(long input)
	{
        Date date = new Date(input);
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);
    }
    
}
