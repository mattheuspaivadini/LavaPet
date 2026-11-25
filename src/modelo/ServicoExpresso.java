package modelo;

import java.math.BigDecimal;

public class ServicoExpresso extends Servico {
	private String taxaExtra;

	public ServicoExpresso(String data, BigDecimal valor, BigDecimal taxaExtra) {
		super(data, valor);
		if (taxaExtra == null) {
			throw new IllegalArgumentException("taxa extra nao pode ser nula");
		}
		this.taxaExtra = taxaExtra.toString();
	}

	public BigDecimal getTaxaExtra() {
		return new BigDecimal(taxaExtra);
	}

	public void setTaxaExtra(BigDecimal taxaExtra) {
		if (taxaExtra == null) {
			throw new IllegalArgumentException("taxa extra nao pode ser nula");
		}
		this.taxaExtra = taxaExtra.toString();
	}

	@Override
	public BigDecimal getValor() {
		return super.getValor().add(getTaxaExtra());
	}

	@Override
	public String toString() {
		return super.toString() + " [taxaExtra=" + getTaxaExtra().toPlainString() + "]";
	}
}
