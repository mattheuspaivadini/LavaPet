package teste;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Consumer;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;
import util.GeradorID;

// Cada operacao roda em um container proprio sobre o arquivo db4o,
// entao o que a leitura enxerga veio do disco e nunca do cache da escrita.
public class TesteBase {
	// Util guarda um Scanner estatico sobre System.in; o pipe precisa existir antes de Util carregar.
	@SuppressWarnings("resource") // vive ate o fim do processo, alimentando System.in
	private static final PipedOutputStream TECLADO = new PipedOutputStream();

	static {
		try {
			System.setIn(new PipedInputStream(TECLADO, 1 << 16));
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
		// entrada faltando bloquearia o Scanner para sempre
		Thread vigia = new Thread(() -> {
			try {
				Thread.sleep(60_000);
			} catch (InterruptedException e) {
				return;
			}
			System.out.println("FAIL timeout: o teste esperou entrada que nao veio");
			System.exit(2);
		});
		vigia.setDaemon(true);
		vigia.start();
	}

	private final File arquivo;
	private final boolean temporario;

	// Grava em lavapet.db4o (mesmo arquivo do app); os dados persistem entre as rodadas.
	public TesteBase() {
		this(new File("lavapet.db4o"), false);
	}

	private TesteBase(File arquivo, boolean temporario) {
		this.arquivo = arquivo;
		this.temporario = temporario;
	}

	public static boolean temDados(ObjectContainer db) {
		return !db.query(Tutor.class).isEmpty();
	}

	// Base isolada em arquivo temporario, apagado em fechar(); para usar junto de outra base aberta.
	public static TesteBase temporaria() {
		try {
			File f = Files.createTempFile("lavapet-teste", ".db4o").toFile();
			f.delete();
			return new TesteBase(f, true);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public void com(Consumer<ObjectContainer> operacao) {
		ObjectContainer db = Db4oEmbedded.openFile(GeradorID.obterConfiguracao(), arquivo.getPath());
		try {
			operacao.accept(db);
			db.commit();
		} finally {
			db.close();
		}
	}

	// Base minima (a mesma que o CadastrarTest cria): 2 tutores, 2 pets da Ana e 1 servico com os dois pets.
	// So grava se o arquivo estiver vazio, para nao duplicar o que ja foi cadastrado.
	public static void semear(ObjectContainer db) {
		if (temDados(db)) {
			return;
		}
		Tutor ana = new Tutor(1, "Ana");
		Tutor bia = new Tutor(2, "Bia");
		Pet rex = new Pet(1, "Rex", "Vira-lata", ana);
		Pet mel = new Pet(2, "Mel", "Poodle", ana);
		Servico s = new Servico(1, "10/05/2026", new BigDecimal("50.00"));
		s.adicionarPet(rex);
		s.adicionarPet(mel);
		for (Object o : new Object[] { ana, bia, rex, mel, s }) {
			db.store(o);
		}
	}

	public void fechar() {
		if (temporario) {
			arquivo.delete();
		}
	}

	// Enfileira as linhas que o appconsole vai ler via Util.ler na proxima chamada.
	public static void entrada(String... linhas) {
		try {
			TECLADO.write((String.join("\n", linhas) + "\n").getBytes("UTF-8"));
			TECLADO.flush();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String capturar(Runnable acao) {
		PrintStream original = System.out;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		System.setOut(new PrintStream(buffer, true));
		try {
			acao.run();
		} finally {
			System.setOut(original);
		}
		return new String(buffer.toByteArray());
	}

	public static void verdade(boolean condicao, String mensagem) {
		if (!condicao) {
			throw new AssertionError(mensagem);
		}
	}

	public static void igual(Object esperado, Object atual, String mensagem) {
		verdade(esperado.equals(atual), mensagem + " (esperado=" + esperado + ", atual=" + atual + ")");
	}

	public static <T> T unico(ObjectContainer db, Class<T> tipo, int id) {
		List<T> r = GeradorID.query(db, tipo, o -> idDe(o) == id);
		return r.isEmpty() ? null : r.get(0);
	}

	private static int idDe(Object o) {
		if (o instanceof Tutor) {
			return ((Tutor) o).getId();
		}
		if (o instanceof Pet) {
			return ((Pet) o).getId();
		}
		return ((Servico) o).getId();
	}

	public static void rodar(String nome, Runnable teste) {
		try {
			teste.run();
			System.out.println("OK   " + nome);
		} catch (Throwable e) {
			System.out.println("FAIL " + nome + ": " + e);
			e.printStackTrace(System.out);
			System.exit(1);
		}
	}
}
