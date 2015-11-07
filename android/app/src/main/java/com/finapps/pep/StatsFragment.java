package com.finapps.pep;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment implements OnChartValueSelectedListener {

    private BarChart mChart;

    private OnFragmentInteractionListener mListener;

    private ArrayList<BarEntry> myVals1 = new ArrayList<BarEntry>();

    private YAxis yLabels;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatsFragment.
     */
    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        // Initialize the chart
        mChart = (BarChart) v.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDescription(" ");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setTouchEnabled(false);

        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(true);

        // change the position of the y-labels
        yLabels = mChart.getAxisLeft();
        yLabels.setValueFormatter(new MyYAxisValueFormatter());
        yLabels.setTextSize(15.0f);

        mChart.getAxisRight().setEnabled(false);

        XAxis xLabels = mChart.getXAxis();
        xLabels.setPosition(XAxisPosition.TOP);

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.BELOW_CHART_RIGHT);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);


        new GetObjectivesTask().execute();

        return v;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        BarEntry entry = (BarEntry) e;

        if (entry.getVals() != null)
            Log.i("VAL SELECTED", "Value: " + entry.getVals()[h.getStackIndex()]);
        else
            Log.i("VAL SELECTED", "Value: " + entry.getVal());
    }

    @Override
    public void onNothingSelected() {
        // TODO Auto-generated method stub

    }

    private int[] getColors() {

        int stacksize = 3;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < stacksize; i++) {
            colors[i] = ColorTemplate.COLORFUL_COLORS[i];
        }

        return colors;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class GetObjectivesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return getObjectives();
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.v("LO", result);
            ArrayList<JSONObject> listdata = new ArrayList<JSONObject>();
            try {
                JSONArray jArray = new JSONArray(result);
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        listdata.add((JSONObject)jArray.get(i));
                    }
                }

                Collections.sort(listdata, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject date1, JSONObject date2) {
                        try {
                            Long d1 = date1.getLong("date");
                            Long d2 = date2.getLong("date");
                            return (d1 < d2) ? 1 : 0;
                        } catch (JSONException e1) {

                        }
                        return 0;
                    }
                });

                ArrayList<JSONObject> retain =
                        new ArrayList<JSONObject>(listdata.size());
                Long d = new Date().getTime(), dr = null;
                boolean picked = false;
                for (JSONObject jo : listdata) {
                    Long dp = jo.getLong("date");
                    if (dp > d) {
                        if (!picked) {
                            dr = d;
                            picked = true;
                            retain.add(jo);
                        }
                        else {
                            if (Math.abs(dr - dp) < 24*3600*1000) {
                                retain.add(jo);
                            }
                        }
                    }
                }
// either assign 'retain' to 'wsResponse.Dealers' or ...
                listdata.clear();
                listdata.addAll(retain);

                // Ara que ja només tenim els necessaris
                float total = 0.0f;
                for (JSONObject jo : listdata) {
                    total += jo.getDouble("value");
                }

                yLabels.setAxisMaxValue(total);

                new GetMuneyzTask().execute();

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getObjectives() throws IOException {
        InputStream is = null;

        try {
            URL url = new URL("http://192.168.10.11/objectives");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("LOL", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return convertInputStreamToString(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private class GetMuneyzTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return getMuneyz();
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            ArrayList<JSONObject> listdata = new ArrayList<JSONObject>();
            try {
                JSONArray jArray = new JSONArray(result);
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        listdata.add((JSONObject)jArray.get(i));
                    }
                }

                String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                float memoney = 0.f, othmoney = 0.f;


                for (JSONObject jo : listdata) {
                    Log.v("LO", jo.getString("balance"));
                    if (jo.getString("name").equals(android_id)) {
                        memoney += jo.getInt("balance");
                    }
                    else {
                        othmoney += jo.getInt("balance");
                    }
                }

                Log.v("LO", String.valueOf(memoney));
                Log.v("LO", String.valueOf(othmoney));
                ArrayList<String> xVals = new ArrayList<String>();
                xVals.add("");

                myVals1.clear();
                myVals1.add(new BarEntry(new float[] {memoney, othmoney}, 0));

                BarDataSet set1 = new BarDataSet(myVals1, " ");
                set1.setColors(getColors());
                set1.setStackLabels(new String[] {"Me", "Others"});

                ArrayList<BarDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                BarData data = new BarData(xVals, dataSets);
                data.setValueFormatter(new MyValueFormatter());
                data.setValueTextSize(15.0f);

                mChart.setData(data);
                mChart.invalidate();

            }
            catch (JSONException e) {

            }
        }
    }


    private String getMuneyz() throws IOException {
        InputStream is = null;

        try {
            URL url = new URL("http://192.168.10.11/balance");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("LOLO", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return convertInputStreamToString(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
