# Spring Security
    인증(Authentication)과 
    인가(권한/Authorization)로 이루어진 Spring framework
## 인증 (Authentication)
    사이트에 접근하는 사람이 누구인지 시스템이 알아야 한다. 익명사용자를 허용하기도 하지만,
    특정 리소스에 접근하거나 개인화된 사용성을 보장 받기 위해서는 반드시 로그인하는 과정이 필요하다.
    로그인은 보통 username / password 를 입력하고 로그인하는 경우와 sns 사이트를 통해 인증을 대리하는
    경우가 있다.
- UsernamePassword 인증
    - session 관리
    - token 관리 (session less)
- SNS 로그인 (소셜 로그인) 인증 위임

## 인가 or 권한 (Authorization)
    사용자가 누구인지 확인된 상태에서 사이트 관리자 혹은 시스템은 로그인한 사용자가 어떤 일을 할 수 있는지
    권한을 설정한다. 권한은 특정 페이지에 접근하거나 특정 리소스에 접근할 수 있는 권한여부를 판단하는데 사용된다.
    개발자는 권한이 있는 사용자에게만 페이지나 리소스 접근을 허용하도록 코딩해야 하는데, 이런 코드를 쉽게 작성할 수
    있도록 프레임워크를 제공하는 것이 스프링 시큐리티이다.
- Secured : deprecated
- PrePostAuthorize
- AOP
## 로그인
    스프링 프레임워크에서 로그인을 한다는 것은 authenticated가 true인 Authentication 객체를
    SecurityContext에 갖고 있는 상태를 말한다. 단 Authentication이 AnonymouseAuthenticationToken만
    아니면 된다. 

## Authentication (인증)의 기본 구조

- 필터들 중에 일부 필터는 인증 정보에 관여한다. 이들 필터가 하는 일은 AuthenticationManager 를 통해 Authentication
  을 인증하고 그 결과를 SecurityContextHolder 에 넣어주는 일이다.
  
<img width="836" alt="fig-3-authentication" src="https://user-images.githubusercontent.com/67107008/125895203-dcfeeadf-9ced-451a-885c-31dfd3e07a97.png">

- 인증 토큰(Authentication)을 제공하는 필터들

  - UsernamePasswordAuthenticationFilter : 폼 로그인 -> UsernamePasswordAuthenticationToken
  - RememberMeAuthenticationFilter : remember-me 쿠키 로그인 -> RememberMeAuthenticationToken
  - AnonymousAuthenticationFilter : 로그인하지 않았다는 것을 인증함 -> AnonymousAuthenticationToken
  - SecurityContextPersistenceFilter : 기존 로그인을 유지함(기본적으로 session 을 이용함)
  - BearerTokenAuthenticationFilter : JWT 로그인
  - BasicAuthenticationFilter : ajax 로그인 -> UsernamePasswordAuthenticationToken
  - OAuth2LoginAuthenticationFilter : 소셜 로그인 -> OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken
  - OpenIDAuthenticationFilter : OpenID 로그인
  - Saml2WebSsoAuthenticationFilter : SAML2 로그인
  - ... 기타

- Authentication 을 제공(Provide) 하는 인증제공자는 여러개가 동시에 존재할 수 있고, 
  인증 방식에 따라 ProviderManager 도 복수로 존재할 수 있다.
- Authentication 은 인터페이스로 아래와 같은 정보들을 갖고 있다.
  - _Set&lt;GrantedAuthority&gt; authorities_ : 인증된 권한 정보
  - _principal_ : 인증 대상에 관한 정보. 주로 UserDetails 객체가 옴
  - _credentials_ : 인증 확인을 위한 정보. 주로 비밀번호가 오지만, 인증 후에는 보안을 위해 삭제함.
  - _details_ : 그 밖에 필요한 정보. IP, 세션정보, 기타 인증요청에서 사용했던 정보들.
  - _boolean authenticated_ : 인증이 되었는지를 체크함.

# Authentication 메커니즘

## 인증 (Authentication)

<img src="../images/fig-6-Authentication.png" width="600" style="max-width:600px;width:100%;" />

- Authentication 는 인증된 결과만 저장하는 것이 아니고, 인증을 하기 위한 정보와 인증을 받기 위한 정보가 하나의 객체에 동시에 들어 있다. 
  왜냐하면, 인증을 제공해줄 제공자(AuthenticationProvider)가 어떤 인증에 대해서 허가를 내줄 것인지 판단하기 위해서는 직접 입력된 인증을 
  보고 허가된 인증을 내주는 방식이기 때문이다. 그래서 AuthenticationProvider 는 처리 가능한 Authentication에 대해 알려주는 support 
  메소드를 지원하고, authenticate() 에서 Authentication을 입력값과 동시에 출력값으로도 사용한다.

  - Credentials : 인증을 받기 위해 필요한 정보, 비번등 (input)
  - Principal : 인증된 결과. 인증 대상 (output)
  - Details : 기타 정보, 인증에 관여된 된 주변 정보들
  - Authorities : 권한 정보들,

- Authentication 을 구현한 객체들은 일반적으로 Token(버스 토큰과 같은 통행권) 이라는 
  이름의 객체로 구현된다. 그래서 Authentication의 구현체를 인증 토큰이라고 불러도 좋다.
- Authentication 객체는 SecurityContextHolder 를 통해 세션이 있건 없건 언제든 
  접근할 수 있도록 필터체인에서 보장해준다.

## 인증 제공자(AuthenticationProvider)

<img src="../images/fig-7-AuthenticationProvider.png" width="600" style="max-width:600px;width:100%;" />

- 인증 제공자(AuthenticationProvider)는 기본적으로 Authentication 을 받아서 인증을 
  하고 인증된 결과를 다시 Authentication 객체로 전달 한다.
- 그런데 인증 제공자는 어떤 인증에 대해서 도장을 찍어줄지 AuthenticationManager 에게 
  알려줘야 하기 때문에 support() 라는 메소드를 제공한다. 인증 대상과 방식이 다양할 수 있기 때문에 인증 제공자도 여러개가 올 수 있다.

## 인증 관리자(AuthenticationManager)

<img src="../images/fig-8-AuthenticationManager.png" width="600" style="max-width:600px;width:100%;" />

- 인증 제공자들을 관리하는 인터페이스가 AuthenticationManager (인증 관리자)이고, 이 인증 관리자를 구현한 객체가 ProviderManager 이다.
- ProviderManager 도 복수개 존재할 수 있다.
- 개발자가 직접 AuthenticationManager를 정의해서 제공하지 않는다면, AuthenticationManager 
  를 만드는 AuthenticationManagerFactoryBean 에서 DaoAuthenticationProvider 를 기본 인증제공자로 등록한 AuthenticationManage를 만든다.
- DaoAuthenticationProvider 는 반드시 1개의 UserDetailsService 를 발견할 수 있어야 한다. 
  만약 없으면 InmemoryUserDetailsManager 에 [username=user, password=(서버가 생성한 패스워드)]인 사용자가 등록되어 제공된다.

## 폼 로그인

### DefaultLoginPageGeneratingFilter

- GET /login 을 처리
- 별도의 로그인 페이지 설정을 하지 않으면 제공되는 필터
- 기본 로그인 폼을 제공
- OAuth2 / OpenID / Saml2 로그인과도 같이 사용할 수 있음.

### UsernamePasswordAuthenticationFilter

- POST /login 을 처리. processingUrl 을 변경하면 주소를 바꿀 수 있음.
- form 인증을 처리해주는 필터로 스프링 시큐리티에서 가장 일반적으로 쓰임.
- 주요 설정 정보

  - filterProcessingUrl : 로그인을 처리해 줄 URL (POST)
  - username parameter : POST에 username에 대한 값을 넘겨줄 인자의 이름
  - password parameter : POST에 password에 대한 값을 넘겨줄 인자의 이름
  - 로그인 성공시 처리 방법
    - defaultSuccessUrl : alwaysUse 옵션 설정이 중요
    - successHandler
  - 로그인 실패시 처리 방법
    - failureUrl
    - failureHandler
  - authenticationDetailSource : Authentication 객체의 details 에 들어갈 정보를 직접 만들어 줌.

  ```java
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  		throws AuthenticationException {
  	if (this.postOnly && !request.getMethod().equals("POST")) {
  		throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
  	}
  	String username = obtainUsername(request);
  	username = (username != null) ? username : "";
  	username = username.trim();
  	String password = obtainPassword(request);
  	password = (password != null) ? password : "";
  	UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
  	// Allow subclasses to set the "details" property
  	setDetails(request, authRequest);
  	return this.getAuthenticationManager().authenticate(authRequest);
  }
  ```

### DefaultLogoutPageGeneratingFilter

- GET /logout 을 처리
- POST /logout 을 요청할 수 있는 UI 를 제공
- DefaultLoginPageGeneratingFilter 를 사용하는 경우에 같이 제공됨.

### LogoutFilter

- POST /logout 을 처리. processiongUrl 을 변경하면 바꿀 수 있음.
- 로그 아웃을 처리

  - session, SecurityContext, csrf, 쿠키, remember-me 쿠키 등을 삭제처리 함.
  - (기본) 로그인 페이지로 redirect

  ```java
  private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
  		throws IOException, ServletException {
  	if (requiresLogout(request, response)) {
  		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
  		if (this.logger.isDebugEnabled()) {
  			this.logger.debug(LogMessage.format("Logging out [%s]", auth));
  		}
  		this.handler.logout(request, response, auth);
  		this.logoutSuccessHandler.onLogoutSuccess(request, response, auth);
  		return;
  	}
  	chain.doFilter(request, response);
  }
  ```

- LogoutHandler

  - void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
  - SecurityContextLogoutHandler : 세션과 SecurityContext 를 clear 함.
  - CookieClearingLogoutHandler : clear 대상이 된 쿠키들을 삭제함.
  - CsrfLogoutHandler : csrfTokenRepository 에서 csrf 토큰을 clear 함.
  - HeaderWriterLogoutHandler
  - RememberMeServices : remember-me 쿠키를 삭제함.
  - LogoutSuccessEventPublishingLogoutHandler : 로그아웃이 성공하면 이벤트를 발행함.

- LogoutSuccessHandler

  - void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    throws IOException, ServletException;
  - SimpleUrlLogoutSuccessHandler

  
## Code Example
```java
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/user")
    public SecurityMessage user(){
        return SecurityMessage.builder()
                .auth(SecurityContextHolder.getContext().getAuthentication())
                .message("User Info")
                .build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public SecurityMessage admin(){
        return SecurityMessage.builder()
                .auth(SecurityContextHolder.getContext().getAuthentication())
                .message("Admin Info")
                .build();
    }
```
@PreAuthorize("hasAnyAuthority('ROLE_USER')") : 해당 url 접근시 USER 권한이 필요함
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')") : 해당 url 접근시 ADMIN 권한이 필요함
```
# application.yml
# 기본적으로 프로젝트에 spring security의 의존성을 주입해서 사용하면 url, resourece  
# 접근시 인증을 요청함
# 그에 따른 id, pwd, role을 설정하는 값들
spring:
  security:
    user:
      name: hong
      password: 1
      roles: USER
```

## ROLE 확인..
```java
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/user")
    public SecurityMessage user(){
        return SecurityMessage.builder()
                .auth(SecurityContextHolder.getContext().getAuthentication())
                .message("User Info")
                .build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public SecurityMessage admin(){
        return SecurityMessage.builder()
                .auth(SecurityContextHolder.getContext().getAuthentication())
                .message("Admin Info")
                .build();
    }
    /*
      spring:
        security:
          user:
            name: hong
            password: 1
            roles: USER
     */
```
위와 같은 상태로 서버에 "/admin" 요청을 하면 role이 USER이기 때문에 접근을 못 할 것 같지만   
접근이 가능함.. 따라서 Spring Security Config를 통해 여러 인증, 권한 설정이 필요함

## USER 접근 막기
- hong.gom.springbootsecuritypractice.config.SecurityConfig.java
```java
package hong.gom.springbootsecuritypractice.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
}
```
위에서 말한 Spring Security Config 설정 예시
- @EnableGlobalMethodSecurity(prePostEnabled = true) : @PreAuthorize()를 통해 인증, 권한 제어를 하겠다라는 의미.
  - 위 처럼 설정하면 admin에는 접근을 못하게 됨

## 접근 가능한 User 추가하기  
```java
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.inMemoryAuthentication()
                .withUser(User.builder()
                .username("hong2").password(passwordEncoder().encode("1")).roles("USER"))
                .withUser(User.builder()
                .username("admin").password(passwordEncoder().encode("1")).roles("ADMIN"));
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```
- configure() : In-Memory 상에 접근 가능한 User를 추가, 이 설정을 하게되면 application.yml 설정은 무시된다.
- passwordEncoder() : 기본적으로 User 추가 시 password를 인코딩해야 되는데 그 인코딩을 하는 역할<br><br>

위 코드까지 설정을 하게되면 hong은 user 권한만, admin은 admin 권한만 접근된다.

## 누구나 접근 가능한 Page 만들기
```java
protected void configure(HttpSecurity http) throws Exception {
    this.logger.debug("Using default configure(HttpSecurity). " 
        + "If subclassed this will potentially override subclass configure(HttpSecurity).");
    http.authorizeRequests((requests) -> requests.anyRequest().authenticated());
    http.formLogin();
    http.httpBasic();
}
```
- 기본적으로 WebSecurityConfigurerAdapter의 메서드인 configure는 위와 같이 정의 돼있다.
  configure(http)의 정의 내용을 보면 모든 요청에 대하여 권한을 확인한다. 따라서 누구나 접근이 가능한
  page를 만드려면 아래와 같이 설정을 변경해줘야 된다.

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests((requests) -> 
        requests.antMatchers("/")
        .permitAll()
        .anyRequest()
        .authenticated());
    http.formLogin();
    http.httpBasic();
}
```
- 위와 같이 설정을 변경해주면 "/" 요청에 대해서는 누구나 접근이 가능하다.

### 기타
CSRF : https://terms.naver.com/entry.naver?docId=3431919&cid=58437&categoryId=58437