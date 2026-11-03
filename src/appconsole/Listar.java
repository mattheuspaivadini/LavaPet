package appconsole;

import com.db4o.ObjectContainer;

public class Listar {

	public static void main(String[] args) {
		Consultar.iniciar(Listar::executar);
		System.out.println("fim do programa");
	}

	public static void executar(ObjectContainer db) {
		Consultar.listarTutores(db);
		Consultar.listarPets(db);
		Consultar.listarServicos(db);
	}
}
