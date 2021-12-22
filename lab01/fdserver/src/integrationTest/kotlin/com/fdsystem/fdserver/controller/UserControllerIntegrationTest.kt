package com.fdsystem.fdserver.controller

import com.fdsystem.fdserver.controllers.jwt.JwtResponse
import com.fdsystem.fdserver.factories.NewPasswordDTOFactory
import com.fdsystem.fdserver.factories.UserControllerFactory
import com.fdsystem.fdserver.factories.UserCredentialsDTOFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserControllerIntegrationTest {
    private val controllerFactory = UserControllerFactory()
    private val userCredentialsDTOFactory = UserCredentialsDTOFactory()
    private val newPasswordDTOFactory = NewPasswordDTOFactory()

    private val controller = controllerFactory.getUserController()

    @Test
    fun loginSuccessTest() {
        // Arrange
        val userCredentials = userCredentialsDTOFactory.getExistingUser()

        // Act
        val gotResponse = controller.login(userCredentials)

        // Assert
        assertTrue(gotResponse.body is JwtResponse)
        assertTrue(gotResponse.statusCode.is2xxSuccessful)
    }

    @Test
    fun loginFailureTest() {
        // Arrange
        val userCredentials = userCredentialsDTOFactory.getNotExistingUser()

        // Act
        val gotResponseCode = controller.login(userCredentials).statusCode

        // Assert
        assertTrue(gotResponseCode.isError)
    }

    @Test
    fun registerSuccessTest() {
        // Arrange
        val userCredentials = userCredentialsDTOFactory.getNewUserForCreation()

        // Act
        val gotResponse = controller.register(userCredentials)

        // Assert
        assertTrue(gotResponse.statusCode.is2xxSuccessful)

        val loginTryResponse = controller.login(userCredentials)
        assertTrue(loginTryResponse.body is JwtResponse)
        assertTrue(loginTryResponse.statusCode.is2xxSuccessful)
    }

    @Test
    fun registerFailureTest() {
        // Arrange
        val userCredentials = userCredentialsDTOFactory.getExistingUser()

        // Act
        val gotResponse = controller.register(userCredentials)

        // Assert
        assertTrue(gotResponse.statusCode.isError)
    }

    @Test
    fun changePasswordSuccessTest() {
        // Arrange
        val userCredentials = userCredentialsDTOFactory.getExistingUser()
        val newUserCredentials = userCredentialsDTOFactory.getExistingUserWithNewPassword()

        val userJwtToken = "Bearer " + (controller.login(userCredentials).body as JwtResponse).token
        val newPassword = newPasswordDTOFactory.getNewPasswordDTO(userCredentials.password)
        controller.changePassword(newPassword, userJwtToken)

        val newUserJwtToken = "Bearer " + (controller.login(newUserCredentials).body as JwtResponse).token
        val newPasswordToReturn =
            newPasswordDTOFactory.getNewPasswordDTOForReturnToPreviousState(userCredentials.password)

        // Act
        val gotResponse = controller.changePassword(newPasswordToReturn, newUserJwtToken)

        // Assert
        assertTrue(gotResponse.statusCode.is2xxSuccessful)

        val loginTryResponse = controller.login(userCredentials)
        assertTrue(loginTryResponse.statusCode.is2xxSuccessful)
    }

    @Test
    fun changePasswordFailureTest() {
        // Arrange
        val userCredentials = userCredentialsDTOFactory.getExistingUser()
        val userJwtToken = "Bearer " + (controller.login(userCredentials).body as JwtResponse).token

        val newPassword = newPasswordDTOFactory.getNewPasswordDTOForReturnToPreviousState(userCredentials.password)

        // Act
        val gotResponse = controller.changePassword(newPassword, userJwtToken)

        // Assert
        assertTrue(gotResponse.statusCode.isError)
    }
}