package es.uma.informatica.sii.contactos.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uma.informatica.sii.contactos.entidades.Contacto;
import es.uma.informatica.sii.contactos.repositorios.ContactoRepo;
import es.uma.informatica.sii.contactos.servicios.excepciones.ContactoNoEncontrado;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

@Service
@Transactional
public class LogicaContactos {
	
	private ContactoRepo repo;
	private JmsTemplate jms;
	
	@Autowired
	public LogicaContactos(ContactoRepo repo, JmsTemplate jmsTemplate) {
		this.repo=repo;
		this.jms=jmsTemplate;
	}
	
	public List<Contacto> getTodosContactos() {
		return repo.findAll();
	}
	
	public Contacto aniadirContacto(Contacto contacto) {
		contacto.setId(null);
		contacto = repo.save(contacto);
		jms.convertAndSend("contactos", "Añadido contacto");
		return contacto; 
	}
	
	public Optional<Contacto> getContactoPorId(Long id) {
		return repo.findById(id);
	}
	
	public Contacto modificarContacto(Contacto contacto) {
		if (repo.existsById(contacto.getId())) {
			contacto = repo.save(contacto);
			jms.convertAndSend("contactos", "Modificado contacto");
			return contacto;
		} else {
			throw new ContactoNoEncontrado();
		}
	}
	
	public void eliminarContacto(Long id) {
		if (repo.existsById(id)) {
			repo.deleteById(id);
			jms.convertAndSend("contactos", "Eliminado contacto");
		} else {
			throw new ContactoNoEncontrado();
		}
	}
	
}
