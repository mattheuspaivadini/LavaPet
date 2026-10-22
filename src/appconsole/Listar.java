package appconsole;

import java.util.List;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;
import util.BancoMemoria;
import util.Util;

public class Listar {

	public static void executar(BancoMemoria manager) {
		listarTutores(manager);
		listarPets(manager);
		listarServicos(manager);
	}

	public static void listarTutores(BancoMemoria manager) {
		System.out.println("------------ TUTORES ------------");
		List<Tutor> tutores = manager.query(Tutor.class);
		if (tutores.isEmpty()) {
			System.out.println("Nenhum tutor cadastrado.");
		} else {
			for (Tutor t : tutores) {
				System.out.println(t);
			}
		}
	}

	public static void listarPets(BancoMemoria manager) {
		System.out.println("\n------------ PETS ------------");
		List<Pet> pets = manager.query(Pet.class);
		if (pets.isEmpty()) {
			System.out.println("Nenhum pet cadastrado.");
		} else {
			for (Pet p : pets) {
				System.out.println(p);
			}
		}
	}

	public static void listarServicos(BancoMemoria manager) {
		System.out.println("\n------------ SERVICOS ------------");
		List<Servico> servicos = manager.query(Servico.class);
		if (servicos.isEmpty()) {
			System.out.println("Nenhum serviço cadastrado.");
		} else {
			for (Servico s : servicos) {
				System.out.println(s);
			}
		}
	}

	public static void main(String[] args) {
		Util.executar(Listar::executar);
		System.out.println("fim do programa");
	}
}
