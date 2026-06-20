package com.example.warehouse.controller;

import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;
import com.example.warehouse.dto.RegisterRequest;
import com.example.warehouse.dto.Result;
import com.example.warehouse.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@DisplayName("LoginController 接口测试")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginService loginService;

    @Nested
    @DisplayName("POST /api/auth/login - 登录接口")
    class LoginTests {

        @Test
        @DisplayName("登录成功 - 返回200和数据")
        void loginSuccess() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("admin", "123456");
            LoginResponse response = new LoginResponse(1, "admin", "管理员",
                    "13800138000", "admin@example.com", 1, 1, null);
            when(loginService.login(any(LoginRequest.class))).thenReturn(response);

            // Act & Assert
            MvcResult result = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("登录成功"))
                    .andExpect(jsonPath("$.data.username").value("admin"))
                    .andExpect(jsonPath("$.data.realName").value("管理员"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            Result<?> resultObj = objectMapper.readValue(content, Result.class);
            assertEquals(200, resultObj.getCode());

            verify(loginService).login(any(LoginRequest.class));
        }

        @Test
        @DisplayName("登录失败 - 用户名为空")
        void loginWithEmptyUsername() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("", "123456");

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户名不能为空"));

            verify(loginService, never()).login(any());
        }

        @Test
        @DisplayName("登录失败 - 密码为空")
        void loginWithEmptyPassword() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("admin", "");

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("密码不能为空"));

            verify(loginService, never()).login(any());
        }

        @Test
        @DisplayName("登录失败 - Service抛出业务异常")
        void loginWithServiceException() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("wrong", "wrong");
            when(loginService.login(any(LoginRequest.class)))
                    .thenThrow(new RuntimeException("用户不存在"));

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户不存在"));
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void loginWithWrongPassword() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("admin", "wrongpass");
            when(loginService.login(any(LoginRequest.class)))
                    .thenThrow(new RuntimeException("密码错误"));

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("密码错误"));
        }

        @Test
        @DisplayName("登录失败 - 账号被禁用")
        void loginWithDisabledAccount() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("admin", "123456");
            when(loginService.login(any(LoginRequest.class)))
                    .thenThrow(new RuntimeException("账号已被禁用"));

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("账号已被禁用"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/register - 注册接口")
    class RegisterTests {

        @Test
        @DisplayName("注册成功")
        void registerSuccess() throws Exception {
            // Arrange
            RegisterRequest request = new RegisterRequest("newuser", "password123", "新用户", "13900139000", "new@example.com");
            LoginResponse response = new LoginResponse(2, "newuser", "新用户",
                    "13900139000", "new@example.com", 2, 1, null);
            when(loginService.register(any(RegisterRequest.class))).thenReturn(response);

            // Act & Assert
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("注册成功"))
                    .andExpect(jsonPath("$.data.username").value("newuser"))
                    .andExpect(jsonPath("$.data.realName").value("新用户"));

            verify(loginService).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("注册失败 - 用户名为空")
        void registerWithEmptyUsername() throws Exception {
            RegisterRequest request = new RegisterRequest("", "password", "新用户", null, null);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户名不能为空"));

            verify(loginService, never()).register(any());
        }

        @Test
        @DisplayName("注册失败 - 密码为空")
        void registerWithEmptyPassword() throws Exception {
            RegisterRequest request = new RegisterRequest("newuser", "", "新用户", null, null);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("密码不能为空"));

            verify(loginService, never()).register(any());
        }

        @Test
        @DisplayName("注册失败 - 真实姓名为空")
        void registerWithEmptyRealName() throws Exception {
            RegisterRequest request = new RegisterRequest("newuser", "password", "", null, null);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("真实姓名不能为空"));

            verify(loginService, never()).register(any());
        }

        @Test
        @DisplayName("注册失败 - 用户名已存在")
        void registerWithDuplicateUsername() throws Exception {
            RegisterRequest request = new RegisterRequest("admin", "password123", "新用户", null, null);
            when(loginService.register(any(RegisterRequest.class)))
                    .thenThrow(new RuntimeException("用户名已存在"));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户名已存在"));
        }
    }
}
