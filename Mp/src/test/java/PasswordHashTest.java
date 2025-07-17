import dgb.Mp.Utils.MailSenderService;
import dgb.Mp.user.UserRepository;
import dgb.Mp.user.UserServiceImpl;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.log4j.Log4j2;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Log4j2
public class PasswordHashTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailSenderService mailSenderService;



    @Test
    public void testPasswordHash() throws MessagingException {

        mailSenderService.sendEmail("farouk.bouhaka@mf.gov.dz","testusername","testpassword") ;


    }
}
