package util;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
public class Banco {
    private static String BANCO_ARQUIVO = "lavapet.db4o";
    
    public static EmbeddedConfiguration obterConfiguracao() {
        EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();

        config.common.objectClass(Tutor.class).store();
        config.common.objectClass(Pet.class).store();
        config.common.objectClass(Servico.class).store();
        
    }
    }
