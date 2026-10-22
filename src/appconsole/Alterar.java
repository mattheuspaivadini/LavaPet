package appconsole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;
import util.BancoMemoria;

public class Alterar {

	public static void executar(BancoMemoria db) {
		boolean continuar = true;
		while (continuar && !Entrada.encerrada()) {
			System.out.println("\n=== Alterar ===\n"
					+ "1 Tutor\n"
					+ "2 Pet\n"
					+ "3 Serviço\n"
					+ "0 Voltar");
			String opcao = Entrada.ler("Opção");
			switch (opcao) {
			case "1":
				alterarTutor(db);
				break;
			case "2":
				alterarPet(db);
				break;
			case "3":
				alterarServico(db);
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

	public static void alterarTutor(BancoMemoria db) {
		Tutor tutor = Consultar.escolherTutor(db);
		if (tutor == null) {
			return;
		}

		String nome = Entrada.textoOpcional("Novo nome", tutor.getNome());
		if (nome == null || Entrada.encerrada()) {
			return;
		}

		tutor.setNome(nome);
		db.store(tutor);
		db.commit();
		System.out.println("Tutor alterado: " + tutor);
	}

	public static void alterarPet(BancoMemoria db) {
		Pet pet = Consultar.escolherPet(db);
		if (pet == null) {
			return;
		}

		String nome = Entrada.textoOpcional("Novo nome", pet.getNome());
		if (nome == null) {
			return;
		}
		String raca = Entrada.textoOpcional("Nova raça", pet.getRaca());
		if (raca == null) {
			return;
		}

		Tutor novoTutor = pet.getTutor();
		if (Entrada.confirmar("Deseja alterar o tutor")) {
			novoTutor = Consultar.escolherTutor(db);
			if (novoTutor == null) {
				return;
			}
		}
		if (Entrada.encerrada()) {
			return;
		}

		pet.setNome(nome);
		pet.setRaca(raca);
		pet.setTutor(novoTutor);
		db.store(pet);
		db.commit();
		System.out.println("Pet alterado: " + pet);
	}

	public static void alterarServico(BancoMemoria db) {
		Servico servico = Consultar.escolherServico(db);
		if (servico == null) {
			return;
		}

		LocalDate data = Entrada.dataOpcional("Nova data", servico.getData());
		if (data == null) {
			return;
		}
		BigDecimal valor = Entrada.valorOpcional("Novo valor", servico.getValor());
		if (valor == null) {
			return;
		}

		List<Pet> pets = null;
		if (Entrada.confirmar("Deseja alterar os pets do serviço")) {
			pets = Consultar.escolherPets(db);
			if (pets == null) {
				return;
			}
		}
		if (Entrada.encerrada()) {
			return;
		}

		servico.setData(data);
		servico.setValor(valor);
		if (pets != null) {
			for (Pet pet : new ArrayList<>(servico.getListaPets())) {
				servico.removerPet(pet);
			}
			for (Pet pet : pets) {
				servico.adicionarPet(pet);
			}
		}
		db.store(servico);
		db.commit();
		System.out.println("Serviço alterado: " + servico);
	}
}
