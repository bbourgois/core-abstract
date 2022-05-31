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
    public void getStock_should_return_state_is_EMPTY_when_stock_is_empty()
            throws Exception {
        mvc.perform(get("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("version", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("state", is("EMPTY")));
    }

    @Test
    public void getStock_should_return_state_is_SOME_when_stock_is_NOT_empty()
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
    public void getStock_should_return_state_is_FULL_when_stock_is_full()
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
    public void patchStock_should_failed_when_stock_is_full()
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
    public void patchStock_should_return_on_line_in_shoes_array_when_adding_many_time_the_same_shoes()
            throws Exception {
        StockDetail stockDetail = mockHikingShoesWithQuantity(10);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", VERSION_1)
                .content(objectMapper.writeValueAsString(stockDetail))
        ).andExpect(status().isOk());


        mvc.perform(get("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("version", VERSION_1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("state", is("SOME")))
                .andExpect(jsonPath("$.shoes[0].quantity", is(10)));


        StockDetail removeStockDetail = mockHikingShoesWithQuantity(-5);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", VERSION_1)
                .content(objectMapper.writeValueAsString(removeStockDetail))
        ).andExpect(status().isOk());
        mvc.perform(get("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("version", VERSION_1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("state", is("SOME")))
                .andExpect(jsonPath("$.shoes[0].quantity", is(5)));

        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", VERSION_1)
                .content(objectMapper.writeValueAsString(removeStockDetail))
        ).andExpect(status().isOk());

        mvc.perform(get("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("version", VERSION_1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("state", is("EMPTY")));
    }


    @Test
    public void patchStock_should_failed_when_shoe_box_is_not_in_stock()
            throws Exception {
        StockDetail removeFromStock = mockHikingShoesWithQuantity(-3);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", VERSION_1)
                .content(objectMapper.writeValueAsString(removeFromStock))
        ).andExpect(status().is4xxClientError());
    }

    @Test
    public void patchStock_should_failed_when_there_are_not_enough_shoes_in_stock()
            throws Exception {
        StockDetail addInStockDetail = mockHikingShoesWithQuantity(1);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", VERSION_1)
                .content(objectMapper.writeValueAsString(addInStockDetail))
        ).andExpect(status().isOk());

        StockDetail removeFromStock = mockHikingShoesWithQuantity(-3);
        mvc.perform(patch("/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", VERSION_1)
                .content(objectMapper.writeValueAsString(removeFromStock))
        ).andExpect(status().is4xxClientError());
    }

    @Test
    public void getStock_should_return_state_EMPTYR_when_all_shoes_are_removed_from_stock()
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