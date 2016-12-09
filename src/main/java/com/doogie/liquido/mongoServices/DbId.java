package com.doogie.liquido.mongoServices;

/**
 * marker class for ID fields
 * This "marker" is used in (de)serialisation
 * If have this marker class instead of directly using org.bson.types.ObjectId because I want the models to be completely independent of mongoDB.
 */
public class DbId  {
  private String _id;

  public DbId() {
    this._id = "";
  }

  public DbId(String id) {
    System.err.println("Creating new DBId from String id="+id);
    this._id = id;
  }

  public String getIdAsHex() {
    return this._id;
  }

  @Override
  public String toString() {
    return _id;
  }
}
