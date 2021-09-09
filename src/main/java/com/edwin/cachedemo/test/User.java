package com.edwin.cachedemo.test;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
    private Long id;

    private String username;

    private String password;

    private Instant createdTime;
    private Instant updatedTime;
}
