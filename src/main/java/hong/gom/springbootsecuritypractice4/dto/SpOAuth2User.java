package hong.gom.springbootsecuritypractice4.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static java.lang.String.format;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sp_oauth2_user")
public class SpOAuth2User {

    @Id
    private String oauth2UserId; // google-{id}, naver-{id} 각 회사가 제공하는 id (primary key)

    private Long userId;

    private String name;
    private String email;

    private LocalDateTime createdAt;
    private Provider provider;

    public static enum Provider {
        google {
            public SpOAuth2User convert(OAuth2User user){
                return SpOAuth2User.builder()
                        .oauth2UserId(format("%s_%s", name(), user.getAttribute("sub")))
                        .provider(google)
                        .email(user.getAttribute("email"))
                        .name(user.getAttribute("name"))
                        .createdAt(LocalDateTime.now())
                        .build();
            }
        },
        naver {
            public SpOAuth2User convert(OAuth2User userInfo){
                return null;
            }
        };
        public abstract SpOAuth2User convert(OAuth2User userInfo);
    }
}
