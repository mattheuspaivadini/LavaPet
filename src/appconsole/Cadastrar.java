package appconsole;

import java.math.BigDecimal;
import java.util.List;
import com.db4o.ObjectContainer;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;
import util.GeradorID;
import util.Util;

public class Cadastrar {

	public static void main(String[] args) {
		Util.executar(Cadastrar::executar);
	}

	public static void executar(ObjectContainer db) {
		boolean continuar = true;
		while (continuar && !Util.encerrada()) {
			System.out.println("\n=== Cadastrar ===\n"
					+ "1 Tutor\n"
					+ "2 Pet\n"
					+ "3 Serviço\n"
					+ "0 Voltar");
			String opcao = Util.ler("Opção");
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
		String nome = Util.texto("Nome do tutor");
		if (nome == null) {
			return null;
		}

		Tutor tutor = new Tutor(nome);
		tutor.setId(GeradorID.gerarIdTutor(db));
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

		String nome = Util.texto("Nome do pet");
		if (nome == null) {
			return null;
		}
		String raca = Util.texto("Raça");
		if (raca == null) {
			return null;
		}

		Tutor tutor = Consultar.escolherTutor(db);
		if (tutor == null) {
			return null;
		}

		Pet pet = new Pet(nome, raca, tutor);
		pet.setId(GeradorID.gerarIdPet(db));
		db.store(pet);
		db.commit();
		System.out.println("Pet cadastrado: " + pet);
		return pet;
	}

	public static Servico cadastrarServico(ObjectContainer db) {
		String data = Util.dataPrompt("Data do serviço");
		if (data == null) {
			return null;
		}
		BigDecimal valor = Util.valorPrompt("Valor do serviço");
		if (valor == null) {
			return null;
		}

		List<Pet> pets = Consultar.escolherPets(db);
		if (pets == null) {
			return null;
		}

		Servico servico = new Servico(data, valor);
		servico.setId(GeradorID.gerarIdServico(db));
		for (Pet pet : pets) {
			servico.adicionarPet(pet);
		}
		db.store(servico);
		db.commit();
		System.out.println("Serviço cadastrado: " + servico);
		return servico;
	}
}
