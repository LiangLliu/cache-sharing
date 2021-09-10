package com.edwin.cachedemo.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user")

@ToString

@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = com.edwin.cachedemo.test.User.class, name = "com.edwin.cachedemo.test.User"),
})
@JsonTypeName(value = "com.edwin.cachedemo.domain.User")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Column(name = "created_time", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp createdTime;

    @Column(name = "updated_time", nullable = false)
    @LastModifiedDate
    private Timestamp updatedTime;
}
