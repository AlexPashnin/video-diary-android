package com.videodiary.android.presentation.screens.auth

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.videodiary.android.domain.repository.AuthRepository

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: LoginViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(LoginState.Idle, viewModel.state.value)
    }

    @Test
    fun `login with invalid email emits Error without calling repository`() {
        viewModel.login("not-an-email", "password123")

        assertTrue(viewModel.state.value is LoginState.Error)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `login with blank password emits Error without calling repository`() {
        viewModel.login("user@example.com", "  ")

        assertTrue(viewModel.state.value is LoginState.Error)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `login success emits Success state`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns mockk()

        viewModel.login("user@example.com", "password123")

        assertEquals(LoginState.Success, viewModel.state.value)
        coVerify(exactly = 1) { authRepository.login("user@example.com", "password123") }
    }

    @Test
    fun `login failure emits Error state with message`() = runTest {
        val errorMessage = "Invalid credentials"
        coEvery { authRepository.login(any(), any()) } throws RuntimeException(errorMessage)

        viewModel.login("user@example.com", "wrongpassword")

        val state = viewModel.state.value
        assertTrue(state is LoginState.Error)
        assertEquals(errorMessage, (state as LoginState.Error).message)
    }

    @Test
    fun `login trims email whitespace before passing to repository`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns mockk()

        viewModel.login("  user@example.com  ", "password123")

        coVerify { authRepository.login("user@example.com", "password123") }
    }

    @Test
    fun `resetState returns to Idle`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns mockk()
        viewModel.login("user@example.com", "password123")
        assertEquals(LoginState.Success, viewModel.state.value)

        viewModel.resetState()

        assertEquals(LoginState.Idle, viewModel.state.value)
    }
}
