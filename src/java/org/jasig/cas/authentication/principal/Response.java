/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication.principal;

import java.net.URLEncoder;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author TrungBH
 */
public class Response {
    static final Logger logger = Logger.getLogger(Response.class);
    private final ResponseType responseType;
    private final String url;
    private final Map<String, String> attributes;

    protected Response(ResponseType responseType, String url, Map<String, String> attributes) {
      this.responseType = responseType;
      this.url = url;
      this.attributes = attributes;
    }

    public static Response getPostResponse(String url, Map<String, String> attributes) {
      return new Response(ResponseType.POST, url, attributes);
    }

    public static Response getRedirectResponse(String url, Map<String, String> parameters) {
      StringBuilder builder = new StringBuilder(parameters.size() * 40 + 100);
      boolean isFirst = true;
      builder.append(url);
      for (Map.Entry<String, String> entry : parameters.entrySet()) {
        if (entry.getValue() != null) {
          if (isFirst) {
            builder.append(url.contains("?") ? "&" : "?");
            isFirst = false;
          } else {
            builder.append("&");
          } 
          builder.append(entry.getKey());
          builder.append("=");
          try {
            builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          } catch (Exception e) {
            logger.error(e);
            builder.append(entry.getValue());
          } 
        } 
      } 
      return new Response(ResponseType.REDIRECT, builder.toString(), parameters);
    }

    public Map<String, String> getAttributes() {
      return this.attributes;
    }

    public ResponseType getResponseType() {
      return this.responseType;
    }

    public String getUrl() {
      return this.url;
    }
}
