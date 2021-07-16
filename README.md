# Spring Security
    인증과 인가(권한)로 이루어진 Spring framework
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
