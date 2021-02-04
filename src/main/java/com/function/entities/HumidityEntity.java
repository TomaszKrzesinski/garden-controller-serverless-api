package com.function.entities;

public class HumidityEntity {
  private String id;
  private String pk;
  private Double value;
  private Integer _ts;

  public HumidityEntity(String pk, Double value) {
    this.pk = pk;
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPk() {
    return pk;
  }

  public void setPk(String pk) {
    this.pk = pk;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public Integer getTs() {
    return _ts;
  }
}
