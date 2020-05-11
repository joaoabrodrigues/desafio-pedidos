package br.com.bluesoft.desafio.api;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ProdutoControllerTest extends AbstractMockMvc {

    @Test
    public void shouldFindAllProducts() throws Exception {
        mockMvc.perform(get("/api/produtos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)))
                .andExpect(jsonPath("$[0].gtin", is("7894900011517")))
                .andExpect(jsonPath("$[0].nome", is("REFRIGERANTE COCA-COLA 2LT")))
                .andExpect(jsonPath("$[1].gtin", is("7891910000197")))
                .andExpect(jsonPath("$[1].nome", is("AÇÚCAR REFINADO UNIÃO 1KG")))
                .andExpect(jsonPath("$[2].gtin", is("7892840222949")))
                .andExpect(jsonPath("$[2].nome", is("SALGADINHO FANDANGOS QUEIJO")))
                .andExpect(jsonPath("$[3].gtin", is("7891910007110")))
                .andExpect(jsonPath("$[3].nome", is("AÇÚCAR DE CONFEITEIRO UNIÃO GLAÇÚCAR")))
                .andExpect(jsonPath("$[4].gtin", is("7891000053508")))
                .andExpect(jsonPath("$[4].nome", is("ACHOCOLATADO NESCAU 2.0")))
                .andExpect(jsonPath("$[5].gtin", is("7891000100103")))
                .andExpect(jsonPath("$[5].nome", is("LEITE CONDENSADO MOÇA")))
                .andExpect(jsonPath("$[6].gtin", is("7891991010856")))
                .andExpect(jsonPath("$[6].nome", is("CERVEJA BUDWEISER")));
    }
}