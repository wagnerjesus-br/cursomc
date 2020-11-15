package com.hexacode.cursomc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import com.hexacode.cursomc.domain.Pedido;

public class SmtpMailService extends AbstractMailService {

	private static final Logger LOG = LoggerFactory.getLogger(SmtpMailService.class);
	
	@Value("${mail.default.sender}")
	private String defaultFrom;
	
	@Autowired
	private MailSender mailSender;
	
	@Override
	public void sendOrderConfirmation(Pedido obj) {
		SimpleMailMessage sm = prepareSimpleMailMessageFromPedido(obj);
		sendEmail(sm);
	}

	@Override
	public void sendEmail(SimpleMailMessage sm) {
		LOG.info("Ponto de envio de email");
		mailSender.send(sm);
		LOG.info("Email enviado");		
	}
	
}
