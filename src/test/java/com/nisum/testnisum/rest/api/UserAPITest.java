package com.nisum.testnisum.rest.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nisum.testnisum.dto.ResponseError;
import com.nisum.testnisum.dto.UserDTO;
import com.nisum.testnisum.dto.UserRequestDTO;
import com.nisum.testnisum.exception.EmailExistsException;
import com.nisum.testnisum.rest.exception.RestResponseEntityExceptionHandler;
import com.nisum.testnisum.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.io.IOException;

import static com.nisum.testnisum.utils.CreateObjectsUtil.getUserDTO;
import static com.nisum.testnisum.utils.CreateObjectsUtil.getUserRequestDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class UserAPITest {
    private UserService userService;
    private MockMvc client;
    private UserAPI userAPI;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        userService = Mockito.mock();
        userAPI = new UserAPI(userService);
        client = MockMvcBuilders.standaloneSetup(userAPI).setControllerAdvice(RestResponseEntityExceptionHandler.class)
                .build();
    }

    @Test
    public void testSave() throws Exception {
        UserRequestDTO userRequestDTO = getUserRequestDTO();
        UserDTO userDTO = getUserDTO();
        Mockito.when(userService.createUser(any()))
                .thenReturn(userDTO);

        MvcResult mvcResult = client.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(userRequestDTO))).andReturn();

        UserDTO response = mapFromJson(mvcResult.getResponse().getContentAsString(), UserDTO.class);
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
        assertEquals(userRequestDTO.email(), response.email());
        assertEquals(userRequestDTO.name(), response.name());
    }

    @Test
    public void testInvalidRequest() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("", "", "", null);
        UserDTO userDTO = getUserDTO();
        Mockito.when(userService.createUser(any()))
                .thenReturn(userDTO);

        MvcResult mvcResult = client.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(userRequestDTO))).andReturn();

        ResponseError response = mapFromJson(mvcResult.getResponse().getContentAsString(), ResponseError.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        assertEquals(4, response.detailedMessages().size());
    }

    @Test
    public void testAlreadyExistsEmail() throws Exception {
        UserRequestDTO userRequestDTO = getUserRequestDTO();
        Mockito.when(userService.createUser(any()))
                .thenThrow(new EmailExistsException(userRequestDTO.email()));

        MvcResult mvcResult = client.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(userRequestDTO))).andReturn();

        ResponseError response = mapFromJson(mvcResult.getResponse().getContentAsString(), ResponseError.class);
        assertEquals(HttpStatus.CONFLICT.value(), mvcResult.getResponse().getStatus());
        assertEquals("The email test@gmail.com is already registered", response.message());
    }

    @Test
    public void testGeneralException() throws Exception {
        Mockito.when(userService.createUser(any()))
                .thenThrow(new RuntimeException("Error generico"));

        MvcResult mvcResult = client.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(getUserRequestDTO()))).andReturn();

        ResponseError response = mapFromJson(mvcResult.getResponse().getContentAsString(), ResponseError.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mvcResult.getResponse().getStatus());
        assertEquals("Error generico", response.message());
    }

    @Test
    public void testServerWebInputException() throws Exception {
        Mockito.when(userService.createUser(any()))
                .thenThrow(new ServerWebInputException("Body no valido"));
        MvcResult mvcResult = client.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(getUserRequestDTO()))).andReturn();

        ResponseError response = mapFromJson(mvcResult.getResponse().getContentAsString(), ResponseError.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        assertEquals("Body no valido", response.message());
    }

    private String mapToJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private <T> T mapFromJson(String json, Class<T> clazz)
            throws IOException {
        return objectMapper.readValue(json, clazz);
    }
}
