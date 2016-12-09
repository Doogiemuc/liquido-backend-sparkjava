package com.doogie.liquido.models;

import com.doogie.liquido.mongoServices.DbId;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

@Data
public class AreaModel implements Validable, HasId {

  @SerializedName("_id")
  DbId id;

  String title;

  String description;

  Date createdAt;

  Date updatedAt;

  @Override
  public boolean isValid() {
    return !Strings.isNullOrEmpty(title) && !Strings.isNullOrEmpty(description);
  }

  public String getId() {
    return this.id != null ? this.id.getIdAsHex() : null;
  }
}
