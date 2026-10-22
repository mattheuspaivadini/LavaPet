package appconsole;

import util.BancoMemoria;
import util.Util;

public class Listar {

	public static void executar(BancoMemoria db) {
		listarTutores(db);
		listarPets(db);
		listarServicos(db);
	}

	public static void listarTutores(BancoMemoria db) {
		Consultar.listarTutores(db);
	}

	public static void listarPets(BancoMemoria db) {
		Consultar.listarPets(db);
	}

	public static void listarServicos(BancoMemoria db) {
		Consultar.listarServicos(db);
	}

	public static void main(String[] args) {
		Util.executar(Listar::executar);
		System.out.println("fim do programa");
	}
}
