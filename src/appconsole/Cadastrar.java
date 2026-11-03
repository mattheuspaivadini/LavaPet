package appconsole;

import java.math.BigDecimal;
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
		Consultar.menu(db, "Cadastrar", "1 Tutor\n2 Pet\n3 Serviço",
				Cadastrar::cadastrarTutor, Cadastrar::cadastrarPet, Cadastrar::cadastrarServico);
	}

	public static void cadastrarTutor(ObjectContainer db) {
		String nome = Consultar.texto("Nome do tutor");
		if (nome == null) {
			return;
		}
		Tutor tutor = new Tutor(nome);
		db.store(tutor);
		db.commit();
		System.out.println("Tutor cadastrado: " + tutor);
	}

	public static void cadastrarPet(ObjectContainer db) {
		if (db.query(Tutor.class).isEmpty()) {
			System.out.println("Cadastre um tutor antes de cadastrar um pet.");
			return;
		}
		String nome = Consultar.texto("Nome do pet");
		String raca = nome == null ? null : Consultar.texto("Raça");
		Tutor tutor = raca == null ? null : Consultar.escolherTutor(db);
		if (tutor == null) {
			return;
		}
		Pet pet = new Pet(nome, raca, tutor);
		db.store(pet);
		db.commit();
		System.out.println("Pet cadastrado: " + pet);
	}

	public static void cadastrarServico(ObjectContainer db) {
		String data = Consultar.lerData("Data do serviço", null);
		BigDecimal valor = data == null ? null : Consultar.lerValor("Valor do serviço", null);
		List<Pet> pets = valor == null ? null : Consultar.escolherPets(db);
		if (pets == null) {
			return;
		}
		Servico servico = new Servico(data, valor);
		pets.forEach(servico::adicionarPet);
		db.store(servico);
		db.commit();
		System.out.println("Serviço cadastrado: " + servico);
	}
}
