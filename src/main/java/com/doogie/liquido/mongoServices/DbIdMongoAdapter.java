package com.doogie.liquido.mongoServices;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * GSON (de)serialization between mongoDB ObjectId and its JSON representation as mongo (java driver) returns it:
 * <pre>
 *   "_id": { "$oid": "57892d793d5b352b9b0134be" }
 * </pre>
 *
 */
public class DbIdMongoAdapter extends TypeAdapter<DbId> {
  @Override
  public void write(JsonWriter jsonWriter, DbId dbId) throws IOException {
    if (dbId == null) {
      System.err.println("WARNING: ObjectId is null   in DbIdMongoAdapter write");
      jsonWriter.nullValue();
      return;
    }
    jsonWriter.beginObject();
    jsonWriter.name("$oid");
    jsonWriter.value(dbId.getIdAsHex());
    jsonWriter.endObject();
  }

  @Override
  public DbId read(JsonReader jsonReader) throws IOException {
    jsonReader.beginObject();
    String nextName = jsonReader.nextName();
    if (!nextName.equals("$oid"))
      throw new IOException("Expected $oid");
    String idValue = jsonReader.nextString();
    jsonReader.endObject();
    return new DbId(idValue);
  }
/*
  same as JsonSerializer  and JsonDeserializer    but the above is more performant

  @Override
  public DbId deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    System.out.println("D ***************** bIdMongoAdapter.deserialize");
    if (jsonElement == null) {
      System.err.println("WARNING: ========= DbIdMongoAdapter.deserialize(jsonElemnt==null");
    }
    String objectIdStr = jsonElement.getAsJsonObject().get("$oid").toString();
    return new DbId(objectIdStr);
  }

  @Override
  public JsonElement serialize(DbId dbId, Type type, JsonSerializationContext jsonSerializationContext) {
    JsonObject obj = new JsonObject();
    obj.add("$oid", new JsonPrimitive(dbId.toString()));
    System.out.println("==== DbIdMongoAdapter.serialize(dbId="+dbId.toString()+"  to "+obj.toString());
    return obj;
  }
  */
}
