package util;

import java.util.function.Consumer;

public class Util {

	public static BancoMemoria getBanco() {
		return BancoMemoria.getInstancia();
	}

	public static void executar(Consumer<BancoMemoria> operacao) {
		try {
			operacao.accept(BancoMemoria.getInstancia());
		} catch (Exception e) {
			System.out.println("ERRO: " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
}
