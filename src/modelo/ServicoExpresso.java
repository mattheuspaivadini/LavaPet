package modelo;

import java.math.BigDecimal;

public class ServicoExpresso extends Servico {
	public static final BigDecimal TAXA = new BigDecimal("20");

	public ServicoExpresso(String data, BigDecimal valorSemTaxa) {
		super(data, somarTaxa(valorSemTaxa));
	}

	private static BigDecimal somarTaxa(BigDecimal valorSemTaxa) {
		if (valorSemTaxa == null) {
			throw new IllegalArgumentException("valor nao pode ser nulo");
		}
		return valorSemTaxa.add(TAXA);
	}
}
