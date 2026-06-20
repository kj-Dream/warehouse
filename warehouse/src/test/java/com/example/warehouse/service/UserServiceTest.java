package com.example.warehouse.service;

import com.example.warehouse.dto.UserCreateRequest;
import com.example.warehouse.dto.UserPageRequest;
import com.example.warehouse.dto.UserUpdateRequest;
import com.example.warehouse.mapper.SysUserMapper;
import com.example.warehouse.pojo.SysUser;
import com.example.warehouse.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private SysUser mockUser;

    @BeforeEach
    void setUp() {
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
    @DisplayName("分页查询用户列表")
    class ListUsersTests {

        @Test
        @DisplayName("正常分页查询 - 无筛选条件")
        void listUsersWithoutFilter() {
            // Arrange
            UserPageRequest request = new UserPageRequest(1, 10, null, null, null, null);
            List<SysUser> userList = Arrays.asList(mockUser, createUser(2, "user2", "用户2"));
            when(sysUserMapper.selectPage(null, null, null, null, 0, 10)).thenReturn(userList);
            when(sysUserMapper.countPage(null, null, null, null)).thenReturn(2L);

            // Act
            Map<String, Object> result = userService.listUsers(request);

            // Assert
            assertNotNull(result);
            assertEquals(2L, result.get("total"));
            assertEquals(1, result.get("pageNum"));
            assertEquals(10, result.get("pageSize"));
            assertEquals(userList, result.get("list"));

            verify(sysUserMapper).selectPage(null, null, null, null, 0, 10);
            verify(sysUserMapper).countPage(null, null, null, null);
        }

        @Test
        @DisplayName("分页查询 - 带用户名模糊搜索")
        void listUsersWithUsernameFilter() {
            // Arrange
            UserPageRequest request = new UserPageRequest(1, 10, "admin", null, null, null);
            List<SysUser> userList = Collections.singletonList(mockUser);
            when(sysUserMapper.selectPage("admin", null, null, null, 0, 10)).thenReturn(userList);
            when(sysUserMapper.countPage("admin", null, null, null)).thenReturn(1L);

            // Act
            Map<String, Object> result = userService.listUsers(request);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.get("total"));
            assertEquals(1, result.get("pageNum"));

            verify(sysUserMapper).selectPage("admin", null, null, null, 0, 10);
            verify(sysUserMapper).countPage("admin", null, null, null);
        }

        @Test
        @DisplayName("分页查询 - 第2页")
        void listUsersPage2() {
            // Arrange
            UserPageRequest request = new UserPageRequest(2, 5, null, null, null, null);
            when(sysUserMapper.selectPage(null, null, null, null, 5, 5)).thenReturn(Collections.emptyList());
            when(sysUserMapper.countPage(null, null, null, null)).thenReturn(0L);

            // Act
            Map<String, Object> result = userService.listUsers(request);

            // Assert
            assertNotNull(result);
            assertEquals(0L, result.get("total"));
            assertEquals(2, result.get("pageNum"));
            assertEquals(5, result.get("pageSize"));
            assertTrue(((List<?>) result.get("list")).isEmpty());

            verify(sysUserMapper).selectPage(null, null, null, null, 5, 5);
        }

        @Test
        @DisplayName("分页查询 - 多条件组合筛选")
        void listUsersWithMultipleFilters() {
            // Arrange
            UserPageRequest request = new UserPageRequest(1, 10, "admin", "管理", 1, 1);
            List<SysUser> userList = Collections.singletonList(mockUser);
            when(sysUserMapper.selectPage("admin", "管理", 1, 1, 0, 10)).thenReturn(userList);
            when(sysUserMapper.countPage("admin", "管理", 1, 1)).thenReturn(1L);

            // Act
            Map<String, Object> result = userService.listUsers(request);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.get("total"));

            verify(sysUserMapper).selectPage("admin", "管理", 1, 1, 0, 10);
            verify(sysUserMapper).countPage("admin", "管理", 1, 1);
        }
    }

    @Nested
    @DisplayName("查询单个用户")
    class GetByIdTests {

        @Test
        @DisplayName("查询存在的用户")
        void getUserFound() {
            when(sysUserMapper.findById(1)).thenReturn(mockUser);

            SysUser user = userService.getById(1);

            assertNotNull(user);
            assertEquals("admin", user.getUsername());
            verify(sysUserMapper).findById(1);
        }

        @Test
        @DisplayName("查询不存在的用户")
        void getUserNotFound() {
            when(sysUserMapper.findById(999)).thenReturn(null);

            SysUser user = userService.getById(999);

            assertNull(user);
            verify(sysUserMapper).findById(999);
        }
    }

    @Nested
    @DisplayName("新增用户")
    class CreateUserTests {

        @Test
        @DisplayName("新增用户成功 - 传入所有字段")
        void createUserSuccess() {
            // Arrange
            UserCreateRequest request = new UserCreateRequest("newuser", "mypassword", "新用户", "13900139000", "new@example.com", 2);
            when(sysUserMapper.findByUsername("newuser")).thenReturn(null);
            when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

            // Act
            userService.createUser(request);

            // Assert
            ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
            verify(sysUserMapper).insert(captor.capture());
            SysUser captured = captor.getValue();

            assertEquals("newuser", captured.getUsername());
            assertEquals("mypassword", captured.getPassword());
            assertEquals("新用户", captured.getRealName());
            assertEquals("13900139000", captured.getPhone());
            assertEquals("new@example.com", captured.getEmail());
            assertEquals(2, captured.getRoleId());
        }

        @Test
        @DisplayName("新增用户 - 未传密码使用默认密码")
        void createUserWithDefaultPassword() {
            // Arrange
            UserCreateRequest request = new UserCreateRequest("newuser", null, "新用户", null, null, 2);
            when(sysUserMapper.findByUsername("newuser")).thenReturn(null);
            when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

            // Act
            userService.createUser(request);

            // Assert
            ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
            verify(sysUserMapper).insert(captor.capture());
            assertEquals("123456", captor.getValue().getPassword());
        }

        @Test
        @DisplayName("新增用户 - 传空字符串密码使用默认密码")
        void createUserWithEmptyPassword() {
            // Arrange
            UserCreateRequest request = new UserCreateRequest("newuser", "", "新用户", null, null, 2);
            when(sysUserMapper.findByUsername("newuser")).thenReturn(null);

            // Act
            userService.createUser(request);

            // Assert
            ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
            verify(sysUserMapper).insert(captor.capture());
            assertEquals("123456", captor.getValue().getPassword());
        }

        @Test
        @DisplayName("新增用户失败 - 用户名已存在")
        void createUserDuplicateUsername() {
            // Arrange
            UserCreateRequest request = new UserCreateRequest("admin", "password", "新用户", null, null, 2);
            when(sysUserMapper.findByUsername("admin")).thenReturn(mockUser);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.createUser(request));
            assertTrue(exception.getMessage().contains("已存在"));

            verify(sysUserMapper, never()).insert(any());
        }

        @Test
        @DisplayName("新增用户 - 不传roleId走SQL默认值")
        void createUserWithoutRoleId() {
            // Arrange
            UserCreateRequest request = new UserCreateRequest("newuser", "pass", "新用户", null, null, null);
            when(sysUserMapper.findByUsername("newuser")).thenReturn(null);
            when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

            // Act
            userService.createUser(request);

            // Assert
            ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
            verify(sysUserMapper).insert(captor.capture());
            assertNull(captor.getValue().getRoleId()); // SQL中用COALESCE处理默认值
        }
    }

    @Nested
    @DisplayName("修改用户")
    class UpdateUserTests {

        @Test
        @DisplayName("修改用户成功")
        void updateUserSuccess() {
            // Arrange
            UserUpdateRequest request = new UserUpdateRequest(1, "admin_new", "管理员新", "13900139001", "new@example.com", 2, 1);
            when(sysUserMapper.findById(1)).thenReturn(mockUser);
            // 用户名没变，不检查重复
            when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

            // Act
            userService.updateUser(request);

            // Assert
            ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
            verify(sysUserMapper).updateById(captor.capture());
            SysUser captured = captor.getValue();

            assertEquals(1, captured.getId());
            assertEquals("admin_new", captured.getUsername());
            assertEquals("管理员新", captured.getRealName());
            assertEquals("13900139001", captured.getPhone());
            assertEquals(2, captured.getRoleId());
            assertEquals(1, captured.getStatus());
        }

        @Test
        @DisplayName("修改用户 - 只修改部分字段")
        void updateUserPartialFields() {
            // Arrange
            UserUpdateRequest request = new UserUpdateRequest(1, null, null, "13900139002", null, null, null);
            when(sysUserMapper.findById(1)).thenReturn(mockUser);

            // Act
            userService.updateUser(request);

            // Assert
            ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
            verify(sysUserMapper).updateById(captor.capture());
            SysUser captured = captor.getValue();

            assertEquals(1, captured.getId());
            assertNull(captured.getUsername());
            assertNull(captured.getRealName());
            assertEquals("13900139002", captured.getPhone());
            assertNull(captured.getRoleId());
            assertNull(captured.getStatus());
        }

        @Test
        @DisplayName("修改用户 - 用户名被占用")
        void updateUserUsernameConflict() {
            // Arrange
            UserUpdateRequest request = new UserUpdateRequest(1, "other", null, null, null, null, null);
            when(sysUserMapper.findById(1)).thenReturn(mockUser);

            SysUser conflictingUser = createUser(2, "other", "其他用户");
            when(sysUserMapper.findByUsername("other")).thenReturn(conflictingUser);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.updateUser(request));
            assertTrue(exception.getMessage().contains("已被占用"));

            verify(sysUserMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("修改用户 - 用户不存在")
        void updateUserNotFound() {
            // Arrange
            UserUpdateRequest request = new UserUpdateRequest(999, "name", null, null, null, null, null);
            when(sysUserMapper.findById(999)).thenReturn(null);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.updateUser(request));
            assertEquals("用户不存在", exception.getMessage());

            verify(sysUserMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("修改用户 - 不改用户名不检查重复")
        void updateUserSameUsername() {
            // Arrange
            UserUpdateRequest request = new UserUpdateRequest(1, "admin", null, null, null, null, null);
            when(sysUserMapper.findById(1)).thenReturn(mockUser);
            // 不改用户名，不应该调用 findByUsername 检查

            // Act
            userService.updateUser(request);

            // Assert
            verify(sysUserMapper, never()).findByUsername(any());
            verify(sysUserMapper).updateById(any());
        }
    }

    @Nested
    @DisplayName("删除用户")
    class DeleteUserTests {

        @Test
        @DisplayName("删除存在的用户")
        void deleteUserSuccess() {
            when(sysUserMapper.findById(1)).thenReturn(mockUser);
            when(sysUserMapper.deleteById(1)).thenReturn(1);

            userService.deleteUser(1);

            verify(sysUserMapper).deleteById(1);
        }

        @Test
        @DisplayName("删除不存在的用户")
        void deleteUserNotFound() {
            when(sysUserMapper.findById(999)).thenReturn(null);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.deleteUser(999));
            assertEquals("用户不存在", exception.getMessage());

            verify(sysUserMapper, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("启用/禁用用户")
    class SetStatusTests {

        @Test
        @DisplayName("启用用户")
        void enableUser() {
            when(sysUserMapper.findById(1)).thenReturn(mockUser);
            when(sysUserMapper.updateStatus(1, 1)).thenReturn(1);

            userService.setStatus(1, 1);

            verify(sysUserMapper).updateStatus(1, 1);
        }

        @Test
        @DisplayName("禁用用户")
        void disableUser() {
            when(sysUserMapper.findById(1)).thenReturn(mockUser);
            when(sysUserMapper.updateStatus(1, 0)).thenReturn(1);

            userService.setStatus(1, 0);

            verify(sysUserMapper).updateStatus(1, 0);
        }

        @Test
        @DisplayName("状态值不合法")
        void setStatusInvalidValue() {
            when(sysUserMapper.findById(1)).thenReturn(mockUser);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.setStatus(1, 2));
            assertTrue(exception.getMessage().contains("不合法"));

            verify(sysUserMapper, never()).updateStatus(anyInt(), anyInt());
        }

        @Test
        @DisplayName("用户不存在时设置状态")
        void setStatusUserNotFound() {
            when(sysUserMapper.findById(999)).thenReturn(null);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.setStatus(999, 1));
            assertEquals("用户不存在", exception.getMessage());

            verify(sysUserMapper, never()).updateStatus(anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("重置密码")
    class ResetPasswordTests {

        @Test
        @DisplayName("重置密码为默认值")
        void resetPasswordSuccess() {
            when(sysUserMapper.findById(1)).thenReturn(mockUser);
            when(sysUserMapper.updatePassword(1, "123456")).thenReturn(1);

            userService.resetPassword(1);

            verify(sysUserMapper).updatePassword(1, "123456");
        }

        @Test
        @DisplayName("用户不存在时重置密码")
        void resetPasswordUserNotFound() {
            when(sysUserMapper.findById(999)).thenReturn(null);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.resetPassword(999));
            assertEquals("用户不存在", exception.getMessage());

            verify(sysUserMapper, never()).updatePassword(anyInt(), anyString());
        }
    }

    private SysUser createUser(Integer id, String username, String realName) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("123456");
        user.setRealName(realName);
        user.setStatus(1);
        return user;
    }
}
