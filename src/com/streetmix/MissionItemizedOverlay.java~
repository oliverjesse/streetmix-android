package com.scotty.games.missionmission;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.PathShape;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MissionItemizedOverlay extends ItemizedOverlay {
    
    //Holds all of our overlay items.
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    //Holds only the points that make up the outline of the largest polygon.
    private ArrayList<OverlayItem> largestPolyList = new ArrayList<OverlayItem>();
    
    private Paint paint;
    private MissionMission mission;
    private float area = 0.0f;
    
    private static int AREA_PROTESTER = 0x4D0059E3;
    private static int AREA_CORPORATE = 0x53FF0000;
    
	public MissionItemizedOverlay(Drawable defaultMarker, MissionMission mission) {
	    //Bounds the markers' bottom's centers to the target locations.
		super(boundCenterBottom(defaultMarker));
		this.mission = mission;
		
		paint = new Paint();
		paint.setDither(true);
		paint.setColor(AREA_PROTESTER);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(3);
		
		//Tell the activity to prepare the empty overlay for drawing.
		//Will otherwise get a null pointer exception and a crash.
		populate();
	}
	
	public void addOverlay(OverlayItem overlay) {
	    //May need to add a delay here to ensure that entire method is 
	    //atomic... but probably not.
	    
	    
	    GeoPolyPoint newPoint = (GeoPolyPoint) overlay.getPoint();
	    for (int i = 0; i < mOverlays.size(); i++) {
	        GeoPolyPoint p = (GeoPolyPoint) mOverlays.get(i).getPoint();
	        if (newPoint.equals(p)) {
	            newPoint.mayBeOnPath = false;
	            break;
	        }
	    }
	    
	    mOverlays.add(overlay);
	    if ((mOverlays.size() > 2) && (newPoint.mayBeOnPath)) {
	        updateArea();
	    }
	    
	    //Prepares overlay objects for drawing.
	    //Makes a request to createItem(i) for marker 'i'.
	    populate();
	}
	
	@Override
	public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
	    //if (mOverlays.size() > 2) {
	        drawPolygon(largestPolyList, canvas, mapView);
	    //} else if (mOverlays.size() > 0) {
	    //    drawPolygon(mOverlays, canvas, mapView);
		//}
	    
		super.draw(canvas, mapView, shadow);
	}
	
	protected void drawPolygon(ArrayList<OverlayItem> polygonItems, android.graphics.Canvas canvas, MapView mapView) {
	    if (this.size() == 0) {
	        return;
	    }
	    
	    Path path = new Path();
	    Point p;
	    for (OverlayItem overlay : polygonItems) {
	        p = mapView.getProjection().toPixels(overlay.getPoint(), null);
	        if (path.isEmpty()) {
	            path.moveTo(p.x, p.y);
	            path.lineTo(p.x, p.y);
	        } else {
	            path.lineTo(p.x, p.y);
	        }
	    }
	    path.close();
	    canvas.drawPath(path, paint);
	}
	
	/**
	 * Updates the perimeter ArrayList.
	 */
	protected void updateArea() {
		if (mOverlays.size() > 2) {
			OverlayItem firstItemOnHull = findLowestPoint();
			constructConvexHull(firstItemOnHull);
			calculateArea();
		}
	    
	    return;
	}
	
	/**
	 * Returns the OverlayItem of the southern-most point in the list.
	 */
	private OverlayItem findLowestPoint() {
	    OverlayItem lowestItem = null;
	    GeoPolyPoint lowestPoint = new GeoPolyPoint(Integer.MAX_VALUE, 0);
	    for (int i = 0; i < mOverlays.size(); i++) {
	        OverlayItem item = mOverlays.get(i);
	        GeoPolyPoint p = (GeoPolyPoint) item.getPoint();
	        if (p.mayBeOnPath && (p.getLatitudeE6() < lowestPoint.getLatitudeE6())) {
	            lowestPoint = p;
	            lowestItem = item;
	        }
	    }
	    
	    return lowestItem;
	}
	
	/**
	 * Constructs a new ArrayList containing the perimeter points of
	 * the largest convex polygon created by the points of mOverlays.
	 * Assumes that mOverlays has 3 or more elements.
	 */
	private void constructConvexHull(OverlayItem startingItem) {
	    
	    //Construct the convex hull path.
	    ArrayList<OverlayItem> convexHull = new ArrayList<OverlayItem>();
	    OverlayItem lastItemOnHull = startingItem;
	    GeoPolyPoint startingPoint = (GeoPolyPoint) startingItem.getPoint();
	    GeoPolyPoint lastPointOnHull = startingPoint;
	    
	    double angleToLastPoint = 0.0d;
	    
	    do {
	        convexHull.add(lastItemOnHull);
	        double lastAngle = Double.POSITIVE_INFINITY;
	        GeoPolyPoint lastPoint = null;
	        OverlayItem lastItem = null;
	        
	        //Check each elligible point to see if it is on the hull path.
	        for (int j = 0; j < mOverlays.size(); j++) {
	            OverlayItem consideredItem = mOverlays.get(j);
	            GeoPolyPoint consideredPoint = (GeoPolyPoint) consideredItem.getPoint();
	            if ((!consideredPoint.mayBeOnPath) || (consideredPoint == lastPointOnHull)) {
	                continue;
	            }
	            
	            double angle = Math.atan2(
	                consideredPoint.getLatitudeE6() - lastPointOnHull.getLatitudeE6(),
	                consideredPoint.getLongitudeE6() - lastPointOnHull.getLongitudeE6());
                
	            if ((angle < 0.0f) || (angle < angleToLastPoint)) {
	                angle += 2.0f * Math.PI;
	            }
	            
	            if (angle <= lastAngle) {
	                lastItem = consideredItem;
	                lastPoint = consideredPoint;
	                lastAngle = angle;
	            }
	        }

	        lastItemOnHull = lastItem;
	        lastPointOnHull = lastPoint;
	        angleToLastPoint = lastAngle;
	        
	    } while (lastItemOnHull != startingItem);
	    
	    //Set points not on path to be ignored later.
	    for (int i = 0; i < mOverlays.size(); i++) {
	        OverlayItem item = mOverlays.get(i);
	        GeoPolyPoint p = (GeoPolyPoint) item.getPoint();
	        
	        if (p.mayBeOnPath && !convexHull.contains(item)) {
	            p.mayBeOnPath = false;
	        }
	    }
	    
	    largestPolyList = convexHull;
	}
	
	/**
	 * Determines the area of the convex polygon outlined by the 
	 * ArrayList largestPolyList. Assumes that largestPolyList
	 * has three or more elements.
     */
	private void calculateArea() {
	    if (largestPolyList.size() < 3) {
	        area = 0.0f;
	    }
	    
	    float areaRunningSum = 0.0f;
	    GeoPolyPoint prevPoint = (GeoPolyPoint) largestPolyList.get(largestPolyList.size() - 1).getPoint();
	    for (int i = 0; i < largestPolyList.size(); i++) { 
	        GeoPolyPoint currentPoint = (GeoPolyPoint) largestPolyList.get(i).getPoint();
	        
	        areaRunningSum += 
	            ((((float) prevPoint.getLongitudeE6()) * 0.000001) * (((float) currentPoint.getLatitudeE6()) * 0.000001)) 
	            - 
	            ((((float) currentPoint.getLongitudeE6()) * 0.000001) * (((float) prevPoint.getLatitudeE6()) * 0.000001));
	        
            prevPoint = currentPoint;	        
	    }
	    
	    area = areaRunningSum / 2;
	}
	
	public float getArea() {
		return area;
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}
	
	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	    OverlayItem item = mOverlays.get(index);
	    AlertDialog.Builder dialog = new AlertDialog.Builder(mission.getView().getContext());
	    dialog.setTitle(item.getTitle());
	    dialog.setMessage(item.getSnippet());
	    dialog.show();
	    return true;
	}
}
