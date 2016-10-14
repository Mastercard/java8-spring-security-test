MasterCard Spring Security Testing
========================================
This Project is intended to provide utilities and features to simplify the process of unit and integration testing
Java 8 application logic that is dependent on authentication/authorization information managed by Spring Security.


### Web Integration Test
```java
@RunWith(SpringSecurityJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceApplication.class})
@WebIntegrationTest(randomPort = true)
public class ControllerTest {

    @Rule
    public LogPrincipalRule logPrincipalRule = new LogPrincipalRule(); //logs the principal used for executing each test

    @Test
    @WithMockUser
    public void shouldTestSomething() {
        ...
    }
}
```

### Service Integration Test
```java
@RunWith(SpringSecurityJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceApplication.class})
public class ServiceTest {

    @Rule
    public LogPrincipalRule logPrincipalRule = new LogPrincipalRule(); //logs the principal used for executing each test

    @Test
    @WithMockUser
    public void shouldTestSomething() {
        ...
    }
}
```

### Multiple Users
```java
@Test
@WithMockUser(username="admin",roles={"USER","ADMIN"})
@WithMockUser(username="user",roles={"USER"})
public void shouldTestSomething() {
    ...
}
```

### Creating Custom User Annotations
```java
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithMockUser(username="admin",roles={"USER","ADMIN"})
@WithMockUser(username="user",roles={"USER"})
public @interface WithAllMockUsers {}
```

### Using Custom User Annotations
```java
@Test
@WithAllMockUsers
public void shouldTestSomething() {
    ...
}
```
