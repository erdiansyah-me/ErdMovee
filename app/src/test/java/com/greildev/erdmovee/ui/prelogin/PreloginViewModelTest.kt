package com.greildev.erdmovee.ui.prelogin

import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.usecase.UserUseCase
import com.greildev.core.utils.UIState
import com.greildev.erdmovee.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class PreloginViewModelTest {

    val email = "mael@mail.com"
    val password = "123123123"

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var userUseCase: UserUseCase
    private lateinit var preloginViewModel: PreloginViewModel
    private lateinit var authRequest: AuthRequest

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        preloginViewModel = PreloginViewModel(userUseCase)
    }

    @Test
    fun `register user success`() = runTest {

        val request = AuthRequest(email = email, password = password)

        val expected = true
        val mockResult = flowOf(UIState.Success(true))

        whenever(userUseCase.userRegister(authRequest = request)).thenReturn(mockResult)

        preloginViewModel.registerUser(email, password)

        verify(userUseCase).userRegister(authRequest= request)

        val result = mutableListOf<Boolean>()
        preloginViewModel.userRegister.collectLatest {
            when (it) {
                is UIState.Success -> it.data?.let { it1 -> result.add(it1) }
                is UIState.Error -> result.add(false)
                is UIState.Loading -> result.add(false)
                is UIState.NoState -> result.add(false)
            }
        }
        assertEquals(expected, result.last())
    }

}
