package util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

public class BancoMemoria {
	private static BancoMemoria instancia;

	private final List<Tutor> tutores = new ArrayList<>();
	private final List<Pet> pets = new ArrayList<>();
	private final List<Servico> servicos = new ArrayList<>();

	private int sequenciaIdTutor = 1;
	private int sequenciaIdPet = 1;
	private int sequenciaIdServico = 1;

	private BancoMemoria() {
		carregarDadosIniciais();
	}

	public static synchronized BancoMemoria getInstancia() {
		if (instancia == null) {
			instancia = new BancoMemoria();
		}
		return instancia;
	}

	public void reiniciarDados() {
		tutores.clear();
		pets.clear();
		servicos.clear();
		sequenciaIdTutor = 1;
		sequenciaIdPet = 1;
		sequenciaIdServico = 1;
		carregarDadosIniciais();
	}

	private void carregarDadosIniciais() {
		// Tutores de exemplo
		Tutor t1 = new Tutor(sequenciaIdTutor++, "Carlos Eduardo");
		Tutor t2 = new Tutor(sequenciaIdTutor++, "Mariana Vasconcelos");
		Tutor t3 = new Tutor(sequenciaIdTutor++, "Roberto Albuquerque");

		tutores.add(t1);
		tutores.add(t2);
		tutores.add(t3);

		// Pets de exemplo
		Pet p1 = new Pet(sequenciaIdPet++, "Thor", "Golden Retriever", t1);
		Pet p2 = new Pet(sequenciaIdPet++, "Mel", "Labrador", t1);
		Pet p3 = new Pet(sequenciaIdPet++, "Pipoca", "Poodle", t2);
		Pet p4 = new Pet(sequenciaIdPet++, "Mimi", "Siamês", t2);
		Pet p5 = new Pet(sequenciaIdPet++, "Bob", "Shih Tzu", t3);

		pets.add(p1);
		pets.add(p2);
		pets.add(p3);
		pets.add(p4);
		pets.add(p5);

		// Serviços de exemplo
		Servico s1 = new Servico(sequenciaIdServico++, LocalDate.now().minusDays(10), new BigDecimal("95.00"));
		s1.adicionarPet(p1);
		s1.adicionarPet(p2);

		Servico s2 = new Servico(sequenciaIdServico++, LocalDate.now().minusDays(7), new BigDecimal("60.00"));
		s2.adicionarPet(p3);

		Servico s3 = new Servico(sequenciaIdServico++, LocalDate.now().minusDays(5), new BigDecimal("40.00"));
		s3.adicionarPet(p4);

		Servico s4 = new Servico(sequenciaIdServico++, LocalDate.now().minusDays(2), new BigDecimal("55.00"));
		s4.adicionarPet(p5);

		Servico s5 = new Servico(sequenciaIdServico++, LocalDate.now(), new BigDecimal("120.00"));
		s5.adicionarPet(p1);
		s5.adicionarPet(p5);

		servicos.add(s1);
		servicos.add(s2);
		servicos.add(s3);
		servicos.add(s4);
		servicos.add(s5);
	}

	public void store(Object obj) {
		if (obj instanceof Tutor) {
			Tutor t = (Tutor) obj;
			if (t.getId() <= 0) {
				t.setId(sequenciaIdTutor++);
			}
			if (!tutores.contains(t)) {
				tutores.add(t);
			}
		} else if (obj instanceof Pet) {
			Pet p = (Pet) obj;
			if (p.getId() <= 0) {
				p.setId(sequenciaIdPet++);
			}
			if (!pets.contains(p)) {
				pets.add(p);
			}
		} else if (obj instanceof Servico) {
			Servico s = (Servico) obj;
			if (s.getId() <= 0) {
				s.setId(sequenciaIdServico++);
			}
			if (!servicos.contains(s)) {
				servicos.add(s);
			}
		}
	}

	public void delete(Object obj) {
		if (obj instanceof Tutor) {
			tutores.remove(obj);
		} else if (obj instanceof Pet) {
			pets.remove(obj);
		} else if (obj instanceof Servico) {
			servicos.remove(obj);
		}
	}

	public void commit() {
		// As operações em memória já estão sincronizadas
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(Class<T> tipo) {
		if (tipo == Tutor.class) {
			return (List<T>) new ArrayList<>(tutores);
		} else if (tipo == Pet.class) {
			return (List<T>) new ArrayList<>(pets);
		} else if (tipo == Servico.class) {
			return (List<T>) new ArrayList<>(servicos);
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(Class<T> tipo, Predicate<T> filtro) {
		List<T> base = query(tipo);
		List<T> resultado = new ArrayList<>();
		for (T item : base) {
			if (filtro.test(item)) {
				resultado.add(item);
			}
		}
		return resultado;
	}

	public List<Tutor> getTutores() {
		return Collections.unmodifiableList(tutores);
	}

	public List<Pet> getPets() {
		return Collections.unmodifiableList(pets);
	}

	public List<Servico> getServicos() {
		return Collections.unmodifiableList(servicos);
	}
}
