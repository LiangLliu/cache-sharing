package com.edwin.cachedemo.test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = com.edwin.cachedemo.domain.User.class, name = "com.edwin.cachedemo.domain.User"),
})
@JsonTypeName(value = "com.edwin.cachedemo.test.User")
public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
    private Timestamp createdTime;
    private Timestamp updatedTime;

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
