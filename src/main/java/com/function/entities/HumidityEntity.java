package com.function.entities;

public class HumidityEntity {
  private Long id;
  private String pk;
  private Double value;
  private Integer _ts;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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
