package com.doogie.liquido.models;

import com.doogie.liquido.mongoServices.DbId;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
public class AreaModel implements Validable, HasId {

  @SerializedName("_id")
  DbId id;

  @NonNull
  String title;

  @NonNull
  String description;

  Date createdAt;

  Date updatedAt;

  public AreaModel(String title, String description) {
    this.title = title;
    this.description = description;
    this.createdAt = new Date();
    this.updatedAt = new Date();
  }

  @Override
  public boolean isValid() {
    return !Strings.isNullOrEmpty(title) && !Strings.isNullOrEmpty(description);
  }

  public String getId() {
    return this.id != null ? this.id.getIdAsHex() : null;
  }
}
