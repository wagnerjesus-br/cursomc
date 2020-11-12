package com.hexacode.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.hexacode.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmation(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
	
}
