package com.edwin.cachedemo.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Column(name = "created_time", nullable = false, updatable = false)
    @CreatedDate
    private Instant createdTime;

    @Column(name = "updated_time", nullable = false)
    @LastModifiedDate
    private Instant updatedTime;
}
