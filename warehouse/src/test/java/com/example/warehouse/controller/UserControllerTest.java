package com.example.warehouse.controller;

import com.example.warehouse.dto.*;
import com.example.warehouse.pojo.SysUser;
import com.example.warehouse.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController 接口测试")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private SysUser createMockUser(Integer id, String username, String realName) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setRealName(realName);
        user.setPhone("13800138000");
        user.setEmail("admin@example.com");
        user.setRoleId(1);
        user.setStatus(1);
        return user;
    }

    @Nested
    @DisplayName("GET /api/user/page - 分页查询用户列表")
    class PageTests {

        @Test
        @DisplayName("分页查询成功")
        void pageSuccess() throws Exception {
            // Arrange
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("list", Arrays.asList(
                    createMockUser(1, "admin", "管理员"),
                    createMockUser(2, "user2", "用户2")
            ));
            pageData.put("total", 2L);
            pageData.put("pageNum", 1);
            pageData.put("pageSize", 10);
            when(userService.listUsers(any(UserPageRequest.class))).thenReturn(pageData);

            // Act & Assert
            mockMvc.perform(get("/api/user/page")
                            .param("pageNum", "1")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("查询成功"))
                    .andExpect(jsonPath("$.data.total").value(2))
                    .andExpect(jsonPath("$.data.list.length()").value(2))
                    .andExpect(jsonPath("$.data.list[0].username").value("admin"));

            verify(userService).listUsers(any(UserPageRequest.class));
        }

        @Test
        @DisplayName("分页查询 - 带筛选参数")
        void pageWithFilters() throws Exception {
            // Arrange
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("list", Collections.singletonList(createMockUser(1, "admin", "管理员")));
            pageData.put("total", 1L);
            pageData.put("pageNum", 1);
            pageData.put("pageSize", 10);
            when(userService.listUsers(any(UserPageRequest.class))).thenReturn(pageData);

            // Act & Assert
            mockMvc.perform(get("/api/user/page")
                            .param("pageNum", "1")
                            .param("pageSize", "10")
                            .param("username", "admin")
                            .param("status", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(1));

            verify(userService).listUsers(any(UserPageRequest.class));
        }

        @Test
        @DisplayName("分页查询 - Service异常")
        void pageWithServiceException() throws Exception {
            when(userService.listUsers(any(UserPageRequest.class)))
                    .thenThrow(new RuntimeException("查询失败"));

            mockMvc.perform(get("/api/user/page")
                            .param("pageNum", "1")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("查询失败"));
        }
    }

    @Nested
    @DisplayName("GET /api/user/{id} - 查询单个用户")
    class GetByIdTests {

        @Test
        @DisplayName("查询存在的用户")
        void getUserFound() throws Exception {
            when(userService.getById(1)).thenReturn(createMockUser(1, "admin", "管理员"));

            mockMvc.perform(get("/api/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("查询成功"))
                    .andExpect(jsonPath("$.data.username").value("admin"));
        }

        @Test
        @DisplayName("查询不存在的用户")
        void getUserNotFound() throws Exception {
            when(userService.getById(999)).thenReturn(null);

            mockMvc.perform(get("/api/user/999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户不存在"));
        }

        @Test
        @DisplayName("查询用户 - Service异常")
        void getUserWithException() throws Exception {
            when(userService.getById(1)).thenThrow(new RuntimeException("数据库异常"));

            mockMvc.perform(get("/api/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("数据库异常"));
        }
    }

    @Nested
    @DisplayName("POST /api/user - 新增用户")
    class CreateTests {

        @Test
        @DisplayName("新增用户成功")
        void createSuccess() throws Exception {
            UserCreateRequest request = new UserCreateRequest("newuser", "pass123", "新用户", "13900139000", "new@example.com", 2);
            doNothing().when(userService).createUser(any(UserCreateRequest.class));

            mockMvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("新增用户成功"));

            verify(userService).createUser(any(UserCreateRequest.class));
        }

        @Test
        @DisplayName("新增用户 - 用户名为空")
        void createWithEmptyUsername() throws Exception {
            UserCreateRequest request = new UserCreateRequest("", "pass123", "新用户", null, null, 2);

            mockMvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户名不能为空"));

            verify(userService, never()).createUser(any());
        }

        @Test
        @DisplayName("新增用户 - 真实姓名为空")
        void createWithEmptyRealName() throws Exception {
            UserCreateRequest request = new UserCreateRequest("newuser", "pass123", "", null, null, 2);

            mockMvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("真实姓名不能为空"));

            verify(userService, never()).createUser(any());
        }

        @Test
        @DisplayName("新增用户 - 角色ID为空")
        void createWithNullRoleId() throws Exception {
            UserCreateRequest request = new UserCreateRequest("newuser", "pass123", "新用户", null, null, null);

            mockMvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("请选择角色"));

            verify(userService, never()).createUser(any());
        }

        @Test
        @DisplayName("新增用户 - 用户名已存在")
        void createWithDuplicateUsername() throws Exception {
            UserCreateRequest request = new UserCreateRequest("admin", "pass123", "新用户", null, null, 2);
            doThrow(new RuntimeException("用户名【admin】已存在"))
                    .when(userService).createUser(any(UserCreateRequest.class));

            mockMvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户名【admin】已存在"));
        }
    }

    @Nested
    @DisplayName("PUT /api/user - 修改用户")
    class UpdateTests {

        @Test
        @DisplayName("修改用户成功")
        void updateSuccess() throws Exception {
            UserUpdateRequest request = new UserUpdateRequest(1, "admin", "管理员", "13900138001", "new@example.com", 1, 1);
            doNothing().when(userService).updateUser(any(UserUpdateRequest.class));

            mockMvc.perform(put("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("修改成功"));

            verify(userService).updateUser(any(UserUpdateRequest.class));
        }

        @Test
        @DisplayName("修改用户 - ID为空")
        void updateWithNullId() throws Exception {
            UserUpdateRequest request = new UserUpdateRequest(null, "admin", null, null, null, null, null);

            mockMvc.perform(put("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户ID不能为空"));

            verify(userService, never()).updateUser(any());
        }

        @Test
        @DisplayName("修改用户 - 用户不存在")
        void updateUserNotFound() throws Exception {
            UserUpdateRequest request = new UserUpdateRequest(999, "name", null, null, null, null, null);
            doThrow(new RuntimeException("用户不存在"))
                    .when(userService).updateUser(any(UserUpdateRequest.class));

            mockMvc.perform(put("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户不存在"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/user/{id} - 删除用户")
    class DeleteTests {

        @Test
        @DisplayName("删除用户成功")
        void deleteSuccess() throws Exception {
            doNothing().when(userService).deleteUser(1);

            mockMvc.perform(delete("/api/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("删除成功"));

            verify(userService).deleteUser(1);
        }

        @Test
        @DisplayName("删除不存在的用户")
        void deleteUserNotFound() throws Exception {
            doThrow(new RuntimeException("用户不存在"))
                    .when(userService).deleteUser(999);

            mockMvc.perform(delete("/api/user/999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户不存在"));
        }
    }

    @Nested
    @DisplayName("PUT /api/user/{id}/status - 启用/禁用用户")
    class SetStatusTests {

        @Test
        @DisplayName("启用用户")
        void enableUser() throws Exception {
            doNothing().when(userService).setStatus(1, 1);

            Map<String, Integer> body = new HashMap<>();
            body.put("status", 1);

            mockMvc.perform(put("/api/user/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("用户已启用"));

            verify(userService).setStatus(1, 1);
        }

        @Test
        @DisplayName("禁用用户")
        void disableUser() throws Exception {
            doNothing().when(userService).setStatus(1, 0);

            Map<String, Integer> body = new HashMap<>();
            body.put("status", 0);

            mockMvc.perform(put("/api/user/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("用户已禁用"));

            verify(userService).setStatus(1, 0);
        }

        @Test
        @DisplayName("设置状态 - 状态值为空")
        void setStatusWithNullStatus() throws Exception {
            Map<String, Integer> body = new HashMap<>();

            mockMvc.perform(put("/api/user/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("状态不能为空"));

            verify(userService, never()).setStatus(anyInt(), anyInt());
        }

        @Test
        @DisplayName("设置状态 - 用户不存在")
        void setStatusUserNotFound() throws Exception {
            doThrow(new RuntimeException("用户不存在"))
                    .when(userService).setStatus(999, 1);

            Map<String, Integer> body = new HashMap<>();
            body.put("status", 1);

            mockMvc.perform(put("/api/user/999/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户不存在"));
        }
    }

    @Nested
    @DisplayName("PUT /api/user/{id}/reset-password - 重置密码")
    class ResetPasswordTests {

        @Test
        @DisplayName("重置密码成功")
        void resetPasswordSuccess() throws Exception {
            doNothing().when(userService).resetPassword(1);

            mockMvc.perform(put("/api/user/1/reset-password"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("密码已重置为 123456"));

            verify(userService).resetPassword(1);
        }

        @Test
        @DisplayName("重置密码 - 用户不存在")
        void resetPasswordUserNotFound() throws Exception {
            doThrow(new RuntimeException("用户不存在"))
                    .when(userService).resetPassword(999);

            mockMvc.perform(put("/api/user/999/reset-password"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("用户不存在"));
        }
    }
}
