package ru.yandexAcadamy.testControllerWithContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandexAcadamy.item.ItemController;
import ru.yandexAcadamy.item.ItemService;
import ru.yandexAcadamy.item.dto.SystemItem;
import ru.yandexAcadamy.item.dto.SystemItemHistoryResponse;
import ru.yandexAcadamy.item.dto.SystemItemHistoryUnit;
import ru.yandexAcadamy.item.model.Type;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mvc;

    @Test
    void test1_findItem() throws Exception {
        String findId = "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4";
        SystemItem systemItem = SystemItem.builder()
                .id(findId)
                .url("/file/url2")
                .date("2022-02-02T12:00:00Z")
                .parentId("d515e43f-f3f6-4471-bb77-6b455017a2d2")
                .type(Type.FILE)
                .size(256)
                .children(null)
                .build();
        Mockito
                .when(itemService.findItemById(findId))
                .thenReturn(systemItem);
        mvc.perform(get("/nodes/" + findId)
                        .content(mapper.writeValueAsString(systemItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(systemItem.getId()), String.class))
                .andExpect(jsonPath("$.url", is(systemItem.getUrl()), String.class))
                .andExpect(jsonPath("$.date", is(systemItem.getDate()), String.class))
                .andExpect(jsonPath("$.parentId", is(systemItem.getParentId()), String.class))
                .andExpect(jsonPath("$.type", is(systemItem.getType().toString()), String.class))
                .andExpect(jsonPath("$.size", is(systemItem.getSize()), Integer.class))
                .andExpect(jsonPath("$.children", Matchers.nullValue(), List.class));
    }

    @Test
    void test2_findUpdatedItem() throws Exception {
        String updateTime = "2022-02-02T12:00:00Z";
        List<SystemItemHistoryUnit> items = new ArrayList<>();
        SystemItemHistoryUnit systemItemHistory = SystemItemHistoryUnit.builder()
                .id("b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4")
                .url("/file/url2")
                .date("2022-02-02T12:00:00Z")
                .parentId("d515e43f-f3f6-4471-bb77-6b455017a2d2")
                .type(Type.FILE)
                .size(256)
                .build();
        items.add(systemItemHistory);
        Mockito
                .when(itemService.findUpdatedFile(updateTime))
                .thenReturn(items);
        mvc.perform(get("/updates?date=" + updateTime)
                        .content(mapper.writeValueAsString(systemItemHistory))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(systemItemHistory.getId()), String.class))
                .andExpect(jsonPath("$[0].url", is(systemItemHistory.getUrl()), String.class))
                .andExpect(jsonPath("$[0].date", is(systemItemHistory.getDate()), String.class))
                .andExpect(jsonPath("$[0].parentId", is(systemItemHistory.getParentId()), String.class))
                .andExpect(jsonPath("$[0].type", is(systemItemHistory.getType().toString()), String.class))
                .andExpect(jsonPath("$[0].size", is(systemItemHistory.getSize()), Integer.class));
    }

    @Test
    void test3_findHistory() throws Exception {
        String findId = "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1";

        List<SystemItemHistoryUnit> items = new ArrayList<>();
        SystemItemHistoryUnit firstSystemItemHistory = SystemItemHistoryUnit.builder()
                .id("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1")
                .date("2022-02-02T12:00:00Z")
                .type(Type.FOLDER)
                .build();
        SystemItemHistoryUnit secondSystemItemHistory = SystemItemHistoryUnit.builder()
                .id("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1")
                .date("2022-02-02T13:00:00Z")
                .type(Type.FOLDER)
                .size(256)
                .build();
        items.add(firstSystemItemHistory);
        items.add(secondSystemItemHistory);

        SystemItemHistoryResponse systemItemHistoryResponse = SystemItemHistoryResponse.builder()
                .items(items)
                .build();

        Mockito
                .when(itemService.getHistory(findId, null, null))
                .thenReturn(systemItemHistoryResponse);

        mvc.perform(get("/node/" + findId + "/history")
                        .content(mapper.writeValueAsString(systemItemHistoryResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id", is(firstSystemItemHistory.getId()), String.class))
                .andExpect(jsonPath("$.items[0].url", nullValue(), String.class))
                .andExpect(jsonPath("$.items[0].date", is(firstSystemItemHistory.getDate()), String.class))
                .andExpect(jsonPath("$.items[0].parentId", nullValue(), String.class))
                .andExpect(jsonPath("$.items[0].type", is(firstSystemItemHistory.getType().toString()), String.class))
                .andExpect(jsonPath("$.items[0].size", nullValue(), Integer.class))
                .andExpect(jsonPath("$.items[1].id", is(secondSystemItemHistory.getId()), String.class))
                .andExpect(jsonPath("$.items[1].url", nullValue(), String.class))
                .andExpect(jsonPath("$.items[1].date", is(secondSystemItemHistory.getDate()), String.class))
                .andExpect(jsonPath("$.items[1].parentId", nullValue(), String.class))
                .andExpect(jsonPath("$.items[1].type", is(secondSystemItemHistory.getType().toString()), String.class))
                .andExpect(jsonPath("$.items[1].size", is(secondSystemItemHistory.getSize()), Integer.class));
    }
}
