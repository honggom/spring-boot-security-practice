package hong.gom.springbootsecuritypractice.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestInformation {

    private String remoteIp;
    private String sessionId;
    private LocalDateTime loginTime;

}
