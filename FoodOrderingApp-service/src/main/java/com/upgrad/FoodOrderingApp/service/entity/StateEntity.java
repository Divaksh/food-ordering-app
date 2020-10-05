package com.upgrad.FoodOrderingApp.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * StateEntity class contains all the attributes to be mapped to all the fields in 'state' table in
 * the database
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "stateByUUID", query = "SELECT s from StateEntity s WHERE  s.uuid = :uuid"),
    @NamedQuery(name = "getAllStates", query = "SELECT s FROM StateEntity s")
})

@Table(name = "state")
public class StateEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @NotNull
  @Column(name = "id")
  private Integer id;

  @Column(name = "uuid")
  @Size(max = 200)
  @NotNull
  private String uuid;

  @Column(name = "state_name")
  @Size(max = 30)
  @NotNull
  private String stateName;
  @OneToMany(mappedBy = "stateId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private final List<AddressEntity> Addresses = new ArrayList<>();

  public StateEntity() {
  }

  public StateEntity(String uuid, String name) {
    this.uuid = uuid;
    this.stateName = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getStateName() {
    return stateName;
  }

  public void setStateName(String stateName) {
    this.stateName = stateName;
  }

}