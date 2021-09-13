# cachedemo

* 探究 Redis + Cacheable 的使用
* 不同微服务共享缓存问题
* 探究由于微服务架构划分错误，两个微服务使用了相同的domain时，配置相同的key，redis读取缓存时存在缓存冲突的问题

### 场景

* 两个微服务，对象结构一致，但是包路径不同
* 两个User对象，结构完全一致，包路径不同
    * com.edwin.cache.account.User
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

    * com.edwin.cache.user.User
  ```java
  package com.edwin.cache.user;
  
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
    * com.edwin.cache.user.UserRepository
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

### 方案1

* 配置一个 Map<String, String> 关系

```java 
Map<String, String> map = new HashMap<>();
map.put("com.edwin.cache.user.User","com.edwin.cache.account.User")
map.put("com.edwin.cache.account.User","com.edwin.cache.user.User")
```

* 当反序列化时，在读取二进制（Json）数据时，进行转换
* 问题：
    * 如果redis存入的对象为 com.edwin.cache.user.User，当反序列化为 com.edwin.cache.account.User时，由于反序列化时需要把数据
      放进实例化对象中，这时候涉及到类加载，在account上下文中并没有com.edwin.cache.user.User的对象，因此会挂掉。
    * 转换时并不知道是哪个对象在获取缓存

