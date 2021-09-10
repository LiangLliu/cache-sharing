package com.edwin.cachedemo.test;

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
@Table(name = "account")
public class Account implements Serializable {
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

