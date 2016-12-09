package com.doogie.liquido.mongoServices;

import com.doogie.liquido.LiquidoBackendSpark;
import com.doogie.liquido.models.HasId;
import com.doogie.liquido.models.Validable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.util.JSONParseException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BaseService<T extends Validable & HasId> {
  Logger log = LoggerFactory.getLogger(LiquidoBackendSpark.class);  // Simple Logging Facade 4 Java
  MongoCollection<Document> col;
  ObjectMapper mapper;
  final Class<T> modelClazz;
  private Gson gson;

  public BaseService(MongoDatabase database, String collectionName, Class<T> modelClass) {
    this.modelClazz = modelClass;
    this.mapper = new ObjectMapper();
    this.col = database.getCollection(collectionName);

    // Here we register our custom gson TypeAdadpters for custom (de)serialisation
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(DbId.class, new DbIdMongoAdapter());
    gsonBuilder.registerTypeAdapter(Date.class, new DateTimeMongoAdapter());
    this.gson = gsonBuilder.create();
  }

  public String toJson(T model) throws Exception {
    try {
      return gson.toJson(model);
    } catch (JSONParseException e) {
      throw new Exception("Cannot convert area to json", e);
    }

  }

  public T fromJson(String json) throws Exception {
    try {
      return gson.fromJson(json, modelClazz);
      //return mapper.readValue(json, modelClazz);
    } catch (Exception e) {
      throw new Exception("fromJson(): Cannot convert json to Area: "+json, e);
    }
  }

  public List<T> getAll() throws Exception {
    log.trace(col.getNamespace().getCollectionName() + " getAll()");
    FindIterable<Document> allAreaDocs = col.find();
    List<T> result = new ArrayList<>();
    for (Document areaDoc : allAreaDocs) {
      result.add(fromJson(areaDoc.toJson()));
    }
    return result;
  }

  public String getAllAsJson() {
    FindIterable<Document> allAreaDocs = col.find();
    List<String> allAsJson = new ArrayList<>();
    for (Document areaDoc : allAreaDocs) {
      allAsJson.add(areaDoc.toJson());
    }
    return "["+String.join(",", allAsJson)+"]";
  }

  public T findById(String id) throws Exception {
    FindIterable<Document> docs = col.find(Filters.eq("_id", new ObjectId(id)));
    //Set<Map.Entry<String, Object>> entries = docs.first().entrySet();
    if (docs != null && docs.first() != null) {
      return fromJson(docs.first().toJson());
    } else {
      return null;
    }
  }

  public void insert(T model) throws Exception {
    if (!model.isValid()) {
      throw new Exception("Cannot insert: Areas is invalid");
    }
    //nice and short: http://stackoverflow.com/questions/739689/how-to-convert-a-java-object-bean-to-key-value-pairs-and-vice-versa
    Map<String, Object> areaAsMap = mapper.convertValue(model, Map.class);
    col.insertOne(new Document(areaAsMap));
  }

  /**
   * Replace an existing document in the DB.
   *
   * @param id   ObjectId of an existing document.
   * @param json new values that will (completely!) replace the old object. json should not contain any ID
   */
  public void replace(String id, String json) throws Exception {
    if (Strings.isNullOrEmpty(id)) throw new Exception("Cannot replace area without id");
    col.replaceOne(
      Filters.eq("_id", new ObjectId(id)),
      Document.parse(json),
      new UpdateOptions().upsert(true).bypassDocumentValidation(false)
    );
  }

  public void replace(T model) throws Exception {
    String json = toJson(model);
    this.replace(model.getId(), json);
  }

  public void updateOne(T model) throws Exception {
    String json = toJson(model);
    col.updateOne(
      Filters.eq("_id", new ObjectId(model.getId())),
      Document.parse(json)
    );
  }
}
