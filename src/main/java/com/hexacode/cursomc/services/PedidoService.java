package com.hexacode.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hexacode.cursomc.domain.Cliente;
import com.hexacode.cursomc.domain.ItemPedido;
import com.hexacode.cursomc.domain.PagamentoComBoleto;
import com.hexacode.cursomc.domain.Pedido;
import com.hexacode.cursomc.domain.enums.EstadoPagamento;
import com.hexacode.cursomc.repositories.ItemPedidoRepository;
import com.hexacode.cursomc.repositories.PagamentoRepository;
import com.hexacode.cursomc.repositories.PedidoRepository;
import com.hexacode.cursomc.security.UserSpringSecurity;
import com.hexacode.cursomc.services.exceptions.AuthorizationException;
import com.hexacode.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagtoRepo;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepo;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private EmailService emailService;
	
	public Pedido find(Integer id) {
		Optional<Pedido> obj = repo.findById(id);
		
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}
	
	@Transactional
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteService.find(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
			
		}
		obj = repo.save(obj);
		pagtoRepo.save(obj.getPagamento());
		
		for (ItemPedido ip : obj.getItens() ) {
			ip.setDesconto(0.0);
			ip.setProduto(produtoService.find(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		itemPedidoRepo.saveAll(obj.getItens());

		emailService.sendOrderConfirmationHtmlEmail(obj);
		return obj;		
	}
	
	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		UserSpringSecurity user = UserService.authenticated();
		
		if(user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		Cliente cliente = clienteService.find(user.getId());
		
		return repo.findByCliente(cliente, pageRequest);
	}
}
