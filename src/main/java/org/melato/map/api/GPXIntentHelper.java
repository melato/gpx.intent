package org.melato.map.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.GPXWriter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**A class that facilitates putting and getting GPX data into an android Intent.
 * This class uses org.melato.gpx to represent GPX data, but
 * the intent contains only XML binary data.
 * The class puts data in the intent as a byte[],
 * but it could also get data from a uri pointing to a GPX file.
 * @author Alex Athanasopoulos
 *
 */
public class GPXIntentHelper {
  public static final String MIME_TYPE = "application/gpx";
  /** The key for the byte[] data */
  public static final String GPX = "gpx";
  /** A convenient waypoint type for denoting the start or origin of a route search. */
  public static String TYPE_START = "start";
  /** A convenient waypoint type for denoting the end or destination of a route search. */
  public static String TYPE_END = "end";
  
  private Context context;
  private boolean useFile = false;
      
  public GPXIntentHelper(Context context) {
    this(context, true);
  }

  
  public GPXIntentHelper(Context context, boolean useFile) {
    super();
    this.context = context;
    this.useFile = useFile;
  }

  public GPX getGPX(Intent intent) throws IOException {
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
  
  public static void putGPXFile(File gpxFile, Intent intent) throws IOException {    
    int flags = intent.getFlags();
    intent.setFlags(flags | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    intent.setDataAndType(Uri.fromFile(gpxFile), MIME_TYPE);
  }
  
  private File getTempDir() {
    File dir = context.getExternalFilesDir("tmp");
    return dir;    
  }
  
  public void deleteTempFiles() {
    File dir = getTempDir();
    if ( dir.exists()) {
      for(String name: dir.list() ) {
        if ( name.endsWith(".gpx")) {
          new File(dir, name).delete();
        }
      }
    }
  }
  
  private File createTempFile() throws IOException {
    File dir = getTempDir();
    dir.mkdirs();
    File file = File.createTempFile("intent", ".gpx", dir);
    return file;
  }
  
  private File createFile(GPX gpx) throws IOException {
    GPXWriter writer = new GPXWriter();
    File file = createTempFile();
    writer.write(gpx, file);
    return file;
  }
  
  public void putGPX(GPX gpx, Intent intent) throws IOException {    
    if ( useFile ) {
      deleteTempFiles();
      File file = createFile(gpx);
      putGPXFile(file, intent);
    } else {
      GPXWriter writer = new GPXWriter();
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      writer.write(gpx, buf);
      intent.putExtra(GPX, buf.toByteArray());
      intent.setType(MIME_TYPE);
    }
  }
}
