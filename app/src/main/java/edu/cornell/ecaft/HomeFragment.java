package edu.cornell.ecaft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Ashley on 11/7/2015.
 */
public class HomeFragment extends Fragment {

    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(
                R.layout.home_fragment, container, false);
        Bundle args = getArguments();
        textView = (TextView) v.findViewById(R.id.home_fragment_text_view);
        return v;
    }
}
