package com.mirea.lab8;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PageFragment extends Fragment implements OnMapReadyCallback {

    private static String TAG = PageFragment.class.getSimpleName();
    private int pageNumber;

    private HashMap<String, ArrayList<Double>> placeWithCoords = new HashMap<>();
    private ArrayList<String> placesList = new ArrayList<>();

    private ListView listView;
   private  ListView listView1;
    private GoogleMap map;
    private GeocodingAPI geocodingApi;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    public interface OnFragmentDataListener {
        void onFragmentDataListener(Double lat, Double lng);
        void onFragmentDataListenr(Double lat,Double lng);
    }

    private OnFragmentDataListener mListener;
    private OnFragmentDataListener MListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentDataListener) {
            mListener = (OnFragmentDataListener) context;
            MListener = (OnFragmentDataListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("NUMBER", page);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments() != null ? getArguments().getInt("NUMBER") : 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        geocodingApi = retrofit.create(GeocodingAPI.class);

        final EditText editText = view.findViewById(R.id.edit_text);
        final EditText editText1=view.findViewById(R.id.edit_text1);
        Button button = view.findViewById(R.id.button);
      Button button1=view.findViewById(R.id.button1);
        listView = view.findViewById(R.id.list);
       listView1=view.findViewById(R.id.list1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputAddress = editText.getText().toString();

                if (inputAddress.length() != 0 ) {
                    new ProcessTask(inputAddress).execute();
                }

            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputAddress = editText1.getText().toString();

                if (inputAddress.length() != 0 ) {
                    new ProcesTask(inputAddress).execute();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String place = placesList.get(position);
                Double lat = placeWithCoords.get(place).get(0);
                Double lng = placeWithCoords.get(place).get(1);
                mListener.onFragmentDataListener(lat, lng);
                Toast.makeText(getContext(),"Added",Toast.LENGTH_LONG).show();
            }
        });
listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String place = placesList.get(position);
        Double lat = placeWithCoords.get(place).get(0);
        Double lng = placeWithCoords.get(place).get(1);
        MListener.onFragmentDataListenr(lat, lng);
        Toast.makeText(getContext(),"Added",Toast.LENGTH_LONG).show();
    }
});
    }

    static String getTitle(int position) {
        return "FROM<->TO";
    }

    private class ProcessTask extends AsyncTask<Void, Void, Void> {
        private String input;
        private Response<GeocodingResponse> response;

        public ProcessTask(String str) {
            input = str;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                response = geocodingApi.getAddress(input, "AIzaSyAI4nxhTP5r6zfpS5cgEJ63k4uNw3wzaDs").execute();
            } catch (IOException ex) {
                Log.e(TAG, "" + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            placesList.clear();
            for (Address i : response.body().addressList) {
                Double lat = i.geometry.coordinate.lat;
                Double lng = i.geometry.coordinate.lng;
                placeWithCoords.put(i.address, new ArrayList<>(Arrays.asList(lat, lng)));
                placesList.add(i.address);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, placesList);
            listView.setAdapter(adapter);
        }
    }
    private class ProcesTask extends AsyncTask<Void, Void, Void> {
        private String input;
        private Response<GeocodingResponse> response;

        public ProcesTask(String str) {
            input = str;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                response = geocodingApi.getAddress(input, "AIzaSyAI4nxhTP5r6zfpS5cgEJ63k4uNw3wzaDs").execute();
            } catch (IOException ex) {
                Log.e(TAG, "" + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            placesList.clear();
            assert response.body() != null;
            for (Address i : response.body().addressList) {
                Double lat = i.geometry.coordinate.lat;
                Double lng = i.geometry.coordinate.lng;
                placeWithCoords.put(i.address, new ArrayList<>(Arrays.asList(lat, lng)));
                placesList.add(i.address);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, placesList);
            listView1.setAdapter(adapter);
        }
    }
}
