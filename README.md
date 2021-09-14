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

### 方案2
* 如同方案1也配置一个map关系
* 对redis存储的数据反序列化时，尝试类加载，如果不能加载该类，那么通过加载 map.get(key)的值，尝试类加载，如果失败，走原来的，如果成功，
  替换原来的序列化类加载路径
* RedisSerializer 默认使用 JdkSerializationRedisSerializer
  * JdkSerializationRedisSerializer 默认会使用 org.springframework.core.serializer.support.SerializingConverter 和
    org.springframework.core.serializer.support.DeserializingConverter, 我们只需要关注反序列化器
  * DeserializingConverter 中 会使用 org.springframework.core.ConfigurableObjectInputStream
  * ConfigurableObjectInputStream 中有一个 resolveClass方法，我们只需要继承这个类并且把整个方法重写, 就可以按照需求加载对应的类
  ```java
  public class DemoObjectInputStream extends ConfigurableObjectInputStream {
      @Override
      protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
  
          String name = classDesc.getName();
          System.out.println(name);
  
          Map<String, String> map = new HashMap<>();
          map.put("com.edwin.cache.account.User", "com.edwin.cache.user.User");
          map.put("com.edwin.cache.user.User", "com.edwin.cache.account.User");
  
          if (!ClassUtils.isPresent(name, classLoader)) {
              String className = map.get(name);
              if (!ObjectUtils.isEmpty(className) && ClassUtils.isPresent(className, classLoader)) {
                  return ClassUtils.forName(className, this.classLoader);
              }
          }
  
          return super.resolveClass(classDesc);
      }
  }

  ```
### 测试
分别启动服务 cache-service 和 cache-account
* 通过测试可以拿到相同的结果
* 访问 http://localhost:8080/user
```shell
curl http://localhost:8080/user | python -m json.too
```
```json
[
    {
        "id": 1,
        "password": "111111",
        "username": "\u5f20\u4e09"
    },
    {
        "id": 2,
        "password": "222222",
        "username": "\u674e\u56db"
    },
    {
        "id": 3,
        "password": "333333",
        "username": "\u738b\u4e94"
    },
    {
        "id": 4,
        "password": "444444",
        "username": "\u7530\u516d"
    }
]
```

* 访问 http://localhost:8090/account
```shell
curl http://localhost:8090/account | python -m json.tool
```
```json
[
    {
        "id": 1,
        "password": "111111",
        "username": "\u5f20\u4e09"
    },
    {
        "id": 2,
        "password": "222222",
        "username": "\u674e\u56db"
    },
    {
        "id": 3,
        "password": "333333",
        "username": "\u738b\u4e94"
    },
    {
        "id": 4,
        "password": "444444",
        "username": "\u7530\u516d"
    }
]
``` 