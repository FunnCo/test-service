package com.funnco.testservice.service

import com.funnco.testservice.utils.RetrofitHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service



@Service
class UserRestService {

    private val logger = LoggerFactory.getLogger(this::class.java.simpleName)

    fun validateGroupsByRest(groups: List<String>, jwt: String): Boolean{
        val callResult = RetrofitHandler.userAPI.areGroupsValid(groups, jwt).execute()
        return if(callResult.isSuccessful && callResult.code() == 200){
            try {
                callResult.body()!!
            } catch (e: Exception){
                logger.info("Error while executing rest call to AuthAPI:\n${callResult.code()}\n\n${callResult.raw()}")
                logger.info("Error while executing rest call to AuthAPI:\n${e.localizedMessage}\n${e.stackTrace}")
                false
            }
        } else {
            logger.info("Error while executing rest call to AuthAPI:\n${callResult.code()}\n\n${callResult.raw()}")
            false
        }
    }

}