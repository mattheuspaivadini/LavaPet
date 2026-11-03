package appconsole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.db4o.ObjectContainer;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

public class Cadastrar {

	public static void main(String[] args) {
		Consultar.iniciar(Cadastrar::executar);
	}

	public static void executar(ObjectContainer db) {
		boolean continuar = true;
		while (continuar && !Consultar.encerrada()) {
			System.out.println("\n=== Cadastrar ===\n"
					+ "1 Tutor\n"
					+ "2 Pet\n"
					+ "3 Serviço\n"
					+ "0 Voltar");
			String opcao = Consultar.ler("Opção");
			switch (opcao) {
			case "1":
				cadastrarTutor(db);
				break;
			case "2":
				cadastrarPet(db);
				break;
			case "3":
				cadastrarServico(db);
				break;
			case "0":
				continuar = false;
				break;
			default:
				System.out.println("Opção inválida.");
				break;
			}
		}
	}

	public static Tutor cadastrarTutor(ObjectContainer db) {
		String nome = Consultar.texto("Nome do tutor");
		if (nome == null) {
			return null;
		}

		Tutor tutor = new Tutor(nome);
		db.store(tutor);
		db.commit();
		System.out.println("Tutor cadastrado: " + tutor);
		return tutor;
	}

	public static Pet cadastrarPet(ObjectContainer db) {
		if (db.query(Tutor.class).isEmpty()) {
			System.out.println("Cadastre um tutor antes de cadastrar um pet.");
			return null;
		}

		String nome = Consultar.texto("Nome do pet");
		if (nome == null) {
			return null;
		}
		String raca = Consultar.texto("Raça");
		if (raca == null) {
			return null;
		}

		Tutor tutor = Consultar.escolherTutor(db);
		if (tutor == null) {
			return null;
		}

		Pet pet = new Pet(nome, raca, tutor);
		db.store(pet);
		db.commit();
		System.out.println("Pet cadastrado: " + pet);
		return pet;
	}

	public static Servico cadastrarServico(ObjectContainer db) {
		String data = dataPrompt("Data do serviço");
		if (data == null) {
			return null;
		}
		BigDecimal valor = valorPrompt("Valor do serviço");
		if (valor == null) {
			return null;
		}

		List<Pet> pets = Consultar.escolherPets(db);
		if (pets == null) {
			return null;
		}

		Servico servico = new Servico(data, valor);
		for (Pet pet : pets) {
			servico.adicionarPet(pet);
		}
		db.store(servico);
		db.commit();
		System.out.println("Serviço cadastrado: " + servico);
		return servico;
	}

	// --- validacao de entrada do cadastro ---
	static String dataPrompt(String prompt) {
		while (true) {
			String s = Consultar.texto(prompt + " (dd/MM/aaaa)");
			if (s == null) {
				return null;
			}
			LocalDate d = Consultar.data(s);
			if (d != null) {
				return d.format(Consultar.DATA);
			}
			System.out.println("Data inválida; use dd/MM/aaaa e tente novamente.");
		}
	}

	static BigDecimal valorPrompt(String prompt) {
		while (true) {
			String s = Consultar.texto(prompt);
			if (s == null) {
				return null;
			}
			BigDecimal v = dinheiro(s);
			if (v != null) {
				return v;
			}
			System.out.println("Valor inválido; tente novamente.");
		}
	}

	static BigDecimal dinheiro(String s) {
		try {
			String n = s.trim();
			if (n.contains(",") && n.contains(".")) {
				return null;
			}
			return new BigDecimal(n.replace(',', '.'));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
