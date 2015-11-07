package com.finapps.pep;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

    private static final String KEY_NAME = "NAME";
    private static final String KEY_DESC = "DESC";
    private ListView mTransactions;

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

        List<Map<String, String>> data = new ArrayList<>();
        Map map = new HashMap<>();
        map.put(KEY_NAME, "Antoni");
        map.put(KEY_DESC, "15/06/2015 - 130 euros");
        data.add(map);
        map = new HashMap<>();
        map.put(KEY_NAME, "Bernat");
        map.put(KEY_DESC, "12/03/2915 - 394 euros");
        data.add(map);

        SimpleAdapter adapter = new SimpleAdapter(
                getContext(), data, android.R.layout.simple_expandable_list_item_2,
                new String[]{KEY_NAME, KEY_DESC},
                new int[]{android.R.id.text1, android.R.id.text2}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                // Set color here.
                // v.setBackgroundColor(Color.BLUE);
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

}
