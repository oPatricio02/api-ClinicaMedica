package med.voll.api.controller;

import med.voll.api.domain.consulta.AgendaDeConsultas;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import med.voll.api.domain.consulta.DadosDetalhamentoConsulta;
import med.voll.api.domain.medico.Especialidade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LOCAL_DATE_TIME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest //Para testar controller usa-se essa anotação
@AutoConfigureMockMvc
@AutoConfigureJsonTesters //Para injetar a classe JacksonTester
class ConsultaControllerTest {

    //Teste unitário(Mock)
    @Autowired
    private MockMvc mvc;

    @Autowired  //Uma classe do próprio spring para simular o json de entrada
    private JacksonTester<DadosAgendamentoConsulta> dadosAgendamentoConsultaJson;

    @Autowired
    private JacksonTester<DadosDetalhamentoConsulta> dadosDetalhamentoConsultaJson;

    @MockBean //Para dizer ao spring não usar a classe agenda de consultas que acessa o banco, para fazer um mock dessa classe
    private AgendaDeConsultas agendaDeConsultas;

    @Test
    @DisplayName("Deveria devolver código http 400 quando informações estão inválidas")
    @WithMockUser //Para dizer ao spring desconsiderar a necessidade de fazer login, simulando um usuário logado
    void agendar_cenario1() throws Exception {
                                                        //Descreve para qual método é a requisição
        var response = mvc.perform(post("/consultas"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deveria devolver código http 200 quando informações estão válidas")
    @WithMockUser //Para dizer ao spring desconsiderar a necessidade de fazer login, simulando um usuário logado
    void agendar_cenario2() throws Exception {

        var data = LocalDateTime.now().plusHours(1);
        var especialidade = Especialidade.ORTOPEDIA;
        var dadosDetalhamento = new DadosDetalhamentoConsulta(null, 2l,2l,data);

        when(agendaDeConsultas.agendar(any())).thenReturn(dadosDetalhamento);      //Classe da biblioteca mockito

        //Descreve para qual método é a requisição
        var response = mvc.perform(
                post("/consultas")
                        .contentType(MediaType.APPLICATION_JSON) //Para informar que está levando dados no formato json
                        .content(dadosAgendamentoConsultaJson.write(            // Para passar um json por parâmetro
                                new DadosAgendamentoConsulta(2l,2l,data,especialidade)
                        ).getJson())
                )
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        var jsonEsperado = dadosDetalhamentoConsultaJson.write(
                dadosDetalhamento)
                .getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado); //Compara se a resposta é igual a esperada
    }
}