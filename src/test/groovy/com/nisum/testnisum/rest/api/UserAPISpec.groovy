package com.nisum.testnisum.rest.api

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.nisum.testnisum.dto.ResponseError
import com.nisum.testnisum.dto.UserDTO
import com.nisum.testnisum.dto.UserRequestDTO
import com.nisum.testnisum.exception.EmailExistsException
import com.nisum.testnisum.rest.exception.RestResponseEntityExceptionHandler
import com.nisum.testnisum.service.UserService
import com.nisum.testnisum.util.CreateObjectsUtil
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.validation.BindingResult
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException
import spock.lang.Specification
import spock.lang.Subject

import java.lang.reflect.Constructor

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class UserAPISpec extends Specification {
    @Subject
    UserAPI userAPI
    UserService userService
    MockMvc mockMvc;
    ObjectMapper objectMapper;

    def setup() {
        objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build()
        userService = Mock()
        userAPI = new UserAPI(userService)
        mockMvc = standaloneSetup(userAPI).setControllerAdvice(RestResponseEntityExceptionHandler.class).build()
    }

    def "createUser should return 201 status and UserDTO"() {
        given:
        def userRequestDTO = CreateObjectsUtil.getUserRequestDTO()
        def responseDTO = CreateObjectsUtil.getUserDTO()
        userService.createUser(CreateObjectsUtil.getUserRequestDTO()) >> responseDTO

        when:
        def response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(userRequestDTO))).andReturn().response
        UserDTO converterDTO = mapFromJson(response.contentAsString, UserDTO.class);

        then:
        response.status == HttpStatus.CREATED.value()
        converterDTO.id() == responseDTO.id()
        converterDTO.email() == responseDTO.email()
        converterDTO.name() == responseDTO.name()
    }

    def "createUser should return 400 status and detailed messages"() {
        given:
        def userRequestDTO = new UserRequestDTO("", "", "", null)

        when:
        def response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(userRequestDTO))).andReturn().response
        ResponseError converterError = mapFromJson(response.contentAsString, ResponseError.class);

        then:
        response.status == HttpStatus.BAD_REQUEST.value()
        converterError.message() == "Validation exception"
        converterError.detailedMessages().size() == 4
    }

    def "createUser should return 409 status and detailed message"() {
        given:
        def userRequestDTO = CreateObjectsUtil.getUserRequestDTO()
        userService.createUser(userRequestDTO) >> { throw new EmailExistsException(userRequestDTO.email()) }

        when:
        def response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(userRequestDTO))).andReturn().response
        ResponseError converterError = mapFromJson(response.contentAsString, ResponseError.class);

        then:
        response.status == HttpStatus.CONFLICT.value()
        converterError.message() == "The email test@gmail.com is already registered"
    }

    def "createUser should return 500 status and detailed message"() {
        given:
        def userRequestDTO = CreateObjectsUtil.getUserRequestDTO()
        userService.createUser(userRequestDTO) >> { throw new RuntimeException("Generic Error") }

        when:
        def response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(userRequestDTO))).andReturn().response
        ResponseError converterError = mapFromJson(response.contentAsString, ResponseError.class);

        then:
        response.status == HttpStatus.INTERNAL_SERVER_ERROR.value()
        converterError.message() == "Generic Error"
    }

    def "createUser should return 400 status and error WebExchangeBindException"() {
        given:
        def userRequestDTO = CreateObjectsUtil.getUserRequestDTO()
        MethodParameter parameter = Mock()
        Constructor executable = Mock()
        BindingResult bindingResult = Mock()
        parameter.getExecutable() >> executable
        bindingResult.getErrorCount() >> 1
        bindingResult.getAllErrors() >> []
        userService.createUser(userRequestDTO) >> { throw new WebExchangeBindException(parameter, bindingResult) }

        when:
        def response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(userRequestDTO))).andReturn().response
        ResponseError converterError = mapFromJson(response.contentAsString, ResponseError.class);

        then:
        response.status == HttpStatus.BAD_REQUEST.value()
        converterError.message() == "Validation exception"
        converterError.detailedMessages().size() == 0
    }

    def "createUser should return 400 status and error ServerWebInputException"() {
        given:
        def userRequestDTO = CreateObjectsUtil.getUserRequestDTO()
        MethodParameter parameter = Mock()
        Constructor executable = Mock()
        BindingResult bindingResult = Mock()
        parameter.getExecutable() >> executable
        bindingResult.getErrorCount() >> 1
        bindingResult.getAllErrors() >> []
        userService.createUser(userRequestDTO) >> { throw new ServerWebInputException("Invalid Body") }

        when:
        def response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(userRequestDTO))).andReturn().response
        ResponseError converterError = mapFromJson(response.contentAsString, ResponseError.class);

        then:
        response.status == HttpStatus.BAD_REQUEST.value()
        converterError.message() == "Invalid Body"
        converterError.detailedMessages().size() == 0
    }

    def "listUsers should return 200 status and list of UserDTO"() {
        given:
        UserDTO responseDTO = CreateObjectsUtil.getUserDTO()
        userService.listUsers() >> [responseDTO]

        when:
        ResponseEntity<List<UserDTO>> response = userAPI.listUsers()

        then:
        response.statusCode == HttpStatus.OK
        response.body[0].id() == responseDTO.id()
        response.body[0].email() == responseDTO.email()
        response.body[0].name() == responseDTO.name()
        response.body.size() == 1
    }

    String mapToJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    <T> T mapFromJson(String json, Class<T> clazz)
            throws IOException {
        return objectMapper.readValue(json, clazz);
    }
}
