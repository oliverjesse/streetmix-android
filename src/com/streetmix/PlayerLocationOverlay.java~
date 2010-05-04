package com.scotty.games.missionmission;

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

    private Context context;
    
	public PlayerLocationOverlay(Context context, MapView mapView) {
		super(context, mapView);
		this.context = context;
	}
	
	@Override 
    protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix, GeoPoint myLocation, long when) {
        // translate the GeoPoint to screen pixels
        Point screenPts = mapView.getProjection().toPixels(myLocation, null);
        Bitmap arrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.man_blue);
        
        //Draw the custom player marker.
        canvas.drawBitmap(
            arrowBitmap, 
            screenPts.x - (arrowBitmap.getWidth()  / 2), 
            screenPts.y - (arrowBitmap.getHeight()), //Divide by 2 to center.
            null
        );
    }
    
}
