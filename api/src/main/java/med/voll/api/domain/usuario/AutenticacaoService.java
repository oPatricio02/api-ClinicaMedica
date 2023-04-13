package med.voll.api.domain.usuario;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service //Para dizer ao spring para carregar essa classe, e diz que ela faz algum serviço na aplicação(Autenticacao no caso)

public class AutenticacaoService implements UserDetailsService{//<- Interface do Spring Security

    @Autowired
    private UsuarioRepository repository; //Injetar dependencia na classe

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username);
    }


}
