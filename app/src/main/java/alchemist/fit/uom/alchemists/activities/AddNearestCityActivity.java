package alchemist.fit.uom.alchemists.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;


public class AddNearestCityActivity extends AppCompatActivity {

    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private String selectedNearestCity;
    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nearest_city);

        alchemistsDataSource = new AlchemistsDataSource(this);
        alchemistsDataSource.open();


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");


        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();

        placeAutocompleteFragment.setFilter(autocompleteFilter);

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Toast.makeText(getApplicationContext(),place.getName().toString(),Toast.LENGTH_SHORT).show();
                selectedNearestCity = place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();

            }
        });



    }
    public void confirmNearestCity(View view){
        finish();
        Intent intent = new Intent(this,EditProfileActivity.class);
        alchemistsDataSource.updateUserDetails(null,selectedNearestCity,null,sharedPrefEmailAddress,null);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void activityAddNearestCityCancel(View view){
        finish();
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }


}
