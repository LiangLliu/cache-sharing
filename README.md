# cachedemo

* 探究 Redis + Cacheable 的使用

### 场景
  
* 两个User对象，结构完全一致，包路径不同
  * com.edwin.cache.account;.User
  ```java
  package com.edwin.cache.account;
  
  import lombok.*;
  
  import java.io.Serializable;
  
  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @ToString
  public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
  }

  ``` 
              
  * com.edwin.cache.domain.User
  ```java
  package com.edwin.cache.domain;
  
  import lombok.*;
  
  import javax.persistence.*;
  import java.io.Serializable;
  
  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Entity
  @Builder
  @Table(name = "user")
  @ToString
  public class User implements Serializable {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      private String username;
      private String password;
  }
  ```
* 缓存方案
  * com.edwin.cache.account.User
  ```java
  @CacheConfig(cacheNames = "user")
  @Repository
  @RequiredArgsConstructor
  public class AccountRepository {
  private final AccountJpaRepository accountJpaRepository;
  
      public static final String USER_PEX = "USER_";
  
      @Cacheable(cacheNames = USER_PEX, key = "'user.id:'+#id")
      public User findUserById(Long id) {
          Optional<Account> optionalAccount = accountJpaRepository.findById(id);
          if (optionalAccount.isEmpty()) {
              return new User();
          }
          Account account = optionalAccount.get();
          return User.builder()
                  .id(account.getId())
                  .username(account.getUsername())
                  .password(account.getPassword())
                  .build();
      }
  }
  ```
  * com.edwin.cache.repository
  ```java
  @CacheConfig(cacheNames = "user")
  @Repository
  @RequiredArgsConstructor
  public class UserRepository {
  private final UserJpaRepository userJpaRepository;
  
      public static final String USER_PEX = "USER_";
  
      @Cacheable(cacheNames = USER_PEX, key = "'user.id:'+#id")
      public User findUserById(Long id) {
          return userJpaRepository.findById(id).orElse(new User());
      }
  }
  ```
* 希望存到Redis后可以做缓存共享

