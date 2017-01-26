package edu.cu.ecaft;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.data.Freezable;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Ashley on 11/8/2015.
 */
public class InfoFragment extends Fragment implements SearchView.OnCloseListener {

    private static final String TAG = "ECaFT";
    private static final String SAVED_LAYOUT_MANAGER = "Layout Manager";
    private StorageReference storageRef = FirebaseApplication
            .getStorageRef();

    private RecyclerView.LayoutManager layoutManagerSavedState;
    private RecyclerView companyRecylerView;
    private CompanyAdapter companyAdapter;
    private View v;
    private List<FirebaseCompany> companies;
    private ListView lv;
    //ArrayList<> data = new ArrayList<>();

    public InfoFragment() {
//        int resultCode = GoogleApiAvailability.getInstance()
//                .isGooglePlayServicesAvailable(getActivity());
//
//        if (resultCode == ConnectionResult.SUCCESS){
//            Log.d("ecaft", "isGooglePlayServicesAvailable SUCCESS");
//        } else {
//            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),
//                    resultCode, 1).show();
//        }
        companies = FirebaseApplication.getCompanies();
        companyAdapter = new CompanyAdapter(companies);

    }



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(R.layout.info_fragment, container, false);

        companyRecylerView = (RecyclerView) v.findViewById(R.id
                .info_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager
                (getActivity()));
        companyRecylerView.setHasFixedSize(true);
        companyRecylerView.setAdapter(companyAdapter);
        //ct.execute();

        //    updateUI();

        getActivity().setTitle("List Of Companies");
        setHasOptionsMenu(true);
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

            inflater.inflate(R.menu.menu_main, menu);
            inflater.inflate(R.menu.menu_search, menu);
            inflater.inflate(R.menu.menu_filter, menu);

            MenuItem filterItem= menu.findItem(R.id.filterButton);
            final MenuItem searchItem = menu.findItem(R.id.search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {

                }

                @Override
                public void onViewDetachedFromWindow(View view) {

                }
            });

            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener(){

                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                        searchItem.collapseActionView();
                        companyAdapter.filter("");
                        searchView.setQuery("",false);
                        Log.d("BYEEEEEEE", "CLOSING THIS NONSENSE");
                    }
                }
            });

            searchView.setIconifiedByDefault(true);
            searchView.setOnCloseListener(this);

            searchView.findViewById(R.id.search_close_btn).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Log.d("AHHHHHHHHHHHHHHHHH", "Hello this is me crying");
                    searchView.setQuery("", false);
                    companyAdapter.filter("");
                    searchView.setIconifiedByDefault(true);
                }
            });
             MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                searchView.setQuery("", false);
                companyAdapter.filter("");
                Log.d("BBBBBBBBBBBBBBBBBBBBbb", "android sucks");
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    companyAdapter.filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    companyAdapter.filter(newText);
                    return false;
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.search:
                //companyAdapter.doSearch();
                return true;
            case R.id.filterButton:
                FragmentManager fm = getFragmentManager();
                final OptionsFragment opt = new OptionsFragment();
                return true;
            default:
                return false;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, companyRecylerView.getLayoutManager()
                .onSaveInstanceState());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState
                    .getParcelable(SAVED_LAYOUT_MANAGER);
            companyRecylerView.getLayoutManager().onRestoreInstanceState
                    (savedRecyclerLayoutState);
        }
    }

    private void updateUI() {
        // To dismiss the dialog

        if (companyAdapter == null) {
            companies = FirebaseApplication.getCompanies();
            companyAdapter = new CompanyAdapter(companies);
            companyRecylerView.setAdapter(companyAdapter);
        } else
            companyAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onClose() {
        companyAdapter.filter("");
        Log.d("HELLLO", "CLOSING THIS NONSENSE");
        return true;
    }

    /**
     * Private classes
     */
    private class CompanyHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mCompanyRL;
        public TextView mCompanyName;
        public TextView mCompanyLocation;
        public ImageView mCompanyLogo;
        public ImageButton mCompanySave;
        public FirebaseCompany currentCompany;


        public CompanyHolder(View itemView) {
            super(itemView);
            mCompanyRL = (RelativeLayout) itemView.findViewById(R.id.info_cardview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle myBundle = new Bundle();
                    myBundle.putString(FirebaseApplication.COMPANY_ID,
                            currentCompany.id);
                    myBundle.putString(FirebaseApplication.COMPANY_NAME,
                            currentCompany.name);
                    myBundle.putString(FirebaseApplication.COMPANY_MAJORS,
                            currentCompany.majors);
                    myBundle.putString(FirebaseApplication.COMPANY_TABLE,
                            currentCompany.location);
                    myBundle.putString(FirebaseApplication.COMPANY_JOBTITLES,
                            currentCompany.jobtitles);
                    myBundle.putString(FirebaseApplication.COMPANY_JOBTYPES,
                            currentCompany.jobtypes);
                    myBundle.putString(FirebaseApplication.COMPANY_INFO,
                            currentCompany.information);
                    myBundle.putString(FirebaseApplication.COMPANY_WEBSITE,
                            currentCompany.website);
                    myBundle.putBoolean(FirebaseApplication.COMPANY_OPTCPT,
                            currentCompany.optcpt);
                    myBundle.putBoolean(FirebaseApplication.COMPANY_SPONSOR,
                            currentCompany.sponsor);

                    Intent i = new Intent(getActivity(), CompanyDetailsActivity.class);
                    i.putExtras(myBundle);
                    startActivity(i);
                }
            });

            mCompanyName = (TextView) itemView.findViewById(R.id.company_name);

            mCompanyLocation = (TextView) itemView.findViewById(R.id
                    .company_table);

            mCompanyLogo = (ImageView) itemView.findViewById(R.id.company_logo);

            mCompanySave = (ImageButton) itemView.findViewById(R.id.save_company);
            mCompanySave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MainActivity.isInDatabase(currentCompany.name)) { //Change to remove icon
                        // TODO: Snackbar instead of toast?
                        Toast.makeText(getContext(), R.string.star, Toast.LENGTH_SHORT).show();
                        // TODO: lol ic unfav and fav are swapped names
                        mCompanySave.setImageResource(R.drawable.ic_unfavorite);
                        MainActivity.addRow(currentCompany.id,
                                currentCompany.name);
                    } else {
                        Toast.makeText(getContext(), R.string.unstar, Toast.LENGTH_SHORT).show();
                        mCompanySave.setImageResource(R.drawable.ic_favorite);
                        MainActivity.deleteRow(currentCompany.id);
                    }
                }
            });

        }

    }

    private class CompanyAdapter extends RecyclerView.Adapter<CompanyHolder> {
        public List<FirebaseCompany> companies;

        public CompanyAdapter(List<FirebaseCompany> companies) {
            this.companies = companies;
        }

        @Override
        public CompanyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.info_fragment_cardview,
                    parent, false);
            return new CompanyHolder(view);
        }

        @Override
        public void onBindViewHolder(CompanyHolder holder, int position) {
            FirebaseCompany currentCompany = companies.get(position);
            holder.currentCompany = currentCompany;
            holder.mCompanyName.setText(currentCompany.name);
            holder.mCompanyLocation.setText("Table " + currentCompany
                    .location);

            StorageReference path = storageRef.child("logos/" +
                    currentCompany.getId() + ".png");

            // Log.d("Firebase", path.toString() + ": " +  path.hashCode() +
            // "");

            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(path)
                    .into(holder.mCompanyLogo);

            if (!MainActivity.isInDatabase(currentCompany.name)) { //Change to remove icon
                holder.mCompanySave.setImageResource(R.drawable.ic_favorite);
            } else {
                holder.mCompanySave.setImageResource(R.drawable.ic_unfavorite);
            }

            Log.d(TAG, "Recycler made for position " + position);
        }

        @Override
        public int getItemCount() {
            return companies.size();
        }

        public void filter(String text){
            List<FirebaseCompany> companyCopy = new ArrayList<FirebaseCompany>();
            for (FirebaseCompany comp: companies){
                companyCopy.add(comp);
            }
            companies.clear();
            if (text.isEmpty()){
                companies.addAll(companyCopy);
                notifyDataSetChanged();
            }
            if (!text.isEmpty()){
                text=text.toLowerCase();
                for(FirebaseCompany comp:companyCopy){
                    if(comp.name.toLowerCase().contains(text)){
                        companies.add(comp);
                        notifyDataSetChanged();
                    }
                }
                notifyDataSetChanged();
            }
            notifyDataSetChanged();
        }


    }


    /**  public class CollectTasks extends AsyncTask<String, Void,
     * List<Company>> {

     protected List<Company> doInBackground(String... strings) {
     List<Company> companies = new ArrayList<>();
     List<ParseObject> list = FirebaseApplication.getCompanyPOS();


     for (ParseObject po : list) {
     Company c = new Company(po.getObjectId(),
     po.getString(FirebaseApplication.COMPANY_NAME),
     (ArrayList<String>) po.get(FirebaseApplication.COMPANY_MAJORS),
     po.getParseFile(FirebaseApplication.COMPANY_LOGO)
     );
     companies.add(c);
     }
     return companies;
     }

     protected void onPostExecute(List<Company> list) {
     companyRecylerView = (RecyclerView) v.findViewById(R.id
     .info_recycler_view);
     companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
     companyRecylerView.setHasFixedSize(true);
     companyRecylerView.setAdapter(new CompanyAdapter(list));

     }
     }
     */
}
