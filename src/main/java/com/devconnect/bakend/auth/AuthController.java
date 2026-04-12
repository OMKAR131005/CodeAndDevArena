package com.devconnect.bakend.auth;

import com.devconnect.bakend.auth.dto.AuthResponse;
import com.devconnect.bakend.auth.dto.LoginRequest;
import com.devconnect.bakend.auth.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
     return ResponseEntity.ok(authService.registration(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response){
        return ResponseEntity.ok(authService.login(request,response));
    }
    @PostMapping("/verify-2fa")
    public ResponseEntity<AuthResponse> verify(@RequestParam String code, HttpServletRequest request, HttpServletResponse response){
        return ResponseEntity.ok(authService.verify2FA(request,code,response));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response){
        return ResponseEntity.ok(authService.logOut(response));
    }
    @GetMapping("/status")
    public ResponseEntity<AuthResponse> status(HttpServletRequest request){
        return ResponseEntity.ok(authService.status(request));
    }
}
