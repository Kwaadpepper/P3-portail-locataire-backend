package com.chatop.rental_backend.model;

import java.time.ZonedDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(schema = "chatop-oc", name = "messages")
public class Message implements Model {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "rental_id")
  private final Rental about;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private final User from;

  @Column(nullable = false, length = 2000)
  private final String textMessage;

  @Column(nullable = false)
  private final ZonedDateTime createdAt;

  @Column(nullable = false)
  private final ZonedDateTime updatedAt;

  public Message(final Rental about, final User from, final String textMessage) {
    this.about = about;
    this.from = from;
    this.textMessage = textMessage;
    createdAt = ZonedDateTime.now();
    updatedAt = ZonedDateTime.now();
  }

  // Required By JPA
  protected Message() {
    about = null;
    from = null;
    textMessage = null;
    createdAt = null;
    updatedAt = null;
  }

  @Override
  public String toString() {
    return this.getClass().getName() + " [id=" + id + ", about=" + about + ", about=" + about
        + ", textMessage=" + textMessage + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
        + "]";
  }
}
