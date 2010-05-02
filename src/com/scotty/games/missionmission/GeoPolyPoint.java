package com.scotty.games.missionmission;

import com.google.android.maps.GeoPoint;

/**
 * A special GeoPoint that remembers if it is elligible to be on the 
 * perimeter of a polygon or not, speeding up the area calculation.
 */
public class GeoPolyPoint extends GeoPoint {
    public boolean mayBeOnPath = true;
    
    public GeoPolyPoint(int latitude, int longitude) {
        super(latitude, longitude);
    }
    
    public GeoPolyPoint(GeoPoint p) {
        super(p.getLatitudeE6(), p.getLongitudeE6());
    }
}
