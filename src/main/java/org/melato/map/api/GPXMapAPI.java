package org.melato.map.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.GPXWriter;
import org.melato.log.Log;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

/**A class that facilitates putting and getting GPX data into an android Intent.
 * This class uses org.melato.gpx to represent GPX data, but
 * the intent refers only to the XML representation of the GPX data, either as a byte[], or as file.  
 * @author Alex Athanasopoulos
 *
 */
public class GPXMapAPI {
  /** The key for the byte[] data */
  public static final String GPX = "gpx";
  /** A convenient waypoint type for denoting the start or origin of a route search. */
  public static String TYPE_START = "start";
  /** A convenient waypoint type for denoting the end or destination of a route search. */
  public static String TYPE_END = "end";
  static boolean useFile = false;
  
  public static GPX getGPX(Context context, Intent intent) throws IOException {
    GPXParser parser = new GPXParser();
    byte[] gpxData = intent.getByteArrayExtra(GPX);
    if ( gpxData != null ) {
      return parser.parse(new ByteArrayInputStream(gpxData));      
    }
    Uri uri = intent.getData();
    if ( uri != null) {
      InputStream inputStream = context.getContentResolver().openInputStream(uri);
      return parser.parse(inputStream);
    }
    return null;    
  }
  public static void putGPX(GPX gpx, Intent intent) throws IOException {    
    GPXWriter writer = new GPXWriter();
    if ( useFile ) {
      File file = new File(Environment.getExternalStorageDirectory(), "routes.gpx");
      writer.write(gpx, file);      
      intent.setDataAndType(Uri.fromFile(file), "application/gpx");
      return;
    }
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.write(gpx, buf);
    intent.putExtra(GPX, buf.toByteArray());
    intent.setType("application/gpx");
    Log.info("gpx: " + buf);
  }
}
