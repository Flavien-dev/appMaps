package com.chapfla.appmaps;

// importation des librairies

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * classe principale de l'application
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // déclaration des variables
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    /**
     * permet de dire si on a la permission de se localiser
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                // vérifie qu'on ait bien la permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,1,locationListener);
                }
            }
        }
    }

    /**
     * s'exécute lors du lancement de l'application
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * permet d'afficher des éléments dès que la map est prête
     * @param googleMap carte google map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // déclaration des objets
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            /**
             * s'exécute lorsque la position du téléphone change
             * @param location position du téléphone
             */
            @Override
            public void onLocationChanged(@NonNull Location location) {

                // affiche le nouveau marqueur à la position donnée
                LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                displayMarker(userLocation);

                // nouvelle coordonnée
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        };

        // vérifie que la version du sdk est assez récente
        if (Build.VERSION.SDK_INT<23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // modifie la position
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,1,locationListener);
        }else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,1,locationListener);

                // crée une nouvelle position
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                // crée la position de l'utilisateur
                LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

                // affiche le marqueur à la position de l'utilisateur
                displayMarker(userLocation);
            }
        }
    }

    /**
     * affiche le marqueur sur la carte
     * @param userLocation position de l'utilisateur
     */
    public void displayMarker(LatLng userLocation) {
        // nettoie la carte
        mMap.clear();
        // ajoute le marqueur à la position de l'utilisateur
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Ma position"));
        // déplace la caméra à la position du marqueur
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
    }
}