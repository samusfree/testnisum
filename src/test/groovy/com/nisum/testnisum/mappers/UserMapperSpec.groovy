package com.nisum.testnisum.mappers


import com.nisum.testnisum.data.entity.User
import com.nisum.testnisum.security.CustomUserDetails
import com.nisum.testnisum.util.CreateObjectsUtil
import org.mapstruct.factory.Mappers
import spock.lang.Specification

class UserMapperSpec extends Specification {

    UserMapper userMapper = Mappers.getMapper(UserMapper)

    def "should map UserRequestDTO to User"() {
        given:
        def request = CreateObjectsUtil.getUserRequestDTO()
        def token = "sampleToken"

        when:
        def user = userMapper.fromRequest(request, token)

        then:
        user.email == request.email()
        user.password == request.password()
        user.token == token
    }

    def "should map UserRequestDTO null and valid token to User"() {
        given:
        def token = "sampleToken"

        when:
        def user = userMapper.fromRequest(null, token)

        then:
        user.email == null
        user.password == null
        user.token == token
    }

    def "should map UserRequestDTO and null token to User"() {
        given:
        def request = CreateObjectsUtil.getUserRequestDTO()

        when:
        def user = userMapper.fromRequest(request, null)

        then:
        user.email == request.email()
        user.password == request.password()
        user.token == null
    }

    def "should map UserRequestDTO null and null token to User"() {
        given:

        when:
        def user = userMapper.fromRequest(null, null)

        then:
        user == null
    }

    def "should map PhoneDTO to Phone"() {
        given:
        def userId = UUID.randomUUID()
        def phoneDTO = CreateObjectsUtil.getPhoneDTO()

        when:
        def phone = userMapper.fromPhoneDTO(userId, phoneDTO)

        then:
        phone.userId == userId
        phone.number == phoneDTO.number()
        phone.cityCode == phoneDTO.cityCode()
        phone.countryCode == phoneDTO.countryCode()
    }

    def "should map PhoneDTO null and valid UserId to Phone"() {
        given:
        def userId = UUID.randomUUID()

        when:
        def phone = userMapper.fromPhoneDTO(userId, null)

        then:
        phone.userId == userId
        phone.number == null
        phone.cityCode == null
        phone.countryCode == null
    }

    def "should map PhoneDTO and null UserId to Phone"() {
        given:
        def phoneDTO = CreateObjectsUtil.getPhoneDTO()

        when:
        def phone = userMapper.fromPhoneDTO(null, phoneDTO)

        then:
        phone.userId == null
        phone.number == phoneDTO.number()
        phone.cityCode == phoneDTO.cityCode()
        phone.countryCode == phoneDTO.countryCode()
    }

    def "should map null PhoneDTO and null UserId to Phone"() {
        given:

        when:
        def phone = userMapper.fromPhoneDTO(null, null)

        then:
        phone == null
    }

    def "should map Phone to PhoneDTO"() {
        given:
        def userId = UUID.randomUUID()
        def phone = userMapper.fromPhoneDTO(userId, CreateObjectsUtil.getPhoneDTO())

        when:
        def phoneDTO = userMapper.fromPhone(phone)

        then:
        phoneDTO.number() == phone.number
        phoneDTO.cityCode() == phone.cityCode
        phoneDTO.countryCode() == phone.countryCode
    }

    def "should map null Phone to PhoneDTO"() {
        given:

        when:
        def phoneDTO = userMapper.fromPhone(null)

        then:
        phoneDTO == null
    }

    def "should map User to UserDTO"() {
        given:
        def user = CreateObjectsUtil.getUser()
        def phones = [CreateObjectsUtil.getPhoneDTO()]

        when:
        def userDTO = userMapper.fromUser(user, phones)

        then:
        userDTO.email() == user.email
        userDTO.created() == user.createdAt
        userDTO.modified() == user.updatedAt
        userDTO.lastLogin() == user.lastLogin
        userDTO.phones() == phones
    }

    def "should map null User and valid phones to UserDTO"() {
        given:
        def phones = [CreateObjectsUtil.getPhoneDTO()]

        when:
        def userDTO = userMapper.fromUser(null, phones)

        then:
        userDTO.email() == null
        userDTO.created() == null
        userDTO.phones() == phones
    }

    def "should map User and null phones to UserDTO"() {
        given:
        def user = CreateObjectsUtil.getUser()

        when:
        def userDTO = userMapper.fromUser(user, null)

        then:
        userDTO.email() == user.email
        userDTO.created() == user.createdAt
        userDTO.modified() == user.updatedAt
        userDTO.lastLogin() == user.lastLogin
        userDTO.phones() == null
    }

    def "should map null User and null phones to UserDTO"() {
        given:

        when:
        def userDTO = userMapper.fromUser(null, null)

        then:
        userDTO == null
    }

    def "should map User to CustomUserDetails"() {
        given:
        User user = new User(email: "test@example.com", password: "password")

        when:
        CustomUserDetails customUserDetails = userMapper.customUserDetailsFromUser(user)

        then:
        customUserDetails.username == user.email
        customUserDetails.password == user.password
        customUserDetails.toString() == "CustomUserDetails(id=null, email=test@example.com, password=password, active=false)"
    }

    def "should map null User to CustomUserDetails"() {
        given:

        when:
        CustomUserDetails customUserDetails = userMapper.customUserDetailsFromUser(null)

        then:
        customUserDetails == null
    }
}
