package com.hexacode.cursomc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hexacode.cursomc.domain.Cliente;
import com.hexacode.cursomc.repositories.ClienteRepository;
import com.hexacode.cursomc.security.UserSpringSecurity;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private ClienteRepository cliRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Cliente cli = cliRepo.findByEmail(email);
		
		if (cli == null) {
			throw new UsernameNotFoundException(email);
		}
		
		return new UserSpringSecurity(cli.getId(), cli.getEmail(), cli.getSenha(), cli.getPerfis());
	}

}
