package appconsole;

import com.db4o.ObjectContainer;
import util.Util;

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
		Util.executar(Listar::executar);
		System.out.println("fim do programa");
	}
}
