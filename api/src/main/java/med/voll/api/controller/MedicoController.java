package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.medico.DadosListagemMedico;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("medicos")
@SecurityRequirement(name = "bearer-key")
public class MedicoController {

    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder)//Vai pegar a uri da onde veio a solicitação
    {
        var medico = new Medico(dados);
        repository.save(medico);

        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();//Vai atribuir a uri e vai pegar o id de forma dinamica

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }

    @GetMapping
    public ResponseEntity <Page<DadosListagemMedico>> listar(@PageableDefault(size = 10, sort ={"nome"}) Pageable paginacao)
    {
        var page =  repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
        return  ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados)
    {
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }


    @DeleteMapping("/{id}")//entre chaves como paramentro é um paremetro dinamico
    @Transactional //transação no banco de dados
    public ResponseEntity excluir(@PathVariable Long id) //PathVariable para utilizar a variavel que veio do caminho, no caso o id
    {
        var medico = repository.getReferenceById(id); //Pega o objeto pelo id
        medico.excluir();

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")//entre chaves como paramentro é um paremetro dinamico
    public ResponseEntity detalhar(@PathVariable Long id) //PathVariable para utilizar a variavel que veio do caminho, no caso o id
    {
        var medico = repository.getReferenceById(id); //Carrega um médico do banco de dados pelo id

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }




}
