package appconsole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;
import util.BancoMemoria;

public class Cadastrar {

	public static void executar(BancoMemoria db) {
		boolean continuar = true;
		while (continuar && !Entrada.encerrada()) {
			System.out.println("\n=== Cadastrar ===\n"
					+ "1 Tutor\n"
					+ "2 Pet\n"
					+ "3 Serviço\n"
					+ "0 Voltar");
			String opcao = Entrada.ler("Opção");
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

	public static Tutor cadastrarTutor(BancoMemoria db) {
		String nome = Entrada.texto("Nome do tutor");
		if (nome == null) {
			return null;
		}

		Tutor tutor = new Tutor(nome);
		db.store(tutor);
		db.commit();
		System.out.println("Tutor cadastrado: " + tutor);
		return tutor;
	}

	public static Pet cadastrarPet(BancoMemoria db) {
		if (db.query(Tutor.class).isEmpty()) {
			System.out.println("Cadastre um tutor antes de cadastrar um pet.");
			return null;
		}

		String nome = Entrada.texto("Nome do pet");
		if (nome == null) {
			return null;
		}
		String raca = Entrada.texto("Raça");
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

	public static Servico cadastrarServico(BancoMemoria db) {
		LocalDate data = Entrada.dataPrompt("Data do serviço");
		if (data == null) {
			return null;
		}
		BigDecimal valor = Entrada.valorPrompt("Valor do serviço");
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
}
