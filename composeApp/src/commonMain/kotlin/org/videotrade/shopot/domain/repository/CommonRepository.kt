package org.videotrade.shopot.domain.repository

import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.StateFlow


interface CommonRepository {
    
    val mainNavigator: StateFlow<Navigator?>
    
    
    fun setNavigator(mainNavigatorNew: Navigator)
    
}