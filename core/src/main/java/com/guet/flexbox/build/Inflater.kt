package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.data.LockedInfo

object Inflater {

    fun inflate(c: ComponentContext, lockedInfo: LockedInfo): Component? {
        return widgets[lockedInfo.type]?.create(
                c,
                lockedInfo,
                lockedInfo.children.mapNotNull {
                    inflate(c, it)
                }
        )
    }

    private val widgets = mapOf(
            "Flex" to Flex,
            "Image" to Image,
            "Empty" to Empty,
            "Native" to Native,
            "Scroller" to Scroller,
            "Stack" to Stack,
            "Text" to Text,
            "TextInput" to TextInput
    )
}