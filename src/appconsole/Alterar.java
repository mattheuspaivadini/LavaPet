package appconsole;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.db4o.ObjectContainer;
import modelo.Pet;
import modelo.Servico;
import modelo.ServicoExpresso;
import modelo.Tutor;

public class Alterar {

	public static void main(String[] args) {
		Consultar.iniciar(Alterar::executar);
	}

	public static void executar(ObjectContainer db) {
		Consultar.menu(db, "Alterar", "1 Tutor\n2 Pet\n3 Serviço\n4 Remover vínculo Pet-Serviço",
				Alterar::alterarTutor, Alterar::alterarPet, Alterar::alterarServico, Alterar::removerVinculoPetServico);
	}

	public static void removerVinculoPetServico(ObjectContainer db) {
		Servico servico = Consultar.escolherServico(db);
		if (servico == null) {
			return;
		}
		if (servico.getListaPets().isEmpty()) {
			System.out.println("Este serviço não tem pets vinculados.");
			return;
		}
		System.out.println("Pets do serviço:");
		servico.getListaPets().forEach(p -> System.out.println("  #" + p.getId() + " " + p.getNome()));
		Integer id = Consultar.inteiro("Id do pet a desvincular");
		if (id == null) {
			return;
		}
		Pet pet = servico.getListaPets().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
		if (pet == null) {
			System.out.println("Pet não está vinculado a este serviço.");
			return;
		}
		servico.removerPet(pet);
		db.store(servico);
		db.store(pet);
		db.commit();
		System.out.println("Vínculo removido: " + pet.getNome() + " <-> serviço #" + servico.getId());
	}

	public static void alterarTutor(ObjectContainer db) {
		Tutor tutor = Consultar.escolherTutor(db);
		String nome = tutor == null ? null : Consultar.lerTexto("Novo nome", tutor.getNome());
		if (nome == null) {
			return;
		}
		tutor.setNome(nome);
		db.store(tutor);
		db.commit();
		System.out.println("Tutor alterado: " + tutor);
	}

	public static void alterarPet(ObjectContainer db) {
		Pet pet = Consultar.escolherPet(db);
		String nome = pet == null ? null : Consultar.lerTexto("Novo nome", pet.getNome());
		String raca = nome == null ? null : Consultar.lerTexto("Nova raça", pet.getRaca());
		if (raca == null) {
			return;
		}
		Tutor antigo = pet.getTutor();
		Tutor novo = Consultar.confirmar("Deseja alterar o tutor") ? Consultar.escolherTutor(db) : antigo;
		if (novo == null || Consultar.encerrada()) {
			return;
		}
		pet.setNome(nome);
		pet.setRaca(raca);
		pet.setTutor(novo);
		db.store(antigo);
		db.store(pet);
		db.commit();
		System.out.println("Pet alterado: " + pet);
	}

	public static void alterarServico(ObjectContainer db) {
		Servico servico = Consultar.escolherServico(db);
		if (servico == null) {
			return;
		}
		boolean eraExpresso = servico instanceof ServicoExpresso;
		BigDecimal valorSemTaxa = servico.getValor();
		if (eraExpresso) {
			valorSemTaxa = valorSemTaxa.subtract(ServicoExpresso.TAXA);
		}

		String data = Consultar.lerData("Nova data", servico.getData());
		if (data == null) {
			return;
		}
		BigDecimal valor = Consultar.lerValor("Novo valor (sem a taxa expresso)", valorSemTaxa);
		if (valor == null) {
			return;
		}
		boolean expresso = Cadastrar.perguntarExpresso();

		List<Pet> novosPets = null;
		if (Consultar.confirmar("Deseja alterar os pets do serviço")) {
			novosPets = Consultar.escolherPets(db);
			if (novosPets == null) {
				return;
			}
		}
		if (Consultar.encerrada()) {
			return;
		}

		db.activate(servico, Integer.MAX_VALUE);
		List<Pet> petsAntigos = new ArrayList<>(servico.getListaPets());
		List<Pet> petsFinais = novosPets != null ? novosPets : petsAntigos;
		Integer idOriginal = null;

		if (expresso == eraExpresso) {
			servico.setData(data);
			servico.setValor(expresso ? valor.add(ServicoExpresso.TAXA) : valor);
			trocarPets(servico, petsAntigos, petsFinais);
		} else {
			idOriginal = servico.getId();
			trocarPets(servico, petsAntigos, List.of());
			db.delete(servico);
			if (expresso) {
				servico = new ServicoExpresso(data, valor);
			} else {
				servico = new Servico(data, valor);
			}
			trocarPets(servico, List.of(), petsFinais);
		}

		db.store(servico);
		if (idOriginal != null) {
			servico.setId(idOriginal); // o GeradorID deu um id novo ao objeto recriado
			db.store(servico);
		}
		for (Pet pet : petsAntigos) {
			db.store(pet);
		}
		for (Pet pet : petsFinais) {
			db.store(pet);
		}
		db.commit();
		System.out.println("Serviço alterado: " + servico);
	}

	private static void trocarPets(Servico servico, List<Pet> remover, List<Pet> adicionar) {
		for (Pet pet : remover) {
			servico.removerPet(pet);
		}
		for (Pet pet : adicionar) {
			servico.adicionarPet(pet);
		}
	}
}
