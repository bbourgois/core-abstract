package com.example.demo.controller;


import com.example.demo.MultipleCoreImplemSampleApplication;
import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.in.StockDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = MultipleCoreImplemSampleApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integration-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StockControllerIntegrationTest {

    public static final int VERSION_1 = 1;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void stockIsEmpty_whenGetStock_thenStateIsEMPTY()
            throws Exception {
        mvc.perform(get("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("version", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("state", is("EMPTY")));
    }

    @Test
    public void stockNotEmpty_whenGetStock_thenStateIsSOME()
            throws Exception {
        StockDetail stockDetail = mockHikingShoesWithQuantity(20);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", 1)
                .content(objectMapper.writeValueAsString(stockDetail))
        ).andExpect(status().isOk());

        mvc.perform(get("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("version", VERSION_1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("state", is("SOME")));
    }

    @Test
    public void stockIsFull_whenGetStock_thenStateIsFull()
            throws Exception {
        StockDetail stockDetail = mockHikingShoesWithQuantity(20);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", 1)
                .content(objectMapper.writeValueAsString(stockDetail))
        ).andExpect(status().isOk());

        StockDetail otherStockDetail = mockOtherShoesWithQuantity(10);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", 1)
                .content(objectMapper.writeValueAsString(otherStockDetail))
        ).andExpect(status().isOk());

        mvc.perform(get("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("version", VERSION_1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("state", is("FULL")));
    }


    @Test
    public void stockIsFull_whenPatchStock_then400()
            throws Exception {
        StockDetail stockDetail = mockHikingShoesWithQuantity(20);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", VERSION_1)
                .content(objectMapper.writeValueAsString(stockDetail))
        ).andExpect(status().isOk());

        StockDetail otherStockDetail = mockOtherShoesWithQuantity(11);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", VERSION_1)
                .content(objectMapper.writeValueAsString(otherStockDetail))
        ).andExpect(status().is4xxClientError());

    }

    @Test
    public void whenPatchStock_add_many_time_the_same_shoes_box_should_work()
            throws Exception {
        StockDetail stockDetail = mockHikingShoesWithQuantity(10);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", VERSION_1)
                .content(objectMapper.writeValueAsString(stockDetail))
        ).andExpect(status().isOk());

        StockDetail otherStockDetail = mockHikingShoesWithQuantity(15);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", VERSION_1)
                .content(objectMapper.writeValueAsString(otherStockDetail))
        ).andExpect(status().isOk());

        mvc.perform(get("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("version", VERSION_1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("state", is("SOME")))
                .andExpect(jsonPath("$.shoes[0].quantity", is(25)));
    }


    private StockDetail mockHikingShoesWithQuantity(int quantity) {
        return mockStockDetailBuilder(quantity)
                .name("hiking shoes")
                .build();
    }

    private StockDetail mockOtherShoesWithQuantity(int quantity) {
        return mockStockDetailBuilder(quantity)
                .name("other shoes")
                .build();
    }

    private StockDetail.StockDetailBuilder mockStockDetailBuilder(int quantity) {
        return StockDetail.builder()
                .color(ShoeFilter.Color.BLUE)
                .size(BigInteger.valueOf(39))
                .quantity(quantity);
    }

}