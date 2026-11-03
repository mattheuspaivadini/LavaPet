package appconsole;

import com.db4o.ObjectContainer;

public class Listar {

	public static void executar(ObjectContainer db) {
		listarTutores(db);
		listarPets(db);
		listarServicos(db);
	}

	public static void listarTutores(ObjectContainer db) {
		Consultar.listarTutores(db);
	}

	public static void listarPets(ObjectContainer db) {
		Consultar.listarPets(db);
	}

	public static void listarServicos(ObjectContainer db) {
		Consultar.listarServicos(db);
	}

	public static void main(String[] args) {
		Consultar.iniciar(Listar::executar);
		System.out.println("fim do programa");
	}
}
