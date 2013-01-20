/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taveloper.http.test;

import com.google.api.client.util.ClassInfo;
import com.google.api.client.util.FieldInfo;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Taveloper@gmail.com
 */
public class HttpHeaders转化 {

  /** {@code "Accept"} header. */
  @Key("Accept")
  private List<String> accept;

  /** {@code "Accept-Encoding"} header. */
  @Key("Accept-Encoding")
  private List<String> acceptEncoding = new ArrayList<String>(Collections.singleton("gzip"));

  /** {@code "Authorization"} header. */
  @Key("Authorization")
  private List<String> authorization;

  /** {@code "Cache-Control"} header. */
  @Key("Cache-Control")
  private List<String> cacheControl;

  /** {@code "Content-Encoding"} header. */
  @Key("Content-Encoding")
  private List<String> contentEncoding;

  /** {@code "Content-Length"} header. */
  @Key("Content-Length")
  private List<Long> contentLength;

  /** {@code "Content-MD5"} header. */
  @Key("Content-MD5")
  private List<String> contentMD5;

  /** {@code "Content-Range"} header. */
  @Key("Content-Range")
  private List<String> contentRange;

  /** {@code "Content-Type"} header. */
  @Key("Content-Type")
  private List<String> contentType;

  /** {@code "Cookie"} header. */
  @Key("Cookie")
  private List<String> cookie;

  /** {@code "Date"} header. */
  @Key("Date")
  private List<String> date;

  /** {@code "ETag"} header. */
  @Key("ETag")
  private List<String> etag;

  /** {@code "Expires"} header. */
  @Key("Expires")
  private List<String> expires;

  /** {@code "If-Modified-Since"} header. */
  @Key("If-Modified-Since")
  private List<String> ifModifiedSince;

  /** {@code "If-Match"} header. */
  @Key("If-Match")
  private List<String> ifMatch;

  /** {@code "If-None-Match"} header. */
  @Key("If-None-Match")
  private List<String> ifNoneMatch;

  /** {@code "If-Unmodified-Since"} header. */
  @Key("If-Unmodified-Since")
  private List<String> ifUnmodifiedSince;

  /** {@code "Last-Modified"} header. */
  @Key("Last-Modified")
  private List<String> lastModified;

  /** {@code "Location"} header. */
  @Key("Location")
  private List<String> location;

  /** {@code "MIME-Version"} header. */
  @Key("MIME-Version")
  private List<String> mimeVersion;

  /** {@code "Range"} header. */
  @Key("Range")
  private List<String> range;

  /** {@code "Retry-After"} header. */
  @Key("Retry-After")
  private List<String> retryAfter;

  /** {@code "User-Agent"} header. */
  @Key("User-Agent")
  private List<String> userAgent;

  /** {@code "WWW-Authenticate"} header. */
  @Key("WWW-Authenticate")
  private List<String> authenticate;

  public HttpHeaders转化() {
    this.classInfo = ClassInfo.of(getClass(), false);;
  }

  /** Class information. */
  final ClassInfo classInfo;

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    HttpHeaders转化 newMain = new HttpHeaders转化();
    newMain.run();
    Set<String> singleton = Collections.singleton("gzip");
    ArrayList<String> al = new ArrayList<String>(singleton);
  }

  private void run() {
    Collection<String> names = classInfo.getNames();
    for (Iterator<String> it = names.iterator(); it.hasNext();) {
      String name = it.next();
      String javaDoc = "/** {@code \"" + name + "\"} header. */";
      System.out.println(javaDoc);
      String javaAnn = "@Key(\"" + name + "\")";
      System.out.println(javaAnn);
      FieldInfo fieldInfo = classInfo.getFieldInfo(name);
      String fieldName = fieldInfo.getField().getName();
      String declare = "public static final String " + fieldName.toUpperCase() + "= \"" + name + "\";";
      System.out.println(declare);
      System.out.println();
    }
  }
}
