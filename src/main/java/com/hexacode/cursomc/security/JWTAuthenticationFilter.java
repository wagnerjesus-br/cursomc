package com.hexacode.cursomc.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexacode.cursomc.dto.CredencialDTO;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private JWTUtil jwtUtil;
	
	private AuthenticationManager authManager;
	
	public JWTAuthenticationFilter(AuthenticationManager authManager, JWTUtil jwtUtil) {
		setAuthenticationFailureHandler(new JWTAuthenticationFailureHandler());
		this.authManager = authManager;
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req,
												HttpServletResponse res) throws AuthenticationException {
		try {
			CredencialDTO cred = new ObjectMapper().readValue(req.getInputStream(), CredencialDTO.class);
			
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(cred.getEmail(), cred.getSenha(), new ArrayList<>());
			
			Authentication auth = authManager.authenticate(authToken);
			
			return auth;
		} 
		catch(IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public void successfulAuthentication(HttpServletRequest req,
										HttpServletResponse res,
										FilterChain chain,
										Authentication auth) throws IOException, ServletException {
		String email = ((UserSpringSecurity) auth.getPrincipal()).getUsername();
		String token = jwtUtil.generateToken(email);
		
		res.addHeader("Authorization", "Bearer " + token);
	}
	
	private class JWTAuthenticationFailureHandler implements AuthenticationFailureHandler {
		 
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
                throws IOException, ServletException {
     
        	response.setStatus(401);
            response.setContentType("application/json"); 
            response.getWriter().append(json());
        }
        
        private String json() {
            long date = new Date().getTime();
            return "{\"timestamp\": " + date + ", "
                + "\"status\": 401, "
                + "\"error\": \"Não autorizado\", "
                + "\"message\": \"Email ou senha inválidos\", "
                + "\"path\": \"/login\"}";
        }
    }	
}
