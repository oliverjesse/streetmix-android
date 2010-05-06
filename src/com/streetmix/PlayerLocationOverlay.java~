package com.streetmix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class PlayerLocationOverlay extends MyLocationOverlay {
    private Bitmap arrowBitmap;
    private Context context;
    private int team;
    
	public PlayerLocationOverlay(Context context, MapView mapView, int team) {
		super(context, mapView);
		this.context = context;
		this.team = team;
		
		arrowBitmap = BitmapFactory.decodeResource(
            context.getResources(), 
            (team == GlobalData.BLUE_TEAM) ? 
            R.drawable.man_blue : R.drawable.man_red
            );
	}
	
	@Override 
    protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix, GeoPoint myLocation, long when) {
        // translate the GeoPoint to screen pixels
        Point screenPts = mapView.getProjection().toPixels(myLocation, null);
        
        //Draw the custom player marker.
        canvas.drawBitmap(
            arrowBitmap, 
            screenPts.x - (arrowBitmap.getWidth()  / 2), 
            screenPts.y - (arrowBitmap.getHeight()), //Divide by 2 to center.
            null
        );
    }
    
    //public void onLocationChanged(android.location.Location location) {
    //    myLocation = location;
    //}
}
