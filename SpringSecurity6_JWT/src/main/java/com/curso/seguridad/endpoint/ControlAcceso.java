package com.curso.seguridad.endpoint;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.curso.seguridad.modelo.LoginRequest;
import com.curso.seguridad.util.JwtUtil;

@RestController
public class ControlAcceso {
	
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    
    public ControlAcceso(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        
		Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        
        if (authentication.isAuthenticated()) {        	
        	List<String> roles = authentication
        		.getAuthorities()
        		.stream()
        		.map( a -> a.getAuthority())
        		.collect(Collectors.toList());        	
            return jwtUtil.generateToken(loginRequest.getUsername(), roles);
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }	
	
}
