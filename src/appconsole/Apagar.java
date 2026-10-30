package appconsole;

import java.util.ArrayList;
import java.util.List;
import com.db4o.ObjectContainer;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;
import util.Util;

public class Apagar {

	public static void main(String[] args) {
		Util.executar(Apagar::executar);
	}

	public static void executar(ObjectContainer db) {
		boolean continuar = true;
		while (continuar && !Util.encerrada()) {
			System.out.println("\n=== Apagar ===\n"
					+ "1 Tutor (com seus pets)\n"
					+ "2 Pet\n"
					+ "3 Serviço\n"
					+ "0 Voltar");
			String opcao = Util.ler("Opção");
			switch (opcao) {
			case "1":
				deletarTutor(db);
				break;
			case "2":
				deletarPet(db);
				break;
			case "3":
				deletarServico(db);
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

	public static void deletarTutor(ObjectContainer db) {
		Tutor tutor = Consultar.escolherTutor(db);
		if (tutor == null) {
			return;
		}
		if (!Util.confirmar("Excluir o tutor e todos os seus pets")) {
			System.out.println("Operação cancelada.");
			return;
		}

		List<Pet> pets = new ArrayList<>(tutor.getListaPets());
		for (Pet pet : pets) {
			excluirPet(db, pet);
		}
		db.delete(tutor);
		db.commit();
		System.out.println("Tutor excluído: " + tutor.getNome()
				+ " (" + pets.size() + " pet(s) removido(s))");
	}

	public static void deletarPet(ObjectContainer db) {
		Pet pet = Consultar.escolherPet(db);
		if (pet == null) {
			return;
		}
		if (!Util.confirmar("Excluir o pet e remover seus vínculos")) {
			System.out.println("Operação cancelada.");
			return;
		}

		String nome = pet.getNome();
		excluirPet(db, pet);
		db.commit();
		System.out.println("Pet excluído: " + nome);
	}

	public static void deletarServico(ObjectContainer db) {
		Servico servico = Consultar.escolherServico(db);
		if (servico == null) {
			return;
		}
		if (!Util.confirmar("Excluir o serviço e remover seus vínculos")) {
			System.out.println("Operação cancelada.");
			return;
		}

		int id = servico.getId();
		// pets alcancados pelo grafo do servico vem parcialmente ativados e nao removeriam o servico da propria lista
		db.activate(servico, Integer.MAX_VALUE);
		List<Pet> pets = new ArrayList<>(servico.getListaPets());
		for (Pet pet : pets) {
			servico.removerPet(pet);
		}
		for (Pet pet : pets) {
			db.store(pet);
		}
		db.delete(servico);
		db.commit();
		System.out.println("Serviço excluído: #" + id);
	}

	private static void excluirPet(ObjectContainer db, Pet pet) {
		Tutor tutor = pet.getTutor();
		for (Servico servico : new ArrayList<>(pet.getListaServicos())) {
			pet.removerServico(servico);
			db.store(servico);
		}
		pet.setTutor(null);
		if (tutor != null) {
			db.store(tutor);
		}
		db.delete(pet);
	}
}
