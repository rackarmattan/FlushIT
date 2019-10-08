package com.example.flushit.model

/**
 * Class that collects the data access objects, for now the toiletDAO and userDAO.
 */
class Database private constructor() {

    val toiletDao = ToiletDAO()
    val userDao = UserDAO()

    /**
     * Makes the class a singleton with the opportunity to add parameters to the constructor later.
     */
    companion object{
        @Volatile private var instance: Database? = null

        fun getInstance()=
            instance ?: synchronized(this){
                instance ?: Database().also { instance = it }
            }
    }
}