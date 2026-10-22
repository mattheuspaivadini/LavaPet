import java.util.ArrayList;
import java.util.List;

public class Pet {
    private int id;
    private String nome;
    private String raca;
    private Tutor tutor;
    private List<Servico> listaServicos;

    public Pet() {
        this.listaServicos = new ArrayList<>();
    }

    public Pet(int id, String nome, String raca) {
        this();
        this.id = id;
        this.nome = nome;
        this.raca = raca;
    }

    public void vincularServico(Servico servico) {
        if (!this.listaServicos.contains(servico)) {
            this.listaServicos.add(servico);
            servico.adicionarPet(this);
        }
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }

    public List<Servico> getListaServicos() { return listaServicos; }
    public void setListaServicos(List<Servico> listaServicos) { this.listaServicos = listaServicos; }

    @Override
    public String toString() {
        return "Pet [id=" + id + ", nome=" + nome + ", raca=" + raca + 
               ", tutor=" + (tutor != null ? tutor.getNome() : "sem tutor") + "]";
    }
}