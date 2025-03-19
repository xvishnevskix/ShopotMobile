package org.videotrade.shopot.api

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

fun navigateToScreen(navigator: Navigator, screen: Screen) {
    
    if (navigator.lastItem::class != screen::class) {
        navigator.push(screen)
    }
}
