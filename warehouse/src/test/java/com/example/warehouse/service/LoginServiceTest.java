package com.example.warehouse.service;

import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;
import com.example.warehouse.dto.RegisterRequest;
import com.example.warehouse.mapper.SysRoleMapper;
import com.example.warehouse.mapper.SysUserMapper;
import com.example.warehouse.pojo.SysRole;
import com.example.warehouse.pojo.SysUser;
import com.example.warehouse.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 单元测试")
class LoginServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysRoleMapper sysRoleMapper;

    @InjectMocks
    private LoginServiceImpl loginService;

    private SysUser mockUser;
    private SysRole mockRole;

    @BeforeEach
    void setUp() {
        mockRole = new SysRole(1, "管理员", "admin", "系统管理员", 1, null);

        mockUser = new SysUser();
        mockUser.setId(1);
        mockUser.setUsername("admin");
        mockUser.setPassword("123456");
        mockUser.setRealName("管理员");
        mockUser.setPhone("13800138000");
        mockUser.setEmail("admin@example.com");
        mockUser.setRoleId(1);
        mockUser.setStatus(1);
    }

    @Nested
    @DisplayName("登录功能测试")
    class LoginTests {

        @Test
        @DisplayName("登录成功 - 用户名密码正确")
        void loginSuccess() {
            // Arrange
            LoginRequest request = new LoginRequest("admin", "123456");
            when(sysUserMapper.findByUsername("admin")).thenReturn(mockUser);
            when(sysRoleMapper.findById(1)).thenReturn(mockRole);

            // Act
            LoginResponse response = loginService.login(request);

            // Assert
            assertNotNull(response);
            assertEquals(1, response.getId());
            assertEquals("admin", response.getUsername());
            assertEquals("管理员", response.getRealName());
            assertEquals(1, response.getStatus());
            assertNotNull(response.getRole());
            assertEquals("管理员", response.getRole().getRoleName());
            // 验证密码没有被返回（LoginResponse 没有 password 字段）
            try {
                response.getClass().getDeclaredField("password");
                fail("LoginResponse 不应包含 password 字段");
            } catch (NoSuchFieldException e) {
                // 期望行为：没有 password 字段
            }

            verify(sysUserMapper).findByUsername("admin");
            verify(sysRoleMapper).findById(1);
        }

        @Test
        @DisplayName("登录失败 - 用户不存在")
        void loginUserNotFound() {
            // Arrange
            LoginRequest request = new LoginRequest("nonexistent", "123456");
            when(sysUserMapper.findByUsername("nonexistent")).thenReturn(null);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> loginService.login(request));
            assertEquals("用户不存在", exception.getMessage());

            verify(sysUserMapper).findByUsername("nonexistent");
            verifyNoInteractions(sysRoleMapper);
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void loginWrongPassword() {
            // Arrange
            LoginRequest request = new LoginRequest("admin", "wrongpass");
            when(sysUserMapper.findByUsername("admin")).thenReturn(mockUser);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> loginService.login(request));
            assertEquals("密码错误", exception.getMessage());

            verify(sysUserMapper).findByUsername("admin");
            verifyNoInteractions(sysRoleMapper);
        }

        @Test
        @DisplayName("登录失败 - 账号已被禁用")
        void loginAccountDisabled() {
            // Arrange
            mockUser.setStatus(0);
            LoginRequest request = new LoginRequest("admin", "123456");
            when(sysUserMapper.findByUsername("admin")).thenReturn(mockUser);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> loginService.login(request));
            assertEquals("账号已被禁用", exception.getMessage());

            verify(sysUserMapper).findByUsername("admin");
            verifyNoInteractions(sysRoleMapper);
        }

        @Test
        @DisplayName("登录成功 - 用户无角色也能登录")
        void loginWithoutRole() {
            // Arrange
            mockUser.setRoleId(null);
            LoginRequest request = new LoginRequest("admin", "123456");
            when(sysUserMapper.findByUsername("admin")).thenReturn(mockUser);

            // Act
            LoginResponse response = loginService.login(request);

            // Assert
            assertNotNull(response);
            assertEquals("admin", response.getUsername());
            assertNull(response.getRole());

            verify(sysUserMapper).findByUsername("admin");
            verifyNoInteractions(sysRoleMapper);
        }
    }

    @Nested
    @DisplayName("注册功能测试")
    class RegisterTests {

        @Test
        @DisplayName("注册成功")
        void registerSuccess() {
            // Arrange
            RegisterRequest request = new RegisterRequest("newuser", "password123", "新用户", "13900139000", "new@example.com");
            when(sysUserMapper.findByUsername("newuser")).thenReturn(null);
            when(sysRoleMapper.findById(2)).thenReturn(new SysRole(2, "仓管员", "clerk", "仓库管理员", 1, null));

            // 模拟 insert 后自动生成ID
            doAnswer(invocation -> {
                SysUser user = invocation.getArgument(0);
                user.setId(2);
                user.setRoleId(2);
                user.setStatus(1);
                return 1;
            }).when(sysUserMapper).insert(any(SysUser.class));

            // Act
            LoginResponse response = loginService.register(request);

            // Assert
            assertNotNull(response);
            assertEquals(2, response.getId());
            assertEquals("newuser", response.getUsername());
            assertEquals("新用户", response.getRealName());
            assertEquals(2, response.getRoleId());
            assertEquals(1, response.getStatus());
            assertNotNull(response.getRole());

            // 验证 insert 的参数
            ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
            verify(sysUserMapper).insert(captor.capture());
            SysUser capturedUser = captor.getValue();
            assertEquals("newuser", capturedUser.getUsername());
            assertEquals("password123", capturedUser.getPassword());
            assertEquals("新用户", capturedUser.getRealName());
            assertEquals("new@example.com", capturedUser.getEmail());
        }

        @Test
        @DisplayName("注册失败 - 用户名已存在")
        void registerDuplicateUsername() {
            // Arrange
            RegisterRequest request = new RegisterRequest("admin", "password123", "新用户", "13900139000", "new@example.com");
            when(sysUserMapper.findByUsername("admin")).thenReturn(mockUser);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> loginService.register(request));
            assertEquals("用户名已存在", exception.getMessage());

            verify(sysUserMapper).findByUsername("admin");
            verify(sysUserMapper, never()).insert(any());
        }
    }
}
