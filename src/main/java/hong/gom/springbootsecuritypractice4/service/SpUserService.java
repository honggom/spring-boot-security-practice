package hong.gom.springbootsecuritypractice4.service;

import hong.gom.springbootsecuritypractice4.dto.SpAuthority;
import hong.gom.springbootsecuritypractice4.dto.SpOAuth2User;
import hong.gom.springbootsecuritypractice4.dto.SpUser;
import hong.gom.springbootsecuritypractice4.repository.SpOAuth2UserRepository;
import hong.gom.springbootsecuritypractice4.repository.SpUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SpUserService implements UserDetailsService {

    private final SpUserRepository userRepository;
    private final SpOAuth2UserRepository oAuth2UserRepository;

    //db 데이터에서 user를 찾는다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findSpUserByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException(username));
    }

    public Optional<SpUser> findUser(String email) {
        return userRepository.findSpUserByEmail(email);
    }

    public SpUser save(SpUser user){
        return userRepository.save(user);
    }

    //권한을 추가한다.
    public void addAuthority(Long userId, String authority){
        userRepository.findById(userId).ifPresent(user -> {
            SpAuthority newRole = new SpAuthority(user.getUserId(), authority); //해당 id의 새로운 권한을 부여
            if(user.getAuthorities() == null){
                HashSet<SpAuthority> authorities = new HashSet<>();
                authorities.add(newRole);
                user.setAuthorities(authorities);
                save(user);
            }else if(!user.getAuthorities().contains(newRole)){
                HashSet<SpAuthority> authorities = new HashSet<>();
                authorities.addAll(user.getAuthorities());
                authorities.add(newRole);
                user.setAuthorities(authorities);
                save(user);
            }
        });
    }

    public SpUser load(SpOAuth2User oAuth2User){
        SpOAuth2User dbUser = oAuth2UserRepository.findById(oAuth2User.getOauth2UserId())
                .orElseGet(() -> {
                    SpUser user = new SpUser();
                    user.setEmail(oAuth2User.getEmail());
                    user.setName(oAuth2User.getName());
                    user.setEnabled(true);
                    user = userRepository.save(user);
                    addAuthority(user.getUserId(), "ROLE_USER");

                    oAuth2User.setUserId(user.getUserId());
                    return oAuth2UserRepository.save(oAuth2User);
                });
        return userRepository.findById(dbUser.getUserId()).get();
    }
}
