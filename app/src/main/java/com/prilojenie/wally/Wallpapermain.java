package com.prilojenie.wally;

import android.app.Activity;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallpapermain extends Fragment implements CategoryRVAdapter.CategoryClickInterface
{
    private EditText searchEdt;
    private ImageView searchIV;
    private RecyclerView categoryRV, wallpaperRV;
    private ProgressBar loadingPB;

    private ArrayList<String> wallpaperArrayList;
    private ArrayList<CategoryRVModal> categoryRVModalArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private WallpaperRVAdapter wallpaperRVAdapter;

    Activity activity;

    View parentHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        activity = getActivity();
        parentHolder = inflater.inflate(R.layout.wallpaper_main, container, false);

        super.onCreate(savedInstanceState);
        activity.setContentView(R.layout.wallpaper_main);
        searchEdt = activity.findViewById(R.id.EditSearch);
        searchIV = activity.findViewById(R.id.IVSearch);
        categoryRV = activity.findViewById(R.id.RVCategory);
        wallpaperRV = activity.findViewById(R.id.RVWallpapers);
        loadingPB = activity.findViewById(R.id.PBLoading);
        wallpaperArrayList = new ArrayList<>();
        categoryRVModalArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
        categoryRV.setLayoutManager(linearLayoutManager);
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModalArrayList,activity,this::onCategoryClick);
        categoryRV.setAdapter(categoryRVAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity,2);
        wallpaperRV.setLayoutManager(gridLayoutManager);
        wallpaperRVAdapter = new WallpaperRVAdapter(wallpaperArrayList, activity);
        wallpaperRV.setAdapter(wallpaperRVAdapter);

        getCategories();
        getWallpapers();

        searchIV.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {

                String searchStr = searchEdt.getText().toString();

                if (searchStr.isEmpty())
                {

                    Toast.makeText(activity, "Please enter your search query" , Toast.LENGTH_SHORT).show();

                }
                else
                {

                    getWallpapersByCategory(searchStr);

                }

            }

        });
        return parentHolder;
    }

    private void getWallpapersByCategory(String category)
    {

        wallpaperArrayList.clear();
        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://api.pexels.com/v1/search?query=" + category + "&per_page=30&page=1";
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
        {

            @Override
            public void onResponse(JSONObject response)
            {

                JSONArray photoArray = null;
                loadingPB.setVisibility(View.GONE);
                try
                {

                    photoArray = response.getJSONArray("photos");
                    for(int i = 0; i < photoArray.length(); i++)
                    {

                        JSONObject photoObj = photoArray.getJSONObject(i);
                        String imgUrl = photoObj.getJSONObject("src").getString("portrait");
                        wallpaperArrayList.add(imgUrl);

                    }

                    wallpaperRVAdapter.notifyDataSetChanged();

                }catch (JSONException e)
                {

                    e.printStackTrace();

                }

            }

        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                Toast.makeText(activity, "Sorry, failed to load wallpapers 😟", Toast.LENGTH_SHORT).show();

            }

        })
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "AEWsurXhKlStiYlSO4x1qKSZ0fA3Hhqba4wXVcN9wCnvADzOif4fAFk1");
                return headers;

            }

        };

        requestQueue.add(jsonObjectRequest);

    }
    private void getWallpapers()
    {

        wallpaperArrayList.clear();
        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://api.pexels.com/v1/curated?per_page=30&page=1";
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
        {

            @Override
            public void onResponse(JSONObject response)
            {

                loadingPB.setVisibility(View.GONE);
                try
                {

                    JSONArray photoArray = response.getJSONArray("photos");
                    for(int i = 0; i < photoArray.length(); i++)
                    {

                        JSONObject photoObj = photoArray.getJSONObject(i);
                        String imgUrl = photoObj.getJSONObject("src").getString("portrait");
                        wallpaperArrayList.add(imgUrl);

                    }

                    wallpaperRVAdapter.notifyDataSetChanged();

                }catch (JSONException e)
                {

                    e.printStackTrace();

                }

            }

        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                Toast.makeText(activity, "Sorry, failed to load wallpapers 😟", Toast.LENGTH_SHORT).show();

            }

        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "AEWsurXhKlStiYlSO4x1qKSZ0fA3Hhqba4wXVcN9wCnvADzOif4fAFk1");
                return headers;

            }
        };

        requestQueue.add(jsonObjectRequest);

    }

    private void getCategories()
    {

        categoryRVModalArrayList.add(new CategoryRVModal("Technology","https://images.pexels.com/photos/2582937/pexels-photo-2582937.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Programming","https://images.pexels.com/photos/249798/pexels-photo-249798.png?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Nature","https://images.pexels.com/photos/572897/pexels-photo-572897.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Travel","https://images.pexels.com/photos/4353813/pexels-photo-4353813.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Architecture","https://images.pexels.com/photos/3153679/pexels-photo-3153679.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Arts","https://images.pexels.com/photos/102127/pexels-photo-102127.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Music","https://images.pexels.com/photos/4709825/pexels-photo-4709825.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Abstract","https://images.pexels.com/photos/2110951/pexels-photo-2110951.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Cars","https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Flowers","https://images.pexels.com/photos/1166869/pexels-photo-1166869.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Cartoons","https://images.pexels.com/photos/163036/mario-luigi-yoschi-figures-163036.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Food","https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Animals","https://images.pexels.com/photos/1851164/pexels-photo-1851164.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Clothes","https://images.pexels.com/photos/325876/pexels-photo-325876.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Shoes","https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Games","https://images.pexels.com/photos/275033/pexels-photo-275033.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));
        categoryRVModalArrayList.add(new CategoryRVModal("Make up","https://images.pexels.com/photos/208052/pexels-photo-208052.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"));

        categoryRVAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCategoryClick(int position)
    {

        String category = categoryRVModalArrayList.get(position).getCategory();
        getWallpapersByCategory(category);

    }

//        return inflater.inflate(R.layout.wallpaper_main, container, false);

}