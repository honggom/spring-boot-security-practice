# DaoAuthenticationManager와 UserDetailsService
### In-memory -> DB로 컨버팅하기
실제로 Security를 사용해서 서비스를 만드려하면, 
UserDetails를 구현한 User 객체와 UserDetailsService 부터 만든다.
왜냐하면, UserDetailsService와 UserDetails 구현체만 구현하면
Security가 나머지는 쉽게 쓸 수 있도록 도움을 많이 주기 때문이다.
그런 다음, 나머지 부분은 하나하나 설정을 배워가면서 처리하면 된다.

![fig-12-userdetails](https://user-images.githubusercontent.com/67107008/127093437-d8049c89-6c71-4cf3-8269-61ad9ef56e08.png)
위 사진상의 주황색 UsernamePasswordAuthenticationToken(x) UsernamePasswordAuthenticationFilter(o)

### 흐름
UsernamePasswordAuthenticationFilter가 UsernamePasswordAuthenticationToken을 가지고
ProviderManager에게 넘겨준다. 그러면 기본적으로 DaoAuthenticationProvider가 UsernamePasswordAuthenticationToken을
처리해준다. 이때 UserDetailsService 빈이 있으면 DaoAuthenticationProvider가 이 빈에게 UsernamePasswordAuthenticationToken을
넘겨서 UserDetails라는 Principal 객체를 받게 돼있다. Principal 객체가 인증을 받은 상태라면 이 객체를 UsernamePasswordAuthenticationToken에
넣어서 다시 ProviderManager에게 반환한다.
