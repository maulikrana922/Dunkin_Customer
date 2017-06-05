package com.dunkin.customer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dunkin.customer.R;
import com.dunkin.customer.adapters.TokenListAdapter;
import com.dunkin.customer.models.TokenModel;

import java.util.List;

/**
 * Created by qtm-c-android on 2/6/17.
 */

public class TokenListFragment extends Fragment {

    private List<TokenModel> tokenModelList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        tokenModelList = (List<TokenModel>) bundle.getSerializable("token_list");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_token, container, false);
         ListView tokenList = (ListView) rootView.findViewById(R.id.tokenList);

        if (tokenModelList != null && tokenModelList.size() > 0) {

            TokenListAdapter tokenListAdapter = new TokenListAdapter(getActivity(),
                    tokenModelList);
            tokenList.setAdapter(tokenListAdapter);
            tokenList.setEmptyView(rootView.findViewById(R.id.emptyElement));
        }
        return rootView;
    }
}

