package com.doogie.liquido.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/** Utilities that you ALWAYS need */
public class DoogieUtils {

  /**
   * Null-save check for empty (or null) string.
   * @return true if s is null or only contains spaces
   */
  public static boolean isEmpty(String s) {
    if (s == null) return true;
    if (s.trim().length() == 0) return true;
    return false;
  }

  /** read an InputStream into a String */
  public static String read(InputStream input) throws IOException {
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
      return buffer.lines().collect(Collectors.joining("\n"));
    }

  }
}
