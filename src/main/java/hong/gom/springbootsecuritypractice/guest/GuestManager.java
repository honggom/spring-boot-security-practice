package hong.gom.springbootsecuritypractice.guest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

@Component
public class GuestManager implements AuthenticationProvider, InitializingBean {

    private HashMap<String ,Guest> guestDb = new HashMap<>();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        if(guestDb.containsKey(token.getName())){
            Guest guest = guestDb.get(token.getName());
            return GuestAuthenticationToken.builder()
                    .principal(guest)
                    .details(guest.getUsername())
                    .authenticated(true)
                    .build();
        }
        return null;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == UsernamePasswordAuthenticationToken.class;
    }


    // 인-메모리로 계정 추가하는 과정
    @Override
    public void afterPropertiesSet() throws Exception {
        Set.of(
                new Guest("hong", "홍", Set.of(new SimpleGrantedAuthority("ROLE_GUEST"))),
                new Guest("kim", "김", Set.of(new SimpleGrantedAuthority("ROLE_GUEST"))),
                new Guest("park", "박", Set.of(new SimpleGrantedAuthority("ROLE_GUEST")))
        ).forEach(guest ->
            guestDb.put(guest.getId(), guest)
        );
    }
}
