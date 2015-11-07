package com.finapps.pep;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransactionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransactionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private List<Map<String, String>> data;
    private static final String KEY_NAME = "NAME";
    private static final String KEY_DESC = "DESC";
    private ListView mTransactions;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private SimpleAdapter adapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TransactionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionsFragment newInstance() {
        return new TransactionsFragment();
    }

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        mTransactions = (ListView) view.findViewById(R.id.transactionsList);

        // Sample values
        List<String> names = new ArrayList<>();
        names.add("Alessio");
        names.add("Biene");
        List<String> amounts = new ArrayList<>();
        amounts.add("15/05/2015 - 234e");
        amounts.add("03/05/2015 - 1337e");

        data = new ArrayList<>();
        Map map = new HashMap<>();
        map.put(KEY_NAME, "Antoni");
        map.put(KEY_DESC, "15/06/2015 - 130 euros");
        data.add(map);
        map = new HashMap<>();
        map.put(KEY_NAME, "Bernat");
        map.put(KEY_DESC, "12/03/2915 - 394 euros");
        data.add(map);

        adapter = new SimpleAdapter(
                getContext(), data, android.R.layout.simple_expandable_list_item_2,
                new String[]{KEY_NAME, KEY_DESC},
                new int[]{android.R.id.text1, android.R.id.text2}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                v.setBackgroundColor(NameToColor.getColor(textView.getText().toString()));
                return v;
            }
        };
        mTransactions.setAdapter(adapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume () {
        super.onResume();
        new GetObjectivesTask().execute();
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
            ArrayList<JSONObject> listdata = new ArrayList<JSONObject>();
            try {
                JSONArray jArray = new JSONArray(result);
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        listdata.add((JSONObject)jArray.get(i));
                    }
                }

                for (JSONObject jo : listdata) {
                    Map map = new HashMap<>();
                    map.put(KEY_NAME, jo.getString("from"));
                    map.put(KEY_DESC, jo.getString("createdAt"));
                    data.add(map);
                }

                adapter.notifyDataSetChanged();

            }
            catch (JSONException e) {

            }
        }
    }

    private String getObjectives() throws IOException {
        InputStream is = null;

        try {
            URL url = new URL("http://192.168.10.11/transactions/year");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("LO", "The response is: " + response);
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
