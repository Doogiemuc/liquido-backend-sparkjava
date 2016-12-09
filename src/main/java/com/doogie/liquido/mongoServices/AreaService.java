package com.doogie.liquido.mongoServices;

import com.doogie.liquido.models.AreaModel;
import com.mongodb.client.MongoDatabase;

public class AreaService extends BaseService<AreaModel> {

  public AreaService(MongoDatabase database) {
    super(database, "areas", AreaModel.class);
  }



}