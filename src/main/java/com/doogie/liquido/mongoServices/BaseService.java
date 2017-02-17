package com.doogie.liquido.mongoServices;

import com.doogie.liquido.models.AreaModel;
import com.doogie.liquido.models.HasId;
import com.doogie.liquido.models.Validable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonReader;
import org.bson.Document;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseService<T extends Validable & HasId> {
  Logger log = LoggerFactory.getLogger(BaseService.class);  // Simple Logging Facade 4 Java
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

  public String toJson(T model) {
    return gson.toJson(model);
  }

  public T fromJson(String json) throws Exception {
    try {
      return gson.fromJson(json, modelClazz);
      //return mapper.readValue(json, modelClazz);
    } catch (JsonSyntaxException e) {
      throw new Exception("fromJson(): Cannot convert json to Area: "+json, e);
    }
  }

  /**
   * get all models from the DB as domain objects
   * mapped with GSON.
   * @return
   * @throws Exception
   */
  public List<T> getAll() throws Exception {
    log.trace(col.getNamespace().getCollectionName() + " getAll()");
    FindIterable<Document> allAreaDocs = col.find();
    List<T> result = new ArrayList<>();
    for (Document areaDoc : allAreaDocs) {

      //MAYBE: areaDoc.entrySet()  => then create object from keySet

      result.add(fromJson(areaDoc.toJson()));
    }
    return result;
  }

  /**
   * get all models in the extends JSON format that mongo-java-driver returns
   * @return
   */
  public String getAllAsMongoJson() {
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
    //TODO: check for "fachlichen Schlüssel"  and then do not insert twice!!

    //nice and short: http://stackoverflow.com/questions/739689/how-to-convert-a-java-object-bean-to-key-value-pairs-and-vice-versa
    //with jaxon mapper
    //Map<String, Object> areaAsMap = mapper.convertValue(model, Map.class);
    //and then    new Document(areaAsMap)     instead of Document.parse(json)
    String json = gson.toJson(model);
    col.insertOne(Document.parse(json));      // May throw MongoWriteException for DUPLICATE_KEY
  }


  /*
  public void updateOne(T model) throws Exception {
    Bson query  = Filters.eq("_id", new ObjectId(model.getId()))

    //You cannot simply send Document.parse(toJson(model))  because that would have _id in it. Mongo requires its "$set" syntax,
    //that can be generated like this:
    Bson update = Updates.combine(
      Updates.set("title", "updated title"),
      Updates.currentDate("updatedAt")
    );

    col.updateOne(query, update);
  }
  */

  public void upsert(T model) throws Exception {
    // the parent class must provide an implementation for
    //Bson getEqualityFilter(model)

    if (model.getId() == null) {
      //TODO: then insert
    } else {
      String json = gson.toJson(model);
      col.replaceOne(
        Filters.eq("_id", new ObjectId(model.getId())),
        Document.parse(json),
        new UpdateOptions().upsert(true).bypassDocumentValidation(false)
      );
    }
  }

  /**
   * replaces the given model with its new data.
   * @param model Model class including its ID field!
   * @throws Exception when ID is not filled
   */
  public void replace(T model) throws Exception {
    if (Strings.isNullOrEmpty(model.getId())) throw new Exception("Cannot replace model without id");
    String json = toJson(model);
    col.replaceOne(
      Filters.eq("_id", new ObjectId(model.getId())),
      Document.parse(json),
      new UpdateOptions().upsert(false).bypassDocumentValidation(false)
    );
  }

  public long count() {
    return col.count();
  }
}
