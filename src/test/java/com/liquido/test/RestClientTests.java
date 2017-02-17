package com.liquido.test;

import com.mashape.unirest.http.Unirest;
import org.junit.Test;

public class RestClientTests {

  public static final String BASE_URL = "http://localhost:8080";

  @Test
  public void testInsertNewArea() {
    String response = Unirest.get(BASE_URL+"/areas").toString();
    System.out.println(response);
  }
}
