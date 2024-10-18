package com.chatop.rental_backend.model;

import java.time.ZonedDateTime;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(schema = "chatop-oc", name = "rentals")
public class Rental implements Model {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false, length = 255)
  private String name;

  private int surface;

  /** Price in euro. */
  private int price;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private List<String> pictures;

  @Column(nullable = false, length = 2000)
  private String description;

  @ManyToOne()
  @JoinColumn(name = "owner_id")
  private User owner;

  @Column(nullable = false)
  private final ZonedDateTime createdAt;

  @Column(nullable = false)
  private ZonedDateTime updatedAt;

  public Rental(final String name, final int surface, final int price, final List<String> pictures,
      final String description, final User owner) {
    setName(name);
    setSurface(surface);
    setPrice(price);
    setPictures(pictures);
    setDescription(description);
    this.owner = owner;
    createdAt = ZonedDateTime.now();
    updatedAt = ZonedDateTime.now();
  }

  // Required By JPA
  protected Rental() {
    name = null;
    surface = 0;
    price = 0;
    pictures = null;
    description = null;
    owner = null;
    createdAt = null;
    updatedAt = null;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public String getDescription() {
    return description;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public User getOwner() {
    return owner;
  }

  public List<String> getPictures() {
    return pictures;
  }

  public int getPrice() {
    return price;
  }

  public int getSurface() {
    return surface;
  }

  public ZonedDateTime getUpdatedAt() {
    return updatedAt;
  }


  public void setDescription(final String description) {
    if (description == null) {
      return;
    }

    this.description = description;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setOwner(final User owner) {
    this.owner = owner;
  }

  public void setPictures(final List<String> picture) {
    pictures = picture.stream().map(String::new).toList();
  }

  public void setPrice(final int price) {
    this.price = price;
  }

  public void setSurface(final int surface) {
    this.surface = surface;
  }

  public void setUpdatedAt(final ZonedDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return this.getClass().getName() + " [id=" + id + ", name=" + name + ", surface=" + surface
        + ", price=" + price + ", picture=" + pictures + ", description=" + description
        + ", ownerId=" + owner.getId() + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
        + "]";
  }
}
