package com.devconnect.bakend.auth;


import com.devconnect.bakend.auth.dto.AuthResponse;
import com.devconnect.bakend.auth.dto.LoginRequest;
import com.devconnect.bakend.auth.dto.RegisterRequest;
import com.devconnect.bakend.config.CookieUtil;
import com.devconnect.bakend.config.JwtUtil;
import com.devconnect.bakend.exceptions.InvalidCredentialsException;
import com.devconnect.bakend.exceptions.ResourceNotFoundException;
import com.devconnect.bakend.exceptions.UserAlreadyExistsException;
import com.devconnect.bakend.profile.Profile;
import com.devconnect.bakend.profile.ProfileRepository;
import com.devconnect.bakend.user.User;
import com.devconnect.bakend.user.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class AuthService {
    private final CookieUtil cookieUtil;
    private final JwtUtil util;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

    @Value("${app.cookie.name}")
    private String cookieName;

    public AuthResponse registration(RegisterRequest request){
        String url=null;
         GoogleAuthenticator gAuth;
         GoogleAuthenticatorKey key;
        User user1 = userRepository.findByEmail(request.getEmail());
        User user2 = userRepository.findByUsername(request.getUsername());
        User user=null;
        if(user1==null&&user2==null){
            user=User.builder()
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .isMfaActive(request.isMfaActive()).username(request.getUsername()).build();
            if(user.isMfaActive()){
                 gAuth = new GoogleAuthenticator();
                 key = gAuth.createCredentials();
                String secret = key.getKey();
                user.setTwoFactorSecretKey(secret);
                 url = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("DevConnect", user.getEmail(), key);
            }
            userRepository.save(user);
            Profile profile = Profile.builder().fullName(request.getFullName()).user(user).build();
            profileRepository.save(profile);

        }else{
            if(user1!=null)throw new UserAlreadyExistsException(request.getEmail());
            if(user2!=null)throw new RuntimeException(request.getUsername());
        }

        return AuthResponse.builder()
                .username(user.getUsername())
                .userId(user.getUserId()).
                secret(user.getTwoFactorSecretKey()).optUrl(url).message("registration successful").build();
    }
    public AuthResponse login(LoginRequest request, HttpServletResponse httpServletResponse){
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail());
        String token = util.generateToken(user.getUserId(), user.isMfaActive());
        cookieUtil.addHttpOnlyCookies(httpServletResponse,token);
        Profile profile = profileRepository.findByUser(user);
        return AuthResponse.builder().username(user.getUsername())
                .userId(user.getUserId()).message("login successful").profilePicture(profile.getProfilePicture())
                .build();

    }
    public AuthResponse verify2FA(HttpServletRequest request,String code,HttpServletResponse httpServletResponse){
        Cookie[] cookies = request.getCookies();
        if(cookies==null)throw new ResourceNotFoundException("cookies not found");
        String token=null;
        for(Cookie cokies:cookies){
            if(cokies.getName().equals(cookieName)){
                token=cokies.getValue();
                break;
            }
        }
        if(token==null) {
            throw new ResourceNotFoundException();
        }
        Long userId = util.getId(token);
        Optional<User> user=userRepository.findById(userId);
        user.orElseThrow(()->new RuntimeException("user with userId:"+userId+"Not found"));

        boolean match=new GoogleAuthenticator().authorize(user.get().getTwoFactorSecretKey(),Integer.parseInt(code));
        if(!match){
            throw new InvalidCredentialsException("2FA fails");
        }

        token=util.generateToken(userId,false);

        cookieUtil.addHttpOnlyCookies(httpServletResponse,token);
        Profile profile = profileRepository.findByUser(user.get());
        return AuthResponse.builder().username(user.get().getUsername()).profilePicture(profile.getProfilePicture()).userId(userId).message("2FA successfully done").build();

    }
    public String logOut(HttpServletResponse httpServletResponse){
        cookieUtil.deleteHttpOnlyCookies(httpServletResponse);
        return "Logout";
    }
    public AuthResponse status(HttpServletRequest request){
        Cookie[] cookies=request.getCookies();
        if(cookies==null)throw new ResourceNotFoundException("cookies not found");
        String token=null;
        for(Cookie cokies:cookies){
            if(cokies.getName().equals(cookieName)){
                token=cokies.getValue();
                break;
            }
        }
        if(token==null) {
            throw new  RuntimeException();
        }
        Long userId = util.getId(token);
        Optional<User> user=userRepository.findById(userId);
        user.orElseThrow(()->new ResourceNotFoundException("user with userId:"+userId+"Not found"));
        Profile profile = profileRepository.findByUser(user.get());

        return AuthResponse.builder().message("status").username(user.get().getUsername()).profilePicture(profile.getProfilePicture()).userId(userId).build();
    }
}
