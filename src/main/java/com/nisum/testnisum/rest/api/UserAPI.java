package com.nisum.testnisum.rest.api;

import com.nisum.testnisum.dto.ResponseError;
import com.nisum.testnisum.dto.UserDTO;
import com.nisum.testnisum.dto.UserRequestDTO;
import com.nisum.testnisum.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserAPI {
    private final UserService userService;

    @Autowired
    public UserAPI(UserService userService) {
        this.userService = userService;
    }

    @Operation(operationId = "createUser", summary = "Create User", description = "Create User",
            tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create User",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "error in params",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "409", description = "duplicated email",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "general error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserRequestDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }

    @Operation(operationId = "listUsers", summary = "List Users", description = "List Users",
            tags = {"user"})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create User",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "error in params",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "409", description = "duplicated email",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "general error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> listUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }
}
