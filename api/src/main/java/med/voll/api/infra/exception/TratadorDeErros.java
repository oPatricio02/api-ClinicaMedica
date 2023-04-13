package med.voll.api.infra.exception;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice //Anotação para dizer que é uma classe para tratar erros
public class TratadorDeErros {

    @ExceptionHandler(EntityNotFoundException.class) //Sempre que aparecer esse erro de entidade não encontrada irá chamar esse método
    public ResponseEntity tratarErro404()
    {
        return ResponseEntity.notFound().build(); //Para criar o objeto reponse
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) //Quando tiver algum erro de argumento inválido cai nesse método
    public ResponseEntity tratarErro400(MethodArgumentNotValidException ex)//Atribui um parametro do tipo do argumento para pegar os erros
    {
        var erros = ex.getFieldErrors(); //Cria uma variável que vai receber um array com os erros
                //Usar o dto "DadosErroValidacao" para devolver o erro e a mensagem de erro
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao :: new).toList()); //Converto a lista de fieldError para uma lista do DTO
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity tratarErro400(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity tratarErroBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity tratarErroAuthentication() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falha na autenticação");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity tratarErroAcessoNegado() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity tratarErro500(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " +ex.getLocalizedMessage());
    }
    private record DadosErroValidacao(String campo,String mensagem)
    {
        public DadosErroValidacao(FieldError erro)  //Construtor que vai receber um findError
        {
            this(erro.getField(),erro.getDefaultMessage()); //Vai dar o nome do Campo e a mensagem de erro
        }
    }

}
